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

package views.common

import base.data.messages.BaseMessages
import forms.common.PhoneNumberFormProvider
import models.PhoneNumber
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.common.PhoneNumberView

class PhoneNumberViewSpec extends QuestionViewBehaviours[PhoneNumber] {

  private val messageKeyPrefix = "authorisedOfficialsPhoneNumber"
  val form: Form[PhoneNumber] = inject[PhoneNumberFormProvider].apply(messageKeyPrefix)


  "AuthorisedOfficialsPhoneNumberView" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[PhoneNumberView](Some(emptyUserAnswers))
      view.apply(form, "Jim Jones", messageKeyPrefix, onwardRoute)(
        fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, Seq("Jim Jones"), section = Some(messages("officialsAndNominees.section")))

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

  }
}
