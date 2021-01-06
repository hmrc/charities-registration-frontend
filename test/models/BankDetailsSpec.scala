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

package models

import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.Json

class BankDetailsSpec extends WordSpec with MustMatchers with OptionValues {

  "A Bank details object" should {

    "serialize correctly new format" when {

      val bankDetails = BankDetails(
        accountName = "fullName",
        sortCode = "12-34-56",
        accountNumber = "12 34 56 78",
        rollNumber = Some("operatingName")
      )
      val result = Json.toJson(bankDetails)(BankDetails.writes)

      "we have a accountName" in {
        (result \ "accountName").as[String] mustBe "fullName"
      }

      "we have sortCode" in {
        (result \ "sortCode").as[String] mustBe "123456"
      }

      "we have accountNumber" in {
        (result \ "accountNumber").as[String] mustBe "12345678"
      }

      "we have rollNumber" in {
        (result \ "rollNumber").as[String] mustBe "operatingName"
      }
    }
  }

  "toString" in {
    BankDetails.toString mustBe "bankDetail"
  }
}
