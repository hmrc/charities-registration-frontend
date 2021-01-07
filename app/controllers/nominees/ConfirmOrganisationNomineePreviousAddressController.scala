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

package controllers.nominees

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.ConfirmAddressController
import javax.inject.Inject
import models.{Index, NormalMode}
import pages.addressLookup.{OrganisationNomineePreviousAddressLookupPage, OtherOfficialAddressLookupPage}
import pages.nominees.OrganisationNomineeNamePage
import pages.otherOfficials.OtherOfficialsNamePage
import play.api.mvc._
import service.CountryService
import views.html.common.ConfirmAddressView

class ConfirmOrganisationNomineePreviousAddressController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val countryService: CountryService,
    override val controllerComponents: MessagesControllerComponents,
    override val view: ConfirmAddressView,
    override implicit val appConfig: FrontendAppConfig
  ) extends ConfirmAddressController {

  override val messagePrefix: String = "nomineeOrganisationPreviousAddress"

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getOrganisationName(OrganisationNomineeNamePage) { organisationNomineeName =>
        getView(controllers.nominees.routes.IsOrganisationNomineePaymentsController.onPageLoad(NormalMode),
          OrganisationNomineePreviousAddressLookupPage,
          controllers.addressLookup.routes.OrganisationNomineePreviousAddressLookupController.initializeJourney(NormalMode),
          Some(organisationNomineeName))
      }
  }
}
