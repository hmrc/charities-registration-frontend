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

package utils

import controllers.authorisedOfficials.{routes => authOfficialsRoutes}
import controllers.contactDetails.{routes => charityInfoRoutes}
import controllers.nominees.{routes => charityNomineeRoutes}
import controllers.operationsAndFunds.{routes => opsAndFundsRoutes}
import controllers.otherOfficials.{routes => otherOfficialsRoutes}
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import models.UserAnswers
import pages.QuestionPage
import pages.sections._
import play.api.mvc.Call
import viewmodels.TaskListRow


class TaskListHelper {

  implicit class TaskList(val userAnswers: UserAnswers) {
    implicit val ua = userAnswers
    val section1: () => TaskListRow = () => getSection("index.section1.spoke1.label",
      charityInfoRoutes.StartInformationController.onPageLoad(),
      charityInfoRoutes.CharityInformationSummaryController.onPageLoad(),
      Section1Page)

    val section2: () => TaskListRow = () => getSection("index.section2.spoke1.label",
      regulatorDocsRoutes.StartCharityRegulatorController.onPageLoad(),
      regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad(),
      Section2Page)(userAnswers)

    val section3: () => TaskListRow = () => getSection("index.section2.spoke2.label",
      regulatorDocsRoutes.StartGoverningDocumentController.onPageLoad(),
      regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad(),
      Section3Page)(userAnswers)

    val section4: () => TaskListRow = () => getSection("index.section3.spoke1.label",
      opsAndFundsRoutes.StartCharitableObjectivesController.onPageLoad(),
      opsAndFundsRoutes.CharityObjectivesSummaryController.onPageLoad(),
      Section4Page)(userAnswers)

    val section5: () => TaskListRow = () => getSection("index.section3.spoke2.label",
      opsAndFundsRoutes.StartFundraisingController.onPageLoad(),
      opsAndFundsRoutes.OperationsFundsSummaryController.onPageLoad(),
      Section5Page)(userAnswers)

    val section6: Boolean => TaskListRow = (isDependent: Boolean) => getSection("index.section3.spoke3.label",
      opsAndFundsRoutes.StartBankDetailsController.onPageLoad(),
      opsAndFundsRoutes.BankDetailsSummaryController.onPageLoad(),
      Section6Page,
      Some(isDependent)
    )(userAnswers)

    val section7: () => TaskListRow = () => getSection("index.section4.spoke1.label",
      authOfficialsRoutes.CharityAuthorisedOfficialsController.onPageLoad(),
      authOfficialsRoutes.AuthorisedOfficialsSummaryController.onPageLoad(),
      Section7Page)(userAnswers)

    val section8: () => TaskListRow = () => getSection("index.section4.spoke2.label",
      otherOfficialsRoutes.CharityOtherOfficialsController.onPageLoad(),
      otherOfficialsRoutes.OtherOfficialsSummaryController.onPageLoad(),
      Section8Page)(userAnswers)

    val section9: () => TaskListRow = () => getSection("index.section4.spoke3.label",
      charityNomineeRoutes.CharityNomineeController.onPageLoad(),
      charityNomineeRoutes.NomineeDetailsSummaryController.onPageLoad(),
      Section9Page)(userAnswers)

    val section10: Boolean => TaskListRow = (isDependent: Boolean) => {
      TaskListRow(
        "index.section5.spoke1.label",
        controllers.routes.StartDeclarationController.onPageLoad(),
        if (isDependent) "index.section.notStarted" else "index.section.canNotStartYet")
    }
  }

  def getTaskListRow(implicit userAnswers: UserAnswers): List[TaskListRow] = {

    val result = List(userAnswers.section1(), userAnswers.section2(), userAnswers.section3(), userAnswers.section4(),
      userAnswers.section5(), userAnswers.section6(userAnswers.section1().state.contains("completed")),
      userAnswers.userAnswers.section7(), userAnswers.section8(), userAnswers.section9())

    val declaration = userAnswers.section10(result.forall(_.state.equals("index.section.completed")))

    result ++ List(declaration)
  }

  private def getSection(name: String, normalUrl: Call, changeUrl: Call, sectionId: QuestionPage[Boolean],
                         isDependent: Option[Boolean] = None)(implicit userAnswers: UserAnswers): TaskListRow = {
    userAnswers.get(sectionId) match {
      case _ if isDependent.contains(false) => TaskListRow(name, normalUrl, "index.section.canNotStartYet")
      case Some(true) => TaskListRow(name, changeUrl, "index.section.completed")
      case Some(false) => TaskListRow(name, normalUrl, "index.section.inProgress")
      case _ => TaskListRow(name, normalUrl, "index.section.notStarted")
    }
  }

}
