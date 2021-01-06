/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.checkEligibility

import base.SpecBase
import models.{NormalMode, UserAnswers}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.logging.SessionId
import views.html.checkEligibility.EligibleCharityView

class EligibleCharityControllerSpec extends SpecBase {


  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  private val view: EligibleCharityView = injector.instanceOf[EligibleCharityView]

  private val controller: EligibleCharityController = inject[EligibleCharityController]

  "SessionExpired Controller" must {

    "return OK and the correct view for a GET" in {

      val result = controller.onPageLoad(NormalMode)(FakeRequest())

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(NormalMode, None)(fakeRequest, messages, frontendAppConfig).toString
    }

    "return OK and the correct view for a GET with session Id" in {

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(NormalMode, Some(SessionId("foo")))(fakeRequest, messages, frontendAppConfig).toString
    }
  }
}

