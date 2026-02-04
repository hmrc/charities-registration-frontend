/*
 * Copyright 2026 HM Revenue & Customs
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

import java.time.{LocalDate, MonthDay}

import base.SpecBase
import base.data.messages.BaseMessages
import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import models.operations.CharityEstablishedOptions.England
import models.operations.{CharityEstablishedOptions, FundRaisingOptions, OperatingLocationOptions}
import models.{CheckMode, MongoDateTimeFormats, UserAnswers}
import org.mockito.Mockito.mock
import pages.operationsAndFunds._
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import utils.CurrencyFormatter
import viewmodels.SummaryListRowHelper
import viewmodels.operationsAndFunds.OperationsFundsSummaryHelper

class OperationsFundsSummaryHelperSpec extends SpecBase with SummaryListRowHelper with CurrencyFormatter {

  private lazy val mockCountryService: CountryService = mock(classOf[CountryService])

  private val helper = new OperationsFundsSummaryHelper(
    UserAnswers("id")
      .set(FundRaisingPage, FundRaisingOptions.valuesIndexed.toSet)
      .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.values.head))
      .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
      .flatMap(_.set(IsFinancialAccountsPage, true))
      .flatMap(_.set(EstimatedIncomePage, BigDecimal.valueOf(1123.12)))
      .flatMap(_.set(ActualIncomePage, BigDecimal.valueOf(11123.12)))
      .flatMap(_.set(IsBankStatementsPage, true))
      .flatMap(
        _.set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-10-01")))(
          MongoDateTimeFormats.localDayMonthWrite
        )
      )
      .success
      .value,
    mockCountryService
  )

  "OperationsFundsSummaryHelper" when {

    "For the Charity raise funds answer" must {

      "have a correctly formatted summary list row" in {

        val fundRaisingList = FundRaisingOptions.valuesIndexed
          .sortBy(_.order)
          .foldLeft("")((str, key) => str + s"""<div>${messages(s"selectFundRaising.${key.toString}")}</div>""")

        helper.fundRaisingRow mustBe Some(
          summaryListRow(
            messages("selectFundRaising.checkYourAnswersLabel"),
            HtmlContent(fundRaisingList),
            Some(messages("selectFundRaising.checkYourAnswersLabel")),
            operationFundsRoutes.FundRaisingController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Country Charity Established In answer" must {

      "have a correctly formatted summary list row when one added" in {

        helper.countryOfEstablishmentRow mustBe Some(
          summaryListRow(
            messages("charityEstablishedIn.checkYourAnswersLabel"),
            HtmlContent(messages(s"charityEstablishedIn.$England")),
            Some(messages("charityEstablishedIn.checkYourAnswersLabel")),
            operationFundsRoutes.CharityEstablishedInController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For Charity Operating locations answer" must {

      "have a correctly formatted summary list row" in {

        val operatingLocationList = OperatingLocationOptions.values
          .sortBy(_.order)
          .foldLeft("")((str, key) => str + s"""<div>${messages(s"operatingLocation.${key.toString}")}</div>""")

        helper.operatingLocationRow mustBe Some(
          summaryListRow(
            messages("operatingLocation.checkYourAnswersLabel"),
            HtmlContent(operatingLocationList),
            Some(messages("operatingLocation.checkYourAnswersLabel")),
            operationFundsRoutes.OperatingLocationController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For Charity has financial accounts answer" must {

      "have a correctly formatted summary list row" in {

        helper.isFinancialAccountsRow mustBe Some(
          summaryListRow(
            messages("isFinancialAccounts.checkYourAnswersLabel"),
            HtmlContent(messages("site.yes")),
            Some(messages("isFinancialAccounts.checkYourAnswersLabel")),
            operationFundsRoutes.IsFinancialAccountsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For Charity's estimated income" must {

      "have a correctly formatted summary list row" in {

        helper.estimatedAmountRow mustBe Some(
          summaryListRow(
            messages("estimatedIncome.checkYourAnswersLabel"),
            HtmlContent("£1,123.12"),
            Some(messages("estimatedIncome.checkYourAnswersLabel")),
            operationFundsRoutes.EstimatedIncomeController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For Charity's actual income" must {

      "have a correctly formatted summary list row" in {

        helper.actualAmountRow mustBe Some(
          summaryListRow(
            messages("actualIncome.checkYourAnswersLabel"),
            HtmlContent("£11,123.12"),
            Some(messages("actualIncome.checkYourAnswersLabel")),
            operationFundsRoutes.ActualIncomeController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For Charity has bank statements for the last 3 months answer" must {

      "have a correctly formatted summary list row" in {

        helper.isBankStatementsRow mustBe Some(
          summaryListRow(
            messages("isBankStatements.checkYourAnswersLabel"),
            HtmlContent(messages("site.yes")),
            Some(messages("isBankStatements.checkYourAnswersLabel")),
            operationFundsRoutes.IsBankStatementsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Accounting period ends answer" must {

      "have a correctly formatted summary list row" in {

        helper.accountingPeriodRow mustBe Some(
          summaryListRow(
            messages("accountingPeriodEndDate.checkYourAnswersLabel"),
            HtmlContent("1 October"),
            Some(messages("accountingPeriodEndDate.checkYourAnswersLabel")),
            operationFundsRoutes.AccountingPeriodEndDateController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
