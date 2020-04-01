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

import com.google.inject.ImplementedBy
import config.AppConfig
import controllers.auth.requests.AuthenticatedRequest
import javax.inject.{Inject, Singleton}
import play.api.mvc.Results.Redirect
import play.api.mvc._
import play.api.{Logger, Mode, Play}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthActionImpl @Inject()( val authConnector: AuthConnector,
                                appConfig: AppConfig,
                                mcc: MessagesControllerComponents,
                                implicit val executionContext: ExecutionContext)
  extends AuthAction with AuthorisedFunctions {

	lazy val url: String = appConfig.signIn
	def loginParams: Map[String, Seq[String]] = Map(
		"continue" -> Seq(appConfig.loginCallback),
		"origin" -> Seq("charities-registration-frontend")
	)

  override def parser: BodyParser[AnyContent] =  mcc.parsers.defaultBodyParser

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
		  Redirect(appConfig.signIn, loginParams)
    case er: AuthorisationException =>
      Logger.error(s"[AuthActionImpl][invokeBlock] AuthorisationException, Redirect to register-as-an-organisation- $er")
      Redirect(controllers.routes.CharitiesRegisterOrganisationController.onPageLoad().url)
	}
}

@ImplementedBy(classOf[AuthActionImpl])
trait AuthAction extends ActionBuilder[AuthenticatedRequest, AnyContent] with ActionFunction[Request, AuthenticatedRequest]

@Singleton
class AuthConnector @Inject()(val http: DefaultHttpClient, val servicesConfig: ServicesConfig) extends PlayAuthConnector {
  val serviceUrl: String = servicesConfig.baseUrl("auth")
  protected def mode: Mode = Play.current.mode
}
