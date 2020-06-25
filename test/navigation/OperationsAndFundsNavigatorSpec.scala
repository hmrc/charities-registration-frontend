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

package navigation

import base.SpecBase
import controllers.routes
import models._
import pages.IndexPage
import pages.operationsAndFunds.PublicBenefitsPage

class OperationsAndFundsNavigatorSpec extends SpecBase {

  val navigator = new OperationsAndFundsNavigator()

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the PublicBenefits page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(PublicBenefitsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(PublicBenefitsPage, NormalMode,
            emptyUserAnswers.set(PublicBenefitsPage, "FreeEducation").getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO modify once summary page created
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

    "in Check mode" when {

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

  }

}
