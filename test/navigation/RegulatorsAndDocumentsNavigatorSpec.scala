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
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import controllers.routes
import models._
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Other, Scottish}
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import pages.regulatorsAndDocuments._
import pages.{IndexPage, IsSwitchOverUserPage, QuestionPage}

class RegulatorsAndDocumentsNavigatorSpec extends SpecBase {

  private val navigator: RegulatorsAndDocumentsNavigator = inject[RegulatorsAndDocumentsNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the IsCharityRegulatorPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(IsCharityRegulatorPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the EnterCharityRegulator page when yes is selected" in {
          navigator.nextPage(
            IsCharityRegulatorPage,
            NormalMode,
            emptyUserAnswers.set(IsCharityRegulatorPage, true).success.value
          ) mustBe
            regulatorDocsRoutes.CharityRegulatorController.onPageLoad(NormalMode)
        }

        "go to the SelectWhyNoRegulator page when No is selected" in {
          navigator.nextPage(
            IsCharityRegulatorPage,
            NormalMode,
            emptyUserAnswers.set(IsCharityRegulatorPage, false).success.value
          ) mustBe
            regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(NormalMode)
        }
      }

      "from the CharityRegulatorPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityRegulatorPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityCommissionRegistrationNumberPage when user answer has EnglandWales selected and click Continue button" in {
          navigator.nextPage(
            CharityRegulatorPage,
            NormalMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)).success.value
          ) mustBe
            regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(NormalMode)
        }

        "go to the ScottishRegulatorRegNumberPage when user answer has Scottish selected and click Continue button" in {
          navigator.nextPage(
            CharityRegulatorPage,
            NormalMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)).success.value
          ) mustBe
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the NIRegulatorRegNumberPage when user answer has NorthernIreland selected and click Continue button" in {
          navigator.nextPage(
            CharityRegulatorPage,
            NormalMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland)).success.value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the CharityOtherRegulatorDetailsPage when user answer has Other selected and click Continue button" in {
          navigator.nextPage(
            CharityRegulatorPage,
            NormalMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](Other)).success.value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }

        "go to the CharityCommissionRegistrationNumberPage page when user answer has all options selected and its switch user journey" in {
          navigator.nextPage(
            CharityRegulatorPage,
            NormalMode,
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, CharityRegulator.values.toSet))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(NormalMode)
        }

        "go to the ScottishRegulatorRegNumberPage page when user answer has Scottish and Other selected and its switch user journey" in {
          navigator.nextPage(
            CharityRegulatorPage,
            NormalMode,
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(
                _.set(
                  CharityOtherRegulatorDetailsPage,
                  CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
                )
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, Other)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the NIRegulatorRegNumberPage page when user answer has NorthernIreland and Other selected and its switch user journey" in {
          navigator.nextPage(
            CharityRegulatorPage,
            NormalMode,
            emptyUserAnswers
              .set(NIRegulatorRegNumberPage, "registrationNumber")
              .flatMap(
                _.set(
                  CharityOtherRegulatorDetailsPage,
                  CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
                )
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland, Other)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer hasOther selected and its switch user journey" in {
          navigator.nextPage(
            CharityRegulatorPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityOtherRegulatorDetailsPage,
                CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the CharityCommissionRegistrationNumberPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityCommissionRegistrationNumberPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(
            CharityCommissionRegistrationNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the ScottishRegulatorRegNumberPage page when user answer has Scottish selected and click Continue button" in {
          navigator.nextPage(
            CharityCommissionRegistrationNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the NIRegulatorRegNumberPage page when user answer has Scottish selected and click Continue button" in {
          navigator.nextPage(
            CharityCommissionRegistrationNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, NorthernIreland)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          navigator.nextPage(
            CharityCommissionRegistrationNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Other)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }

        "go to the ScottishRegulatorRegNumberPage page when user answer has EnglandWales, Scottish and Other selected and its switch user journey" in {
          navigator.nextPage(
            CharityCommissionRegistrationNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityCommissionRegistrationNumberPage, "registrationNumber"))
              .flatMap(
                _.set(
                  CharityOtherRegulatorDetailsPage,
                  CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
                )
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, Other)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the RegulatorsSummaryController when user answer has EnglandWales and its switch user journey" in {
          navigator.nextPage(
            CharityCommissionRegistrationNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }
      }

      "from the ScottishRegulatorRegNumberPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(ScottishRegulatorRegNumberPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(
            ScottishRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the NIRegulatorRegNumberPage page when user answer has NorthernIreland selected and click Continue button" in {
          navigator.nextPage(
            ScottishRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, NorthernIreland)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          navigator.nextPage(
            ScottishRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, Other)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          navigator.nextPage(
            ScottishRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers.set(ScottishRegulatorRegNumberPage, "registrationNumber").success.value
          ) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has EnglandWales, Scottish and Other selected and its switch user journey" in {
          navigator.nextPage(
            ScottishRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityCommissionRegistrationNumberPage, "registrationNumber"))
              .flatMap(
                _.set(
                  CharityOtherRegulatorDetailsPage,
                  CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
                )
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, Other)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the NIRegulatorRegNumberPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(NIRegulatorRegNumberPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(
            NIRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(NIRegulatorRegNumberPage, "nIRegistrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          navigator.nextPage(
            NIRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(NIRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland, Other)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          navigator.nextPage(
            NIRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers.set(NIRegulatorRegNumberPage, "registrationNumber").success.value
          ) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has EnglandWales, Scottish and Other selected and its switch user journey" in {
          navigator.nextPage(
            NIRegulatorRegNumberPage,
            NormalMode,
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityCommissionRegistrationNumberPage, "registrationNumber"))
              .flatMap(
                _.set(
                  CharityOtherRegulatorDetailsPage,
                  CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
                )
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, Other)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the CharityOtherRegulatorDetailsPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityOtherRegulatorDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(
            CharityOtherRegulatorDetailsPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityOtherRegulatorDetailsPage,
                CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          navigator.nextPage(
            CharityOtherRegulatorDetailsPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityOtherRegulatorDetailsPage,
                CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
              )
              .success
              .value
          ) mustBe
            routes.PageNotFoundController.onPageLoad()
        }
      }

      "from the SelectWhyNoRegulator" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(SelectWhyNoRegulatorPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityNotRegisteredReason page when Other is selected" in {

          navigator.nextPage(
            SelectWhyNoRegulatorPage,
            NormalMode,
            emptyUserAnswers.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other).getOrElse(emptyUserAnswers)
          ) mustBe
            regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(NormalMode)
        }

        "go to the Summary page when other than Other is selected" in {
          navigator.nextPage(
            SelectWhyNoRegulatorPage,
            NormalMode,
            emptyUserAnswers
              .set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)
              .getOrElse(emptyUserAnswers)
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(RegulatorsSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }

      "from the WhyNotRegisteredWithCharityPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(WhyNotRegisteredWithCharityPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          navigator.nextPage(
            WhyNotRegisteredWithCharityPage,
            NormalMode,
            emptyUserAnswers.set(WhyNotRegisteredWithCharityPage, "reason").getOrElse(emptyUserAnswers)
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
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

      "from the IsCharityRegulatorPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(IsCharityRegulatorPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when yes is selected and an answer exists for the CharityRegulator page" in {

          navigator.nextPage(
            IsCharityRegulatorPage,
            CheckMode,
            emptyUserAnswers
              .set(IsCharityRegulatorPage, true)
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the Summary page when no is selected and an answer exists for the SelectWhyNoRegulator page" in {

          navigator.nextPage(
            IsCharityRegulatorPage,
            CheckMode,
            emptyUserAnswers
              .set(IsCharityRegulatorPage, false)
              .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the EnterCharityRegulator page when yes is selected no data exists for the CharityRegulator page" in {
          navigator.nextPage(
            IsCharityRegulatorPage,
            CheckMode,
            emptyUserAnswers.set(IsCharityRegulatorPage, true).success.value
          ) mustBe
            regulatorDocsRoutes.CharityRegulatorController.onPageLoad(CheckMode)
        }

        "go to the SelectWhyNoRegulator page when No is selected and no data exists for the SelectWhyNoRegulator page" in {
          navigator.nextPage(
            IsCharityRegulatorPage,
            CheckMode,
            emptyUserAnswers.set(IsCharityRegulatorPage, false).success.value
          ) mustBe
            regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(CheckMode)
        }
      }

      "from the CharityRegulatorPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityRegulatorPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityCommissionRegistrationNumberPage when user answer has EnglandWales selected and click Continue button" in {
          navigator.nextPage(
            CharityRegulatorPage,
            CheckMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)).success.value
          ) mustBe
            regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(CheckMode)
        }

        "go to the ScottishRegulatorRegNumberPage when user answer has Scottish selected and click Continue button" in {
          navigator.nextPage(
            CharityRegulatorPage,
            CheckMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)).success.value
          ) mustBe
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(CheckMode)
        }

        "go to the NIRegulatorRegNumberPage when user answer has NorthernIreland selected and click Continue button" in {
          navigator.nextPage(
            CharityRegulatorPage,
            CheckMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland)).success.value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode)
        }

        "go to the CharityOtherRegulatorDetailsPage when user answer has Other selected and click Continue button" in {
          navigator.nextPage(
            CharityRegulatorPage,
            CheckMode,
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](Other)).success.value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode)
        }

        "from the CharityCommissionRegistrationNumberPage" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(CharityCommissionRegistrationNumberPage, CheckMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the Summary page when clicked continue button" in {
            navigator.nextPage(
              CharityCommissionRegistrationNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
          }

          "go to the ScottishRegulatorRegNumberPage page when user answer has Scottish selected and click Continue button" in {
            navigator.nextPage(
              CharityCommissionRegistrationNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(CheckMode)
          }

          "go to the NIRegulatorRegNumberPage page when user answer has Scottish selected and click Continue button" in {
            navigator.nextPage(
              CharityCommissionRegistrationNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, NorthernIreland)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode)
          }

          "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
            navigator.nextPage(
              CharityCommissionRegistrationNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Other)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode)
          }
        }

        "from the ScottishRegulatorRegNumberPage" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(ScottishRegulatorRegNumberPage, CheckMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the Summary page when clicked continue button" in {
            navigator.nextPage(
              ScottishRegulatorRegNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(ScottishRegulatorRegNumberPage, "registrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
          }

          "go to the NIRegulatorRegNumberPage page when user answer has NorthernIreland selected and click Continue button" in {
            navigator.nextPage(
              ScottishRegulatorRegNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(ScottishRegulatorRegNumberPage, "registrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, NorthernIreland)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode)
          }

          "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
            navigator.nextPage(
              ScottishRegulatorRegNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(ScottishRegulatorRegNumberPage, "registrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, Other)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode)
          }

          "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
            navigator.nextPage(
              ScottishRegulatorRegNumberPage,
              CheckMode,
              emptyUserAnswers.set(ScottishRegulatorRegNumberPage, "registrationNumber").success.value
            ) mustBe
              routes.PageNotFoundController.onPageLoad()
          }
        }

        "from the NIRegulatorRegNumberPage" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(NIRegulatorRegNumberPage, CheckMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the Summary page when clicked continue button" in {
            navigator.nextPage(
              NIRegulatorRegNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(NIRegulatorRegNumberPage, "nIRegistrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
          }

          "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
            navigator.nextPage(
              NIRegulatorRegNumberPage,
              CheckMode,
              emptyUserAnswers
                .set(NIRegulatorRegNumberPage, "registrationNumber")
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland, Other)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode)
          }

          "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
            navigator.nextPage(
              NIRegulatorRegNumberPage,
              CheckMode,
              emptyUserAnswers.set(NIRegulatorRegNumberPage, "registrationNumber").success.value
            ) mustBe
              routes.PageNotFoundController.onPageLoad()
          }
        }

        "from the CharityOtherRegulatorDetailsPage" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(CharityOtherRegulatorDetailsPage, CheckMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the Summary page when clicked continue button" in {
            navigator.nextPage(
              CharityOtherRegulatorDetailsPage,
              CheckMode,
              emptyUserAnswers
                .set(
                  CharityOtherRegulatorDetailsPage,
                  CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
                )
                .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
                .success
                .value
            ) mustBe
              regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
          }

          "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
            navigator.nextPage(
              CharityOtherRegulatorDetailsPage,
              CheckMode,
              emptyUserAnswers
                .set(
                  CharityOtherRegulatorDetailsPage,
                  CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
                )
                .success
                .value
            ) mustBe
              routes.PageNotFoundController.onPageLoad()
          }
        }

        "from the SelectWhyNoRegulator" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(SelectWhyNoRegulatorPage, CheckMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the CharityNotRegisteredReason page when Other is selected" in {

            navigator.nextPage(
              SelectWhyNoRegulatorPage,
              CheckMode,
              emptyUserAnswers.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other).getOrElse(emptyUserAnswers)
            ) mustBe
              regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(CheckMode)
          }

          "go to the Summary page when other than Other is selected" in {
            navigator.nextPage(
              SelectWhyNoRegulatorPage,
              CheckMode,
              emptyUserAnswers
                .set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)
                .getOrElse(emptyUserAnswers)
            ) mustBe
              regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
          }
        }

        "from the Summary page" must {

          "go to the Task List page when click continue button" in {
            navigator.nextPage(RegulatorsSummaryPage, CheckMode, emptyUserAnswers) mustBe
              routes.IndexController.onPageLoad(None)
          }
        }

        "from any UnKnownPage" must {

          "go to the IndexController page when user answer is empty" in {
            navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
              routes.IndexController.onPageLoad(None)
          }
        }
        "from SelectWhyNoRegulator " must {

          "go to the RegulatorsSummaryController page when Other is not selected" in {

            navigator.nextPage(
              SelectWhyNoRegulatorPage,
              CheckMode,
              emptyUserAnswers
                .set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)
                .getOrElse(emptyUserAnswers)
            ) mustBe
              regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
          }

          "go to the WhyNotRegisteredWithCharityController page when Other is selected" in {

            navigator.nextPage(
              SelectWhyNoRegulatorPage,
              CheckMode,
              emptyUserAnswers.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other).getOrElse(emptyUserAnswers)
            ) mustBe
              regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(CheckMode)
          }

          "go to the  RegulatorsSummaryController page when Other was selected and we did not change the answer and hit continue" in {
            navigator.nextPage(
              SelectWhyNoRegulatorPage,
              CheckMode,
              emptyUserAnswers
                .set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other)
                .flatMap(_.set(WhyNotRegisteredWithCharityPage, "abcd"))
                .getOrElse(emptyUserAnswers)
            ) mustBe
              regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
          }
        }

        "from WhyNotRegisteredWithCharityRegulator " must {

          "go to the RegulatorsSummaryController page When answer is provided" in {

            navigator.nextPage(
              WhyNotRegisteredWithCharityPage,
              CheckMode,
              emptyUserAnswers.set(WhyNotRegisteredWithCharityPage, "abcd").getOrElse(emptyUserAnswers)
            ) mustBe
              regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
          }

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(WhyNotRegisteredWithCharityPage, CheckMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }
        }
      }
    }
    "in Playback mode" when {
      "attempting to go to any site" must {
        "go to the PageNotFoundController page" in {
          navigator.nextPage(WhyNotRegisteredWithCharityPage, PlaybackMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }
      }
    }

  }
}
