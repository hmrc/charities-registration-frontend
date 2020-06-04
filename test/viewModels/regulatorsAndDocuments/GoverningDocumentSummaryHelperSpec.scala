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

package utils

import assets.messages.BaseMessages
import base.SpecBase
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import models.regulators.SelectWhyNoRegulator.EnglandWalesUnderThreshold
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import models.{CharityOtherRegulatorDetails, CheckMode, UserAnswers}
import pages.regulatorsAndDocuments._
import viewmodels.SummaryListRowHelper
import viewmodels.regulatorsAndDocuments.GoverningDocumentSummaryHelper

class GoverningDocumentSummaryHelperSpec extends SpecBase with SummaryListRowHelper with CurrencyFormatter {


  val helper = new GoverningDocumentSummaryHelper(UserAnswers("id")
    .set(IsCharityRegulatorPage, true).get
    .set(CharityRegulatorPage, CharityRegulator.values.toSet).get
    .set(CharityCommissionRegistrationNumberPage, "123456").get
    .set(ScottishRegulatorRegNumberPage, "SC123456").get
    .set(NIRegulatorRegNumberPage, "123456").get
    .set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("test", "123423")).get
    .set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.values.head).get
  )

  val newLine = "<br>"
  val englandAndWales = CharityRegulator.EnglandWales
  val scottish = CharityRegulator.Scottish
  val northernIreland = CharityRegulator.NorthernIreland
  val other = CharityRegulator.Other

  "Check Your Answers Helper" must {

    "For the IsCharityRegulator answer" must {

      "have a correctly formatted summary list row" in {

        helper.isCharityRegulatorRow mustBe Some(summaryListRow(
          messages("isCharityRegulator.checkYourAnswersLabel"),
          BaseMessages.yes,
          Some(messages("isCharityRegulator.checkYourAnswersLabel")),
          regulatorDocsRoutes.IsCharityRegulatorController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the CharityRegulator answer" must {

      "have a correctly formatted summary list row" in {

        helper.charityRegulatorRow mustBe Some(summaryListRow(
          messages("charityRegulator.checkYourAnswersLabel"),
          s"""${messages(s"charityRegulator.$englandAndWales")}$newLine${messages(s"charityRegulator.$scottish")}$newLine${messages(s"charityRegulator.$northernIreland")}$newLine${messages(s"charityRegulator.$other")}$newLine""",
          Some(messages("charityRegulator.checkYourAnswersLabel")),
          regulatorDocsRoutes.CharityRegulatorController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the CharityCommissionRegistrationNumber answer" must {

      "have a correctly formatted summary list row" in {

        helper.charityCommissionRegRow mustBe Some(summaryListRow(
          messages("charityCommissionRegistrationNumber.checkYourAnswersLabel"),
          "123456",
          Some(messages("charityCommissionRegistrationNumber.checkYourAnswersLabel")),
          regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the ScottishRegulatorRegNumber answer" must {

      "have a correctly formatted summary list row" in {

        helper.scottishRegulatorRegRow mustBe Some(summaryListRow(
          messages("scottishRegulatorRegNumber.checkYourAnswersLabel"),
          "SC123456",
          Some(messages("scottishRegulatorRegNumber.checkYourAnswersLabel")),
          regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the NIRegulatorRegNumber answer" must {

      "have a correctly formatted summary list row" in {

        helper.nIRegulatorRegRow mustBe Some(summaryListRow(
          messages("nIRegulatorRegNumber.checkYourAnswersLabel"),
          "123456",
          Some(messages("nIRegulatorRegNumber.checkYourAnswersLabel")),
          regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the CharityOtherRegulatorDetails answer" must {

      "have a correctly formatted summary list row" in {

        helper.charityOtherRegulatorRow mustBe Some(summaryListRow(
          messages("charityOtherRegulatorDetails.checkYourAnswersLabel"),
          s"test$newLine$newLine${"123423"}",
          Some(messages("charityOtherRegulatorDetails.checkYourAnswersLabel")),
          regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the SelectWhyNoRegulator Groups Added" must {

      "have a correctly formatted summary list row when one added" in {

        helper.selectWhyNoRegulatorRow mustBe Some(summaryListRow(
          messages("selectWhyNoRegulator.checkYourAnswersLabel"),
          messages(s"selectWhyNoRegulator.$EnglandWalesUnderThreshold"),
          Some(messages("selectWhyNoRegulator.checkYourAnswersLabel")),
          regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }
  }
}
