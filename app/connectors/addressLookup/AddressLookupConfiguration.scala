/*
 * Copyright 2021 HM Revenue & Customs
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

package connectors.addressLookup

import config.FrontendAppConfig
import javax.inject.Inject
import models.addressLookup._
import play.api.i18n.{Lang, MessagesApi}
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl

class AddressLookupConfiguration @Inject()(callbackUrl: String,
                                           messagePrefix: String,
                                           fullName: Option[String],
                                           allowedCountryCodes: Option[Set[String]])
                                          (implicit appConfig: FrontendAppConfig, messages: MessagesApi) {

  def apply: AddressLookupConfigurationModel = {
    val english = Lang("en")
    val welsh = Lang("cy")

    AddressLookupConfigurationModel(
      version = 2,
      options = AddressLookupOptionsModel(
        continueUrl = SafeRedirectUrl(appConfig.host + callbackUrl),
        phaseFeedbackLink = SafeRedirectUrl(appConfig.feedbackUrlAddressLookup),
        signOutHref = SafeRedirectUrl(controllers.routes.SignOutController.signOut().url),
        deskProServiceName = appConfig.contactFormServiceIdentifier,
        showPhaseBanner = true,
        showBackButtons = true,
        includeHMRCBranding = false,
        allowedCountryCodes = allowedCountryCodes,
        ukMode = false,
        disableTranslations = false,
        selectPageConfig = AddressLookupSelectConfigModel (
          showSearchAgainLink = true
        ),
        confirmPageConfig = AddressLookupConfirmConfigModel(
          showChangeLinkcontinueUrl = true,
          showSubHeadingAndInfo = true,
          showSearchAgainLink = false,
          showConfirmChangeText = false
        ),
        timeoutConfig = AddressLookupConfirmTimeoutModel(
          timeoutAmount = appConfig.timeout,
          timeoutUrl = SafeRedirectUrl(appConfig.host + controllers.routes.SignOutController.signOut())
        )
      ),
      labels = AddressMessageLanguageModel(
        en = AddressMessagesModel.forLang(english, messagePrefix, fullName),
        cy = AddressMessagesModel.forLang(welsh, messagePrefix, fullName)

      )
    )
  }

}
