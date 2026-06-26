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

import base.SpecBase
import connectors.CharitiesConnector
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.Result
import uk.gov.hmrc.http.UpstreamErrorResponse
import play.api.http.Status._
import scala.concurrent.Future

class UserDataRetrievalActionSpec extends SpecBase with ScalaFutures {

  class Harness(charitiesConnector: CharitiesConnector) extends UserDataRetrievalActionImpl(charitiesConnector) {
    def callTransform[A](request: IdentifierRequest[A]): Future[Either[Result, OptionalDataRequest[A]]] = refine(
      request
    )
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "set userAnswers to 'None' in the request" in {

        val charitiesConnector = mock(classOf[CharitiesConnector])
        when(charitiesConnector.getUserAnswers(any())(any(), any())) `thenReturn` Future.successful(Right(None))
        val action             = new Harness(charitiesConnector)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "id"))

        whenReady(futureResult) { result =>
          result.map(_.userAnswers.isEmpty) mustBe Right(true)
        }
      }
    }

    "there is data in the cache" must {

      "build a userAnswers object and add it to the request" in {

        val charitiesConnector = mock(classOf[CharitiesConnector])
        when(charitiesConnector.getUserAnswers(any())(any(), any())) `thenReturn` Future.successful(
          Right(Some(UserAnswers("id")))
        )
        val action             = new Harness(charitiesConnector)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "id"))

        whenReady(futureResult) { result =>
          result.map(_.userAnswers.isDefined) mustBe Right(true)
        }
      }
    }

    "there is a 4xx returned from user answers call" must {

      "set userAnswers to 'None' in the request" in {

        val charitiesConnector = mock(classOf[CharitiesConnector])
        when(charitiesConnector.getUserAnswers(any())(any(), any())) `thenReturn` Future.successful(
          Left(UpstreamErrorResponse("error", NOT_FOUND))
        )
        val action             = new Harness(charitiesConnector)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "id"))

        whenReady(futureResult) { result =>
          result.map(_.userAnswers.isEmpty) mustBe Right(true)
        }
      }
    }
    "there is a 5xx returned from user answers call" must {

      "set userAnswers to 'None' in the request" in {

        val charitiesConnector = mock(classOf[CharitiesConnector])
        when(charitiesConnector.getUserAnswers(any())(any(), any())) `thenReturn` Future.successful(
          Left(UpstreamErrorResponse("error", INTERNAL_SERVER_ERROR))
        )
        val action             = new Harness(charitiesConnector)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "id"))

        whenReady(futureResult) { result =>
          result.map(_.userAnswers.isEmpty) mustBe Right(true)
        }
      }
    }
  }
}
