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

package controllers.actions

import config.FrontendAppConfig
import controllers.routes
import models.requests.{DataRequest, OptionalDataRequest}
import pages.AcknowledgementReferencePage
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRequiredActionImpl @Inject() (implicit val executionContext: ExecutionContext, appConfig: FrontendAppConfig)
    extends DataRequiredAction {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] =
    request.userAnswers match {
      case None       =>
        Future.successful(Left(Redirect(routes.PageNotFoundController.onPageLoad())))
      case Some(data) =>
        data.get(AcknowledgementReferencePage) match {
          case Some(_) if appConfig.noEmailPost =>
            Future.successful(Left(Redirect(routes.RegistrationSentController.onPageLoad)))
          case Some(_)                          => Future.successful(Left(Redirect(routes.EmailOrPostController.onPageLoad)))
          case _                                => Future.successful(Right(DataRequest(request.request, request.internalId, data)))
        }
    }
}

trait DataRequiredAction extends ActionRefiner[OptionalDataRequest, DataRequest]
