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

package viewmodels.regulatorsAndDocuments

import models.UserAnswers
import models.regulators.SelectGoverningDocument
import models.regulators.SelectGoverningDocument.Other
import pages.QuestionPage
import pages.regulatorsAndDocuments._
import viewmodels.StatusHelper

object GoverningDocumentStatusHelper extends StatusHelper {

  private val allPages: Seq[QuestionPage[_]] = Seq(
    SelectGoverningDocumentPage,
    GoverningDocumentNamePage,
    IsApprovedGoverningDocumentPage,
    WhenGoverningDocumentApprovedPage,
    HasCharityChangedPartsOfGoverningDocumentPage,
    SectionsChangedGoverningDocumentPage
  )

  private val common = Seq(WhenGoverningDocumentApprovedPage, SelectGoverningDocumentPage, IsApprovedGoverningDocumentPage)
  private val commonWithDocName = common ++ Seq(GoverningDocumentNamePage)

  private val governingDoc = (documentType: SelectGoverningDocument) => if(documentType == Other) commonWithDocName else common
  private val hasCharityChanged = (list: Seq[QuestionPage[_]]) => list ++ Seq(HasCharityChangedPartsOfGoverningDocumentPage)
  private val sectionsChanged = (list: Seq[QuestionPage[_]]) => list ++ Seq(SectionsChangedGoverningDocumentPage)

  override def checkComplete(userAnswers: UserAnswers): Boolean = {

    def noAdditionalPagesDefined(list: Seq[QuestionPage[_]]): Boolean = userAnswers.unneededPagesNotPresent(list, allPages)

    userAnswers.get(SelectGoverningDocumentPage) match {
      case Some(governingDocument) =>
        userAnswers.get(IsApprovedGoverningDocumentPage) match {
          case Some(false) =>
            val newPages = governingDoc(governingDocument)
            userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages)
          case _ =>
            userAnswers.get(HasCharityChangedPartsOfGoverningDocumentPage) match {
              case Some(false) =>
                val newPages = (governingDoc andThen hasCharityChanged)(governingDocument)
                userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages)
              case _ =>
                val pages = (governingDoc andThen hasCharityChanged andThen sectionsChanged)(governingDocument)
                userAnswers.arePagesDefined(pages) && noAdditionalPagesDefined(pages)
            }
        }
      case _=> false
    }
  }

}
