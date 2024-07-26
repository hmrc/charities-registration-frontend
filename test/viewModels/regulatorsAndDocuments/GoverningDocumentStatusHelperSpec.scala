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

package viewModels.regulatorsAndDocuments

import base.SpecBase
import models.regulators.SelectGoverningDocument
import pages.regulatorsAndDocuments._
import viewmodels.regulatorsAndDocuments.GoverningDocumentStatusHelper

import java.time.LocalDate

class GoverningDocumentStatusHelperSpec extends SpecBase {

  private val helper = GoverningDocumentStatusHelper

  "GoverningDocumentStatusHelper" must {

    "when verifying section 3 answers" must {

      "return false when user answers is empty" in {

        helper.checkComplete(emptyUserAnswers) mustBe false
      }

      "return false when one of answer is defined" in {
        helper.checkComplete(
          emptyUserAnswers.set(SelectGoverningDocumentPage, SelectGoverningDocument.TrustDeed).success.value
        ) mustBe false
      }

      "return true when other, document approved(No) and related questions are answered correctly (Scenario 2)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .flatMap(
              _.set(GoverningDocumentNamePage, "Test Document")
                .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
                .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
            )
            .success
            .value
        ) mustBe true
      }

      "return false when other, document approved(No) and additional questions are answered (Scenario 2)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .flatMap(
              _.set(GoverningDocumentNamePage, "Test Document")
                .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
                .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
                .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
            )
            .success
            .value
        ) mustBe false
      }

      "return false when other, document approved(No) and no other data (Scenario 2)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .flatMap(
              _.set(GoverningDocumentNamePage, "Test Document").flatMap(_.set(IsApprovedGoverningDocumentPage, false))
            )
            .success
            .value
        ) mustBe false
      }

      "return true when other, document approved(Yes), HasCharityChangedParts(No) and related questions are answered correctly (Scenario 3)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .flatMap(
              _.set(GoverningDocumentNamePage, "Test Document")
                .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
                .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
                .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
            )
            .success
            .value
        ) mustBe true
      }

      "return false when other, document approved(Yes), HasCharityChangedParts(No) and additional questions are answered (Scenario 3)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .flatMap(
              _.set(GoverningDocumentNamePage, "Test Document")
                .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
                .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
                .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
                .flatMap(_.set(SectionsChangedGoverningDocumentPage, "Test Change"))
            )
            .success
            .value
        ) mustBe false
      }

      "return false when other, document approved(Yes), HasCharityChangedParts(No) and no other data (Scenario 3)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .flatMap(
              _.set(GoverningDocumentNamePage, "Test Document")
                .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
            )
            .success
            .value
        ) mustBe false
      }

      "return true when other, document approved(Yes), HasCharityChangedParts(Yes) " +
        "and related questions are answered correctly (Scenario 4)" in {
          helper.checkComplete(
            emptyUserAnswers
              .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
              .flatMap(_.set(GoverningDocumentNamePage, "Test Document"))
              .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
              .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
              .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, true))
              .flatMap(_.set(SectionsChangedGoverningDocumentPage, "Test Change"))
              .success
              .value
          ) mustBe true
        }

      "return false when other, document approved(Yes), HasCharityChangedParts(Yes) and no other data (Scenario 4)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .flatMap(_.set(GoverningDocumentNamePage, "Test Document"))
            .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
            .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
            .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, true))
            .success
            .value
        ) mustBe false
      }

      "return true when something other than 'other', document approved(No) and related questions are answered correctly (Scenario 5)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.TrustDeed)
            .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
            .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
            .success
            .value
        ) mustBe true
      }

      "return false when something other than 'other', document approved(No) and additional questions are answered (Scenario 5)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.TrustDeed)
            .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
            .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
            .flatMap(_.set(GoverningDocumentNamePage, "Test Document"))
            .success
            .value
        ) mustBe false
      }

      "return false when something other than 'other', document approved(No) and no other data (Scenario 5)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.TrustDeed)
            .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
            .success
            .value
        ) mustBe false
      }

      "return true when something other than 'other', document approved(Yes), HasCharityChangedParts(No) and related " +
        "questions are answered correctly (Scenario 6)" in {
          helper.checkComplete(
            emptyUserAnswers
              .set(SelectGoverningDocumentPage, SelectGoverningDocument.RoyalCharacter)
              .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
              .flatMap(
                _.set(IsApprovedGoverningDocumentPage, true)
                  .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
              )
              .success
              .value
          ) mustBe true
        }

      "return false when something other than 'other', document approved(Yes), HasCharityChangedParts(No) and additional " +
        "questions are answered (Scenario 6)" in {
          helper.checkComplete(
            emptyUserAnswers
              .set(SelectGoverningDocumentPage, SelectGoverningDocument.RoyalCharacter)
              .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
              .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
              .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
              .flatMap(_.set(GoverningDocumentNamePage, "Test Document"))
              .success
              .value
          ) mustBe false
        }

      "return false when something other than 'other', document approved(Yes), " +
        "HasCharityChangedParts(No) and no other data (Scenario 6)" in {
          helper.checkComplete(
            emptyUserAnswers
              .set(SelectGoverningDocumentPage, SelectGoverningDocument.RoyalCharacter)
              .flatMap(
                _.set(IsApprovedGoverningDocumentPage, true)
                  .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
              )
              .success
              .value
          ) mustBe false
        }

      "return true when something other than 'other', document approved(Yes), " +
        "HasCharityChangedParts(Yes) and related questions are answered correctly (Scenario 7)" in {
          helper.checkComplete(
            emptyUserAnswers
              .set(SelectGoverningDocumentPage, SelectGoverningDocument.RulesConstitution)
              .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
              .flatMap(
                _.set(IsApprovedGoverningDocumentPage, true)
                  .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, true))
                  .flatMap(_.set(SectionsChangedGoverningDocumentPage, "Test Change"))
              )
              .success
              .value
          ) mustBe true
        }

      "return false when something other than 'other', document approved(Yes), " +
        "HasCharityChangedParts(Yes) and additional questions are answered correctly (Scenario 7)" in {
          helper.checkComplete(
            emptyUserAnswers
              .set(SelectGoverningDocumentPage, SelectGoverningDocument.RulesConstitution)
              .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
              .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
              .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, true))
              .flatMap(_.set(SectionsChangedGoverningDocumentPage, "Test Change"))
              .flatMap(_.set(GoverningDocumentNamePage, "Test Document"))
              .success
              .value
          ) mustBe false
        }

      "return false when something other than 'other', document approved(Yes), HasCharityChangedParts(Yes) and no other data (Scenario 7)" in {
        helper.checkComplete(
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.RulesConstitution)
            .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
            .flatMap(
              _.set(IsApprovedGoverningDocumentPage, true)
                .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, true))
            )
            .success
            .value
        ) mustBe false
      }
    }
  }
}
