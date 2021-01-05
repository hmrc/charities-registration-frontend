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

package controllers.contactDetails

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.ConfirmAddressController
import models.NormalMode
import models.addressLookup.AddressModel
import pages.QuestionPage
import pages.addressLookup.CharityOfficialAddressLookupPage
import play.api.mvc._
import views.html.common.ConfirmAddressView
import javax.inject.Inject
import service.CountryService

class ConfirmCharityOfficialAddressController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val countryService: CountryService,
    override val controllerComponents: MessagesControllerComponents,
    override val view: ConfirmAddressView,
    override implicit val appConfig: FrontendAppConfig
  ) extends ConfirmAddressController {

  override val messagePrefix: String = "charityOfficialAddress"
  override val page: QuestionPage[AddressModel] = CharityOfficialAddressLookupPage
  override def changeLinkCall: Call = controllers.addressLookup.routes.CharityOfficialAddressLookupController.initializeJourney()


  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
    getView(controllers.contactDetails.routes.CanWeSendToThisAddressController.onPageLoad(NormalMode))
  }
}
