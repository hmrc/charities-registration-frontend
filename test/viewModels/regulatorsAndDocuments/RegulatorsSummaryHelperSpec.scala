/*
 * Copyright 2025 HM Revenue & Customs
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

package viewModels.regulatorsAndDocuments

import base.SpecBase
import base.data.messages.BaseMessages
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import models.regulators.SelectWhyNoRegulator.EnglandWalesUnderThreshold
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import models.{CharityOtherRegulatorDetails, CheckMode, UserAnswers}
import pages.regulatorsAndDocuments._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.SummaryListRowHelper
import viewmodels.regulatorsAndDocuments.RegulatorsSummaryHelper

class RegulatorsSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val helper = new RegulatorsSummaryHelper(
    UserAnswers("id")
      .set(IsCharityRegulatorPage, true)
      .flatMap(_.set(CharityRegulatorPage, CharityRegulator.values.toSet))
      .flatMap(_.set(CharityCommissionRegistrationNumberPage, charityCommissionRegistrationNumber))
      .flatMap(_.set(ScottishRegulatorRegNumberPage, scottishRegulatorRegistrationNumber))
      .flatMap(_.set(NIRegulatorRegNumberPage, niRegulatorRegistrationNumber))
      .flatMap(_.set(CharityOtherRegulatorDetailsPage, charityRegulatorDetails))
      .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.values.head))
      .flatMap(_.set(WhyNotRegisteredWithCharityPage, whyNotRegistered))
      .success
      .value
  )

  private val englandAndWales = CharityRegulator.EnglandWales
  private val scottish        = CharityRegulator.Scottish
  private val northernIreland = CharityRegulator.NorthernIreland
  private val other           = CharityRegulator.Other

  "Check Your Answers Helper" must {

    "For the IsCharityRegulator answer" must {

      "have a correctly formatted summary list row" in {

        helper.isCharityRegulatorRow mustBe Some(
          summaryListRow(
            messages("isCharityRegulator.checkYourAnswersLabel"),
            HtmlContent(BaseMessages.yes),
            Some(messages("isCharityRegulator.checkYourAnswersLabel")),
            regulatorDocsRoutes.IsCharityRegulatorController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the CharityRegulator answer" must {

      "have a correctly formatted summary list row" in {

        helper.charityRegulatorRow mustBe Some(
          summaryListRow(
            messages("charityRegulator.checkYourAnswersLabel"),
            HtmlContent(s"""<div>${messages(s"charityRegulator.$englandAndWales")}</div><div>${messages(
                            s"charityRegulator.$scottish"
                          )}</div><div>${messages(s"charityRegulator.$northernIreland")}</div><div>${messages(
                            s"charityRegulator.$other"
                          )}</div>""".stripMargin),
            Some(messages("charityRegulator.checkYourAnswersLabel")),
            regulatorDocsRoutes.CharityRegulatorController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the CharityCommissionRegistrationNumber answer" must {

      "have a correctly formatted summary list row" in {

        helper.charityCommissionRegRow mustBe Some(
          summaryListRow(
            messages("charityCommissionRegistrationNumber.checkYourAnswersLabel"),
            HtmlContent(charityCommissionRegistrationNumber),
            Some(messages("charityCommissionRegistrationNumber.checkYourAnswersLabel")),
            regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(
              CheckMode
            ) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the ScottishRegulatorRegNumber answer" must {

      "have a correctly formatted summary list row" in {

        helper.scottishRegulatorRegRow mustBe Some(
          summaryListRow(
            messages("scottishRegulatorRegNumber.checkYourAnswersLabel"),
            HtmlContent(s"$scottishRegulatorRegistrationNumber"),
            Some(messages("scottishRegulatorRegNumber.checkYourAnswersLabel")),
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the NIRegulatorRegNumber answer" must {

      "have a correctly formatted summary list row" in {

        helper.nIRegulatorRegRow mustBe Some(
          summaryListRow(
            messages("nIRegulatorRegNumber.checkYourAnswersLabel"),
            HtmlContent(niRegulatorRegistrationNumber),
            Some(messages("nIRegulatorRegNumber.checkYourAnswersLabel")),
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the CharityOtherRegulatorDetails answer" must {

      "have a correctly formatted summary list row for Registration Name" in {

        helper.regulatorNameRow mustBe Some(
          summaryListRow(
            messages("charityOtherRegulatorDetails.name.checkYourAnswersLabel"),
            HtmlContent(charityRegulatorName),
            Some(messages("charityOtherRegulatorDetails.name.checkYourAnswersLabel")),
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list row for Registration Number" in {

        helper.regulatorRegistrationNumberRow mustBe Some(
          summaryListRow(
            messages("charityOtherRegulatorDetails.registrationNumber.checkYourAnswersLabel"),
            HtmlContent(chartyRegulatorRegistrationNumber),
            Some(messages("charityOtherRegulatorDetails.registrationNumber.checkYourAnswersLabel")),
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the SelectWhyNoRegulator Groups Added" must {

      "have a correctly formatted summary list row when one added" in {

        helper.selectWhyNoRegulatorRow mustBe Some(
          summaryListRow(
            messages("selectWhyNoRegulator.checkYourAnswersLabel"),
            HtmlContent(messages(s"selectWhyNoRegulator.$EnglandWalesUnderThreshold")),
            Some(messages("selectWhyNoRegulator.checkYourAnswersLabel")),
            regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the WhyNotRegisteredWithCharity answer" must {

      "have a correctly formatted summary list row" in {

        helper.whyNotRegisteredCharityRow mustBe Some(
          summaryListRow(
            messages("whyNotRegisteredWithCharity.checkYourAnswersLabel"),
            HtmlContent(whyNotRegistered),
            Some(messages("whyNotRegisteredWithCharity.checkYourAnswersLabel")),
            regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
