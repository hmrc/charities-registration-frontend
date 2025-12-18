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

package viewModels.operationsAndFunds

import base.SpecBase
import base.data.messages.BaseMessages
import controllers.operationsAndFunds.{routes => operations}
import models.operations.CharitablePurposes
import models.{CheckMode, UserAnswers}
import pages.operationsAndFunds.{CharitableObjectivesPage, CharitablePurposesPage, PublicBenefitsPage}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.SummaryListRowHelper
import viewmodels.operationsAndFunds.CharityObjectivesSummaryHelper

class CharityObjectivesSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val helper = new CharityObjectivesSummaryHelper(
    UserAnswers("id")
      .set(CharitableObjectivesPage, charityObjective)
      .flatMap(_.set(CharitablePurposesPage, CharitablePurposes.values.toSet))
      .flatMap(_.set(PublicBenefitsPage, "Public Benefits"))
      .success
      .value
  )

  "Check your answers helper" must {

    "For the Charitable Objectives answer" must {

      "have a correctly formatted summary list row" in {

        helper.charitableObjectivesRow mustBe Some(
          summaryListRow(
            messages("charitableObjectives.checkYourAnswersLabel"),
            HtmlContent(charityObjective),
            Some(messages("charitableObjectives.checkYourAnswersLabel")),
            operations.CharitableObjectivesController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Charitable Purposes answer" must {

      "have a correctly formatted summary list row" in {

        val purposeList = CharitablePurposes.values
          .sortBy(_.order)
          .foldLeft("")((str, key) => str + s"""<div>${messages(s"charitablePurposes.${key.toString}")}</div>""")

        helper.charitablePurposesRow mustBe Some(
          summaryListRow(
            messages("charitablePurposes.checkYourAnswersLabel"),
            HtmlContent(purposeList),
            Some(messages("charitablePurposes.checkYourAnswersLabel")),
            operations.CharitablePurposesController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Public Benefits answer" must {

      "have a correctly formatted summary list row" in {

        helper.publicBenefitsRow mustBe Some(
          summaryListRow(
            messages("publicBenefits.checkYourAnswersLabel"),
            HtmlContent("Public Benefits"),
            Some(messages("publicBenefits.checkYourAnswersLabel")),
            operations.PublicBenefitsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
