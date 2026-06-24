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
import play.api.mvc.ActionTransformer
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserDataRetrievalActionImpl @Inject() (val charitiesConnector: CharitiesConnector)(implicit
  val executionContext: ExecutionContext
) extends UserDataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    val futureOptUserAnswers       = charitiesConnector.getUserAnswers(request.identifier).map {
      case Left(_)               => None
      case Right(optUserAnswers) => optUserAnswers
    }
    futureOptUserAnswers.map(optUserAnswers => OptionalDataRequest(request.request, request.identifier, optUserAnswers))
    // TODO: Convert Left to Future failed - wrap in DataRetrievalException - then redirect to error page in error handler for this exception type
  }
}

trait UserDataRetrievalAction extends ActionTransformer[IdentifierRequest, OptionalDataRequest]
