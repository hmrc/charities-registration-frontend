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

import models.UserAnswers
import models.operations.FundRaisingOptions
import models.operations.OperatingLocationOptions.Overseas
import pages.QuestionPage
import pages.operationsAndFunds._
import viewmodels.StatusHelper

object OperationsFundsStatusHelper extends StatusHelper {

  private val common: Seq[QuestionPage[_]] = Seq(
    FundRaisingPage,
    CharityEstablishedInPage,
    OperatingLocationPage,
    IsFinancialAccountsPage,
    EstimatedIncomePage,
    ActualIncomePage,
    IsBankStatementsPage,
    AccountingPeriodEndDatePage
  )

  private val allPages: Seq[QuestionPage[_]] = common ++ Seq(
    OtherFundRaisingPage,
    WhyNoBankStatementPage,
    OverseasOperatingLocationSummaryPage,
    OverseasCountriesPage
  )

  private val f1 = (list: Seq[QuestionPage[_]], isOtherFundRaising: Boolean) => if(isOtherFundRaising) list ++ Seq(OtherFundRaisingPage) else list

  private val f2 = (list: Seq[QuestionPage[_]], isOverseas: Boolean) => {
    if(isOverseas) list ++ Seq(OverseasOperatingLocationSummaryPage, OverseasCountriesPage) else list
  }

  private val f3 = (list: Seq[QuestionPage[_]]) => list ++ Seq(WhyNoBankStatementPage)

  override def checkComplete(userAnswers: UserAnswers): Boolean = {

    def noAdditionalPagesDefined(list: Seq[QuestionPage[_]]): Boolean = userAnswers.unneededPagesNotPresent(list, allPages)

    userAnswers.get(FundRaisingPage) match {
      case Some(fundRaisingOptions) =>
        val isFundRaising = fundRaisingOptions.contains(FundRaisingOptions.Other)

        userAnswers.get(OperatingLocationPage) match {
          case Some(locations) =>
            val isOverseas = locations.toList.contains(Overseas)

            userAnswers.get(IsBankStatementsPage) match {
              case Some(false) =>
                val newPages = f3(f2(f1(common, isFundRaising), isOverseas))
                userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages)
              case _ =>
                val newPages = f2(f1(common, isFundRaising), isOverseas)
                userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages)
            }
          case _ =>
            false
        }
      case _ => false
    }
  }

}
