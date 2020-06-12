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
import javax.inject.Inject
import models._
import models.regulators.SelectGoverningDocument.Other
import pages.Page
import pages.regulatorsAndDocuments._
import play.api.mvc.Call

class DocumentsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call =  {

    case SelectGoverningDocumentPage => userAnswers: UserAnswers => selectGoverningDocumentPagePageNav(userAnswers, NormalMode)

    case IsApprovedGoverningDocumentPage => userAnswers: UserAnswers => IsApprovedGoverningDocumentPageNav(userAnswers, NormalMode)

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

  private def selectGoverningDocumentPagePageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(SelectGoverningDocumentPage) match {
    case Some(Other) => routes.IndexController.onPageLoad() // TODO modify once Governing Document name page is created
    case Some(_) => routes.IndexController.onPageLoad()  // TODO modify once Governing Document approved page is created
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def IsApprovedGoverningDocumentPageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(IsApprovedGoverningDocumentPage) match {
    case Some(true) => routes.IndexController.onPageLoad() // TODO modify once Governing Document name page is created
    case Some(false) => routes.IndexController.onPageLoad()  // TODO modify once Governing Document approved page is created
    case _ => routes.SessionExpiredController.onPageLoad()
  }

}
