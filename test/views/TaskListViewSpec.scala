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

import models.NormalMode
import play.twirl.api.HtmlFormat
import viewmodels.TaskListRow
import views.behaviours.ViewBehaviours
import views.html.TaskList

class TaskListViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "index"

    "TaskList View" must {

      val section1 = TaskListRow("index.section1.spoke1.label",
        controllers.charityInformation.routes.CharityNameController.onPageLoad(NormalMode),
        "index.section.notStarted")

      val section2 = TaskListRow("index.section2.spoke1.label",
        controllers.regulatorsAndDocuments.routes.IsCharityRegulatorController.onPageLoad(NormalMode),
        "index.section.completed")

      val section3 = TaskListRow("index.section2.spoke2.label",
        controllers.regulatorsAndDocuments.routes.SelectGoverningDocumentController.onPageLoad(NormalMode),
        "index.section.inProgress")

      val section4 = TaskListRow("index.section3.spoke1.label",
        controllers.operationsAndFunds.routes.CharitableObjectivesController.onPageLoad(NormalMode),
        "index.section.notStarted")

      val section5 = TaskListRow("index.section3.spoke2.label",
        controllers.operationsAndFunds.routes.FundRaisingController.onPageLoad(NormalMode),
        "index.section.notStarted")

      val section6 = TaskListRow("index.section3.spoke3.label",
        controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(NormalMode),
        "index.section.completed")

      val section7 = TaskListRow("index.section4.spoke1.label",
        controllers.authorisedOfficials.routes.CharityAuthorisedOfficialsController.onPageLoad(),
        "index.section.notStarted")

      val section8 = TaskListRow("index.section4.spoke2.label",
        controllers.otherOfficials.routes.CharityOtherOfficialsController.onPageLoad(),
        "index.section.notStarted")

      val section9 = TaskListRow("index.section4.spoke3.label",
        controllers.nominees.routes.CharityNomineeController.onPageLoad(),
        "index.section.inProgress")

      val section10 = TaskListRow("index.section5.spoke1.label",
        controllers.routes.StartDeclarationController.onPageLoad(),
        "index.section.inProgress")

      def applyView(isCompleted: Boolean = false, isSwitchOver:Option[Boolean] = None): HtmlFormat.Appendable = {
        val view = viewFor[TaskList](Some(emptyUserAnswers))
        view.apply(
          List(section1, section2, section3, section4, section5, section6, section7, section8, section9, section10), status = isCompleted, isSwitchOver)(
          fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix)

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "guidance",
        "section.numbers1", "section.numbers2", "section.numbers3", "section.numbers4",
        "section1.label", "section1.spoke1.label",
        "section2.label", "section2.spoke1.label", "section2.spoke2.label",
        "section3.label", "section3.spoke1.label", "section3.spoke2.label", "section3.spoke3.label",
        "section4.label", "section4.spoke1.label", "section4.spoke2.label", "section4.spoke3.label",
        "section5.label", "section5.spoke1.label",
        "section.note.label"
      )

      "Charity contact details row" must {
        behave like pageWithHyperLink(applyView(),
          "charity-info", controllers.charityInformation.routes.CharityNameController.onPageLoad(NormalMode).url, "Enter the charity’s contact details")
      }

      "Regulator row" must {
        behave like pageWithHyperLink(applyView(),
          "name-regulator",controllers.regulatorsAndDocuments.routes.IsCharityRegulatorController.onPageLoad(NormalMode).url,"Enter regulator information")
      }

      "Governing document row" must {
        behave like pageWithHyperLink(applyView(),
          "governing-doc-info", controllers.regulatorsAndDocuments.routes.SelectGoverningDocumentController.onPageLoad(NormalMode).url, "Enter governing document details")
      }

      "Objectives row" must {
        behave like pageWithHyperLink(applyView(),
          "charity-objective", controllers.operationsAndFunds.routes.CharitableObjectivesController.onPageLoad(NormalMode).url, "Enter objectives and purposes")
      }

      "Fundraising information row" must {
        behave like pageWithHyperLink(applyView(),
          "fundraising-info", controllers.operationsAndFunds.routes.FundRaisingController.onPageLoad(NormalMode).url, "Enter fundraising and operations details")
      }

      "Bank details row" must {
        behave like pageWithHyperLink(applyView(),
          "bank-details", controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(NormalMode).url, "Enter bank details")
      }

      "Authorised officials row" must {
        behave like pageWithHyperLink(applyView(),
          "authorised-officials", controllers.authorisedOfficials.routes.CharityAuthorisedOfficialsController.onPageLoad().url, "Enter details for authorised officials")
      }

      "Other officials row" must {
        behave like pageWithHyperLink(applyView(),
          "other-officials", controllers.otherOfficials.routes.CharityOtherOfficialsController.onPageLoad().url, "Enter details for other officials")
      }

      "Nominee row" must {
        behave like pageWithHyperLink(applyView(),
          "nominee-info",controllers.nominees.routes.CharityNomineeController.onPageLoad().url,"Enter details for nominee")
      }

      "Declaration row" must {
        behave like pageWithHyperLink(applyView(),
          "declaration-info",controllers.routes.StartDeclarationController.onPageLoad().url,"Confirm the declaration and send the supporting documents")
      }

      "dont display declaration message at the bottom pf page if status is completed" in {
        val doc = asDocument(applyView(true))
        assertNotRenderedById(doc, "declaration")
      }

      "dont display switch over message if its not switchover case" in {
        val doc = asDocument(applyView(true))
        assertNotRenderedById(doc, "isSwitchOver")
      }

      "display switch over message if its not switchover case" in {
        val doc = asDocument(applyView(true, isSwitchOver = Some(true)))
        assertRenderedById(doc, "isSwitchOver")
      }

    }
  }
