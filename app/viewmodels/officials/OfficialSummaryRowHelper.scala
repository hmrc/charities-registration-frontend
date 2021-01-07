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

package viewmodels.officials

import models.requests.DataRequest
import models.{Index, Name, NormalMode}
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import pages.otherOfficials.OtherOfficialsNamePage
import viewmodels.OfficialSummaryListRow
import viewmodels.authorisedOfficials.AuthorisedOfficialsStatusHelper.authorisedOfficialCompleted
import viewmodels.otherOfficials.OtherOfficialStatusHelper.otherOfficialCompleted

trait OfficialSummaryRowHelper {

  private def authorisedOfficialAnswers(index: Index)(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = {
    val isCompleted = authorisedOfficialCompleted(index, request.userAnswers)
    request.userAnswers.get[Name](AuthorisedOfficialsNamePage(index)).map(name =>
      OfficialSummaryListRow(name,
        if(isCompleted){
          controllers.authorisedOfficials.routes.AddedAuthorisedOfficialController.onPageLoad(index)
        } else {
          controllers.authorisedOfficials.routes.AuthorisedOfficialsNameController.onPageLoad(NormalMode, index)
        },
        controllers.authorisedOfficials.routes.RemoveAuthorisedOfficialsController.onPageLoad(index),
        isCompleted
      )
    ).foldLeft(Seq[OfficialSummaryListRow]())(_ :+ _)
  }

  private def otherOfficialAnswers(index: Index)(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = {
    val isCompleted = otherOfficialCompleted(index, request.userAnswers)
    request.userAnswers.get[Name](OtherOfficialsNamePage(index)).map(name =>
      OfficialSummaryListRow(name,
        if(isCompleted){
          controllers.otherOfficials.routes.AddedOtherOfficialController.onPageLoad(index)
        } else {
          controllers.otherOfficials.routes.OtherOfficialsNameController.onPageLoad(NormalMode, index)
        },
        controllers.otherOfficials.routes.RemoveOtherOfficialsController.onPageLoad(index),
        isCompleted
      )
    ).foldLeft(Seq[OfficialSummaryListRow]())(_ :+ _)
  }

  def firstAuthorisedOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = authorisedOfficialAnswers(0)

  def secondAuthorisedOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = authorisedOfficialAnswers(1)

  def firstOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = otherOfficialAnswers(0)

  def secondOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = otherOfficialAnswers(1)

  def thirdOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = otherOfficialAnswers(2)

}
