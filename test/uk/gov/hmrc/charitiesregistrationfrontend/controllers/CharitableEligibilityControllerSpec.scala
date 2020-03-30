/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.charitiesregistrationfrontend.controllers


import play.api.http.Status
import play.api.test.FakeRequest
import uk.gov.hmrc.charitiesregistrationfrontend.controllers.helpers.ControllerTestSpec


class CharitableEligibilityControllerSpec extends ControllerTestSpec {

  def createTestController() = {
    object TestController extends CharitableEligibilityController(mockConfig, mcc, mockForm)
    TestController
  }

    "EligibilityController" should {

      "Successfully load the eligibility page" in {
        lazy val request = FakeRequest("GET", "/eligible-purposes")
        lazy val controller = createTestController()
        lazy val result = controller.onPageLoad(request)
        status(result) shouldBe Status.OK
      }

      "process 'Yes' submit of the eligibility page" in {
        lazy val controller = createTestController()
        val form = ("charitable", "Yes")
        implicit val request = FakeRequest("POST", "/eligible-purposes").withFormUrlEncodedBody(form)
        lazy val result = controller.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER
        result.header.headers.get("Location").get shouldBe "/charities-registration-frontend/hello-world"
      }

      "process 'No' submit of the eligibility page" in {
        lazy val controller = createTestController()
        val form = ("charitable", "No")
        implicit val request = FakeRequest("POST", "/eligible-purposes").withFormUrlEncodedBody(form)
        lazy val result = controller.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER
        result.header.headers.get("Location").get shouldBe "/charities-registration-frontend/hello-world"
      }
    }
}