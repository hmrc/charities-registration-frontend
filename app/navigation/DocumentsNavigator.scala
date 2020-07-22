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
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
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

    case WhenGoverningDocumentApprovedPage => userAnswers: UserAnswers => whenGoverningDocumentApprovedPageNav(userAnswers, NormalMode)

    case IsApprovedGoverningDocumentPage => userAnswers: UserAnswers => isApprovedGoverningDocumentPageNav(userAnswers, NormalMode)

    case GoverningDocumentSummaryPage => _ => routes.IndexController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case WhenGoverningDocumentApprovedPage => userAnswers: UserAnswers => whenGoverningDocumentApprovedPageNav(userAnswers, CheckMode)

    case _ => _ => routes.IndexController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

  private def selectGoverningDocumentPagePageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(SelectGoverningDocumentPage) match {
    case Some(Other) => routes.DeadEndController.onPageLoad() // TODO modify once Governing Document name page is created
    case Some(_) => regulatorDocsRoutes.WhenGoverningDocumentApprovedController.onPageLoad(mode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def whenGoverningDocumentApprovedPageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(WhenGoverningDocumentApprovedPage) match {
    case Some(_) if mode == CheckMode => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case Some(_) => regulatorDocsRoutes.IsApprovedGoverningDocumentController.onPageLoad(mode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def isApprovedGoverningDocumentPageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(IsApprovedGoverningDocumentPage) match {
    case Some(true) => routes.DeadEndController.onPageLoad() // TODO modify once Have you changed governing document page is created
    case Some(false) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad()
  }

}
