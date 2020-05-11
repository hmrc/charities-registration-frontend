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
import controllers.routes
import controllers.eligibility.{routes => elroutes}
import javax.inject.Inject
import models._
import pages.Page
import pages.eligibility._
import play.api.mvc.Call


class EligibilityNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case IsEligiblePurposePage => userAnswers: UserAnswers => navigate(
      userAnswers.get(IsEligiblePurposePage),
      elroutes.IsEligibleAccountController.onPageLoad(),
      elroutes.InEligibleController.onPageLoad()
    )
    case IsEligibleAccountPage => userAnswers: UserAnswers => navigate(
      userAnswers.get(IsEligibleAccountPage),
      elroutes.IsEligibleLocationController.onPageLoad(),
      elroutes.InEligibleController.onPageLoad()
    )
    case IsEligibleLocationPage => userAnswers: UserAnswers => navigate(
      userAnswers.get(IsEligibleLocationPage),
      routes.IndexController.onPageLoad(),
      elroutes.IsEligibleLocationOtherController.onPageLoad()
    )
    case IsEligibleLocationOtherPage =>  userAnswers: UserAnswers => navigate(
        userAnswers.get(IsEligibleLocationOtherPage),
        routes.IndexController.onPageLoad(),
        elroutes.InEligibleController.onPageLoad()
      )
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

  def navigate(value : Option[Boolean], ifTrue:Call, ifFalse:Call): Call ={
    value match{
      case Some(true) => ifTrue
      case Some(false) => ifFalse
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }
}
