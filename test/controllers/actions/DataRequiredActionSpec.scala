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

import base.SpecBase
import config.FrontendAppConfig
import controllers.routes
import models.UserAnswers
import models.requests.{DataRequest, OptionalDataRequest}
import org.mockito.MockitoSugar.{mock, when}
import pages.AcknowledgementReferencePage
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Result}

import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase {

  implicit val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

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

      "redirect to the RegistrationSent page when the UserAnswers have an acknowledgment reference and noEmailPost feature is on" in {
        when(mockFrontendAppConfig.noEmailPost).thenReturn(true)

        val request: OptionalDataRequest[AnyContent] =
          OptionalDataRequest(fakeRequest, "internalId", Some(acknowledgedUserAnswers))

        val futureResult: Future[Either[Result, DataRequest[AnyContent]]] =
          sut.callRefine(OptionalDataRequest(request, "internalId", Some(acknowledgedUserAnswers)))

        whenReady(futureResult) { result =>
          result.isLeft mustBe true
          result.left.toOption.get mustBe Redirect(routes.RegistrationSentController.onPageLoad)
        }
      }

      "redirect to the EmailOrPost page when the UserAnswers have an acknowledgment reference and noEmailPost feature is off" in {
        when(mockFrontendAppConfig.noEmailPost).thenReturn(false)

        val request: OptionalDataRequest[AnyContent] =
          OptionalDataRequest(fakeRequest, "internalId", Some(acknowledgedUserAnswers))

        val futureResult: Future[Either[Result, DataRequest[AnyContent]]] =
          sut.callRefine(OptionalDataRequest(request, "internalId", Some(acknowledgedUserAnswers)))

        whenReady(futureResult) { result =>
          result.isLeft mustBe true
          result.left.toOption.get mustBe Redirect(routes.EmailOrPostController.onPageLoad)
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
