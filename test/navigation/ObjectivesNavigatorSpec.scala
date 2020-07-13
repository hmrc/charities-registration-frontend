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
import models.operations.CharitablePurposes
import models.operations.CharitablePurposes.{AmateurSport, Other}
import pages.IndexPage
import pages.operationsAndFunds.{CharitableObjectivesPage, CharitablePurposesPage, CharityObjectivesSummaryPage, PublicBenefitsPage}
import controllers.operationsAndFunds.{routes => operations}

class ObjectivesNavigatorSpec extends SpecBase {

  private val navigator: ObjectivesNavigator = inject[ObjectivesNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the CharitableObjectivesPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharitableObjectivesPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the CharitablePurposes page when clicked button" in {
          navigator.nextPage(CharitableObjectivesPage, NormalMode,
            emptyUserAnswers.set(CharitableObjectivesPage, "abcd").getOrElse(emptyUserAnswers)) mustBe
            operations.CharitablePurposesController.onPageLoad(NormalMode)
        }
      }

      "from the CharitablePurposesPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharitablePurposesPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to How does your charity benefit the public? page when selected any option other than the other and clicked continue" in {
          navigator.nextPage(CharitablePurposesPage, NormalMode,
            emptyUserAnswers.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport)).success.value) mustBe
            operations.PublicBenefitsController.onPageLoad(NormalMode)
        }

        "go to How does your charity benefit the public? page when selected other option and clicked continue" in {
          navigator.nextPage(CharitablePurposesPage, NormalMode,
            emptyUserAnswers.set(CharitablePurposesPage, Set[CharitablePurposes](Other)).success.value) mustBe
            routes.DeadEndController.onPageLoad()
        }
      }

      "from the PublicBenefits page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(PublicBenefitsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(PublicBenefitsPage, NormalMode,
            emptyUserAnswers.set(PublicBenefitsPage, "FreeEducation").getOrElse(emptyUserAnswers)) mustBe
            operations.CharityObjectivesSummaryController.onPageLoad()
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(CharityObjectivesSummaryPage, NormalMode, emptyUserAnswers) mustBe
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

      "from the CharitableObjectivesPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharitableObjectivesPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the CharitablePurposes page when clicked button" in {
          navigator.nextPage(CharitableObjectivesPage, CheckMode,
            emptyUserAnswers.set(CharitableObjectivesPage, "abcd").getOrElse(emptyUserAnswers)) mustBe
            operations.CharityObjectivesSummaryController.onPageLoad()        }
      }

      "from the CharitablePurposesPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharitablePurposesPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to How does your charity benefit the public? page when selected any option and clicked continue" in {
          navigator.nextPage(CharitablePurposesPage, CheckMode,
            emptyUserAnswers.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport)).success.value) mustBe
            operations.CharityObjectivesSummaryController.onPageLoad()        }
      }

      "from the PublicBenefits page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(PublicBenefitsPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(PublicBenefitsPage, CheckMode,
            emptyUserAnswers.set(PublicBenefitsPage, "FreeEducation").getOrElse(emptyUserAnswers)) mustBe
            operations.CharityObjectivesSummaryController.onPageLoad()
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
