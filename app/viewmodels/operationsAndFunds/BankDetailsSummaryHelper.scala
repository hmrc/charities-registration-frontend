/*
 * Copyright 2021 HM Revenue & Customs
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

  def accountName: Option[SummaryListRow] =
    answerAccountName(BankDetailsPage,
      controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "bankDetails.accountName")

  def accountSortCode: Option[SummaryListRow] =
    answerSortCode(BankDetailsPage,
      controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "bankDetails.sortCode")

  def accountNumber: Option[SummaryListRow] =
    answerAccountNumber(BankDetailsPage,
      controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "bankDetails.accountNumber")

  def accountRollNumber: Option[SummaryListRow] =
    answerRollNumber(BankDetailsPage,
      controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "bankDetails.rollNumber")

    val rows: Seq[SummaryListRow] = Seq(
      accountName,
      accountSortCode,
      accountNumber,
      accountRollNumber
    ).flatten

  }
