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
import forms.regulatorsAndDocuments.CharityOtherRegulatorDetailsFormProvider
import models.{CharityOtherRegulatorDetails, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.regulatorsAndDocuments.CharityOtherRegulatorDetailsView

class CharityOtherRegulatorDetailsViewSpec extends QuestionViewBehaviours[CharityOtherRegulatorDetails] {

  private val messageKeyPrefix                 = "charityOtherRegulatorDetails"
  val form: Form[CharityOtherRegulatorDetails] = inject[CharityOtherRegulatorDetailsFormProvider].apply()

  "CharityOtherRegulatorDetailsView" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[CharityOtherRegulatorDetailsView](Some(emptyUserAnswers))
      view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, section = Some(messages("charityRegulator.section")))

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

  }
}
