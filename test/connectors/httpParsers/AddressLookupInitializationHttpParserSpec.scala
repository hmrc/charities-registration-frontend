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
import connectors.httpParsers.AddressLookupInitializationHttpParser.{AddressLookupInitializationReads, AddressLookupOnRamp}
import play.api.http.{HeaderNames, Status}
import uk.gov.hmrc.http.HttpResponse

class AddressLookupInitializationHttpParserSpec extends SpecBase {

  "AddressLookupInitializationHttpParser.AddressLookupInitializationReads" when {

    "given an (202) ACCEPTED" when {

      "the Location header is returned" must {

        "return a AddressLookupOnRamp with modulusUrl (/foo)" in {

          val expectedResult = Right(AddressLookupOnRamp("/foo"))
          val actualResult   = AddressLookupInitializationReads.read(
            "",
            "",
            HttpResponse(Status.ACCEPTED, "", headers = Map(HeaderNames.LOCATION -> Seq("/foo")))
          )

          actualResult mustBe expectedResult
        }
      }

      "the location header is NOT returned" must {

        "return a ValidClaimPeriodModel with the value false" in {

          val expectedResult = Left(NoLocationHeaderReturned)
          val actualResult   = AddressLookupInitializationReads.read("", "", HttpResponse(Status.ACCEPTED, ""))

          actualResult mustBe expectedResult
        }
      }
    }

    "given any other status" should {

      "return a Left(DefaultedUnexpectedFailure)" in {

        val expectedResult = Left(DefaultedUnexpectedFailure(status = Status.INTERNAL_SERVER_ERROR))
        val actualResult   = AddressLookupInitializationReads.read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        actualResult mustBe expectedResult
      }
    }
  }
}
