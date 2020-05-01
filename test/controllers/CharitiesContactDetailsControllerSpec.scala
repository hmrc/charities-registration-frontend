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


class CharitiesContactDetailsControllerSpec extends TestHelper {

  def testController() = new CharitiesContactDetailsController()(mockAppConfig, mcc)

    "CharitiesContactDetailsController" should {

     "Successfully load the contactDetails page" in {
        lazy val request = FakeRequest("GET", "/contact-details")
        lazy val result = testController.onPageLoad(request)
        status(result) shouldBe Status.OK
      }

      "successfully process a valid submit of the charity Contact details page with just mandatory fields filled" in {
        val form = List(
          ("daytimePhone", "+44 0808 157 0192"),
          ("emailAddress", "name@example.com")
        )
        implicit val request = FakeRequest("POST", "/contact-details").withFormUrlEncodedBody(form: _*)
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER

      }

     "show an error if an incorrect Daytime phone number is provided" in {
        val form = List(
          ("daytimePhone", "+44 0808"),
          ("mobilePhone", "01632 960 001"),
          ("emailAddress", "name@example.com")
        )
        implicit val request = FakeRequest("POST", "/contact-details").withFormUrlEncodedBody(form: _*)
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.BAD_REQUEST

      }
     "show an error if an incorrect email address is provided" in {
         val form = List(
           ("daytimePhone", "+44 0808 157 0192"),
           ("mobilePhone", "01632 960 001"),
           ("emailAddress", "name@examplecom")
         )
         implicit val request = FakeRequest("POST", "/contact-details").withFormUrlEncodedBody(form: _*)
         lazy val result = testController.onSubmit(request)
         status(result) shouldBe Status.BAD_REQUEST

       }
      "show an error if mandatory data is not provided" in {
                  val form = List(
                    ("daytimePhone", ""),
                    ("mobilePhone", ""),
                    ("emailAddress", "")
                  )
                  implicit val request = FakeRequest("POST", "/contact-details").withFormUrlEncodedBody(form: _*)
                  lazy val result = testController.onSubmit(request)
                  status(result) shouldBe Status.BAD_REQUEST

                }

    }
}