/*
 * Copyright 2025 HM Revenue & Customs
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
import forms.regulatorsAndDocuments.WhyNotRegisteredWithCharityFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.TextAreaViewBehaviours
import views.html.regulatorsAndDocuments.WhyNotRegisteredWithCharityView

class WhyNotRegisteredWithCharityViewSpec extends TextAreaViewBehaviours {

  private val messageKeyPrefix: String = "whyNotRegisteredWithCharity"
  val form: Form[String]               = inject[WhyNotRegisteredWithCharityFormProvider].apply()

  private val view: WhyNotRegisteredWithCharityView = viewFor[WhyNotRegisteredWithCharityView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[String]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[String]): HtmlFormat.Appendable =
    view.render(form, NormalMode, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[String]): HtmlFormat.Appendable =
    view.f(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  "WhyNotRegisteredWithCharityView" when {
    def test(method: String, view: HtmlFormat.Appendable, createView: Form[String] => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(messages("charityRegulator.section")))

        behave like pageWithBackLink(view)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)

        behave like textAreaPage(
          form,
          createView,
          messageKeyPrefix,
          section = Some(messages("charityRegulator.section"))
        )
      }

    val input: Seq[(String, HtmlFormat.Appendable, Form[String] => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form), viewViaApply),
      (".render", viewViaRender(form), viewViaRender),
      (".f", viewViaF(form), viewViaF)
    )

    input.foreach(args => test.tupled(args))
  }
}
