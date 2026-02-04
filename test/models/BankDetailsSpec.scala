/*
 * Copyright 2026 HM Revenue & Customs
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

package models

import base.SpecBase
import play.api.libs.json.Json

class BankDetailsSpec extends SpecBase {

  "A Bank details object" should {

    "serialize correctly new format" when {

      val bankDetails = BankDetails(
        accountName = accountName,
        sortCode = sortCodeWithHyphens,
        accountNumber = accountNumberWithSpaces,
        rollNumber = Some(rollNumber)
      )
      val result      = Json.toJson(bankDetails)(BankDetails.writes)

      "we have a accountName" in {
        (result \ "accountName").as[String] mustBe accountName
      }

      "we have sortCode" in {
        (result \ "sortCode").as[String] mustBe sortCode
      }

      "we have accountNumber" in {
        (result \ "accountNumber").as[String] mustBe accountNumber
      }

      "we have rollNumber" in {
        (result \ "rollNumber").as[String] mustBe rollNumber
      }
    }

    "deserialize correctly new format" when {

      val json   = Json.obj(
        "accountName"   -> accountName,
        "sortCode"      -> sortCodeWithHyphens,
        "accountNumber" -> accountNumberWithSpaces,
        "rollNumber"    -> rollNumber
      )
      val result = json.as[BankDetails]

      "we have a accountName" in {
        result.accountName mustBe accountName
      }

      "we have sortCode" in {
        result.sortCode mustBe sortCodeWithHyphens
      }

      "we have accountNumber" in {
        result.accountNumber mustBe accountNumberWithSpaces
      }

      "we have rollNumber" in {
        result.rollNumber.value mustBe rollNumber
      }
    }

  }

  "toString" in {
    BankDetails.toString mustBe "bankDetail"
  }
}
