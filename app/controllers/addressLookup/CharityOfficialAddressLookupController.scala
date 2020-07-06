/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.addressLookup

import config.FrontendAppConfig
import connectors.addressLookup.AddressLookupConnector
import controllers.actions._
import javax.inject.Inject
import navigation.CharityInformationNavigator
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.sections.Section1Page
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.ErrorHandler


class CharityOfficialAddressLookupController @Inject()(
  sessionRepository: UserAnswerRepository,
  navigator: CharityInformationNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  addressLookupConnector: AddressLookupConnector,
  errorHandler: ErrorHandler,
  val controllerComponents: MessagesControllerComponents
 )(implicit appConfig: FrontendAppConfig) extends BaseAddressController {

  def initializeJourney: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val callBack : String = controllers.addressLookup.routes.CharityOfficialAddressLookupController.callback().url
      addressLookupInitialize(addressLookupConnector, errorHandler, callBack, "charityOfficialAddress")
  }

  def callback(id: Option[String]): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      addressLookupCallback(addressLookupConnector, errorHandler, sessionRepository, CharityOfficialAddressLookupPage, Section1Page, navigator, id)
  }
}
