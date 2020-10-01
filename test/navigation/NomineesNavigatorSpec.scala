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
import controllers.nominees.{routes => nomineesRoutes}
import controllers.routes
import models._
import pages.IndexPage
import pages.nominees._

class NomineesNavigatorSpec extends SpecBase {

  private val navigator: NomineesNavigator = inject[NomineesNavigator]
  private val nomineeName: Name = Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")
  private val minYear = 16

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the IsAuthoriseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the NomineeDetailsSummary page when No selected and clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, false).success.value) mustBe
            nomineesRoutes.NomineeDetailsSummaryController.onPageLoad()
        }

        "go to Is the nominee an individual or an organisation? page when yes selected clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, true).success.value) mustBe
            nomineesRoutes.ChooseNomineeController.onPageLoad(NormalMode)
        }
      }

      "from the ChooseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(ChooseNomineePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Individual Nominee Name page when selected person and clicked continue button" in {
          navigator.nextPage(ChooseNomineePage, NormalMode,
            emptyUserAnswers.set(ChooseNomineePage, true).success.value) mustBe
            nomineesRoutes.IndividualNomineeNameController.onPageLoad(NormalMode)
        }

        "go to What is the name of Organisation page when selected Organisation and clicked continue button" in {
          navigator.nextPage(ChooseNomineePage, NormalMode,
            emptyUserAnswers.set(ChooseNomineePage, false).success.value) mustBe
            nomineesRoutes.WhatIsTheNameOfTheOrganisationController.onPageLoad(NormalMode)
        }

      }

      "from the IndividualNomineeNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineeNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to What is the nominee's date of birth page when clicked continue button" in {
          navigator.nextPage(IndividualNomineeNamePage, NormalMode,
            emptyUserAnswers.set(IndividualNomineeNamePage, nomineeName).success.value) mustBe
            nomineesRoutes.IndividualNomineeDOBController.onPageLoad(NormalMode)

        }
      }

      "from the IndividualNomineeDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineeDOBPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to What is the nominee's phone number page when clicked continue button" in {
          navigator.nextPage(IndividualNomineeDOBPage, NormalMode,
            emptyUserAnswers.set(IndividualNomineeDOBPage, LocalDate.now().minusYears(minYear)).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready

        }
      }

      "from the WhatIsTheNameOfOrganisation page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(WhatIsTheNameOfOrganisationPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to What is the name of Organisation page when clicked continue button" in {
          navigator.nextPage(WhatIsTheNameOfOrganisationPage, NormalMode,
            emptyUserAnswers.set(WhatIsTheNameOfOrganisationPage, "abc").success.value) mustBe
           routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the NomineeDetailsSummaryPage" must {

        "go to the TaskList page when clicked continue button" in {
          navigator.nextPage(NomineeDetailsSummaryPage, NormalMode, emptyUserAnswers) mustBe
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

        "go to the NomineeDetailsSummary page when No selected and clicked continue button" in {
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

      "from the ChooseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(ChooseNomineePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(ChooseNomineePage, CheckMode,
            emptyUserAnswers.set(ChooseNomineePage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IndividualNomineeNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineeNamePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(IndividualNomineeNamePage, CheckMode,
            emptyUserAnswers.set(IndividualNomineeNamePage, nomineeName).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IndividualNomineeDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineeDOBPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to What is the nominee's phone number page when clicked continue button" in {
          navigator.nextPage(IndividualNomineeDOBPage, CheckMode,
            emptyUserAnswers.set(IndividualNomineeDOBPage,LocalDate.now().minusYears(minYear)).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready

        }
      }

      "from the WhatIsTheNameOfOrganisation page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(WhatIsTheNameOfOrganisationPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(WhatIsTheNameOfOrganisationPage, CheckMode,
            emptyUserAnswers.set(WhatIsTheNameOfOrganisationPage, "abc").success.value) mustBe
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

    "in Playback mode" when {
      "attempting to go to any site" must {
        "go to the SessionExpiredController page" in {
          navigator.nextPage(ChooseNomineePage, PlaybackMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }

  }

}
