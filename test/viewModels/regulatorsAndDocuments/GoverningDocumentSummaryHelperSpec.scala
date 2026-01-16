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

package viewModels.regulatorsAndDocuments

import base.SpecBase
import base.data.messages.BaseMessages
import controllers.regulatorsAndDocuments.routes as regulatorDocsRoutes
import models.regulators.SelectGoverningDocument
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.{CheckMode, UserAnswers}
import pages.regulatorsAndDocuments.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.SummaryListRowHelper
import viewmodels.regulatorsAndDocuments.GoverningDocumentSummaryHelper

import java.time.LocalDate
import utils.ImplicitDateFormatter

class GoverningDocumentSummaryHelperSpec extends SpecBase with SummaryListRowHelper with ImplicitDateFormatter {

  private val helper = new GoverningDocumentSummaryHelper(
    UserAnswers("id")
      .set(SelectGoverningDocumentPage, SelectGoverningDocument.values.head)
      .flatMap(_.set(GoverningDocumentNamePage, governingDocument))
      .flatMap(_.set(WhenGoverningDocumentApprovedPage, govDocApprovedDate))
      .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
      .flatMap(_.set(SectionsChangedGoverningDocumentPage, "Governing document change"))
      .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, true))
      .success
      .value
  )

  "Check Your Answers Helper" must {

    "For the selectGoverningDocument answer" must {

      "have a correctly formatted summary list row when one added" in {

        helper.selectGoverningDocumentRow mustBe Some(
          summaryListRow(
            messages("selectGoverningDocument.checkYourAnswersLabel"),
            HtmlContent(messages(s"selectGoverningDocument.$MemorandumArticlesAssociation")),
            Some(messages("selectGoverningDocument.checkYourAnswersLabel")),
            regulatorDocsRoutes.SelectGoverningDocumentController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the GoverningDocumentName answer" must {

      "have a correctly formatted summary list row when one added" in {

        helper.whatIsTheGoverningDocumentNameRow mustBe Some(
          summaryListRow(
            messages("governingDocumentName.checkYourAnswersLabel"),
            HtmlContent(governingDocument),
            Some(messages("governingDocumentName.checkYourAnswersLabel")),
            regulatorDocsRoutes.GoverningDocumentNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the WhenGoverningDocumentApproved answer" must {

      "have a correctly formatted summary list row" in {

        helper.dateApprovedGoverningDocumentRow mustBe Some(
          summaryListRow(
            messages("whenGoverningDocumentApproved.checkYourAnswersLabel"),
            HtmlContent(dayToString(govDocApprovedDate, dayOfWeek = false)),
            Some(messages("whenGoverningDocumentApproved.checkYourAnswersLabel")),
            regulatorDocsRoutes.WhenGoverningDocumentApprovedController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the IsApprovedGoverningDocument answer" must {

      "have a correctly formatted summary list row" in {

        helper.isApprovedGoverningDocumentRow mustBe Some(
          summaryListRow(
            messages("isApprovedGoverningDocument.checkYourAnswersLabel"),
            HtmlContent(BaseMessages.yes),
            Some(messages("isApprovedGoverningDocument.checkYourAnswersLabel")),
            regulatorDocsRoutes.IsApprovedGoverningDocumentController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the HasCharityChangedPartsOfGoverningDocument answer" must {

      "have a correctly formatted summary list row" in {

        helper.hasCharityChangedPartsOfGoverningDocumentRow mustBe Some(
          summaryListRow(
            messages("hasCharityChangedPartsOfGoverningDocument.checkYourAnswersLabel"),
            HtmlContent(BaseMessages.yes),
            Some(messages("hasCharityChangedPartsOfGoverningDocument.checkYourAnswersLabel")),
            regulatorDocsRoutes.HasCharityChangedPartsOfGoverningDocumentController.onPageLoad(
              CheckMode
            ) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the SectionsChangedGoverningDocument answer" must {

      "have a correctly formatted summary list row" in {

        helper.sectionsChangedGoverningDocumentRow mustBe Some(
          summaryListRow(
            messages("sectionsChangedGoverningDocument.checkYourAnswersLabel"),
            HtmlContent("Governing document change"),
            Some(messages("sectionsChangedGoverningDocument.checkYourAnswersLabel")),
            regulatorDocsRoutes.SectionsChangedGoverningDocumentController.onPageLoad(
              CheckMode
            ) -> BaseMessages.changeLink
          )
        )
      }
    }

  }
}
