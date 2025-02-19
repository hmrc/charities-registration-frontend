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

package views.common

import base.data.messages.BaseMessages
import forms.common.NameFormProvider
import models.{Name, SelectTitle}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.common.NameView

class NameViewSpec extends QuestionViewBehaviours[Name] {

  private val messageKeyPrefix: String = "authorisedOfficialsName"
  val form: Form[Name]                 = inject[NameFormProvider].apply(messageKeyPrefix)

  private val view: NameView = viewFor[NameView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[Name]): HtmlFormat.Appendable =
    view.apply(form, messageKeyPrefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[Name]): HtmlFormat.Appendable =
    view.render(form, messageKeyPrefix, onwardRoute, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[Name]): HtmlFormat.Appendable =
    view.f(form, messageKeyPrefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)

  "NameView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(messages("officialsAndNominees.section")))

        behave like pageWithBackLink(view)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form)),
      (".render", viewViaRender(form)),
      (".f", viewViaF(form))
    )

    input.foreach(args => test.tupled(args))

    ".apply" must {
      SelectTitle.options(form).zipWithIndex.foreach { case (option, i) =>
        val id: String = if (i == 0) "value" else s"value-${i + 1}"

        s"contain radio buttons for the value '${option.value.get}'" in {
          val doc: Document = asDocument(viewViaApply(form))
          assertContainsRadioButton(doc, id, "value", option.value.get, isChecked = false)
        }

        s"have the '${option.value.get}' radio button selected" when {
          s"rendered with a value of '${option.value.get}'" in {
            val formWithData: Form[Name] = form.bind(Map("value" -> s"${option.value.get}"))
            val doc: Document            = asDocument(viewViaApply(formWithData))

            assertContainsRadioButton(doc, id, "value", option.value.get, isChecked = true)
          }
        }
      }
    }
  }
}
