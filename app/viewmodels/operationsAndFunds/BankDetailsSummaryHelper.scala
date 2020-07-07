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

import models.{BankDetails, CheckMode, UserAnswers}
import pages.operationsAndFunds._
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class BankDetailsSummaryHelper(override val userAnswers: UserAnswers)
                              (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
    with SummaryListRowHelper {


  def bankDetailsRows: Seq[SummaryListRow] =
    userAnswers.get(BankDetailsPage).map{ contact =>
      answerRows(contact, controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(CheckMode))
    }.fold(List[SummaryListRow]())(_.toList)

  private def answerRows(bankDetails: BankDetails,
                                          changeLinkCall: Call)( implicit messages: Messages): Seq[SummaryListRow] = Seq(

    Some(
      summaryListRow(
        label = messages("bankDetails.accountName.checkYourAnswersLabel"),
        value = bankDetails.accountName,
        visuallyHiddenText = Some(messages("bankDetails.accountName.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    ),

    Some(
      summaryListRow(
        label = messages("bankDetails.sortCode.checkYourAnswersLabel"),
        value = bankDetails.sortCode,
        visuallyHiddenText = Some(messages("bankDetails.sortCode.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    ),

    Some(
      summaryListRow(
        label = messages("bankDetails.accountNumber.checkYourAnswersLabel"),
        value = bankDetails.accountNumber,
        visuallyHiddenText = Some(messages("bankDetails.accountNumber.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    ),

    bankDetails.rollNumber.map( rollNo =>
      summaryListRow(
        label = messages("bankDetails.rollNumber.checkYourAnswersLabel"),
        value = rollNo,
        visuallyHiddenText = Some(messages("bankDetails.rollNumber.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

    val rows: Seq[SummaryListRow] = Seq(
      bankDetailsRows
    ).flatten

  }
