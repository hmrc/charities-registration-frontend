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


class WebsiteAddressControllerSpec extends TestHelper  {

  lazy val websiteAddressController = fakeApplication.injector.instanceOf[WebsiteAddressController]

    "websiteAddressController" should {

      "Successfully load the valid websiteaddress page" in {
        lazy val request = FakeRequest("GET", "/website-address")
        lazy val result = websiteAddressController.onPageLoad(request)
        status(result) shouldBe Status.OK
      }

      "redirect to the next page when valid data is submitted" in {
        implicit val request = fakeRequest.withFormUrlEncodedBody(("website", "www.google.com"))

        lazy val result = websiteAddressController.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER
        result.header.headers.get("Location").get shouldBe "/hmrc-register-charity-details/hello-world"
      }

      "return a Bad Request and errors when invalid data is submitted" in {
        implicit val request = fakeRequest.withFormUrlEncodedBody(("website", "www.google"))
        lazy val result = websiteAddressController.onSubmit(request)
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
}