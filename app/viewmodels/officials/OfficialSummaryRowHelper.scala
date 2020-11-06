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

  def officialAnswers(pageToCall: QuestionPage[Name], onChangeCall: Call, onDeleteCall: Call)(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] =
    request.userAnswers.get[Name](pageToCall).map(name =>
      OfficialSummaryListRow(name,
        onChangeCall,
        onDeleteCall
      )
    ).foldLeft(Seq[OfficialSummaryListRow]())(_ :+ _)

  def firstAuthorisedOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] =
    officialAnswers(AuthorisedOfficialsNamePage(0),
      controllers.authorisedOfficials.routes.AddedAuthorisedOfficialController.onPageLoad(Index(0)),
      controllers.authorisedOfficials.routes.RemoveAuthorisedOfficialsController.onPageLoad(0)
    )

  def secondAuthorisedOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] =
    officialAnswers(AuthorisedOfficialsNamePage(1),
      controllers.authorisedOfficials.routes.AddedAuthorisedOfficialController.onPageLoad(Index(1)),
      controllers.authorisedOfficials.routes.RemoveAuthorisedOfficialsController.onPageLoad(1)
    )

  def firstOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] =
    officialAnswers(OtherOfficialsNamePage(0),
      controllers.otherOfficials.routes.AddedOtherOfficialController.onPageLoad(0),
      controllers.routes.DeadEndController.onPageLoad()
    )

  def secondOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] =
    officialAnswers(OtherOfficialsNamePage(1),
      controllers.otherOfficials.routes.AddedOtherOfficialController.onPageLoad(1),
      controllers.routes.DeadEndController.onPageLoad()
    )

  def thirdOtherOfficialRow(implicit request: DataRequest[_]): Seq[OfficialSummaryListRow] =
    officialAnswers(OtherOfficialsNamePage(2),
      controllers.otherOfficials.routes.AddedOtherOfficialController.onPageLoad(2),
      controllers.routes.DeadEndController.onPageLoad()
    )

}
