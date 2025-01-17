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

package views.operationsAndFunds

import base.data.messages.BaseMessages
import forms.operationsAndFunds.CharitablePurposesFormProvider
import models.NormalMode
import models.operations.CharitablePurposes
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CheckboxViewBehaviours
import views.html.operationsAndFunds.CharitablePurposesView

class CharitablePurposesViewSpec extends CheckboxViewBehaviours[CharitablePurposes] {

  private val messageKeyPrefix: String    = "charitablePurposes"
  private val section: String             = messages("operationsAndFunds.section")
  val form: Form[Set[CharitablePurposes]] = inject[CharitablePurposesFormProvider].apply()

  private val view: CharitablePurposesView = viewFor[CharitablePurposesView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[Set[CharitablePurposes]]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[Set[CharitablePurposes]]): HtmlFormat.Appendable =
    view.render(form, NormalMode, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[Set[CharitablePurposes]]): HtmlFormat.Appendable =
    view.f(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  "CharitablePurposesView" when {
    def test(
      method: String,
      view: HtmlFormat.Appendable,
      createView: Form[Set[CharitablePurposes]] => HtmlFormat.Appendable
    ): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(section))

        behave like pageWithBackLink(view)

        behave like checkboxPage(form, createView, messageKeyPrefix, CharitablePurposes.options(form), section)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable, Form[Set[CharitablePurposes]] => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form), viewViaApply),
      (".render", viewViaRender(form), viewViaRender),
      (".f", viewViaF(form), viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
