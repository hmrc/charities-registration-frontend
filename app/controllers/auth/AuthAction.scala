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

package controllers.auth

import java.util.UUID

import com.google.inject.ImplementedBy
import config.AppConfig
import controllers.auth.requests.AuthenticatedRequest
import controllers.routes
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.mvc.Results.Redirect
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthActionImpl @Inject()( val authConnector: AuthConnector,
                                appConfig: AppConfig,
                                mcc: MessagesControllerComponents)
  extends AuthAction with AuthorisedFunctions {

  override def parser: BodyParser[AnyContent] =  mcc.parsers.defaultBodyParser
  override implicit protected def executionContext: ExecutionContext = mcc.executionContext

	override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised(AffinityGroup.Organisation)
      .retrieve(Retrievals.credentials) {
        case Some(credentials) =>
          block(AuthenticatedRequest(credentials.providerId, request))
      }
  } recover {
    case e: NoActiveSession =>
			Logger.warn(s"[AuthActionImpl][invokeBlock] NoActive Session, Redirect to sign in - $e")
      Redirect(appConfig.signIn, Map("continue" -> Seq(appConfig.loginCallback), "origin" -> Seq(appConfig.appName)))
    case er: AuthorisationException =>
      Logger.error(s"[AuthActionImpl][invokeBlock] AuthorisationException, Redirect to register-as-an-organisation- $er")
      Redirect(controllers.routes.CharitiesRegisterOrganisationController.onPageLoad().url)
	}
}

@ImplementedBy(classOf[AuthActionImpl])
trait AuthAction extends ActionBuilder[AuthenticatedRequest, AnyContent] with ActionFunction[Request, AuthenticatedRequest]

class SessionIdentifierAction @Inject()(appConfig: AppConfig,
                                        val controllerComponents: MessagesControllerComponents
                                       ) extends FrontendBaseController with AuthAction {

  override implicit protected def executionContext: ExecutionContext = controllerComponents.executionContext
  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.defaultBodyParser

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    hc.sessionId match {
      case Some(session) =>
        block(AuthenticatedRequest(session.value, request))
      case _ =>
        Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad().url))
    }
  }
}
