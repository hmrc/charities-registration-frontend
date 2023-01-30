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

package navigation

import base.SpecBase
import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import controllers.routes
import models._
import models.operations.{CharityEstablishedOptions, FundRaisingOptions, OperatingLocationOptions}
import org.joda.time.{LocalDate, MonthDay}
import pages.IndexPage
import pages.operationsAndFunds._

class FundRaisingNavigatorSpec extends SpecBase {

  private val navigator: FundRaisingNavigator = inject[FundRaisingNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the FundRaisingPage page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(FundRaisingPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the What country was your charity established in page when user answers other than the Other when clicked continue button" in {
          navigator.nextPage(
            FundRaisingPage,
            NormalMode,
            emptyUserAnswers
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.CharityEstablishedInController.onPageLoad(NormalMode)
        }

        "go to the Where does your other fundraising page when user answer has other and clicked continue button" in {
          navigator.nextPage(
            FundRaisingPage,
            NormalMode,
            emptyUserAnswers
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Other))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OtherFundRaisingController.onPageLoad(NormalMode)
        }
      }

      "from the What Country was your charity established in page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityEstablishedInPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to OperatingLocationPage page when country is selected and clicked continue button" in {
          navigator.nextPage(
            CharityEstablishedInPage,
            NormalMode,
            emptyUserAnswers
              .set(CharityEstablishedInPage, CharityEstablishedOptions.England)
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OperatingLocationController.onPageLoad(NormalMode)
        }
      }

      "from the SelectOperatingLocation page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(OtherFundRaisingPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to OperatingLocationPage page when user answers and clicked continue button" in {
          navigator.nextPage(
            OtherFundRaisingPage,
            NormalMode,
            emptyUserAnswers.set(OtherFundRaisingPage, "sdf").getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.CharityEstablishedInController.onPageLoad(NormalMode)
        }
      }

      "from the OperatingLocationPage page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(OperatingLocationPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to Has your charity prepared financial accounts page when user answers other than the Other when clicked continue button" in {
          navigator.nextPage(
            OperatingLocationPage,
            NormalMode,
            emptyUserAnswers
              .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.IsFinancialAccountsController.onPageLoad(NormalMode)
        }

        "go to WhatCountryDoesTheCharityOperateIn Page when selected overseas and clicked continue button" in {
          navigator.nextPage(
            OperatingLocationPage,
            NormalMode,
            emptyUserAnswers
              .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(NormalMode, Index(0))
        }

        "go to the WhatCountryDoesTheCharityOperateIn summary page when answers already exist" in {
          navigator.nextPage(
            OperatingLocationPage,
            NormalMode,
            emptyUserAnswers
              .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .success
              .value
          ) mustBe
            operationFundsRoutes.OverseasOperatingLocationSummaryController.onPageLoad(NormalMode)
        }
      }

      "from the WhatCountryDoesTheCharityOperateInPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(WhatCountryDoesTheCharityOperateInPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to What countries does the charity operate in summary page when country is entered and clicked continue when summary wasn't visited before" in {
          navigator.nextPage(
            WhatCountryDoesTheCharityOperateInPage(0),
            NormalMode,
            emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0), "PL").success.value
          ) mustBe
            operationFundsRoutes.OverseasOperatingLocationSummaryController.onPageLoad(NormalMode)
        }

        "go to What countries does the charity operate in summary page when country is entered and clicked continue when summary was visited before" in {
          navigator.nextPage(
            WhatCountryDoesTheCharityOperateInPage(0),
            NormalMode,
            emptyUserAnswers
              .set(OverseasOperatingLocationSummaryPage, true)
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .success
              .value
          ) mustBe
            operationFundsRoutes.OverseasOperatingLocationSummaryController.onPageLoad(NormalMode)
        }

        "go to What countries does the charity operate page when country is entered and clicked continue when" +
          "summary was visited before but answered OverseasOperatingLocationSummaryPage" in {
            navigator.nextPage(
              WhatCountryDoesTheCharityOperateInPage(0),
              NormalMode,
              emptyUserAnswers.set(OverseasOperatingLocationSummaryPage, true).success.value
            ) mustBe
              operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(NormalMode, Index(0))
          }

        "go to What countries does the charity operate page when country is entered and clicked continue when" +
          "summary was visited before but not answered OverseasOperatingLocationSummaryPage" in {
            navigator.nextPage(
              WhatCountryDoesTheCharityOperateInPage(0),
              NormalMode,
              emptyUserAnswers
                .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas))
                .success
                .value
            ) mustBe
              operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(NormalMode, Index(0))
          }
      }

      "from the OverseasOperatingLocationSummaryPage" must {
        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(OverseasOperatingLocationSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the PageNotFoundController page when user answer is not empty, but has no answer to page" in {
          navigator.nextPage(
            OverseasOperatingLocationSummaryPage,
            NormalMode,
            emptyUserAnswers
              .set(WhatCountryDoesTheCharityOperateInPage(0), "PL")
              .success
              .value
          ) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to WhatCountryDoesTheCharityOperateInPage when there is no country in the UserAnswers" in {
          navigator.nextPage(
            OverseasOperatingLocationSummaryPage,
            NormalMode,
            emptyUserAnswers
              .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas))
              .success
              .value
          ) mustBe
            operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(NormalMode, Index(0))
        }

        "go to the IsFinancialAccounts page when user selects No" in {
          navigator.nextPage(
            OverseasOperatingLocationSummaryPage,
            NormalMode,
            emptyUserAnswers
              .set(WhatCountryDoesTheCharityOperateInPage(Index(0)), "PL")
              .flatMap(_.set(OverseasOperatingLocationSummaryPage, false))
              .success
              .value
          ) mustBe
            operationFundsRoutes.IsFinancialAccountsController.onPageLoad(NormalMode)
        }

        "go to the IsFinancialAccounts page when user clicks Continue after selecting 5 countries" in {
          navigator.nextPage(
            OverseasOperatingLocationSummaryPage,
            NormalMode,
            emptyUserAnswers
              .set(OverseasOperatingLocationSummaryPage, true)
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), "US"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(3), "DE"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(4), "IE"))
              .success
              .value
          ) mustBe
            operationFundsRoutes.IsFinancialAccountsController.onPageLoad(NormalMode)
        }

        "go to the WhatCountryDoesCharityOperateIn page when user selects Yes with fewer than 5 countries selected" in {
          navigator.nextPage(
            OverseasOperatingLocationSummaryPage,
            NormalMode,
            emptyUserAnswers
              .set(OverseasOperatingLocationSummaryPage, true)
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
              .success
              .value
          ) mustBe
            operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(NormalMode, 2)
        }

      }

      "from the IsFinancialAccountsPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(IsFinancialAccountsPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to Estimated income page when yes is selected" in {
          navigator.nextPage(
            IsFinancialAccountsPage,
            NormalMode,
            emptyUserAnswers.set(IsFinancialAccountsPage, true).success.value
          ) mustBe
            operationFundsRoutes.EstimatedIncomeController.onPageLoad(NormalMode)
        }

        "go to Estimated income page when no is selected" in {
          navigator.nextPage(
            IsFinancialAccountsPage,
            NormalMode,
            emptyUserAnswers.set(IsFinancialAccountsPage, false).success.value
          ) mustBe
            operationFundsRoutes.EstimatedIncomeController.onPageLoad(NormalMode)
        }
      }

      "from the EstimatedIncome page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(EstimatedIncomePage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to ActualIncome page when a number is provided" in {
          navigator.nextPage(
            EstimatedIncomePage,
            NormalMode,
            emptyUserAnswers.set(EstimatedIncomePage, BigDecimal.valueOf(123.12)).success.value
          ) mustBe
            operationFundsRoutes.ActualIncomeController.onPageLoad(NormalMode)
        }
      }

      "from the ActualIncome page" must {
        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(ActualIncomePage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to ActualIncome page when a number is provided" in {
          navigator.nextPage(
            ActualIncomePage,
            NormalMode,
            emptyUserAnswers.set(ActualIncomePage, BigDecimal.valueOf(123.12)).success.value
          ) mustBe
            operationFundsRoutes.IsBankStatementsController.onPageLoad(NormalMode)
        }
      }

      "from the IsBankStatements page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(IsBankStatementsPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to accounting period end date page when yes is selected" in {
          navigator.nextPage(
            IsBankStatementsPage,
            NormalMode,
            emptyUserAnswers.set(IsBankStatementsPage, true).getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.AccountingPeriodEndDateController.onPageLoad(NormalMode)
        }

        "go to Why No Bank Statement page when no is selected" in {
          navigator.nextPage(
            IsBankStatementsPage,
            NormalMode,
            emptyUserAnswers.set(IsBankStatementsPage, false).getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.WhyNoBankStatementController.onPageLoad(NormalMode)
        }
      }

      "from the WhyNoBankStatement page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(WhyNoBankStatementPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to Charity Period End Date" in {
          navigator.nextPage(
            WhyNoBankStatementPage,
            NormalMode,
            emptyUserAnswers
              .set(IsBankStatementsPage, false)
              .flatMap(_.set(WhyNoBankStatementPage, "something"))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.AccountingPeriodEndDateController.onPageLoad(NormalMode)
        }
      }

      "from the AccountingPeriodEndDate page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(AccountingPeriodEndDatePage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the summary page when clicked continue button" in {
          navigator.nextPage(
            AccountingPeriodEndDatePage,
            NormalMode,
            emptyUserAnswers
              .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new LocalDate(2020, 10, 1).toDate))(
                MongoDateTimeFormats.localDayMonthWrite
              )
              .success
              .value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(OperationsFundsSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Check mode" when {

      "from the FundRaisingPage page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(FundRaisingPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {
          navigator.nextPage(
            FundRaisingPage,
            CheckMode,
            emptyUserAnswers
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }

        "go to the What is the other fundraising method page when user answer is 'other' and clicked continue button" in {
          navigator.nextPage(
            FundRaisingPage,
            CheckMode,
            emptyUserAnswers
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Other))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OtherFundRaisingController.onPageLoad(CheckMode)
        }

        "go to the Summary page when user answer is 'other', the 'other fundraising methods' page has already been answered, and clicked continue button" in {
          navigator.nextPage(
            FundRaisingPage,
            CheckMode,
            emptyUserAnswers
              .set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Other))
              .flatMap(_.set(OtherFundRaisingPage, "some fundraising method"))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the What Country was your charity established in page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityEstablishedInPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to OperatingLocationPage page when country is selected and clicked continue button" in {
          navigator.nextPage(
            CharityEstablishedInPage,
            CheckMode,
            emptyUserAnswers
              .set(CharityEstablishedInPage, CharityEstablishedOptions.England)
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the OtherFundRaising page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(OtherFundRaisingPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to OperationsFundsSummaryController Page page when user answers and clicked continue button" in {
          navigator.nextPage(
            OtherFundRaisingPage,
            CheckMode,
            emptyUserAnswers.set(OtherFundRaisingPage, "123").getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the OperatingLocationPage page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(OperatingLocationPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {
          navigator.nextPage(
            OperatingLocationPage,
            CheckMode,
            emptyUserAnswers
              .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }

        "go to What country does you charity operate in page when user answer has overseas but no previous country input, and clicked continue button" in {
          navigator.nextPage(
            OperatingLocationPage,
            CheckMode,
            emptyUserAnswers
              .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, 0)
        }

        "go to OperationsFundsSummary page when user answer has overseas and previous country input, and clicked continue button" in {
          navigator.nextPage(
            OperatingLocationPage,
            CheckMode,
            emptyUserAnswers
              .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "DE"))
              .getOrElse(emptyUserAnswers)
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the WhatCountryDoesTheCharityOperateIn page" must {
        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(WhatCountryDoesTheCharityOperateInPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the OverseasOperationLocationSummary page when user inputs a country" in {
          navigator.nextPage(
            WhatCountryDoesTheCharityOperateInPage(0),
            CheckMode,
            emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0), "PL").success.value
          ) mustBe
            operationFundsRoutes.OverseasOperatingLocationSummaryController.onPageLoad(CheckMode)
        }

        "go to What countries does the charity operate page when country is entered and clicked continue when" +
          "summary was visited before but answered OverseasOperatingLocationSummaryPage" in {
            navigator.nextPage(
              WhatCountryDoesTheCharityOperateInPage(0),
              CheckMode,
              emptyUserAnswers.set(OverseasOperatingLocationSummaryPage, true).success.value
            ) mustBe
              operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, Index(0))
          }

        "go to What countries does the charity operate page when country is entered and clicked continue when" +
          "summary was visited before and answered OperatingLocationOptions" in {
            navigator.nextPage(
              WhatCountryDoesTheCharityOperateInPage(0),
              CheckMode,
              emptyUserAnswers
                .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas))
                .success
                .value
            ) mustBe
              operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, Index(0))
          }
      }

      "from the OverseasOperatingLocationSummaryPage" must {
        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(OverseasOperatingLocationSummaryPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Operations Funds Summary page when user selects No" in {
          navigator.nextPage(
            OverseasOperatingLocationSummaryPage,
            CheckMode,
            emptyUserAnswers.set(OverseasOperatingLocationSummaryPage, false).success.value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }

        "go to the Operations Funds Summary page when user clicks Continue after selecting 5 countries" in {
          navigator.nextPage(
            OverseasOperatingLocationSummaryPage,
            CheckMode,
            emptyUserAnswers
              .set(OverseasOperatingLocationSummaryPage, true)
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), "US"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(3), "DE"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(4), "IE"))
              .success
              .value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }

        "go to the WhatCountryDoesCharityOperateIn page when user selects Yes with fewer than 5 countries selected" in {
          navigator.nextPage(
            OverseasOperatingLocationSummaryPage,
            CheckMode,
            emptyUserAnswers
              .set(OverseasOperatingLocationSummaryPage, true)
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "PL"))
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
              .success
              .value
          ) mustBe
            operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, 2)
        }

        "go to What countries does the charity operate page when country is entered and clicked continue when" +
          "summary was visited before and answered OperatingLocationOptions" in {
            navigator.nextPage(
              OverseasOperatingLocationSummaryPage,
              CheckMode,
              emptyUserAnswers
                .set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas))
                .success
                .value
            ) mustBe
              operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, Index(0))
          }
      }

      "from the IsFinancialAccountsPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(IsFinancialAccountsPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to Summary page when yes is selected" in {
          navigator.nextPage(
            IsFinancialAccountsPage,
            CheckMode,
            emptyUserAnswers.set(IsFinancialAccountsPage, true).success.value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }

        "go to Summary page when no is selected" in {
          navigator.nextPage(
            IsFinancialAccountsPage,
            CheckMode,
            emptyUserAnswers.set(IsFinancialAccountsPage, false).success.value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the EstimatedIncome page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(EstimatedIncomePage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to Summary page when a number is provided" in {
          navigator.nextPage(
            EstimatedIncomePage,
            CheckMode,
            emptyUserAnswers.set(EstimatedIncomePage, BigDecimal.valueOf(123.12)).success.value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the ActualIncome page" must {
        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(ActualIncomePage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to Summary page when a number is provided" in {
          navigator.nextPage(
            ActualIncomePage,
            CheckMode,
            emptyUserAnswers.set(ActualIncomePage, BigDecimal.valueOf(123.12)).success.value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the IsBankStatements page" must {
        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(IsBankStatementsPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to Summary page when the answer is Yes" in {
          navigator.nextPage(
            IsBankStatementsPage,
            CheckMode,
            emptyUserAnswers.set(IsBankStatementsPage, true).success.value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }

        "go to WhyNoBankStatement page when the answer is No and the WhyNoBankStatement question hasn't yet been answered" in {
          navigator.nextPage(
            IsBankStatementsPage,
            CheckMode,
            emptyUserAnswers.set(IsBankStatementsPage, false).success.value
          ) mustBe
            operationFundsRoutes.WhyNoBankStatementController.onPageLoad(CheckMode)
        }

        "go to Summary page when the answer is No and the WhyNoBankStatement question was already answered" in {
          navigator.nextPage(
            IsBankStatementsPage,
            CheckMode,
            emptyUserAnswers
              .set(IsBankStatementsPage, false)
              .flatMap(_.set(WhyNoBankStatementPage, "reason for no bank statement"))
              .success
              .value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the WhyNoBankStatement page" must {
        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(WhyNoBankStatementPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to Summary page when a reason is provided" in {
          navigator.nextPage(
            WhyNoBankStatementPage,
            CheckMode,
            emptyUserAnswers
              .set(IsBankStatementsPage, false)
              .flatMap(_.set(WhyNoBankStatementPage, "reason for no bank statement"))
              .success
              .value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the AccountingPeriodEndDate page" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(AccountingPeriodEndDatePage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {
          navigator.nextPage(
            AccountingPeriodEndDatePage,
            CheckMode,
            emptyUserAnswers
              .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new LocalDate(2020, 10, 1).toDate))(
                MongoDateTimeFormats.localDayMonthWrite
              )
              .success
              .value
          ) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Playback mode" when {
      "attempting to go to any site" must {
        "go to the PageNotFoundController page" in {
          navigator.nextPage(AccountingPeriodEndDatePage, PlaybackMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }
      }
    }
  }
}
