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

package utils

import base.SpecBase

class CurrencyFormatterSpec extends SpecBase with CurrencyFormatter {

  ".currencyFormat(amt: BigDecimal)" must {

    "correctly format an amount" which {

      "is 1.12" in {
        currencyFormat(1.12) mustBe "&pound;1.12"
      }

      "is 1.1" in {
        currencyFormat(1.1) mustBe "&pound;1.10"
      }

      "is 1.01" in {
        currencyFormat(1.01) mustBe "&pound;1.01"
      }

      "is 0.12" in {
        currencyFormat(0.01) mustBe "&pound;0.01"
      }

      "is 10.00" in {
        currencyFormat(10.00) mustBe "&pound;10"
      }

      "is 1000.12" in {
        currencyFormat(1000.12) mustBe "&pound;1,000.12"
      }

      "is 1000000" in {
        currencyFormat(1000000) mustBe "&pound;1,000,000"
      }

      "is 10000000000.99" in {
        currencyFormat(10000000000.99) mustBe "&pound;10,000,000,000.99"
      }
    }
  }
}
