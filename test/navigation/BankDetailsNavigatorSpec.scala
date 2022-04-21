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

package navigation

import base.SpecBase
import controllers.routes
import models._
import pages.IndexPage
import pages.operationsAndFunds.{BankDetailsPage, BankDetailsSummaryPage}

class BankDetailsNavigatorSpec extends SpecBase {

  private val navigator: BankDetailsNavigator = inject[BankDetailsNavigator]

  private val bankDetails = BankDetails(
    accountName = "fullName",
    sortCode = "123456",
    accountNumber = "12345678",
    rollNumber = Some("operatingName")
  )

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the BankDetails page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(BankDetailsPage, NormalMode, emptyUserAnswers) mustBe
             routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(BankDetailsPage, NormalMode,
            emptyUserAnswers.set(BankDetailsPage, bankDetails).getOrElse(emptyUserAnswers)) mustBe
            controllers.operationsAndFunds.routes.BankDetailsSummaryController.onPageLoad()
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(BankDetailsSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Check mode" when {

      "from the BankDetails page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(BankDetailsPage, CheckMode, emptyUserAnswers) mustBe
             routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(BankDetailsPage, CheckMode,
            emptyUserAnswers.set(BankDetailsPage, bankDetails).getOrElse(emptyUserAnswers)) mustBe
            controllers.operationsAndFunds.routes.BankDetailsSummaryController.onPageLoad()
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Playback mode" when {
      "attempting to go to any site" must {
        "go to the PageNotFoundController page" in {
          navigator.nextPage(BankDetailsPage, PlaybackMode, emptyUserAnswers) mustBe
             routes.PageNotFoundController.onPageLoad()
        }
      }
    }
  }

}
