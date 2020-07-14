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

import java.time.LocalDate

import base.SpecBase
import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import controllers.routes
import models._
import models.AuthorisedOfficialsPhoneNumber
import pages.IndexPage
import pages.authorisedOfficials.{AuthorisedOfficialsDOBPage, AuthorisedOfficialsNamePage, AuthorisedOfficialsPhoneNumberPage}

class AuthorisedOfficialsNavigatorSpec extends SpecBase {

  private val navigator: AuthorisedOfficialsNavigator = inject[AuthorisedOfficialsNavigator]

  private val authorisedOfficialsName: AuthorisedOfficialsName = AuthorisedOfficialsName("FName", Some("MName"), "lName")
  private val authorisedOfficialsPhoneNumber: AuthorisedOfficialsPhoneNumber = AuthorisedOfficialsPhoneNumber("07700 900 982", Some("07700 900 982"))

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the AuthorisedOfficialsNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsNamePage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]'s date of birth? when clicked continue button" in {
          navigator.nextPage(AuthorisedOfficialsNamePage(0), NormalMode,
            emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName).getOrElse(emptyUserAnswers)) mustBe
            authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(NormalMode, 0)
        }
      }

      "from the AuthorisedOfficialsDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsDOBPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [full name]'s phone number? page when clicked continue button" in {
          val minYear = 16
          navigator.nextPage(AuthorisedOfficialsDOBPage(0), NormalMode,
            emptyUserAnswers.set(AuthorisedOfficialsDOBPage(0), LocalDate.now().minusYears(minYear)).getOrElse(emptyUserAnswers)) mustBe
            authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(NormalMode, 0)
        }
      }

      "from the AuthorisedOfficialsPhoneNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [full name]'s position in charity? page when clicked continue button" in {
          navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(0), NormalMode,
            emptyUserAnswers.set(AuthorisedOfficialsPhoneNumberPage(0), authorisedOfficialsPhoneNumber).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
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

      "from the AuthorisedOfficialsNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsNamePage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(AuthorisedOfficialsNamePage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the AuthorisedOfficialsDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsDOBPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          val minYear = 16
          navigator.nextPage(AuthorisedOfficialsDOBPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsDOBPage(0), LocalDate.now().minusYears(minYear)).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the AuthorisedOfficialsPhoneNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsPhoneNumberPage(0), authorisedOfficialsPhoneNumber).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
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
