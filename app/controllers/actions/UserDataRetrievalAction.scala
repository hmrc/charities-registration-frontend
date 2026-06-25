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

package controllers.actions

import connectors.CharitiesConnector
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserDataRetrievalActionImpl @Inject() (val charitiesConnector: CharitiesConnector)(implicit
  val executionContext: ExecutionContext
) extends UserDataRetrievalAction {

  override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, OptionalDataRequest[A]]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    charitiesConnector.getUserAnswers(request.identifier).map {
      case Left(UpstreamErrorResponse(_, _, _, _)) =>
        // TODO: This is temporary - design ticket created to come up with a more suitable error page.
        Left(Redirect(controllers.routes.PageNotFoundController.onPageLoad())) 
      case Right(optUserAnswers)                   => Right(OptionalDataRequest(request.request, request.identifier, optUserAnswers))
    }
  }
}

trait UserDataRetrievalAction extends ActionRefiner[IdentifierRequest, OptionalDataRequest]
