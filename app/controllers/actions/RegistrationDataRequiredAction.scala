/*
 * Copyright 2023 HM Revenue & Customs
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

import javax.inject.Inject
import controllers.routes
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class RegistrationDataRequiredActionImpl @Inject() (implicit val executionContext: ExecutionContext)
    extends RegistrationDataRequiredAction {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = {

//    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    request.userAnswers match {
      case None                                                       =>
        Future.successful(Left(Redirect(routes.PageNotFoundController.onPageLoad())))
//      case Some(data) if data.get(OldServiceSubmissionPage).isDefined =>
//        Future.successful(Left(Redirect(routes.ApplicationBeingProcessedController.onPageLoad)))
      case Some(data)                                                 =>
        Future.successful(Right(DataRequest(request.request, request.internalId, data)))
    }
  }
}

trait RegistrationDataRequiredAction extends ActionRefiner[OptionalDataRequest, DataRequest]
