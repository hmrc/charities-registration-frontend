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

package viewModels.regulatorsAndDocuments

import java.time.LocalDate

import assets.messages.BaseMessages
import base.SpecBase
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import models.regulators.SelectGoverningDocument
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.{CheckMode, UserAnswers}
import pages.regulatorsAndDocuments._
import viewmodels.SummaryListRowHelper
import viewmodels.regulatorsAndDocuments.GoverningDocumentSummaryHelper

class GoverningDocumentSummaryHelperSpec extends SpecBase with SummaryListRowHelper {


  private val helper = new GoverningDocumentSummaryHelper(UserAnswers("id")
    .set(SelectGoverningDocumentPage, SelectGoverningDocument.values.head).flatMap
  (_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2000, 1, 2))).flatMap
  (_.set(IsApprovedGoverningDocumentPage,true)).success.value
  )

  "Check Your Answers Helper" must {

    "For the selectGoverningDocument answer" must {

      "have a correctly formatted summary list row when one added" in {

        helper.selectGoverningDocumentRow mustBe Some(summaryListRow(
          messages("selectGoverningDocument.checkYourAnswersLabel"),
          messages(s"selectGoverningDocument.$MemorandumArticlesAssociation"),
          Some(messages("selectGoverningDocument.checkYourAnswersLabel")),
          regulatorDocsRoutes.SelectGoverningDocumentController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }
    "For the WhenGoverningDocumentApproved answer" must {

      "have a correctly formatted summary list row" in {

        helper.dateApprovedGoverningDocumentRow mustBe Some(summaryListRow(
          messages("whenGoverningDocumentApproved.checkYourAnswersLabel"),
          "2 January 2000",
          Some(messages("whenGoverningDocumentApproved.checkYourAnswersLabel")),
          regulatorDocsRoutes.WhenGoverningDocumentApprovedController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the IsApprovedGoverningDocument answer" must {

      "have a correctly formatted summary list row" in {

        helper.isApprovedGoverningDocumentRow  mustBe Some(summaryListRow(
          messages("isApprovedGoverningDocument.checkYourAnswersLabel"),
          BaseMessages.yes,
          Some(messages("isApprovedGoverningDocument.checkYourAnswersLabel")),
          regulatorDocsRoutes.IsApprovedGoverningDocumentController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }
  }
}
