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

package connectors.httpParsers

import base.SpecBase
import connectors.httpParsers.CharitiesRegistrationHttpParser.CharitiesRegistrationResponseReads
import models.RegistrationResponse
import play.api.http.Status
import play.api.libs.json.{JsResultException, Json}
import uk.gov.hmrc.http.HttpResponse

class CharitiesRegistrationHttpParserSpec extends SpecBase {

  "CharitiesRegistrationHttpParser.read" when {

    "given an ACCEPTED" when {

      "passing valid Json" must {

        "return a valid registration response" in {

          val expectedResult = Right(RegistrationResponse("765432"))
          val actualResult = CharitiesRegistrationResponseReads.read("", "", HttpResponse(Status.ACCEPTED,
            json = Json.parse("""{"acknowledgementReference":"765432"}"""), Map.empty))

          actualResult mustBe expectedResult
        }
      }

      "passing valid Json" must {

        "throw an exception if response is not valid" in {

          intercept[JsResultException] {
            CharitiesRegistrationResponseReads.read("", "", HttpResponse(Status.ACCEPTED,
              json = Json.parse("""{"acknowledgementRef":"765432"}"""), Map.empty))
          }
        }
      }
    }

    "given NOT_ACCEPTABLE status" should {

      "return a Left(CharitiesInvalidJson)" in {

        val expectedResult = Left(CharitiesInvalidJson)
        val actualResult = CharitiesRegistrationResponseReads.read("", "", HttpResponse(Status.NOT_ACCEPTABLE, ""))

        actualResult mustBe expectedResult
      }
    }

    "given BAD_REQUEST status" should {

      "return a Left(EtmpFailed)" in {

        val expectedResult = Left(EtmpFailed)
        val actualResult = CharitiesRegistrationResponseReads.read("", "", HttpResponse(Status.BAD_REQUEST, ""))

        actualResult mustBe expectedResult
      }
    }

    "given any other response status" should {

      "return a Left(DefaultedUnexpectedFailure)" in {

        val expectedResult = Left(DefaultedUnexpectedFailure(Status.INTERNAL_SERVER_ERROR))
        val actualResult = CharitiesRegistrationResponseReads.read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        actualResult mustBe expectedResult
      }
    }
  }
}
