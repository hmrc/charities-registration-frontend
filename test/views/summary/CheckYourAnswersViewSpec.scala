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

package views.summary

import assets.messages.BaseMessages
import models.CheckMode
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.SummaryListRowHelper
import views.behaviours.ViewBehaviours
import views.html.summary.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBehaviours with SummaryListRowHelper {

  val messageKeyCYAPrefix = "charityName.checkYourAnswers"
  val messageKeyPrefix = "charityName"

  val answers: Seq[SummaryListRow] = Seq(
    summaryListRow(
      label = "checkYourAnswersRowLabel1",
      value = "charityName",
      visuallyHiddenText = Some(messages("charityName.fullName.checkYourAnswersLabel")),
      controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode) -> messages("site.edit")
    ),
    summaryListRow(
      label = "checkYourAnswersRowLabel2",
      value = "charityOptionalName",
      visuallyHiddenText = Some(messages("charityName.fullName.checkYourAnswersLabel")),
      controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode) -> messages("site.edit")
    )
  )

    "CheckYourAnswersView" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = inject[CheckYourAnswersView]
        view.apply(answers, messageKeyPrefix, controllers.routes.IndexController.onPageLoad())(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyCYAPrefix)

      behave like pageWithBackLink(applyView())

      behave like pageWithSubmitButton(applyView(), BaseMessages.continue)

      implicit val document: Document = asDocument(applyView())

      checkYourAnswersRowChecks(1,
        ("checkYourAnswersRowLabel1", "charityName" , controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode).url),
        ("checkYourAnswersRowLabel2", "charityOptionalName" , controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode).url)
      )
    }
  }
