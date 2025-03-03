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

package controllers.addressLookup

import connectors.addressLookup.AddressLookupConnector
import connectors.httpParsers.AddressLookupInitializationHttpParser.AddressLookupOnRamp
import controllers.LocalBaseController
import models.addressLookup.AddressModel
import models.requests.DataRequest
import models.{Mode, NormalMode}
import navigation.BaseNavigator
import pages.QuestionPage
import play.api.Logger
import play.api.mvc.{AnyContent, Result}
import service.UserAnswerService
import viewmodels.ErrorHandler

import scala.concurrent.{ExecutionContext, Future}

trait BaseAddressController extends LocalBaseController {
  protected val addressLookupConnector: AddressLookupConnector
  protected val errorHandler: ErrorHandler
  protected val sessionRepository: UserAnswerService
  protected val navigator: BaseNavigator
  protected val messagePrefix: String

  private val logger = Logger(this.getClass)

  def addressLookupInitialize(
    callbackUrl: String,
    fullName: Option[String] = None,
    allowedCountryCodes: Option[Set[String]] = None
  )(implicit request: DataRequest[AnyContent], ec: ExecutionContext): Future[Result] =
    addressLookupConnector
      .initialize(callbackUrl, messagePrefix, fullName, allowedCountryCodes)(
        hc,
        ec,
        messagesApi
      )
      .flatMap {
        case Right(AddressLookupOnRamp(url)) => Future.successful(Redirect(url))
        case Left(_)                         => errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
      }

  def addressLookupCallback(
    page: QuestionPage[AddressModel],
    pageSection: QuestionPage[Boolean],
    id: Option[String],
    mode: Mode = NormalMode
  )(implicit request: DataRequest[AnyContent]): Future[Result] =
    id match {
      case Some(addressId) =>
        addressLookupConnector.retrieveAddress(addressId) flatMap {
          case Right(address) =>
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(page, address).flatMap(_.set(pageSection, false)))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(page, mode, updatedAnswers))
          case _              =>
            logger.error(
              s"[BaseAddressController][addressLookupCallback][$page] error was returned on callback from address lookup"
            )
            errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
        }
      case _               =>
        logger.error(
          s"[BaseAddressController][addressLookupCallback][$page] No ID was returned on callback from address lookup"
        )
        errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
    }

}
