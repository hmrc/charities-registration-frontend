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

package navigation

import base.SpecBase
import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import controllers.routes
import models._
import models.operations.{FundRaisingOptions, OperatingLocationOptions}
import org.joda.time.{LocalDate, MonthDay}
import pages.IndexPage
import pages.operationsAndFunds._
class FundRaisingNavigatorSpec extends SpecBase {

  private val navigator: FundRaisingNavigator = inject[FundRaisingNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the FundRaisingPage page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(FundRaisingPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Where does your charity operate page when user answers other than the Other when clicked continue button" in {
          navigator.nextPage(FundRaisingPage, NormalMode,
            emptyUserAnswers.set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations)).getOrElse(emptyUserAnswers)) mustBe
            operationFundsRoutes.OperatingLocationController.onPageLoad(NormalMode)
        }

        "go to the Where does your other fundraising page when user answer has other and clicked continue button" in {
          navigator.nextPage(FundRaisingPage, NormalMode,
            emptyUserAnswers.set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Other)).getOrElse(emptyUserAnswers)) mustBe
            operationFundsRoutes.OtherFundRaisingController.onPageLoad(NormalMode)
        }
      }

      "from the SelectOperatingLocation page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherFundRaisingPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to OperatingLocationPage page when user answers and clicked continue button" in {
          navigator.nextPage(OtherFundRaisingPage, NormalMode,
            emptyUserAnswers.set(OtherFundRaisingPage, "sdf").getOrElse(emptyUserAnswers)) mustBe
            operationFundsRoutes.OperatingLocationController.onPageLoad(NormalMode)
        }


      }

      "from the OperatingLocationPage page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OperatingLocationPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Has your charity prepared financial accounts page when user answers other than the Other when clicked continue button" in {
          navigator.nextPage(OperatingLocationPage, NormalMode,
            emptyUserAnswers.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.EnglandAndWales)).getOrElse(emptyUserAnswers)) mustBe
            operationFundsRoutes.IsFinancialAccountsController.onPageLoad(NormalMode)
        }

        "go to Has your charity prepared financial accounts page when user answer has other and clicked continue button" in {
          navigator.nextPage(OperatingLocationPage, NormalMode,
            emptyUserAnswers.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas)).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad()
        }
      }

      "from the IsFinancialAccountsPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsFinancialAccountsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Estimated income page when yes is selected" in {
          navigator.nextPage(IsFinancialAccountsPage, NormalMode,
            emptyUserAnswers.set(IsFinancialAccountsPage,true).success.value) mustBe
            operationFundsRoutes.EstimatedIncomeController.onPageLoad(NormalMode)
        }

        "go to Estimated income page when no is selected" in {
          navigator.nextPage(IsFinancialAccountsPage, NormalMode,
            emptyUserAnswers.set(IsFinancialAccountsPage,false).success.value) mustBe
            operationFundsRoutes.EstimatedIncomeController.onPageLoad(NormalMode)
        }
      }

      "from the EstimatedIncome page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(EstimatedIncomePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Has bank statements page when a number is provided" in {
          navigator.nextPage(EstimatedIncomePage, NormalMode,
            emptyUserAnswers.set(EstimatedIncomePage, BigDecimal.valueOf(123.12)).success.value) mustBe
            operationFundsRoutes.IsBankStatementsController.onPageLoad(NormalMode)
        }
      }

      "from the IsBankStatements page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsBankStatementsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to accounting period end date page when yes is selected" in {
          navigator.nextPage(IsBankStatementsPage, NormalMode,
            emptyUserAnswers.set(IsBankStatementsPage, true).getOrElse(emptyUserAnswers)) mustBe
           operationFundsRoutes.AccountingPeriodEndDateController.onPageLoad(NormalMode)
        }

        "go to DeadEnd page when no is selected" in {
          navigator.nextPage(IsBankStatementsPage, NormalMode,
            emptyUserAnswers.set(IsBankStatementsPage, false).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad()
        }
      }

      "from the AccountingPeriodEndDdate page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AccountingPeriodEndDatePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

       "go to the summary page when clicked continue button" in {
         navigator.nextPage(AccountingPeriodEndDatePage, NormalMode, emptyUserAnswers.set(AccountingPeriodEndDatePage,
           MonthDay.fromDateFields(new LocalDate(2020, 10, 1).toDate))
         (MongoDateTimeFormats.localDayMonthWrite).success.value) mustBe
           operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(OperationsFundsSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

    "in Check mode" when {

      "from the FundRaisingPage page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(FundRaisingPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {
          navigator.nextPage(FundRaisingPage, CheckMode,
            emptyUserAnswers.set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Donations)).getOrElse(emptyUserAnswers)) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }

        "go to the Where does your charity operate page when user answer has other and clicked continue button" in {
          navigator.nextPage(FundRaisingPage, CheckMode,
            emptyUserAnswers.set(FundRaisingPage, Set[FundRaisingOptions](FundRaisingOptions.Other)).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad()
        }
      }


      "from the OtherFundRaising page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherFundRaisingPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to OperationsFundsSummaryController Page page when user answers and clicked continue button" in {
          navigator.nextPage(OtherFundRaisingPage, CheckMode,
            emptyUserAnswers.set(OtherFundRaisingPage,"123").getOrElse(emptyUserAnswers)) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }
      "from the OperatingLocationPage page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OperatingLocationPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {
          navigator.nextPage(OperatingLocationPage, CheckMode,
            emptyUserAnswers.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.EnglandAndWales)).getOrElse(emptyUserAnswers)) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }

        "go to Has your charity prepared financial accounts page when user answer has other and clicked continue button" in {
          navigator.nextPage(OperatingLocationPage, CheckMode,
            emptyUserAnswers.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.Overseas)).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad()
        }
      }

      "from the EstimatedIncome page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(EstimatedIncomePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Has bank statements page when a number is provided" in {
          navigator.nextPage(EstimatedIncomePage, CheckMode,
            emptyUserAnswers.set(EstimatedIncomePage, BigDecimal.valueOf(123.12)).success.value) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }
      "from the AccountingPeriodEndDdate page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AccountingPeriodEndDatePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {
          navigator.nextPage(AccountingPeriodEndDatePage, CheckMode, emptyUserAnswers.set(AccountingPeriodEndDatePage,
            MonthDay.fromDateFields(new LocalDate(2020, 10, 1).toDate))
          (MongoDateTimeFormats.localDayMonthWrite).success.value) mustBe
            operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

    "in Playback mode" when {
      "attempting to go to any site" must {
        "go to the SessionExpiredController page" in {
          navigator.nextPage(AccountingPeriodEndDatePage, PlaybackMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }
  }
}
