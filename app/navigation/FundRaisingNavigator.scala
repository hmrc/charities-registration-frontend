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

import config.FrontendAppConfig
import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import controllers.routes
import javax.inject.Inject
import models._
import pages.Page
import pages.operationsAndFunds.{FundRaisingPage, IsBankStatementsPage, IsFinancialAccountsPage, OperatingLocationPage}
import play.api.mvc.Call

class FundRaisingNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call =  {

    case FundRaisingPage => userAnswers: UserAnswers => userAnswers.get(FundRaisingPage) match {
      case Some(_) => operationFundsRoutes.OperatingLocationController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OperatingLocationPage => userAnswers: UserAnswers => userAnswers.get(OperatingLocationPage) match {
      case Some(_) => operationFundsRoutes.IsFinancialAccountsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsFinancialAccountsPage => userAnswers: UserAnswers => userAnswers.get(IsFinancialAccountsPage) match {
      case Some(_) => operationFundsRoutes.IsBankStatementsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsBankStatementsPage => userAnswers: UserAnswers => userAnswers.get(IsBankStatementsPage) match {
      case Some(_) => routes.IndexController.onPageLoad() // TODO modify once Accounting period end date page created
      case _ => routes.SessionExpiredController.onPageLoad()
    }


    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case _ => _ => routes.IndexController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

}