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


class ValidCountriesEligibilityControllerSpec extends TestHelper  {

  def testController() = new ValidCountriesEligibilityController()(mockAppConfig, mcc)

    "ValidCountriesEligibilityController" should {

      "Successfully load the valid countries page" in {
        lazy val request = FakeRequest("GET", "/eligible-countries")
        lazy val result = testController.onPageLoad(request)
        status(result) shouldBe Status.OK
      }

      "redirect to eligible sign in page when 'Yes' is submitted in valid countries eligibility page" in {
        val form = ("charitable", "Yes")
        implicit val request = FakeRequest("POST", "/eligible-countries").withFormUrlEncodedBody(form)
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER
        result.header.headers.get("Location").get shouldBe "/hmrc-register-charity-details/hello-world"
      }

      "redirect to not eligible page when 'No' is submitted in valid countries eligibility page" in {
        val form = ("charitable", "No")
        implicit val request = FakeRequest("POST", "/eligible-countries").withFormUrlEncodedBody(form)
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER
        result.header.headers.get("Location").get shouldBe "/hmrc-register-charity-details/ineligible-for-registration"
      }

      "show an error if nothing is selected" in {
        val form = ("charitable", "")
        implicit val request = FakeRequest("POST", "/eligible-countries").withFormUrlEncodedBody(form)
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
}