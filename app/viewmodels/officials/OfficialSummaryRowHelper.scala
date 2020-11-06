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

package viewmodels.officials

import models.{Index, Name}
import models.requests.DataRequest
import pages.QuestionPage
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import pages.otherOfficials.OtherOfficialsNamePage
import play.api.mvc.Call
import viewmodels.OfficialSummaryListRow

trait OfficialSummaryRowHelper {

  private def officialAnswers(pageToCall: QuestionPage[Name], onChangeCall: Call, onDeleteCall: Call)(
    implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] =
    request.userAnswers.get[Name](pageToCall).map(name =>
      OfficialSummaryListRow(name,
        onChangeCall,
        onDeleteCall
      )
    ).foldLeft(Seq[OfficialSummaryListRow]())(_ :+ _)

  private def authorisedOfficialAnswers(index: Index)(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = {
    officialAnswers(AuthorisedOfficialsNamePage(index),
      controllers.authorisedOfficials.routes.AddedAuthorisedOfficialController.onPageLoad(index),
      controllers.authorisedOfficials.routes.RemoveAuthorisedOfficialsController.onPageLoad(index)
    )
  }

  private def otherOfficialAnswers(index: Index)(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = {
    officialAnswers(OtherOfficialsNamePage(index),
      controllers.otherOfficials.routes.AddedOtherOfficialController.onPageLoad(index),
      controllers.otherOfficials.routes.RemoveOtherOfficialsController.onPageLoad(index)
    )
  }

  def firstAuthorisedOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = authorisedOfficialAnswers(0)

  def secondAuthorisedOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = authorisedOfficialAnswers(1)

  def firstOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = otherOfficialAnswers(0)

  def secondOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = otherOfficialAnswers(1)

  def thirdOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] = otherOfficialAnswers(2)

}
