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
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import controllers.routes
import javax.inject.Inject
import models._
import models.regulators.SelectGoverningDocument.Other
import pages.Page
import pages.regulatorsAndDocuments._
import play.api.mvc.Call

class DocumentsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  override val normalRoutes: Page => UserAnswers => Call = {

    case SelectGoverningDocumentPage => userAnswers: UserAnswers => selectGoverningDocumentPagePageNav(userAnswers, NormalMode)

    case GoverningDocumentNamePage => userAnswers: UserAnswers => governingDocumentNamePageNav(userAnswers, NormalMode)

    case WhenGoverningDocumentApprovedPage => userAnswers: UserAnswers => whenGoverningDocumentApprovedPageNav(userAnswers, NormalMode)

    case IsApprovedGoverningDocumentPage => userAnswers: UserAnswers => isApprovedGoverningDocumentPageNav(userAnswers, NormalMode)

    case HasCharityChangedPartsOfGoverningDocumentPage => userAnswers: UserAnswers => hasCharityChangedPartsOfGoverningDocumentPageNav(userAnswers, NormalMode)

    case SectionsChangedGoverningDocumentPage => userAnswers: UserAnswers => sectionsChangedGoverningDocumentPageNav(userAnswers, NormalMode)

    case GoverningDocumentSummaryPage => _ => routes.IndexController.onPageLoad(None)

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val checkRouteMap: Page => UserAnswers => Call = {

    case SelectGoverningDocumentPage => userAnswers: UserAnswers => selectGoverningDocumentPagePageNav(userAnswers, CheckMode)

    case GoverningDocumentNamePage => userAnswers: UserAnswers => governingDocumentNamePageNav(userAnswers, CheckMode)

    case WhenGoverningDocumentApprovedPage => userAnswers: UserAnswers => whenGoverningDocumentApprovedPageNav(userAnswers, CheckMode)

    case IsApprovedGoverningDocumentPage => userAnswers: UserAnswers => isApprovedGoverningDocumentPageNav(userAnswers, CheckMode)

    case HasCharityChangedPartsOfGoverningDocumentPage => userAnswers: UserAnswers => hasCharityChangedPartsOfGoverningDocumentPageNav(userAnswers, CheckMode)

    case SectionsChangedGoverningDocumentPage => userAnswers: UserAnswers => sectionsChangedGoverningDocumentPageNav(userAnswers, CheckMode)

    case _ => _ => routes.IndexController.onPageLoad(None)

  }

  private def selectGoverningDocumentPagePageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(SelectGoverningDocumentPage) match {
    case Some(Other) if mode == CheckMode => userAnswers.get(GoverningDocumentNamePage) match {
      case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
      case _ => regulatorDocsRoutes.GoverningDocumentNameController.onPageLoad(mode)
    }
    case Some(Other) => regulatorDocsRoutes.GoverningDocumentNameController.onPageLoad(mode)
    case Some(_) if mode == CheckMode => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case Some(_) => regulatorDocsRoutes.WhenGoverningDocumentApprovedController.onPageLoad(mode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def governingDocumentNamePageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(GoverningDocumentNamePage) match {
    case Some(_) if mode == CheckMode => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case Some(_) => regulatorDocsRoutes.WhenGoverningDocumentApprovedController.onPageLoad(mode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def whenGoverningDocumentApprovedPageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(WhenGoverningDocumentApprovedPage) match {
    case Some(_) if mode == CheckMode => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case Some(_) => regulatorDocsRoutes.IsApprovedGoverningDocumentController.onPageLoad(mode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def isApprovedGoverningDocumentPageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(IsApprovedGoverningDocumentPage) match {
    case Some(true) if mode == CheckMode && userAnswers.get(HasCharityChangedPartsOfGoverningDocumentPage).isDefined =>
      regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case Some(true) => regulatorDocsRoutes.HasCharityChangedPartsOfGoverningDocumentController.onPageLoad(mode)
    case Some(false) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def hasCharityChangedPartsOfGoverningDocumentPageNav(userAnswers: UserAnswers, mode: Mode): Call =
    userAnswers.get(HasCharityChangedPartsOfGoverningDocumentPage) match {
    case Some(true) if mode == CheckMode && userAnswers.get(SectionsChangedGoverningDocumentPage).isDefined =>
      regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case Some(true) => regulatorDocsRoutes.SectionsChangedGoverningDocumentController.onPageLoad(mode)
    case Some(false) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def sectionsChangedGoverningDocumentPageNav(userAnswers: UserAnswers, mode: Mode): Call =
    userAnswers.get(SectionsChangedGoverningDocumentPage) match {
    case Some(_) if mode == CheckMode => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad()
  }

}
