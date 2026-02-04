/*
 * Copyright 2026 HM Revenue & Customs
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

package viewmodels

import config.FrontendAppConfig
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.errors.{PageNotFoundView, TechnicalDifficultiesErrorView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ErrorHandler @Inject() (
  val messagesApi: MessagesApi,
  technicalDifficultiesErrorView: TechnicalDifficultiesErrorView,
  pageNotFoundView: PageNotFoundView,
  authConnector: AuthConnector
)(implicit appConfig: FrontendAppConfig, val ec: ExecutionContext)
    extends FrontendErrorHandler
    with I18nSupport {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit
    request: RequestHeader
  ): Future[Html] =
    Future.successful(technicalDifficultiesErrorView(pageTitle, heading, message))

  override def notFoundTemplate(implicit request: RequestHeader): Future[Html] =
    isUserSignedIn(request).map { signedIn =>
      pageNotFoundView(signedIn)
    }

  private def isUserSignedIn(request: RequestHeader): Future[Boolean] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authConnector
      .authorise(AffinityGroup.Organisation, Retrievals.credentials)
      .map {
        case Some(_) => true
        case _       => false
      }
      .recover { case _ =>
        false
      }
  }

}
