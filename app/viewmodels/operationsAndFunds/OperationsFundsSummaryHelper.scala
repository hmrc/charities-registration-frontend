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

import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import controllers.routes
import models.MongoDateTimeFormats._
import models.{CheckMode, UserAnswers}
import org.joda.time.MonthDay
import pages.QuestionPage
import pages.operationsAndFunds._
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.{CurrencyFormatter, ImplicitDateFormatter}
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class OperationsFundsSummaryHelper(override val userAnswers: UserAnswers)
                                  (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
    with SummaryListRowHelper with CurrencyFormatter {

    def fundRaisingRow: Option[SummaryListRow] =
      multiLineAnswer(FundRaisingPage, operationFundsRoutes.FundRaisingController.onPageLoad(CheckMode))

    def operatingLocationRow: Option[SummaryListRow] =
      multiLineAnswer(OperatingLocationPage, operationFundsRoutes.OperatingLocationController.onPageLoad(CheckMode))

    def isFinancialAccountsRow: Option[SummaryListRow] =
      answer(IsFinancialAccountsPage, routes.DeadEndController.onPageLoad())

    def estimatedAmountRow: Option[SummaryListRow] =
      answer(EstimatedIncomePage, routes.DeadEndController.onPageLoad())

  def whyNoBankStatementRow: Option[SummaryListRow] =
    answer(WhyNoBankStatementPage, routes.DeadEndController.onPageLoad())

    def actualAmountRow: Option[SummaryListRow] =
      answer(ActualIncomePage, routes.DeadEndController.onPageLoad())

    def isBankStatementsRow: Option[SummaryListRow] =
      answer(IsBankStatementsPage, routes.DeadEndController.onPageLoad())

    def accountingPeriodRow: Option[SummaryListRow] =
      answerAccountingPeriod(AccountingPeriodEndDatePage, operationFundsRoutes.AccountingPeriodEndDateController.onPageLoad(CheckMode))

    def otherFundRaisingRow: Option[SummaryListRow] =
      answer(OtherFundRaisingPage, operationFundsRoutes.OtherFundRaisingController.onPageLoad(CheckMode))

    private def answerAccountingPeriod[A](page: QuestionPage[MonthDay],
                                        changeLinkCall: Call) : Option[SummaryListRow] =
      userAnswers.get(page).map{ ans =>
        summaryListRow(
          label = messages(s"$page.checkYourAnswersLabel"),
          value = ans,
          visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      }

    val rows: Seq[SummaryListRow] = Seq(
      fundRaisingRow,
      otherFundRaisingRow,
      operatingLocationRow,
      isFinancialAccountsRow,
      estimatedAmountRow,
      actualAmountRow,
      isBankStatementsRow,
      whyNoBankStatementRow,
      accountingPeriodRow
    ).flatten

  }
