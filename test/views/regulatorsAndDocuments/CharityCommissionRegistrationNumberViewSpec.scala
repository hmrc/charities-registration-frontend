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

package views.regulatorsAndDocuments

import assets.messages.BaseMessages
import forms.regulatorsAndDocuments.CharityCommissionRegistrationNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.regulatorsAndDocuments.CharityCommissionRegistrationNumberView

class CharityCommissionRegistrationNumberViewSpec extends QuestionViewBehaviours[String] {

  private val messageKeyPrefix = "charityCommissionRegistrationNumber"
  val form: Form[String] = inject[CharityCommissionRegistrationNumberFormProvider].apply()

    "CharityCommissionRegistrationNumberView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
          val view = viewFor[CharityCommissionRegistrationNumberView](Some(emptyUserAnswers))
          view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix, section = Some(messages("charityRegulator.section")))

      behave like pageWithBackLink(applyView(form))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

    }
  }
