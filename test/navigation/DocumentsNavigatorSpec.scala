/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import controllers.routes
import models._
import models.regulators.SelectGoverningDocument
import pages.IndexPage
import pages.regulatorsAndDocuments._

import java.time.LocalDate

class DocumentsNavigatorSpec extends SpecBase {

  private val navigator: DocumentsNavigator = inject[DocumentsNavigator]

  private val year       = 2020
  private val month      = 1
  private val dayOfMonth = 1

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the SelectGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(SelectGoverningDocumentPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Governing Document name page when Other is selected" in {
          navigator.nextPage(
            SelectGoverningDocumentPage,
            NormalMode,
            emptyUserAnswers.set(SelectGoverningDocumentPage, SelectGoverningDocument.Other).success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentNameController.onPageLoad(NormalMode)
        }

        "go to the Governing Document approved page when other than Other is selected" in {
          navigator.nextPage(
            SelectGoverningDocumentPage,
            NormalMode,
            emptyUserAnswers.set(SelectGoverningDocumentPage, SelectGoverningDocument.Will).success.value
          ) mustBe
            regulatorDocsRoutes.WhenGoverningDocumentApprovedController.onPageLoad(NormalMode)
        }
      }

      "from the NameOfTheGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(GoverningDocumentNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Governing Document approved page when Other is selected" in {
          navigator.nextPage(
            GoverningDocumentNamePage,
            NormalMode,
            emptyUserAnswers.set(GoverningDocumentNamePage, "will").success.value
          ) mustBe
            regulatorDocsRoutes.WhenGoverningDocumentApprovedController.onPageLoad(NormalMode)
        }
      }

      "from the WhenGoverningDocumentApproved" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(WhenGoverningDocumentApprovedPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Is Governing Document approved page when a date is submitted" in {

          navigator.nextPage(
            WhenGoverningDocumentApprovedPage,
            NormalMode,
            emptyUserAnswers
              .set(WhenGoverningDocumentApprovedPage, LocalDate.of(year, month, dayOfMonth))(
                MongoDateTimeFormats.localDateWrites
              )
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.IsApprovedGoverningDocumentController.onPageLoad(NormalMode)
        }
      }

      "from the IsApprovedGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(IsApprovedGoverningDocumentPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the HasCharityChangedPartsofGoverningDocument page when yes is selected" in {
          navigator.nextPage(
            IsApprovedGoverningDocumentPage,
            NormalMode,
            emptyUserAnswers.set(IsApprovedGoverningDocumentPage, true).success.value
          ) mustBe
            regulatorDocsRoutes.HasCharityChangedPartsOfGoverningDocumentController.onPageLoad(NormalMode)
        }

        "go to the Governing Document summary page when no is selected" in {
          navigator.nextPage(
            IsApprovedGoverningDocumentPage,
            NormalMode,
            emptyUserAnswers.set(IsApprovedGoverningDocumentPage, false).success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from the HasCharityChangedPartsofGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(HasCharityChangedPartsOfGoverningDocumentPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the SectionsChangedGoverningDocument page when yes is selected" in {

          navigator.nextPage(
            HasCharityChangedPartsOfGoverningDocumentPage,
            NormalMode,
            emptyUserAnswers.set(HasCharityChangedPartsOfGoverningDocumentPage, true).success.value
          ) mustBe
            regulatorDocsRoutes.SectionsChangedGoverningDocumentController.onPageLoad(NormalMode)
        }

        "go to the Governing Document summary page when no is selected" in {
          navigator.nextPage(
            HasCharityChangedPartsOfGoverningDocumentPage,
            NormalMode,
            emptyUserAnswers.set(HasCharityChangedPartsOfGoverningDocumentPage, false).success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from the SectionsChangedGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(SectionsChangedGoverningDocumentPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Governing Document summary page when continue is clicked" in {
          navigator.nextPage(
            SectionsChangedGoverningDocumentPage,
            NormalMode,
            emptyUserAnswers.set(SectionsChangedGoverningDocumentPage, "abcd").success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(GoverningDocumentSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }

      "from any Unknown page" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Check mode" when {

      "from the SelectGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(SelectGoverningDocumentPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Governing Document name page when Other is selected and Name is not defined" in {
          navigator.nextPage(
            SelectGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers.set(SelectGoverningDocumentPage, SelectGoverningDocument.Other).success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentNameController.onPageLoad(CheckMode)
        }

        "go to the Governing Document summary page when Other is selected and Name is defined" in {
          navigator.nextPage(
            SelectGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers
              .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
              .flatMap(_.set(GoverningDocumentNamePage, "other"))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }

        "go to the Governing Document Name page when other than Other is selected" in {
          navigator.nextPage(
            SelectGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers.set(SelectGoverningDocumentPage, SelectGoverningDocument.Will).success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from the NameOfTheGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(GoverningDocumentNamePage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Governing Document summary page when a name is submitted" in {
          navigator.nextPage(
            GoverningDocumentNamePage,
            CheckMode,
            emptyUserAnswers.set(GoverningDocumentNamePage, "will").success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from the WhenGoverningDocumentApproved" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(WhenGoverningDocumentApprovedPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Is Governing Document summary page when a date is submitted" in {

          navigator.nextPage(
            WhenGoverningDocumentApprovedPage,
            CheckMode,
            emptyUserAnswers
              .set(WhenGoverningDocumentApprovedPage, LocalDate.of(year, month, dayOfMonth))(
                MongoDateTimeFormats.localDateWrites
              )
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from the IsApprovedGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(IsApprovedGoverningDocumentPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the HasCharityChangedPartsofGoverningDocument page when yes is selected" in {

          navigator.nextPage(
            IsApprovedGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers.set(IsApprovedGoverningDocumentPage, true).success.value
          ) mustBe
            regulatorDocsRoutes.HasCharityChangedPartsOfGoverningDocumentController.onPageLoad(CheckMode)
        }

        "go to the HasCharityChangedPartsofGoverningDocument page when yes is selected and HasCharityChangedPartsOfGoverningDocumentPage already answered" in {

          navigator.nextPage(
            IsApprovedGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers
              .set(IsApprovedGoverningDocumentPage, true)
              .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }

        "go to the Governing Document summary page when no is selected" in {
          navigator.nextPage(
            IsApprovedGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers.set(IsApprovedGoverningDocumentPage, false).success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from the HasCharityChangedPartsofGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(HasCharityChangedPartsOfGoverningDocumentPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the SectionsChangedGoverningDocument page when yes is selected" in {

          navigator.nextPage(
            HasCharityChangedPartsOfGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers.set(HasCharityChangedPartsOfGoverningDocumentPage, true).success.value
          ) mustBe
            regulatorDocsRoutes.SectionsChangedGoverningDocumentController.onPageLoad(CheckMode)
        }

        "go to the Governing Document summary page when yes is selected and SectionsChangedGoverningDocumentPage has been already answered" in {

          navigator.nextPage(
            HasCharityChangedPartsOfGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers
              .set(HasCharityChangedPartsOfGoverningDocumentPage, true)
              .flatMap(_.set(SectionsChangedGoverningDocumentPage, "Section change"))
              .success
              .value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }

        "go to the Governing Document summary page when no is selected" in {
          navigator.nextPage(
            HasCharityChangedPartsOfGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers.set(HasCharityChangedPartsOfGoverningDocumentPage, false).success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from the SectionsChangedGoverningDocument" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(SectionsChangedGoverningDocumentPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Governing Document summary page when continue is clicked" in {
          navigator.nextPage(
            SectionsChangedGoverningDocumentPage,
            CheckMode,
            emptyUserAnswers.set(SectionsChangedGoverningDocumentPage, "abcd").success.value
          ) mustBe
            regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
        }
      }

      "from any Unknown page" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Playback mode" when {
      "attempting to go to any site" must {
        "go to the PageNotFoundController page" in {
          navigator.nextPage(HasCharityChangedPartsOfGoverningDocumentPage, PlaybackMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }
      }
    }
  }

}
