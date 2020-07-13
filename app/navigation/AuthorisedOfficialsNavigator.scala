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
import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import controllers.routes
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.authorisedOfficials.{AuthorisedOfficialsDOBPage, AuthorisedOfficialsNamePage}
import play.api.mvc.Call

class AuthorisedOfficialsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call =  {

    case AuthorisedOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNamePage(index)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsDOBPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case AuthorisedOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNamePage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsDOBPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
