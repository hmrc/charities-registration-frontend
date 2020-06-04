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

package viewmodels.regulatorsAndDocuments

import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import models.{CharityOtherRegulatorDetails, CheckMode, UserAnswers}
import pages.QuestionPage
import pages.regulatorsAndDocuments.{CharityOtherRegulatorDetailsPage, _}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.{CurrencyFormatter, ImplicitDateFormatter}
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class GoverningDocumentSummaryHelper(override val userAnswers: UserAnswers)
                                    (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper with CurrencyFormatter {

  def isCharityRegulatorRow: Option[SummaryListRow] =
    answer(IsCharityRegulatorPage, regulatorDocsRoutes.IsCharityRegulatorController.onPageLoad(CheckMode))

  def charityRegulatorRow: Option[SummaryListRow] =
    answerCharityRegulator(CharityRegulatorPage, regulatorDocsRoutes.CharityRegulatorController.onPageLoad(CheckMode))

  def charityCommissionRegRow: Option[SummaryListRow] =
    answer(CharityCommissionRegistrationNumberPage, regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(CheckMode))

  def scottishRegulatorRegRow: Option[SummaryListRow] =
    answer(ScottishRegulatorRegNumberPage, regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(CheckMode))

  def nIRegulatorRegRow: Option[SummaryListRow] =
    answer(NIRegulatorRegNumberPage, regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(CheckMode))

  def charityOtherRegulatorRow: Option[SummaryListRow] =
    answerOtherRegulator(CharityOtherRegulatorDetailsPage, regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(CheckMode))

  def selectWhyNoRegulatorRow: Option[SummaryListRow] =
    answer(SelectWhyNoRegulatorPage, regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(CheckMode), true)


  private def answerOtherRegulator[A](page: QuestionPage[CharityOtherRegulatorDetails],
                              changeLinkCall: Call)
                             (implicit reads: Reads[CharityOtherRegulatorDetails], conversion: CharityOtherRegulatorDetails => String): Option[SummaryListRow] =
    userAnswers.get(page) map { ans =>
      summaryListRow(
        label = messages(s"$page.checkYourAnswersLabel"),
        value = ans.regulatorName + "<br><br>" + ans.registrationNumber,
        visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }


  private def answerCharityRegulator[CharityRegulator](page: QuestionPage[Set[CharityRegulator]],
                changeLinkCall: Call)
               (implicit reads: Reads[CharityRegulator], conversion: CharityRegulator => String): Option[SummaryListRow] =
    userAnswers.get(page).map { ans =>

      summaryListRow(
        label = messages(s"$page.checkYourAnswersLabel"),
        ans.foldLeft("")((accumulator,item) => accumulator + messages(s"$page.$item") + "<br>"),
        visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }


  val rows: Seq[SummaryListRow] = Seq(
    isCharityRegulatorRow,
    charityRegulatorRow,
    charityCommissionRegRow,
    scottishRegulatorRegRow,
    nIRegulatorRegRow,
    charityOtherRegulatorRow,
    selectWhyNoRegulatorRow
  ).flatten

}
