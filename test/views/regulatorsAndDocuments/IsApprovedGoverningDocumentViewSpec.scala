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
import forms.regulatorsAndDocuments.IsApprovedGoverningDocumentFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.regulatorsAndDocuments.IsApprovedGoverningDocumentView

class IsApprovedGoverningDocumentViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix: String = "isApprovedGoverningDocument.4"
  private val section: Option[String]  = Some(messages("charityRegulator.section"))
  val form: Form[Boolean]              = inject[IsApprovedGoverningDocumentFormProvider].apply()

  private val view: IsApprovedGoverningDocumentView = viewFor[IsApprovedGoverningDocumentView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[?]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, "4")(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[?]): HtmlFormat.Appendable =
    view.render(form, NormalMode, "4", fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[?]): HtmlFormat.Appendable =
    view.f(form, NormalMode, "4")(fakeRequest, messages, frontendAppConfig)

  "IsApprovedGoverningDocumentView" when {
    def test(method: String, view: HtmlFormat.Appendable, createView: Form[Boolean] => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = section)

        behave like pageWithAdditionalGuidance(view, "isApprovedGoverningDocument", "p")

        behave like pageWithBackLink(view)

        behave like yesNoPage(form, createView, messageKeyPrefix, section = section)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable, Form[Boolean] => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form), viewViaApply),
      (".render", viewViaRender(form), viewViaRender),
      (".f", viewViaF(form), viewViaF)
    )

    input.foreach(args => test.tupled(args))
  }
}
