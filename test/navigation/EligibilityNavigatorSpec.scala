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
import controllers.checkEligibility.{routes => elroutes}
import controllers.routes
import models._
import pages.checkEligibility._
import pages.{IndexPage, QuestionPage}

class EligibilityNavigatorSpec extends SpecBase{

  val navigator = new EligibilityNavigator()

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the IsEligiblePurposePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsEligiblePurposePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IsEligibleAccountController page when yes is selected" in {
          navigator.nextPage(IsEligiblePurposePage, NormalMode, userAnsewers(IsEligiblePurposePage, true)) mustBe
            elroutes.IsEligibleAccountController.onPageLoad()
        }

        "go to the InEligibleController page when No is selected" in {
          navigator.nextPage(IsEligiblePurposePage, NormalMode, userAnsewers(IsEligiblePurposePage, false)) mustBe
            elroutes.InEligibleController.onPageLoad()
        }
      }

      "from the IsEligibleAccountPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsEligibleAccountPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IsEligibleLocationController page when yes is selected" in {
          navigator.nextPage(IsEligibleAccountPage, NormalMode, userAnsewers(IsEligibleAccountPage, true)) mustBe
            elroutes.IsEligibleLocationController.onPageLoad()
        }

        "go to the InEligibleController page when No is selected" in {
          navigator.nextPage(IsEligibleAccountPage, NormalMode, userAnsewers(IsEligibleAccountPage, false)) mustBe
            elroutes.InEligibleController.onPageLoad()
        }
      }

      "from the IsEligibleLocationPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IsEligibleLocationPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IndexController page when yes is selected" in {
          navigator.nextPage(IsEligibleLocationPage, NormalMode, userAnsewers(IsEligibleLocationPage, true)) mustBe
            routes.IndexController.onPageLoad()
        }

        "go to the IsEligibleLocationOtherController page when No is selected" in {
          navigator.nextPage(IsEligibleLocationPage, NormalMode, userAnsewers(IsEligibleLocationPage, false)) mustBe
            elroutes.IsEligibleLocationOtherController.onPageLoad()
        }
      }

      "from the IsEligibleLocationOtherPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsEligibleLocationOtherPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IndexController page when yes is selected" in {
          navigator.nextPage(IsEligibleLocationOtherPage, NormalMode, userAnsewers(IsEligibleLocationOtherPage, true)) mustBe
            routes.IndexController.onPageLoad()
        }

        "go to the InEligibleController page when No is selected" in {
          navigator.nextPage(IsEligibleLocationOtherPage, NormalMode, userAnsewers(IsEligibleLocationOtherPage, false)) mustBe
            elroutes.InEligibleController.onPageLoad()
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
  
  def userAnsewers(page: QuestionPage[Boolean], value : Boolean): UserAnswers ={
    emptyUserAnswers.set(page, value).getOrElse(emptyUserAnswers)
  }
}
