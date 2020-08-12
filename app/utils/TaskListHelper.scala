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

package utils

import controllers.authorisedOfficials.{routes => authOfficialsRoutes}
import controllers.charityInformation.{routes => charityInfoRoutes}
import controllers.nominees.{routes => charityNomineeRoutes}
import controllers.operationsAndFunds.{routes => opsAndFundsRoutes}
import controllers.otherOfficials.{routes => otherOfficialsRoutes}
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import pages.sections._
import play.api.mvc.Call
import viewmodels.TaskListRow

class TaskListHelper {

  def getTaskListRow(implicit userAnswers: UserAnswers): List[TaskListRow] = {

    val section1 = getSection("index.section1.spoke1.label",
      charityInfoRoutes.StartInformationController.onPageLoad(),
      charityInfoRoutes.CharityInformationSummaryController.onPageLoad(),
      Section1Page)

    val section2 = getSection("index.section2.spoke1.label",
      regulatorDocsRoutes.StartCharityRegulatorController.onPageLoad(),
      regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad(),
      Section2Page)

    val section3 = getSection("index.section2.spoke2.label",
      regulatorDocsRoutes.StartGoverningDocumentController.onPageLoad(),
      regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad(),
      Section3Page)

    val section4 = getSection("index.section3.spoke1.label",
      opsAndFundsRoutes.StartCharitableObjectivesController.onPageLoad(),
      opsAndFundsRoutes.CharityObjectivesSummaryController.onPageLoad(),
      Section4Page)

    val section5 = getSection("index.section3.spoke2.label",
      opsAndFundsRoutes.FundRaisingController.onPageLoad(NormalMode),
      opsAndFundsRoutes.OperationsFundsSummaryController.onPageLoad(),
      Section5Page)

    val section6 = getSection("index.section3.spoke3.label",
      opsAndFundsRoutes.BankDetailsController.onPageLoad(NormalMode),
      opsAndFundsRoutes.BankDetailsSummaryController.onPageLoad(),
      Section6Page)

    val section7 = getSection("index.section4.spoke1.label",
      authOfficialsRoutes.CharityAuthorisedOfficialsController.onPageLoad(),
      authOfficialsRoutes.AuthorisedOfficialsSummaryController.onPageLoad(),
      Section7Page)

    val section8 = getSection("index.section4.spoke2.label",
      otherOfficialsRoutes.CharityOtherOfficialsController.onPageLoad(),
      otherOfficialsRoutes.OtherOfficialsSummaryController.onPageLoad(),
      Section8Page)

    val section9 = getSection("index.section4.spoke3.label",
      charityNomineeRoutes.CharityNomineeController.onPageLoad(),
      charityNomineeRoutes.NomineeDetailsSummaryController.onPageLoad(),
      Section9Page)

    List(section1, section2, section3, section4, section5, section6, section7, section8, section9)

  }

  private def getSection(name: String, normalUrl: Call, changeUrl: Call, sectionId: QuestionPage[Boolean])(
    implicit userAnswers: UserAnswers) : TaskListRow = {
    userAnswers.get(sectionId) match {
      case Some(true) => TaskListRow(name, changeUrl, "index.section.completed")
      case Some(false) => TaskListRow(name, normalUrl, "index.section.inProgress")
      case _ => TaskListRow(name, normalUrl, "index.section.notStarted")
    }
  }

}
