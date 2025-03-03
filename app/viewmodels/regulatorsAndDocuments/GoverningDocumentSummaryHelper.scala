/*
 * Copyright 2025 HM Revenue & Customs
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

package viewmodels.regulatorsAndDocuments

import controllers.regulatorsAndDocuments.{routes => documentsRoutes}
import pages.regulatorsAndDocuments._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}
import models._

class GoverningDocumentSummaryHelper(override val userAnswers: UserAnswers)(implicit val messages: Messages)
    extends ImplicitDateFormatter
    with CheckYourAnswersHelper
    with SummaryListRowHelper {

  def selectGoverningDocumentRow: Option[SummaryListRow] =
    answer(
      SelectGoverningDocumentPage,
      documentsRoutes.SelectGoverningDocumentController.onPageLoad(CheckMode),
      answerIsMsgKey = true
    )

  def whatIsTheGoverningDocumentNameRow: Option[SummaryListRow] =
    answer(GoverningDocumentNamePage, documentsRoutes.GoverningDocumentNameController.onPageLoad(CheckMode))

  def dateApprovedGoverningDocumentRow: Option[SummaryListRow] =
    answer(
      WhenGoverningDocumentApprovedPage,
      documentsRoutes.WhenGoverningDocumentApprovedController.onPageLoad(CheckMode)
    )

  def isApprovedGoverningDocumentRow: Option[SummaryListRow] =
    answer(IsApprovedGoverningDocumentPage, documentsRoutes.IsApprovedGoverningDocumentController.onPageLoad(CheckMode))

  def hasCharityChangedPartsOfGoverningDocumentRow: Option[SummaryListRow] =
    answer(
      HasCharityChangedPartsOfGoverningDocumentPage,
      documentsRoutes.HasCharityChangedPartsOfGoverningDocumentController.onPageLoad(CheckMode)
    )

  def sectionsChangedGoverningDocumentRow: Option[SummaryListRow] =
    textBoxAnswer(
      SectionsChangedGoverningDocumentPage,
      documentsRoutes.SectionsChangedGoverningDocumentController.onPageLoad(CheckMode)
    )

  val rows: Seq[SummaryListRow] = Seq(
    selectGoverningDocumentRow,
    whatIsTheGoverningDocumentNameRow,
    dateApprovedGoverningDocumentRow,
    isApprovedGoverningDocumentRow,
    hasCharityChangedPartsOfGoverningDocumentRow,
    sectionsChangedGoverningDocumentRow
  ).flatten

}
