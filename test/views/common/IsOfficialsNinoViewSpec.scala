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
import forms.common.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.common.IsOfficialsNinoView

class IsOfficialsNinoViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix: String = "isAuthorisedOfficialPosition"
  private val section: Option[String]  = Some(messages("officialsAndNominees.section"))
  val form: Form[Boolean]              = inject[YesNoFormProvider].apply(messageKeyPrefix)

  private val view: IsOfficialsNinoView = viewFor[IsOfficialsNinoView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[Boolean]): HtmlFormat.Appendable =
    view.apply(form, "test", messageKeyPrefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[Boolean]): HtmlFormat.Appendable =
    view.render(form, "test", messageKeyPrefix, onwardRoute, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[Boolean]): HtmlFormat.Appendable =
    view.f(form, "test", messageKeyPrefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)

  "IsOfficialsNinoView" when {
    def test(method: String, view: HtmlFormat.Appendable, createView: Form[Boolean] => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, Seq("test"), section = section)

        behave like pageWithBackLink(view)

        behave like yesNoPage(form, createView, messageKeyPrefix, Seq("test"), section = section)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable, Form[Boolean] => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form), viewViaApply),
      (".render", viewViaRender(form), viewViaRender),
      (".f", viewViaF(form), viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
