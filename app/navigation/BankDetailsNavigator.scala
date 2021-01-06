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
import controllers.routes
import javax.inject.Inject
import models._
import pages.Page
import pages.operationsAndFunds._
import play.api.mvc.Call

class BankDetailsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  override val normalRoutes: Page => UserAnswers => Call = {

    case BankDetailsPage => userAnswers: UserAnswers => navigate(userAnswers)

    case BankDetailsSummaryPage => _ => routes.IndexController.onPageLoad(None)

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val checkRouteMap: Page => UserAnswers => Call = {

    case BankDetailsPage => userAnswers: UserAnswers => navigate(userAnswers)

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  private def navigate(userAnswers: UserAnswers): Call = userAnswers.get(BankDetailsPage) match {
    case Some(_) => controllers.operationsAndFunds.routes.BankDetailsSummaryController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad()
  }
}
