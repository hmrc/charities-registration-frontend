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
import forms.regulatorsAndDocuments.CharityRegulatorFormProvider
import models.NormalMode
import models.regulators.CharityRegulator
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CheckboxViewBehaviours
import views.html.regulatorsAndDocuments.CharityRegulatorView

class CharityRegulatorViewSpec extends CheckboxViewBehaviours[CharityRegulator] {

  private val messageKeyPrefix: String  = "charityRegulator"
  private val section: String           = messages("charityRegulator.section")
  val form: Form[Set[CharityRegulator]] = inject[CharityRegulatorFormProvider].apply()

  private val view: CharityRegulatorView = viewFor[CharityRegulatorView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[Set[CharityRegulator]]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[Set[CharityRegulator]]): HtmlFormat.Appendable =
    view.render(form, NormalMode, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[Set[CharityRegulator]]): HtmlFormat.Appendable =
    view.f(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  "CharityRegulatorView" when {
    def test(
      method: String,
      view: HtmlFormat.Appendable,
      createView: Form[Set[CharityRegulator]] => HtmlFormat.Appendable
    ): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(section))

        behave like pageWithBackLink(view)

        behave like checkboxPage(form, createView, messageKeyPrefix, CharityRegulator.options(form), section)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable, Form[Set[CharityRegulator]] => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form), viewViaApply),
      (".render", viewViaRender(form), viewViaRender),
      (".f", viewViaF(form), viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
