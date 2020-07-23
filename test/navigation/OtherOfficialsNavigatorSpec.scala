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
import models.{CheckMode, Name, NormalMode}
import pages.IndexPage
import pages.authorisedOfficials._
import pages.otherOfficials.OtherOfficialsNamePage

class OtherOfficialsNavigatorSpec extends SpecBase {

  private val navigator: OtherOfficialsNavigator = inject[OtherOfficialsNavigator]

  private val otherOfficialsName: Name = Name("FName", Some("MName"), "lName")

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the OtherOfficialsNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsNamePage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]'s date of birth? when save and continue button clicked" in {
          navigator.nextPage(OtherOfficialsNamePage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsNamePage(0), otherOfficialsName).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // Todo Other Officials DOB page is ready
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

      "from the OtherOfficialsNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsNamePage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the dead-end when save and continue button is clicked" in {
          navigator.nextPage(OtherOfficialsNamePage(0), CheckMode,
            emptyUserAnswers.set(OtherOfficialsNamePage(0), otherOfficialsName).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when summary page is ready
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
