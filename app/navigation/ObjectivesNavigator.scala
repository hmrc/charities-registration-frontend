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
import controllers.operationsAndFunds.{routes => operations}
import controllers.routes
import javax.inject.Inject
import models._
import models.operations.CharitablePurposes.Other
import pages.Page
import pages.operationsAndFunds.{CharitableObjectivesPage, CharitablePurposesPage, PublicBenefitsPage}
import play.api.mvc.Call

class ObjectivesNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  override val normalRoutes: Page => UserAnswers => Call = {

    case CharitableObjectivesPage => userAnswers: UserAnswers => userAnswers.get(CharitableObjectivesPage) match {
      case Some(_) => operations.CharitablePurposesController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case CharitablePurposesPage => userAnswers: UserAnswers => userAnswers.get(CharitablePurposesPage) match {
      case Some(_) => operations.PublicBenefitsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case PublicBenefitsPage => userAnswers: UserAnswers => userAnswers.get(PublicBenefitsPage) match {
      case Some(_) => operations.CharityObjectivesSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val checkRouteMap: Page => UserAnswers => Call = {

    case CharitableObjectivesPage => userAnswers: UserAnswers => userAnswers.get(CharitableObjectivesPage) match {
      case Some(_) => operations.CharityObjectivesSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case CharitablePurposesPage => userAnswers: UserAnswers => userAnswers.get(CharitablePurposesPage) match {
      case Some(_) => operations.CharityObjectivesSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case PublicBenefitsPage => userAnswers: UserAnswers => userAnswers.get(PublicBenefitsPage) match {
      case Some(_) => operations.CharityObjectivesSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

}
