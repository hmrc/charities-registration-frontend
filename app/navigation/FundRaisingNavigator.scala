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

package navigation

import config.FrontendAppConfig
import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import controllers.routes
import javax.inject.Inject
import models.MongoDateTimeFormats.localDayMonthRead
import models._
import models.operations.FundRaisingOptions.Other
import models.operations.OperatingLocationOptions.Overseas
import pages.Page
import pages.operationsAndFunds._
import play.api.mvc.Call

class FundRaisingNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  def overseasRedirect(userAnswers: UserAnswers, mode: Mode): Call = {
    val result = for(i <- 0 to 4) yield userAnswers.get(WhatCountryDoesTheCharityOperateInPage(i))
    if(result.filter(_.nonEmpty).flatten.nonEmpty) {
      operationFundsRoutes.OverseasOperatingLocationSummaryController.onPageLoad(mode)
    } else {
      operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(mode, Index(0))
    }
  }

  override val normalRoutes: Page => UserAnswers => Call = {

    case FundRaisingPage => userAnswers: UserAnswers => userAnswers.get(FundRaisingPage) match {
      case Some(items) if items.toSeq.contains(Other) => operationFundsRoutes.OtherFundRaisingController.onPageLoad(NormalMode)
      //case Some(_) => operationFundsRoutes.OperatingLocationController.onPageLoad(NormalMode)
      case Some(_) => operationFundsRoutes.CharityEstablishedInController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case CharityEstablishedInPage => userAnswers: UserAnswers => userAnswers.get(CharityEstablishedInPage) match {
      case Some(_) => operationFundsRoutes.OperatingLocationController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OperatingLocationPage => userAnswers: UserAnswers => userAnswers.get(OperatingLocationPage) match {
      case Some(items) if !items.toSeq.contains(Overseas) => operationFundsRoutes.IsFinancialAccountsController.onPageLoad(NormalMode)
      case Some(_) => overseasRedirect(userAnswers, NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case WhatCountryDoesTheCharityOperateInPage(_) => userAnswers: UserAnswers => userAnswers.get(OverseasOperatingLocationSummaryPage) match {
      case Some(_) =>
        overseasRedirect(userAnswers, NormalMode)
      case _ if userAnswers.get(WhatCountryDoesTheCharityOperateInPage(Index(0))).isDefined=>
        operationFundsRoutes.OverseasOperatingLocationSummaryController.onPageLoad(NormalMode)
      case _ if userAnswers.get(OperatingLocationPage).isDefined =>
        operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(NormalMode, Index(0))
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OverseasOperatingLocationSummaryPage => userAnswers: UserAnswers =>
      userAnswers.get(WhatCountryDoesTheCharityOperateInPage(Index(0))) match {
        case None => userAnswers.get(OperatingLocationPage) match {
          case Some(_) => operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(NormalMode, Index(0))
          case _ => routes.SessionExpiredController.onPageLoad()
        }
        case _ =>
          userAnswers.get(OverseasOperatingLocationSummaryPage) match {
          case Some(true) =>
            val result = for(i <- 0 to 4) yield userAnswers.get(WhatCountryDoesTheCharityOperateInPage(i))
            val index = result.filter(_.nonEmpty).flatten.length

            index match {
              case lessThanFive if lessThanFive < 5 => operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(NormalMode, Index(index))
              case _ => operationFundsRoutes.IsFinancialAccountsController.onPageLoad(NormalMode)
            }

          case Some(false) => operationFundsRoutes.IsFinancialAccountsController.onPageLoad(NormalMode)

          case _ => routes.SessionExpiredController.onPageLoad()
          }
        }

    case IsFinancialAccountsPage => userAnswers: UserAnswers => userAnswers.get(IsFinancialAccountsPage) match {
      case Some(_) => operationFundsRoutes.EstimatedIncomeController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case EstimatedIncomePage => userAnswers: UserAnswers => userAnswers.get(EstimatedIncomePage) match {
      case Some(_) => operationFundsRoutes.ActualIncomeController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case ActualIncomePage => userAnswers: UserAnswers => userAnswers.get(ActualIncomePage) match {
      case Some(_) => operationFundsRoutes.IsBankStatementsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsBankStatementsPage => userAnswers: UserAnswers => userAnswers.get(IsBankStatementsPage) match {
      case Some(true) => operationFundsRoutes.AccountingPeriodEndDateController.onPageLoad(NormalMode)
      case Some(false) => operationFundsRoutes.WhyNoBankStatementController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case WhyNoBankStatementPage => userAnswers: UserAnswers => userAnswers.get(IsBankStatementsPage) match {
      case Some(_) => operationFundsRoutes.AccountingPeriodEndDateController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AccountingPeriodEndDatePage => userAnswers: UserAnswers => userAnswers.get(AccountingPeriodEndDatePage) match {
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OtherFundRaisingPage => userAnswer: UserAnswers => userAnswer.get(OtherFundRaisingPage) match{
      case Some(_) => operationFundsRoutes.CharityEstablishedInController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OperationsFundsSummaryPage => _ => routes.IndexController.onPageLoad(None)

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val checkRouteMap: Page => UserAnswers => Call = {

    case FundRaisingPage => userAnswers: UserAnswers => userAnswers.get(FundRaisingPage) match {
      case Some(items) if items.toSeq.contains(Other) => userAnswers.get(OtherFundRaisingPage) match {
        case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        case _ => operationFundsRoutes.OtherFundRaisingController.onPageLoad(CheckMode)
      }
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case CharityEstablishedInPage => userAnswers: UserAnswers => userAnswers.get(CharityEstablishedInPage) match {
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OtherFundRaisingPage => userAnswers: UserAnswers => userAnswers.get(OtherFundRaisingPage)match{
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OperatingLocationPage => userAnswers: UserAnswers => userAnswers.get(OperatingLocationPage) match {
      case Some(items) if items.toSeq.contains(Overseas) => userAnswers.get(WhatCountryDoesTheCharityOperateInPage(0)) match {
        case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        case _ => operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, Index(0))
      }
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case WhatCountryDoesTheCharityOperateInPage(_) => userAnswers: UserAnswers => userAnswers.get(OverseasOperatingLocationSummaryPage) match {
      case Some(_) =>
        overseasRedirect(userAnswers, CheckMode)
      case _ if userAnswers.get(WhatCountryDoesTheCharityOperateInPage(Index(0))).isDefined=>
        operationFundsRoutes.OverseasOperatingLocationSummaryController.onPageLoad(CheckMode)
      case _ if userAnswers.get(OperatingLocationPage).isDefined =>
        operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, Index(0))
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OverseasOperatingLocationSummaryPage => userAnswers: UserAnswers => userAnswers.get(OverseasOperatingLocationSummaryPage) match {
      case Some(true) =>
        val result = for(i <- 0 to 4) yield userAnswers.get(WhatCountryDoesTheCharityOperateInPage(i))
        if(result.flatten.length < 5) {
          operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, Index(result.flatten.length))
        } else {
          operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
        }
      case Some(false) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ if userAnswers.get(OperatingLocationPage).isDefined =>
        operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onPageLoad(CheckMode, Index(0))
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AccountingPeriodEndDatePage => userAnswers: UserAnswers => userAnswers.get(AccountingPeriodEndDatePage) match {
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsFinancialAccountsPage => userAnswers: UserAnswers => userAnswers.get(IsFinancialAccountsPage) match {
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case EstimatedIncomePage => userAnswers: UserAnswers => userAnswers.get(EstimatedIncomePage) match {
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case ActualIncomePage => userAnswers: UserAnswers => userAnswers.get(ActualIncomePage) match {
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsBankStatementsPage => userAnswers: UserAnswers => userAnswers.get(IsBankStatementsPage) match {
      case Some(true) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case Some(false) if userAnswers.get(WhyNoBankStatementPage).isDefined => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case Some(false) => operationFundsRoutes.WhyNoBankStatementController.onPageLoad(CheckMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case WhyNoBankStatementPage => userAnswers: UserAnswers => userAnswers.get(IsBankStatementsPage) match {
      case Some(_) => operationFundsRoutes.OperationsFundsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

}
