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

package viewmodels.operationsAndFunds

import models.{CheckMode, UserAnswers}
import pages.QuestionPage
import pages.operationsAndFunds._
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.{CurrencyFormatter, ImplicitDateFormatter}
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}
import controllers.operationsAndFunds.{routes => operations}

class CharityObjectivesSummaryHelper(override val userAnswers: UserAnswers)
                                    (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
    with SummaryListRowHelper with CurrencyFormatter {

    def charitableObjectivesRow: Option[SummaryListRow] =
      answer(CharitableObjectivesPage, operations.CharitableObjectivesController.onPageLoad(CheckMode))

    def charitablePurposesRow: Option[SummaryListRow] =
      answerCharitablePurpose(CharitablePurposesPage, operations.CharitablePurposesController.onPageLoad(CheckMode))

    //TODO Add other Charitable Purposes row once page is created

    def publicBenefitsRow: Option[SummaryListRow] =
      answer(PublicBenefitsPage, operations.PublicBenefitsController.onPageLoad(CheckMode))

    private def answerCharitablePurpose[CharitablePurposes](page: QuestionPage[Set[CharitablePurposes]],
                                                         changeLinkCall: Call)
                                                        (implicit reads: Reads[CharitablePurposes],
                                                         conversion: CharitablePurposes => String): Option[SummaryListRow] =
      userAnswers.get(page).map { ans =>

        summaryListRow(
          label = messages(s"$page.checkYourAnswersLabel"),
          ans.foldLeft("")((accumulator,item) => accumulator + "<div>" + messages(s"$page.$item") + "</div>"),
          visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      }

    val rows: Seq[SummaryListRow] = Seq(
      charitableObjectivesRow,
      charitablePurposesRow,
      publicBenefitsRow
    ).flatten

  }
