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

package controllers

import helpers.TestHelper
import play.api.http.Status
import play.api.test.FakeRequest


class UKBasedEligibilityControllerSpec extends TestHelper {

  def testController() = new UKBasedEligibilityController()(mockAppConfig, mcc)

    "EligibilityController" should {

      "Successfully load the eligibility page" in {
        lazy val request = FakeRequest("GET", "/eligible-location")
        lazy val result = testController.onPageLoad(request)
        status(result) shouldBe Status.OK
      }

      "process 'Yes' submit of the eligibility page" in {
        val form = ("ukbased", "Yes")
        implicit val request = FakeRequest("POST", "/eligible-location").withFormUrlEncodedBody(form)
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER
        result.header.headers.get("Location").get shouldBe "/hmrc-register-charity-details/hello-world"
      }

      "process 'No' submit of the eligibility page" in {
        val form = ("ukbased", "No")
        implicit val request = FakeRequest("POST", "/eligible-location").withFormUrlEncodedBody(form)
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER
        result.header.headers.get("Location").get shouldBe "/hmrc-register-charity-details/hello-world"
      }

      "show an error if nothing is selected" in {
        val form = ("ukbased", "")
        implicit val request = FakeRequest("POST", "/eligible-location").withFormUrlEncodedBody(form)
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
}