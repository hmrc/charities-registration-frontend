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

package controllers.contactDetails

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.AmendAddressController
import forms.common.AmendAddressFormProvider
import models.Mode
import navigation.CharityInformationNavigator
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.sections.Section1Page
import play.api.mvc._
import service.{CountryService, UserAnswerService}
import views.html.common.AmendAddressView

import javax.inject.Inject
import scala.concurrent.Future

class AmendCharityOfficialAddressController @Inject() (
  val identify: AuthIdentifierAction,
  val getData: UserDataRetrievalAction,
  val requireData: DataRequiredAction,
  val countryService: CountryService,
  val formProvider: AmendAddressFormProvider,
  override val sessionRepository: UserAnswerService,
  override val navigator: CharityInformationNavigator,
  override val controllerComponents: MessagesControllerComponents,
  override val view: AmendAddressView
)(implicit appConfig: FrontendAppConfig)
    extends AmendAddressController {

  override val messagePrefix: String = "amendCharityOfficialAddress"

  private val countryCodes = Set(
    "AT",
    "BE",
    "BG",
    "HR",
    "CY",
    "CZ",
    "DK",
    "EE",
    "FI",
    "FR",
    "DE",
    "GR",
    "HU",
    "IS",
    "IE",
    "IT",
    "LV",
    "LI",
    "LT",
    "LU",
    "MT",
    "NL",
    "NO",
    "PL",
    "PT",
    "RO",
    "SK",
    "SI",
    "ES",
    "SE",
    "GB"
  )

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    Future.successful(
      getView(
        CharityOfficialAddressLookupPage,
        controllers.contactDetails.routes.AmendCharityOfficialAddressController.onSubmit(),
        countryService.countries().filter(country => countryCodes.contains(country._1))
      )
    )
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      postView(
        mode,
        CharityOfficialAddressLookupPage,
        Section1Page,
        controllers.contactDetails.routes.AmendCharityOfficialAddressController.onSubmit(),
        countryService.countries().filter(country => countryCodes.contains(country._1))
      )
  }

}
