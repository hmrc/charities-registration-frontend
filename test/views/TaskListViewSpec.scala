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

package views

import controllers.routes
import models.{NormalMode, TaskListSection}
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.TaskList

class TaskListViewSpec extends ViewBehaviours  {

  val messageKeyPrefix = "index"

    "TaskList View" must {

      val section1 = TaskListSection(
        controllers.charityInformation.routes.CharityNameController.onPageLoad(NormalMode).url, "index.section.notStarted")

      val section2 = TaskListSection(
        controllers.regulatorsAndDocuments.routes.IsCharityRegulatorController.onPageLoad(NormalMode).url, "index.section.notStarted")

      val section3 = TaskListSection(
        controllers.regulatorsAndDocuments.routes.SelectGoverningDocumentController.onPageLoad(NormalMode).url, "index.section.notStarted")

      val section4 = TaskListSection(
        controllers.operationsAndFunds.routes.CharitableObjectivesController.onPageLoad(NormalMode).url, "index.section.notStarted")

      val section5 = TaskListSection(
        controllers.operationsAndFunds.routes.FundRaisingController.onPageLoad(NormalMode).url, "index.section.notStarted")

      val section6 = TaskListSection(
        controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(NormalMode).url, "index.section.notStarted")

      val section7 = TaskListSection(
        controllers.authorisedOfficials.routes.CharityAuthorisedOfficialsController.onPageLoad().url, "index.section.notStarted")

      val section8 = TaskListSection(
        routes.IndexController.onPageLoad().url, "index.section.notStarted")

      val section9 = TaskListSection(
        controllers.nominees.routes.CharityNomineeController.onPageLoad().url, "index.section.notStarted")

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[TaskList](Some(emptyUserAnswers))
        view.apply(List(section1, section2, section3, section4, section5, section6, section7, section8, section9))(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix)

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "guidance1", "guidance2", "guidance3",
        "section.numbers1", "section.numbers2", "section.numbers3", "section.numbers4",
        "section1.label", "section1.spoke1.label",
        "section2.label", "section2.spoke1.label", "section2.spoke2.label",
        "section3.label", "section3.spoke1.label", "section3.spoke2.label", "section3.spoke3.label",
        "section4.label", "section4.spoke1.label", "section4.spoke2.label", "section4.spoke3.label",
        "section.note.label"
      )

    }
  }
