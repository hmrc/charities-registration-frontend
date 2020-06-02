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
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import controllers.routes
import models._
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import pages.regulatorsAndDocuments._
import pages.{IndexPage, QuestionPage}

class RegulatorsAndDocumentsNavigatorSpec extends SpecBase {

  val navigator = new RegulatorsAndDocumentsNavigator()

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the IsCharityRegulatorPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsCharityRegulatorPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the EnterCharityRegulator page when yes is selected" in {
          navigator.nextPage(IsCharityRegulatorPage, NormalMode, userAnswers(IsCharityRegulatorPage, true)) mustBe
            regulatorDocsRoutes.CharityRegulatorController.onPageLoad(NormalMode)
        }

        "go to the SelectWhyNoRegulator page when No is selected" in {
          navigator.nextPage(IsCharityRegulatorPage, NormalMode, userAnswers(IsCharityRegulatorPage, false)) mustBe
            regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(NormalMode)
        }
      }

      "from the CharityRegulatorPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityRegulatorPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the EnterCharityRegistrationPage page when click Continue button" in {
          navigator.nextPage(CharityRegulatorPage, NormalMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set(CharityRegulator.values.head)).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO modify once next page created
        }
      }

      "from the CharityCommissionRegistrationNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityCommissionRegistrationNumberPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IndexController page when clicked continue button" in {
          navigator.nextPage(CharityCommissionRegistrationNumberPage, NormalMode,
            emptyUserAnswers.set(CharityCommissionRegistrationNumberPage, CharityCommissionRegistrationNumber("registrationNumber")).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO modify once next page created
        }
      }

      "from the ScottishRegulatorRegNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(ScottishRegulatorRegNumberPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IndexController page when clicked continue button" in {
          navigator.nextPage(ScottishRegulatorRegNumberPage, NormalMode,
            emptyUserAnswers.set(ScottishRegulatorRegNumberPage, ScottishRegulatorRegNumber("registrationNumber")).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO modify once next page created
        }
      }

      "from the NIRegulatorRegNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(NIRegulatorRegNumberPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IndexController page when clicked continue button" in {
          navigator.nextPage(NIRegulatorRegNumberPage, NormalMode,
            emptyUserAnswers.set(NIRegulatorRegNumberPage, NIRegulatorRegNumber("nIRegistrationNumber")).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "from the CharityOtherRegulatorDetailsPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityOtherRegulatorDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IndexController page when clicked continue button" in {
          navigator.nextPage(CharityOtherRegulatorDetailsPage, NormalMode,
            emptyUserAnswers.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "from the SelectWhyNoRegulator" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(SelectWhyNoRegulatorPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the CharityNotRegisteredReason page when Other is selected" in {

          navigator.nextPage(SelectWhyNoRegulatorPage, NormalMode,
            emptyUserAnswers.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO modify once next page created
        }

        "go to the Summary page when other then Other is selected" in {
          navigator.nextPage(SelectWhyNoRegulatorPage, NormalMode,
            emptyUserAnswers.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO modify once next page created
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

    def userAnswers(page: QuestionPage[Boolean], value: Boolean): UserAnswers = {
      emptyUserAnswers.set(page, value).getOrElse(emptyUserAnswers)
    }
  }
}
