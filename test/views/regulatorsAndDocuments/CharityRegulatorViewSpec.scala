/*
 * Copyright 2023 HM Revenue & Customs
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

package views.regulatorsAndDocuments

import base.data.messages.BaseMessages
import forms.regulatorsAndDocuments.CharityRegulatorFormProvider
import models.NormalMode
import models.regulators.CharityRegulator
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CheckboxViewBehaviours
import views.html.regulatorsAndDocuments.CharityRegulatorView

class CharityRegulatorViewSpec extends CheckboxViewBehaviours[CharityRegulator] {

  private val messageKeyPrefix: String  = "charityRegulator"
  private val section: String           = messages("charityRegulator.section")
  val form: Form[Set[CharityRegulator]] = inject[CharityRegulatorFormProvider].apply()

  "CharityRegulatorView" must {

    def applyView(form: Form[Set[CharityRegulator]]): HtmlFormat.Appendable = {
      val view = viewFor[CharityRegulatorView](Some(emptyUserAnswers))
      view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, section = Some(section))

    behave like pageWithBackLink(applyView(form))

    behave like checkboxPage(form, applyView, messageKeyPrefix, CharityRegulator.options(form), section)

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)
  }
}
