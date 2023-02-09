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

package connectors.httpParsers

import base.SpecBase
import ConfirmedAddressHttpParser.ConfirmedAddressReads
import base.data.constants.ConfirmedAddressConstants
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

class ConfirmedAddressHttpParserSpec extends SpecBase {

  "ConfirmedAddressHttpParser.ConfirmedAddressReads" when {

    "given an (200) ACCEPTED" when {

      "valid address is returned" must {

        "return the address" in {

          val expectedResult = Right(ConfirmedAddressConstants.address)
          val actualResult   = ConfirmedAddressReads.read(
            "",
            "",
            HttpResponse(Status.OK, json = ConfirmedAddressConstants.addressLookupResponse, Map.empty)
          )

          actualResult mustBe expectedResult
        }
      }

      "the address is malfored" must {

        "return a AddressMalformed err" in {

          val expectedResult = Left(AddressMalformed)
          val actualResult   = ConfirmedAddressReads.read(
            "",
            "",
            HttpResponse(Status.OK, json = Json.obj("address" -> Json.obj()), Map.empty)
          )

          actualResult mustBe expectedResult
        }
      }
    }

    "given any other status" should {

      "return a Left(AddressNotFound)" in {

        val expectedResult = Left(AddressNotFound)
        val actualResult   = ConfirmedAddressReads.read("", "", HttpResponse(Status.NOT_FOUND, ""))

        actualResult mustBe expectedResult
      }

      "return a Left(UnexpectedFailure)" in {

        val expectedResult = Left(DefaultedUnexpectedFailure(status = Status.INTERNAL_SERVER_ERROR))
        val actualResult   = ConfirmedAddressReads.read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        actualResult mustBe expectedResult
      }
    }
  }
}
