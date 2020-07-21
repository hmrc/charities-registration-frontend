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
import controllers.nominees.{routes => nomineesRoutes}
import models._
import pages.IndexPage
import pages.nominees.{IsAuthoriseNomineePage, NomineeDetailsSummaryPage}

class NomineesNavigatorSpec extends SpecBase {

  private val navigator: NomineesNavigator = inject[NomineesNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the IsAuthoriseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the NomineeDetailsSummar page when No selected and clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, false).success.value) mustBe
            nomineesRoutes.NomineeDetailsSummaryController.onPageLoad()
        }

        "go to the DeadEnd page when clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the NomineeDetailsSummaryPage" must {

        "go to the TaskList page when clicked continue button" in {
          navigator.nextPage(NomineeDetailsSummaryPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
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

      "from the IsAuthoriseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAuthoriseNomineePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the NomineeDetailsSummar page when No selected and clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, CheckMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, false).success.value) mustBe
            nomineesRoutes.NomineeDetailsSummaryController.onPageLoad()
        }

        "go to the DeadEnd page when clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, CheckMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the NomineeDetailsSummaryPage" must {

        "go to the TaskList page when clicked continue button" in {
          navigator.nextPage(NomineeDetailsSummaryPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

  }

}
