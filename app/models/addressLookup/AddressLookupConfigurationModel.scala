/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.addressLookup

import config.FrontendAppConfig
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{Json, Writes}

case class AddressLookupConfigurationModel(
  version: Int,
  options: AddressLookupOptionsModel,
  labels: AddressMessageLanguageModel
)

object AddressLookupConfigurationModel {
  implicit val writes: Writes[AddressLookupConfigurationModel] = Json.writes[AddressLookupConfigurationModel]

  def toAddressLookupConfigurationModel(
    callbackUrl: String,
    messagePrefix: String,
    fullName: Option[String],
    allowedCountryCodes: Option[Set[String]]
  )(implicit appConfig: FrontendAppConfig, messages: MessagesApi): AddressLookupConfigurationModel = {
    val english = Lang("en")
    val welsh   = Lang("cy")

    val fullCallbackURL = appConfig.host + callbackUrl
    val fullSignOutURL  = controllers.routes.SignOutController.signOut().url

    AddressLookupConfigurationModel(
      version = 2,
      options = AddressLookupOptionsModel(
        continueUrl = fullCallbackURL,
        signOutHref = fullSignOutURL,
        useNewGovUkServiceNavigation = true,
        deskProServiceName = appConfig.contactFormServiceIdentifier,
        showBackButtons = true,
        includeHMRCBranding = false,
        allowedCountryCodes = allowedCountryCodes,
        ukMode = false,
        selectPageConfig = AddressLookupSelectConfigModel(
          showSearchAgainLink = true
        ),
        confirmPageConfig = AddressLookupConfirmConfigModel(
          showSubHeadingAndInfo = false
        ),
        timeoutConfig = AddressLookupConfirmTimeoutModel(
          timeoutAmount = appConfig.timeout,
          timeoutUrl = fullSignOutURL
        ),
        manualAddressEntryConfig = ManualAddressEntryConfigModel(
          line1MaxLength = 35,
          line2MaxLength = 35,
          line3MaxLength = 35,
          townMaxLength = 35,
          mandatoryFields = MandatoryFields(
            addressLine1 = true,
            addressLine2 = false,
            addressLine3 = false,
            town = true,
            postcode = false
          ),
          showOrganisationName = false
        )
      ),
      labels = AddressMessageLanguageModel(
        en = AddressMessagesModel.forLang(english, messagePrefix, fullName),
        cy = AddressMessagesModel.forLang(welsh, messagePrefix, fullName)
      )
    )
  }
}
