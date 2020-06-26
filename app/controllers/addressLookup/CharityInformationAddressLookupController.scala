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
import connectors.addressLookup.httpParsers.AddressLookupInitializationHttpParser.AddressLookupOnRamp
import controllers.LocalBaseController
import controllers.actions._
import javax.inject.Inject
import models.NormalMode
import navigation.CharityInformationNavigator
import pages.addressLookup.CharityInformationAddressLookupPage
import pages.sections.Section1Page
import play.api.Logger
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.ErrorHandler

import scala.concurrent.Future


class CharityInformationAddressLookupController @Inject()(
  sessionRepository: UserAnswerRepository,
  navigator: CharityInformationNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  addressLookupConnector: AddressLookupConnector,
  errorHandler: ErrorHandler,
  val controllerComponents: MessagesControllerComponents
 )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  def initializeJourney: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
          addressLookupConnector.initialize()(hc, ec, messagesApi) map {
            case Right(AddressLookupOnRamp(url)) => Redirect(url)
            case Left(_) => InternalServerError(errorHandler.internalServerErrorTemplate)
          }
  }

  def callback(id: Option[String]): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    id match {
      case Some(addressId) =>
          addressLookupConnector.retrieveAddress(addressId) flatMap {
            case Right(address) =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(CharityInformationAddressLookupPage, address).flatMap(_.set(Section1Page, false)))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(CharityInformationAddressLookupPage, NormalMode, updatedAnswers))
            case _ =>
              Logger.error("[AddressLookupController][callback] error was returned on callback from address lookup")
              Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
          }
      case _ =>
        Logger.error("[AddressLookupController][callback] No ID was returned on callback from address lookup")
        Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
    }
  }
}
