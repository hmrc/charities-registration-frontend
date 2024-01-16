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

package views.regulatorsAndDocuments

import base.data.messages.BaseMessages
import forms.regulatorsAndDocuments.SelectWhyNoRegulatorFormProvider
import models.NormalMode
import models.regulators.SelectWhyNoRegulator
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.regulatorsAndDocuments.SelectWhyNoRegulatorView

class SelectWhyNoRegulatorViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String = "selectWhyNoRegulator"
  private val section: String          = messages("charityRegulator.section")
  val form: Form[SelectWhyNoRegulator] = inject[SelectWhyNoRegulatorFormProvider].apply()

  private val view: SelectWhyNoRegulatorView = viewFor[SelectWhyNoRegulatorView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[SelectWhyNoRegulator]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable =
    view.render(form, NormalMode, fakeRequest, messages, frontendAppConfig)

  private val viewViaF: HtmlFormat.Appendable = view.f(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  "SelectWhyNoRegulatorView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(section))

        behave like pageWithBackLink(view)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form)),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))

    ".apply" must {
      SelectWhyNoRegulator.options(form).zipWithIndex.foreach { case (option, i) =>
        val id: String = if (i == 0) "value" else s"value-${i + 1}"

        s"contain radio buttons for the value '${option.value.get}'" in {
          val doc: Document = asDocument(viewViaApply(form))
          assertContainsRadioButton(doc, id, "value", option.value.get, isChecked = false)
        }

        s"have the '${option.value.get}' radio button selected" when {
          s"rendered with a value of '${option.value.get}'" in {
            val formWithData: Form[SelectWhyNoRegulator] = form.bind(Map("value" -> s"${option.value.get}"))
            val doc: Document                            = asDocument(viewViaApply(formWithData))

            assertContainsRadioButton(doc, id, "value", option.value.get, isChecked = true)
          }
        }
      }
    }
  }
}
