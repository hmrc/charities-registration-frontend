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

import base.SpecBase
import models.UserAnswers
import pages.sections.{Section1Page, Section2Page}
import viewmodels.TaskListRow

class TaskListHelperSpec extends SpecBase {

  private val helper = new TaskListHelper

  private val section1 = TaskListRow("index.section1.spoke1.label",
    controllers.charityInformation.routes.StartInformationController.onPageLoad(),
    "index.section.notStarted")

  private val section1Competed = TaskListRow("index.section1.spoke1.label",
    controllers.charityInformation.routes.CharityInformationSummaryController.onPageLoad(),
    "index.section.completed")

  private val section2 = TaskListRow("index.section2.spoke1.label",
    controllers.regulatorsAndDocuments.routes.StartCharityRegulatorController.onPageLoad(),
    "index.section.notStarted")

  private val section2InProgress = TaskListRow("index.section2.spoke1.label",
    controllers.regulatorsAndDocuments.routes.StartCharityRegulatorController.onPageLoad(),
    "index.section.inProgress")

  private val section3 = TaskListRow("index.section2.spoke2.label",
    controllers.regulatorsAndDocuments.routes.StartGoverningDocumentController.onPageLoad(),
    "index.section.notStarted")

  private val section4 = TaskListRow("index.section3.spoke1.label",
    controllers.operationsAndFunds.routes.StartCharitableObjectivesController.onPageLoad(),
    "index.section.notStarted")

  private val section5 = TaskListRow("index.section3.spoke2.label",
    controllers.operationsAndFunds.routes.StartFundraisingController.onPageLoad(),
    "index.section.notStarted")

  private val section6 = TaskListRow("index.section3.spoke3.label",
    controllers.operationsAndFunds.routes.StartBankDetailsController.onPageLoad(),
    "index.section.notStarted")

  private val section7 = TaskListRow("index.section4.spoke1.label",
    controllers.authorisedOfficials.routes.CharityAuthorisedOfficialsController.onPageLoad(),
    "index.section.notStarted")

  private val section8 = TaskListRow("index.section4.spoke2.label",
    controllers.otherOfficials.routes.CharityOtherOfficialsController.onPageLoad(),
    "index.section.notStarted")

  private val section9 = TaskListRow("index.section4.spoke3.label",
    controllers.nominees.routes.CharityNomineeController.onPageLoad(),
    "index.section.notStarted")

  "getTaskListRow" must {

    "return all rows with not started status" in {

      implicit val userAnswers: UserAnswers = emptyUserAnswers

      helper.getTaskListRow mustBe List(section1, section2, section3, section4, section5, section6, section7, section8, section9)
    }

    "return all rows with mix status depending upon the status flag" in {

      implicit val userAnswers: UserAnswers = emptyUserAnswers.set(Section1Page, true).flatMap(
        _.set(Section2Page, false)).success.value

      helper.getTaskListRow mustBe List(section1Competed, section2InProgress, section3, section4, section5, section6, section7, section8, section9)
    }

  }

}
