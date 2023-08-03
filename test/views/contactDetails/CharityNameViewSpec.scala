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

package views.contactDetails

import base.data.messages.BaseMessages
import forms.contactDetails.CharityNameFormProvider
import models.{CharityName, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.contactDetails.CharityNameView

class CharityNameViewSpec extends QuestionViewBehaviours[CharityName] {

  private val messageKeyPrefix: String = "charityName"
  val form: Form[CharityName]          = inject[CharityNameFormProvider].apply()

  private val view: CharityNameView = viewFor[CharityNameView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable =
    view.render(form, NormalMode, fakeRequest, messages, frontendAppConfig)

  private val viewViaF: HtmlFormat.Appendable = view.f(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  "CharityNameView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(messages("contactDetail.section")))

        behave like pageWithBackLink(view)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
