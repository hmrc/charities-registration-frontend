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

package controllers.actions

import java.util.UUID

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierAction @Inject()(
   override val authConnector: AuthConnector,
   config: FrontendAppConfig,
   val parser: BodyParsers.Default
  )
  (implicit val executionContext: ExecutionContext) extends AuthIdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    if (config.isExternalTest) {
      //scalastyle:off magic.number
      val internalId: String = UUID.randomUUID.toString.replaceAll(
        "[^a-zA-Z0-9]", "").toUpperCase.substring(0,16)
      block(IdentifierRequest(request, hc.sessionId.fold(internalId)(_.value)))
    } else {
      authorised(AffinityGroup.Organisation).retrieve(Retrievals.credentials) {
        _.map {
          credentials => block(IdentifierRequest(request, credentials.providerId))
        }.getOrElse(throw new UnauthorizedException("Unable to retrieve internal Id"))
      } recover {
        case _: NoActiveSession =>
          val redirectUrl = hc.sessionId match {
            case Some(id) => s"${config.loginContinueUrl}/${id.value}"
            case None => config.loginContinueUrl
          }
          Redirect(config.loginUrl, Map(config.loginContinueKey -> Seq(redirectUrl), "origin" -> Seq(config.appName)))
        case _: AuthorisationException =>
          Redirect(controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad())
      }
    }
  }
}

trait AuthIdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]


class SessionIdentifierAction @Inject()(
   val parser: BodyParsers.Default
 )
 (implicit val executionContext: ExecutionContext) extends IdentifierAction {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    hc.sessionId match {
      case Some(session) =>
        block(IdentifierRequest(request, session.value))
      case None =>
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }
}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]
