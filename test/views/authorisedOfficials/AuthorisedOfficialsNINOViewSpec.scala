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

package views.authorisedOfficials

import assets.messages.BaseMessages
import forms.authorisedOfficials.AuthorisedOfficialsNINOFormProvider
import models.{Index, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.authorisedOfficials.AuthorisedOfficialsNINOView

class AuthorisedOfficialsNINOViewSpec extends QuestionViewBehaviours[String] {

  private val messageKeyPrefix = "authorisedOfficialsNINO"
  val form: Form[String] = inject[AuthorisedOfficialsNINOFormProvider].apply()

  "AuthorisedOfficialsNINOViewSpec view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[AuthorisedOfficialsNINOView](Some(emptyUserAnswers))
      view.apply(form, NormalMode, Index(0), "Jack")(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, Seq("Jack"), section = Some(messages("officialsAndNominees.section")))

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)
  }

}
