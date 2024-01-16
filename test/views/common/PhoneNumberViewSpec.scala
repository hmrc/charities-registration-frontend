/*
 * Copyright 2024 HM Revenue & Customs
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

  private val messageKeyPrefix: String = "authorisedOfficialsPhoneNumber"
  val form: Form[PhoneNumber]          = inject[PhoneNumberFormProvider].apply(messageKeyPrefix)

  private val view: PhoneNumberView = viewFor[PhoneNumberView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable = view.apply(
    form,
    "test",
    messageKeyPrefix,
    onwardRoute
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaRender: HtmlFormat.Appendable = view.render(
    form,
    "test",
    messageKeyPrefix,
    onwardRoute,
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaF: HtmlFormat.Appendable = view.f(
    form,
    "test",
    messageKeyPrefix,
    onwardRoute
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  "PhoneNumberView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(
          view,
          messageKeyPrefix,
          Seq("test"),
          section = Some(messages("officialsAndNominees.section"))
        )

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
