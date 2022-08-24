/*
 * Copyright 2022 HM Revenue & Customs
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

package viewModels.operationsAndFunds

import base.SpecBase
import models.BankDetails
import pages.operationsAndFunds.BankDetailsPage
import viewmodels.operationsAndFunds.BankDetailsStatusHelper

class BankDetailsStatusHelperSpec extends SpecBase {
  "BankDetailsStatusHelper" must {
    "give the correct completion status" when {

      "no data is provided" in {
        val result = BankDetailsStatusHelper.checkComplete(emptyUserAnswers)

        result mustBe false
      }

      "all data is provided" in {
        val result = BankDetailsStatusHelper.checkComplete(
          emptyUserAnswers
            .set(
              BankDetailsPage,
              BankDetails(
                accountName = "PM Cares",
                sortCode = "abc",
                accountNumber = "43444546",
                rollNumber = Some("765431234")
              )
            )
            .success
            .value
        )

        result mustBe true
      }

    }
  }
}
