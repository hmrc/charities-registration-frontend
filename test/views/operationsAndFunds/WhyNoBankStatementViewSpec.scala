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

package views.operationsAndFunds

import assets.messages.BaseMessages
import forms.operationsAndFunds.WhyNoBankStatementFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.operationsAndFunds.WhyNoBankStatementView


class WhyNoBankStatementViewSpec extends QuestionViewBehaviours[String] {

  private val messageKeyPrefix: String = "whyNoBankStatement"
  private val section: String = messages("operationsAndFunds.section")
  val form: Form[String] = inject[WhyNoBankStatementFormProvider].apply()

    "WhyNoBankStatementView" must {

      def applyView(form: Form[String]): HtmlFormat.Appendable = {
          val view = viewFor[WhyNoBankStatementView](Some (emptyUserAnswers))
          view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix, section = Some(section))

      behave like pageWithBackLink(applyView(form))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)
  }
}
