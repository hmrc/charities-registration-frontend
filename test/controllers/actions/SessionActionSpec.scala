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

import base.SpecBase
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.SessionKeys

class SessionActionSpec extends SpecBase {

  class Harness(action: SessionIdentifierAction) {
    def onPageLoad(): Action[AnyContent] = action(_ => Results.Ok)
  }

  "Session Action" when {

    "there's no active session" must {

      "redirect to the session expired page" in {

        val application = new GuiceApplicationBuilder()
          .configure(
            "play.filters.disabled" -> List("filters.SessionIdFilter")
          )
          .build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val sessionAction = new SessionIdentifierAction(bodyParsers)

        val controller = new Harness(sessionAction)

        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(controllers.routes.PageNotFoundController.onPageLoad().url)
      }
    }

    "there's an active session" must {

      "perform the action" in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val sessionAction = new SessionIdentifierAction(bodyParsers)

        val controller = new Harness(sessionAction)

        val request = fakeRequest.withSession(SessionKeys.sessionId -> "foo")

        val result = controller.onPageLoad()(request)

        status(result) mustBe OK
      }
    }
  }
}
