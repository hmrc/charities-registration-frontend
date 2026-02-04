/*
 * Copyright 2026 HM Revenue & Customs
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
import forms.common.OfficialsPositionFormProvider
import models.authOfficials.OfficialsPosition
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.common.OfficialsPositionView

class OfficialsPositionViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String = "authorisedOfficialsPosition"
  private val section: String          = messages("officialsAndNominees.section")
  val form: Form[OfficialsPosition]    = inject[OfficialsPositionFormProvider].apply(messageKeyPrefix)

  private val view: OfficialsPositionView = viewFor[OfficialsPositionView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[OfficialsPosition]): HtmlFormat.Appendable =
    view.apply(form, "test", messageKeyPrefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[OfficialsPosition]): HtmlFormat.Appendable =
    view.render(form, "test", messageKeyPrefix, onwardRoute, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[OfficialsPosition]): HtmlFormat.Appendable =
    view.f(form, "test", messageKeyPrefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)

  "OfficialsPositionView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, Seq("test"), section = Some(section))

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
      OfficialsPosition.options(form).zipWithIndex.foreach { case (option, i) =>
        val id: String = if (i == 0) "value" else s"value-${i + 1}"

        s"contain radio buttons for the value '${option.value.get}'" in {
          val doc: Document = asDocument(viewViaApply(form))
          assertContainsRadioButton(doc, id, "value", option.value.get, isChecked = false)
        }

        s"have the '${option.value.get}' radio button selected" when {
          s"rendered with a value of '${option.value.get}'" in {
            val formWithData: Form[OfficialsPosition] = form.bind(Map("value" -> s"${option.value.get}"))
            val doc: Document                         = asDocument(viewViaApply(formWithData))

            assertContainsRadioButton(doc, id, "value", option.value.get, isChecked = true)
          }
        }
      }
    }
  }
}
