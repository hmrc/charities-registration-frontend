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
import controllers.routes
import models.UserAnswers
import models.requests.{DataRequest, OptionalDataRequest}
import pages.AcknowledgementReferencePage
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Result}

import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase {

  private val sut: Harness                         = new Harness
  private val acknowledgedUserAnswers: UserAnswers =
    emptyUserAnswers.set(AcknowledgementReferencePage, "ReferenceCode").success.value

  class Harness extends DataRequiredActionImpl {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  "DataRequiredAction" when {
    ".refine" must {
      "redirect to PageNotFound page when there are no UserAnswers" in {
        val request: OptionalDataRequest[AnyContent] = OptionalDataRequest(fakeRequest, "internalId", None)

        val futureResult: Future[Either[Result, DataRequest[AnyContent]]] =
          sut.callRefine(OptionalDataRequest(request, "internalId", None))

        whenReady(futureResult) { result =>
          result.isLeft mustBe true
          result.left.toOption.get mustBe Redirect(routes.PageNotFoundController.onPageLoad())
        }

      }

      "redirect to the RegistrationSent page when the UserAnswers have an acknowledgment reference" in {
        val request: OptionalDataRequest[AnyContent] =
          OptionalDataRequest(fakeRequest, "internalId", Some(acknowledgedUserAnswers))

        val futureResult: Future[Either[Result, DataRequest[AnyContent]]] =
          sut.callRefine(OptionalDataRequest(request, "internalId", Some(acknowledgedUserAnswers)))

        whenReady(futureResult) { result =>
          result.isLeft mustBe true
          result.left.toOption.get mustBe Redirect(routes.RegistrationSentController.onPageLoad)
        }
      }

      "pass the DataRequest on when UserAnswers do not have an acknowledgment reference" in {

        val request: OptionalDataRequest[AnyContent] =
          OptionalDataRequest(fakeRequest, "internalId", Some(emptyUserAnswers))

        val futureResult: Future[Either[Result, DataRequest[AnyContent]]] =
          sut.callRefine(OptionalDataRequest(request, "internalId", Some(emptyUserAnswers)))

        whenReady(futureResult) { result =>
          result.isRight mustBe true
          result.toOption.get mustBe DataRequest(request, request.internalId, emptyUserAnswers)
        }
      }
    }
  }
}
