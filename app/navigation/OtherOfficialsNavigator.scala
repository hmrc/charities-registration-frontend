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
import controllers.otherOfficials.{routes => otherOfficialRoutes}
import controllers.routes
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.otherOfficials.{OtherOfficialsDOBPage, OtherOfficialsNamePage, OtherOfficialsPositionPage}
import play.api.mvc.Call

class OtherOfficialsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call =  {

    case OtherOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNamePage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsDOBController.onPageLoad(NormalMode, index) // Todo OtherOfficialsDOB page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsDOBPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsPositionController.onPageLoad(NormalMode, index) // Todo phone number page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPositionPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO National Insurance number page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case OtherOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNamePage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsDOBPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPositionPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
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
