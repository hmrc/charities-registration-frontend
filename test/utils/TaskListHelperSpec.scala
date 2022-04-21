/*
 * Copyright 2022 HM Revenue & Customs
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
import pages.sections._
import viewmodels.TaskListRow

class TaskListHelperSpec extends SpecBase {

  private val helper = new TaskListHelper

  private def section1(status:String = "notStarted") = TaskListRow("index.section1.spoke1.label",
    if(status=="completed") {
      controllers.contactDetails.routes.CharityInformationSummaryController.onPageLoad()
    } else {
      controllers.contactDetails.routes.StartInformationController.onPageLoad()
    },
    s"index.section.$status")

  private def section2(status:String = "notStarted") = TaskListRow("index.section2.spoke1.label",
    if(status=="completed") {
      controllers.regulatorsAndDocuments.routes.RegulatorsSummaryController.onPageLoad()
    } else {
      controllers.regulatorsAndDocuments.routes.StartCharityRegulatorController.onPageLoad()
    },
    s"index.section.$status")

  private def section3(status:String = "notStarted") = TaskListRow("index.section2.spoke2.label",
    if(status=="completed") {
      controllers.regulatorsAndDocuments.routes.GoverningDocumentSummaryController.onPageLoad()
    } else {
      controllers.regulatorsAndDocuments.routes.StartGoverningDocumentController.onPageLoad()
    },
    s"index.section.$status")

  private def section4(status:String = "notStarted") = TaskListRow("index.section3.spoke1.label",
    if(status=="completed") {
      controllers.operationsAndFunds.routes.CharityObjectivesSummaryController.onPageLoad()
    } else {
      controllers.operationsAndFunds.routes.StartCharitableObjectivesController.onPageLoad()
    },
    s"index.section.$status")

  private def section5(status:String = "notStarted") = TaskListRow("index.section3.spoke2.label",
    if(status=="completed") {
      controllers.operationsAndFunds.routes.OperationsFundsSummaryController.onPageLoad()
    } else {
      controllers.operationsAndFunds.routes.StartFundraisingController.onPageLoad()
    },
    s"index.section.$status")

  private def section6(status:String = "canNotStartYet") = TaskListRow("index.section3.spoke3.label",
    if(status=="completed") {
      controllers.operationsAndFunds.routes.BankDetailsSummaryController.onPageLoad()
    } else {
      controllers.operationsAndFunds.routes.StartBankDetailsController.onPageLoad()
    },
    s"index.section.$status")

  private def section7(status:String = "notStarted") = TaskListRow("index.section4.spoke1.label",
    if(status=="completed") {
      controllers.authorisedOfficials.routes.AuthorisedOfficialsSummaryController.onPageLoad()
    } else {
      controllers.authorisedOfficials.routes.CharityAuthorisedOfficialsController.onPageLoad()
    },
    s"index.section.$status")

  private def section8(status:String = "notStarted") = TaskListRow("index.section4.spoke2.label",
    if(status=="completed") {
      controllers.otherOfficials.routes.OtherOfficialsSummaryController.onPageLoad()
    } else {
      controllers.otherOfficials.routes.CharityOtherOfficialsController.onPageLoad()
    },
    s"index.section.$status")

  private def section9(status:String = "notStarted") = TaskListRow("index.section4.spoke3.label",
    if(status=="completed") {
      controllers.nominees.routes.NomineeDetailsSummaryController.onPageLoad()
    } else {
      controllers.nominees.routes.CharityNomineeController.onPageLoad()
    },
    s"index.section.$status")

  private def section10(status:String = "canNotStartYet") = TaskListRow("index.section5.spoke1.label",
    controllers.routes.StartDeclarationController.onPageLoad(),
    s"index.section.$status")

  "getTaskListRow" must {

    "return all rows with not started and Cannot start yet status" in {

      implicit val userAnswers: UserAnswers = emptyUserAnswers

      helper.getTaskListRow mustBe List(section1(), section2(), section3(), section4(), section5(),
        section6(), section7(), section8(), section9(), section10())
    }

    "return all rows with completed(section1), not started and Cannot start yet(section10) status" in {

      implicit val userAnswers: UserAnswers = emptyUserAnswers.set(Section1Page, true).success.value

      helper.getTaskListRow mustBe List(section1("completed"), section2(), section3(), section4(), section5(),
        section6("notStarted"), section7(), section8(), section9(), section10())
    }

    "return all rows with completed(section1), inProgress(Section6), not started and Cannot start yet(section10) status" in {

      implicit val userAnswers: UserAnswers = emptyUserAnswers.set(Section1Page, true).flatMap(
        _.set(Section6Page, false)).success.value

      helper.getTaskListRow mustBe List(section1("completed"), section2(), section3(), section4(), section5(),
        section6("inProgress"), section7(), section8(), section9(), section10())
    }

    "return all rows with mix status depending upon the status flag" in {

      implicit val userAnswers: UserAnswers = emptyUserAnswers.set(Section1Page, true).flatMap(
        _.set(Section2Page, false)).flatMap(
        _.set(Section6Page, false)).success.value

      helper.getTaskListRow mustBe List(section1("completed"), section2("inProgress"), section3(), section4(),
        section5(), section6("inProgress"), section7(), section8(), section9(), section10())
    }

    "return all rows with completed status except Section 10 with not started status" in {

      implicit val userAnswers: UserAnswers = emptyUserAnswers.set(Section1Page, true).flatMap(
        _.set(Section2Page, true)).flatMap(
        _.set(Section3Page, true)).flatMap(
        _.set(Section4Page, true)).flatMap(
        _.set(Section5Page, true)).flatMap(
        _.set(Section6Page, true)).flatMap(
        _.set(Section7Page, true)).flatMap(
        _.set(Section8Page, true)).flatMap(
        _.set(Section9Page, true)).success.value

      helper.getTaskListRow mustBe List(section1("completed"), section2("completed"), section3("completed"),
        section4("completed"), section5("completed"), section6("completed"), section7("completed"),
        section8("completed"), section9("completed"), section10("notStarted"))
    }

  }

}
