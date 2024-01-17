/*
 * Copyright 2024 HM Revenue & Customs
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
import pages.{IndexPage, IsSwitchOverUserPage}

class RegulatorsAndDocumentsNavigatorSpec extends SpecBase {

  private val navigator: RegulatorsAndDocumentsNavigator = inject[RegulatorsAndDocumentsNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the IsCharityRegulatorPage" must {

        val nextPageF = navigator.nextPage(IsCharityRegulatorPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageF(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the EnterCharityRegulator page when yes is selected" in {
          nextPageF(emptyUserAnswers.set(IsCharityRegulatorPage, true).success.value) mustBe
            regulatorDocsRoutes.CharityRegulatorController.onPageLoad(NormalMode)
        }

        "go to the SelectWhyNoRegulator page when No is selected" in {
          nextPageF(emptyUserAnswers.set(IsCharityRegulatorPage, false).success.value) mustBe
            regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(NormalMode)
        }
      }

      "from the CharityRegulatorPage" must {

        val nextPageF = navigator.nextPage(CharityRegulatorPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageF(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityCommissionRegistrationNumberPage when user answer has EnglandWales selected and click Continue button" in {
          nextPageF(
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)).success.value
          ) mustBe
            regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(NormalMode)
        }

        "go to the ScottishRegulatorRegNumberPage when user answer has Scottish selected and click Continue button" in {
          nextPageF(emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)).success.value) mustBe
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the NIRegulatorRegNumberPage when user answer has NorthernIreland selected and click Continue button" in {
          nextPageF(
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland)).success.value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the CharityOtherRegulatorDetailsPage when user answer has Other selected and click Continue button" in {
          nextPageF(emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](Other)).success.value) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }

        "go to the CharityCommissionRegistrationNumberPage page when user answer has all options selected and its switch user journey" in {
          nextPageF(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, CharityRegulator.values.toSet))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(NormalMode)
        }

        "go to the ScottishRegulatorRegNumberPage page when user answer has Scottish and Other selected and its switch user journey" in {
          nextPageF(
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
          ) mustBe regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the NIRegulatorRegNumberPage page when user answer has NorthernIreland and Other selected and its switch user journey" in {
          nextPageF(
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
          ) mustBe regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer hasOther selected and its switch user journey" in {
          nextPageF(
            emptyUserAnswers
              .set(
                CharityOtherRegulatorDetailsPage,
                CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the CharityCommissionRegistrationNumberPage" must {

        val nextPageF = navigator.nextPage(CharityCommissionRegistrationNumberPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageF(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the ScottishRegulatorRegNumberPage page when user answer has Scottish selected and click Continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the NIRegulatorRegNumberPage page when user answer has Scottish selected and click Continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, NorthernIreland)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Other)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }

        "go to the ScottishRegulatorRegNumberPage page when user answer has EnglandWales, Scottish and Other selected and its switch user journey" in {
          nextPageF(
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
          ) mustBe regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the RegulatorsSummaryController when user answer has EnglandWales and its switch user journey" in {
          nextPageF(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)))
              .flatMap(_.set(IsSwitchOverUserPage, true))
              .success
              .value
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }
      }

      "from the ScottishRegulatorRegNumberPage" must {

        val nextPageF = navigator.nextPage(ScottishRegulatorRegNumberPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageF(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the NIRegulatorRegNumberPage page when user answer has NorthernIreland selected and click Continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, NorthernIreland)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, Other)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          nextPageF(emptyUserAnswers.set(ScottishRegulatorRegNumberPage, "registrationNumber").success.value) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has EnglandWales, Scottish and Other selected and its switch user journey" in {
          nextPageF(
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
          ) mustBe regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the NIRegulatorRegNumberPage" must {

        val nextPageF = navigator.nextPage(NIRegulatorRegNumberPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageF(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(NIRegulatorRegNumberPage, "nIRegistrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(NIRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland, Other)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          nextPageF(
            emptyUserAnswers.set(NIRegulatorRegNumberPage, "registrationNumber").success.value
          ) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has EnglandWales, Scottish and Other selected and its switch user journey" in {
          nextPageF(
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
          ) mustBe regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the CharityOtherRegulatorDetailsPage" must {

        val nextPageF = navigator.nextPage(CharityOtherRegulatorDetailsPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageF(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(
                CharityOtherRegulatorDetailsPage,
                CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          nextPageF(
            emptyUserAnswers
              .set(
                CharityOtherRegulatorDetailsPage,
                CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
              )
              .success
              .value
          ) mustBe routes.PageNotFoundController.onPageLoad()
        }
      }

      "from the SelectWhyNoRegulatorPage" must {

        val nextPageF = navigator.nextPage(SelectWhyNoRegulatorPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageF(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityNotRegisteredReason page when Other is selected" in {
          nextPageF(
            emptyUserAnswers.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other).getOrElse(emptyUserAnswers)
          ) mustBe
            regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(NormalMode)
        }

        "go to the Summary page when other than Other is selected" in {
          nextPageF(
            emptyUserAnswers
              .set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)
              .getOrElse(emptyUserAnswers)
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }
      }

      "from the RegulatorsSummaryPage" must {

        val nextPageF = navigator.nextPage(RegulatorsSummaryPage, NormalMode, _)

        "go to the Task List page when click continue button" in {
          nextPageF(emptyUserAnswers) mustBe routes.IndexController.onPageLoad(None)
        }
      }

      "from the WhyNotRegisteredWithCharityPage" must {

        val nextPageF = navigator.nextPage(WhyNotRegisteredWithCharityPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageF(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the RegulatorsSummary page when clicked continue button" in {
          nextPageF(
            emptyUserAnswers
              .set(WhyNotRegisteredWithCharityPage, "reason")
              .getOrElse(emptyUserAnswers)
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }
      }

      "from any Unknown page" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Check mode" when {

      "from the IsCharityRegulatorPage" must {

        val nextPageCheckMode = navigator.nextPage(IsCharityRegulatorPage, CheckMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageCheckMode(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when yes is selected and an answer exists for the CharityRegulator page" in {

          nextPageCheckMode(
            emptyUserAnswers
              .set(IsCharityRegulatorPage, true)
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the Summary page when no is selected and an answer exists for the SelectWhyNoRegulator page" in {

          nextPageCheckMode(
            emptyUserAnswers
              .set(IsCharityRegulatorPage, false)
              .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold))
              .success
              .value
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the EnterCharityRegulator page when yes is selected no data exists for the CharityRegulator page" in {
          nextPageCheckMode(emptyUserAnswers.set(IsCharityRegulatorPage, true).success.value) mustBe
            regulatorDocsRoutes.CharityRegulatorController.onPageLoad(CheckMode)
        }

        "go to the SelectWhyNoRegulator page when No is selected and no data exists for the SelectWhyNoRegulator page" in {
          nextPageCheckMode(emptyUserAnswers.set(IsCharityRegulatorPage, false).success.value) mustBe
            regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(CheckMode)
        }
      }

      "from the CharityRegulatorPage" must {

        val nextPageCheckMode = navigator.nextPage(CharityRegulatorPage, CheckMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageCheckMode(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityCommissionRegistrationNumberPage when user answer has EnglandWales selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)).success.value
          ) mustBe
            regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(CheckMode)
        }

        "go to the ScottishRegulatorRegNumberPage when user answer has Scottish selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)).success.value
          ) mustBe
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(CheckMode)
        }

        "go to the NIRegulatorRegNumberPage when user answer has NorthernIreland selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland)).success.value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode)
        }

        "go to the CharityOtherRegulatorDetailsPage when user answer has Other selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers.set(CharityRegulatorPage, Set[CharityRegulator](Other)).success.value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode)
        }
      }

      "from the CharityCommissionRegistrationNumberPage" must {

        val nextPageCheckMode = navigator.nextPage(CharityCommissionRegistrationNumberPage, CheckMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageCheckMode(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the ScottishRegulatorRegNumberPage page when user answer has Scottish selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(CheckMode)
        }

        "go to the NIRegulatorRegNumberPage page when user answer has Scottish selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(CharityCommissionRegistrationNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, NorthernIreland)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode)
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          nextPageCheckMode(
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

        val nextPageCheckMode = navigator.nextPage(ScottishRegulatorRegNumberPage, CheckMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageCheckMode(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the NIRegulatorRegNumberPage page when user answer has NorthernIreland selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, NorthernIreland)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode)
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(ScottishRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish, Other)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode)
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          nextPageCheckMode(
            emptyUserAnswers.set(ScottishRegulatorRegNumberPage, "registrationNumber").success.value
          ) mustBe
            routes.PageNotFoundController.onPageLoad()
        }
      }

      "from the NIRegulatorRegNumberPage" must {

        val nextPageCheckMode = navigator.nextPage(NIRegulatorRegNumberPage, CheckMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageCheckMode(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(NIRegulatorRegNumberPage, "nIRegistrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the CharityOtherRegulatorDetailsPage page when user answer has Other selected and click Continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(NIRegulatorRegNumberPage, "registrationNumber")
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](NorthernIreland, Other)))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode)
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          nextPageCheckMode(
            emptyUserAnswers.set(NIRegulatorRegNumberPage, "registrationNumber").success.value
          ) mustBe
            routes.PageNotFoundController.onPageLoad()
        }
      }

      "from the CharityOtherRegulatorDetailsPage" must {

        val nextPageCheckMode = navigator.nextPage(CharityOtherRegulatorDetailsPage, CheckMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageCheckMode(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the Summary page when clicked continue button" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(
                CharityOtherRegulatorDetailsPage,
                CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
              )
              .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
              .success
              .value
          ) mustBe regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }

        "go to the PageNotFoundController page when user answer has no CharityRegulator is selected" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(
                CharityOtherRegulatorDetailsPage,
                CharityOtherRegulatorDetails("ORegulatorName", "registrationNumber")
              )
              .success
              .value
          ) mustBe routes.PageNotFoundController.onPageLoad()
        }
      }

      "from the SelectWhyNoRegulatorPage" must {

        val nextPageCheckMode = navigator.nextPage(SelectWhyNoRegulatorPage, CheckMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageCheckMode(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityNotRegisteredReason page when Other is selected" in {

          nextPageCheckMode(
            emptyUserAnswers.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other).getOrElse(emptyUserAnswers)
          ) mustBe
            regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(CheckMode)
        }

        "go to the Summary page when other than Other is selected" in {
          nextPageCheckMode(
            emptyUserAnswers
              .set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)
              .getOrElse(emptyUserAnswers)
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }
      }

      "from WhyNotRegisteredWithCharityRegulatorPage" must {

        val nextPageCheckMode = navigator.nextPage(WhyNotRegisteredWithCharityPage, CheckMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPageCheckMode(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the RegulatorsSummaryController page When answer is provided" in {

          nextPageCheckMode(
            emptyUserAnswers.set(WhyNotRegisteredWithCharityPage, "abcd").getOrElse(emptyUserAnswers)
          ) mustBe
            regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(RegulatorsSummaryPage, CheckMode, emptyUserAnswers) mustBe routes.IndexController
            .onPageLoad(None)
        }
      }

      "from any Unknown page" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe routes.IndexController.onPageLoad(None)
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
