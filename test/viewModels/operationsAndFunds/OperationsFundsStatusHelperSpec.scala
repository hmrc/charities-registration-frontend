/*
 * Copyright 2023 HM Revenue & Customs
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
import models.{MongoDateTimeFormats, UserAnswers}
import models.operations.{CharityEstablishedOptions, FundRaisingOptions, OperatingLocationOptions}
import org.joda.time.{MonthDay, LocalDate => JLocalDate}
import pages.operationsAndFunds._
import viewmodels.operationsAndFunds.OperationsFundsStatusHelper

class OperationsFundsStatusHelperSpec extends SpecBase {

  //scalastyle:off magic.number

  private val helper = OperationsFundsStatusHelper

  val commonData: UserAnswers = emptyUserAnswers
    .set(IsFinancialAccountsPage, true)
    .flatMap(_.set(EstimatedIncomePage, BigDecimal.valueOf(1123.12)))
    .flatMap(_.set(ActualIncomePage, BigDecimal.valueOf(11123.12)))
    .flatMap(
      _.set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 10, 1).toDate))(
        MongoDateTimeFormats.localDayMonthWrite
      )
    )
    .success
    .value

  "OperationsFundsStatusHelper" must {

    "when verifying section 5 answers" must {

      "return false when user answers is empty" in {

        helper.checkComplete(emptyUserAnswers) mustBe false
      }

      "return false when one of answer is defined" in {
        helper.checkComplete(
          emptyUserAnswers.set(FundRaisingPage, FundRaisingOptions.values.toSet).success.value
        ) mustBe false
      }

      "return true when other fund raising, overseas countries, no bank statement, one overseas country and " +
        "related questions are answered correctly (Scenario 2)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, FundRaisingOptions.values.toSet)
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OtherFundRaisingPage, "sdf"))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, false))
              .flatMap(_.set(WhyNoBankStatementPage, "something"))
              .success
              .value
          ) mustBe true
        }

      "return false when other fund raising, overseas countries, no bank statement, one overseas country not answered correctly (Scenario 2)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(FundRaisingPage, FundRaisingOptions.values.toSet)
            .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
            .flatMap(_.set(OtherFundRaisingPage, "sdf"))
            .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
            .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
            .flatMap(_.set(IsFinancialAccountsPage, true))
            .flatMap(_.set(IsBankStatementsPage, false))
            .flatMap(_.set(WhyNoBankStatementPage, "something"))
            .success
            .value
        ) mustBe false
      }

      "return true when other fund raising, overseas countries, yes bank statement and related questions are answered correctly (Scenario 3)" in {
        helper.checkComplete(
          commonData
            .set(FundRaisingPage, FundRaisingOptions.values.toSet)
            .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
            .flatMap(_.set(OtherFundRaisingPage, "sdf"))
            .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
            .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
            .flatMap(_.set(IsFinancialAccountsPage, true))
            .flatMap(_.set(IsBankStatementsPage, true))
            .success
            .value
        ) mustBe true
      }

      "return false when other fund raising, overseas countries, yes bank statement and additional data (Scenario 3)" in {
        helper.checkComplete(
          commonData
            .set(FundRaisingPage, FundRaisingOptions.values.toSet)
            .flatMap(_.set(OtherFundRaisingPage, "sdf"))
            .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
            .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
            .flatMap(_.set(IsFinancialAccountsPage, true))
            .flatMap(_.set(IsBankStatementsPage, true))
            .flatMap(_.set(WhyNoBankStatementPage, "something"))
            .success
            .value
        ) mustBe false
      }

      "return false when other fund raising, overseas countries, yes bank statement not answered correctly (Scenario 3)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(FundRaisingPage, FundRaisingOptions.values.toSet)
            .flatMap(_.set(OtherFundRaisingPage, "sdf"))
            .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
            .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
            .flatMap(_.set(IsFinancialAccountsPage, true))
            .flatMap(_.set(IsBankStatementsPage, true))
            .success
            .value
        ) mustBe false
      }

      "return true when other fund raising, overseas countries, no bank statement, multiple overseas country and " +
        "related questions are answered correctly (Scenario 4)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, FundRaisingOptions.values.toSet)
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OtherFundRaisingPage, "sdf"))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), "AS"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, false))
              .flatMap(_.set(WhyNoBankStatementPage, "something"))
              .success
              .value
          ) mustBe true
        }

      "return true when other fund raising, overseas countries, yes bank statement, multiple overseas country and " +
        "related questions are answered correctly (Scenario 5)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, FundRaisingOptions.values.toSet)
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OtherFundRaisingPage, "sdf"))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), "AS"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, true))
              .success
              .value
          ) mustBe true
        }

      "return true when  something other than 'other' fund raising, overseas countries, no bank statement, " +
        "one overseas country and related questions are answered correctly (Scenario 6)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, false))
              .flatMap(_.set(WhyNoBankStatementPage, "something"))
              .success
              .value
          ) mustBe true
        }

      "return false when  something other than 'other' fund raising, overseas countries, no bank statement, " +
        "one overseas country and additional questions are answered (Scenario 6)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(OtherFundRaisingPage, "sdf"))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, false))
              .flatMap(_.set(WhyNoBankStatementPage, "something"))
              .success
              .value
          ) mustBe false
        }

      "return false when  something other than 'other' fund raising, overseas countries, no bank statement, " +
        "one overseas country not answered correctly (Scenario 6)" in {
          helper.checkComplete(
            emptyUserAnswers
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(OtherFundRaisingPage, "sdf"))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, false))
              .flatMap(_.set(WhyNoBankStatementPage, "something"))
              .success
              .value
          ) mustBe false
        }

      "return true when  something other than 'other' fund raising, overseas countries, yes bank statement, " +
        "one overseas country and related questions are answered correctly (Scenario 7)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, true))
              .success
              .value
          ) mustBe true
        }

      "return false when  something other than 'other' fund raising, overseas countries, yes bank statement, " +
        "one overseas country and additional questions are answered (Scenario 7)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, true))
              .flatMap(_.set(WhyNoBankStatementPage, "something"))
              .success
              .value
          ) mustBe false
        }

      "return false when  something other than 'other' fund raising, overseas countries, yes bank statement, " +
        "one overseas country not answered correctly (Scenario 7)" in {
          helper.checkComplete(
            emptyUserAnswers
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(OtherFundRaisingPage, "sdf"))
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, true))
              .success
              .value
          ) mustBe false
        }

      "return true when  something other than 'other' fund raising, overseas countries, no bank statement, " +
        "multiple overseas country and related questions are answered correctly (Scenario 8)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "FR"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, false))
              .flatMap(_.set(WhyNoBankStatementPage, "something"))
              .success
              .value
          ) mustBe true
        }

      "return true when  something other than 'other' fund raising, overseas countries, yes bank statement, " +
        "mutliple overseas country and related questions are answered correctly (Scenario 9)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, true))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "FR"))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, true))
              .success
              .value
          ) mustBe true
        }

      "return false when Some data is saved for section 5, but not all of the data in any of the scenarios above (Scenario 10)" in {
        helper.checkComplete(commonData) mustBe false
      }

      "return true when  something other than 'other' fund raising, no overseas countries, yes bank statement, " +
        "mutliple overseas country and related questions are answered correctly (Scenario 11)" in {
          helper.checkComplete(
            commonData
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
              .flatMap(_.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England)))
              .flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(IsBankStatementsPage, true))
              .success
              .value
          ) mustBe true
        }

    }
  }
}
