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
import models.Index
import navigation.AuthorisedOfficialsNavigator
import pages.addressLookup.AuthorisedOfficialAddressLookupPage
import pages.sections.Section7Page
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.ErrorHandler

class AuthorisedOfficialsAddressLookupController @Inject()(
  sessionRepository: UserAnswerRepository,
  navigator: AuthorisedOfficialsNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  addressLookupConnector: AddressLookupConnector,
  errorHandler: ErrorHandler,
  val controllerComponents: MessagesControllerComponents
 )(implicit appConfig: FrontendAppConfig) extends BaseAddressController {

  def initializeJourney(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getAuthorisedOfficialName(index) { authorisedOfficialsName =>

        val callBack: String = controllers.addressLookup.routes.AuthorisedOfficialsAddressLookupController.callback(index).url

        addressLookupInitialize(addressLookupConnector, errorHandler, callBack, "authorisedOfficialAddress", Some(authorisedOfficialsName))
      }
  }

  def callback(index: Index, id: Option[String]): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      addressLookupCallback(addressLookupConnector, errorHandler, sessionRepository, AuthorisedOfficialAddressLookupPage(index), Section7Page, navigator, id)
  }
}
