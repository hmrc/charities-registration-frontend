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

package connectors.httpParsers

import base.SpecBase
import connectors.httpParsers.CharitiesRegistrationHttpParser.CharitiesRegistrationResponseReads
import models.RegistrationResponse
import play.api.http.Status
import play.api.libs.json.{JsResultException, Json}
import uk.gov.hmrc.http.HttpResponse

class CharitiesRegistrationHttpParserSpec extends SpecBase {

  "CharitiesRegistrationHttpParser.read" when {
    "status is ACCEPTED" when {
      "passing valid Json" must {
        "return a valid registration response" in {
          val expectedResult = Right(RegistrationResponse("765432"))
          val actualResult   = CharitiesRegistrationResponseReads.read(
            "",
            "",
            HttpResponse(Status.ACCEPTED, json = Json.parse("""{"acknowledgementReference":"765432"}"""), Map.empty)
          )

          actualResult mustBe expectedResult
        }
      }

      "passing invalid Json" must {
        "throw an exception" in {
          intercept[JsResultException] {
            CharitiesRegistrationResponseReads.read(
              "",
              "",
              HttpResponse(Status.ACCEPTED, json = Json.parse("""{"blah":"765432"}"""), Map.empty)
            )
          }
        }
      }
    }

    "status is BAD_REQUEST" should {
      "return a RequestNotAccepted" in {
        val expectedResult = Left(RequestNotAccepted)
        val actualResult   = CharitiesRegistrationResponseReads.read("", "", HttpResponse(Status.BAD_REQUEST, ""))

        actualResult mustBe expectedResult
      }
    }

    "any other status" should {
      "return an UnexpectedFailure" in {
        val expectedResult = Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR))
        val actualResult   =
          CharitiesRegistrationResponseReads.read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        actualResult mustBe expectedResult
      }
    }
  }
}
