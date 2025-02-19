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
import forms.common.AmendAddressFormProvider
import models.addressLookup.AmendAddressModel
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.common.AmendAddressView

class AmendAddressViewSpec extends QuestionViewBehaviours[AmendAddressModel] {

  private val messageKeyPrefix: String = "charityOfficialAmendAddress"
  val form: Form[AmendAddressModel]    = inject[AmendAddressFormProvider].apply(messageKeyPrefix)

  private val view: AmendAddressView = viewFor[AmendAddressView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable = view.apply(
    form,
    messageKeyPrefix,
    onwardRoute,
    countries = Seq(("GB", "United Kingdom"))
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaRender: HtmlFormat.Appendable = view.render(
    form,
    messageKeyPrefix,
    onwardRoute,
    None,
    countries = Seq(("GB", "United Kingdom")),
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaF: HtmlFormat.Appendable = view.f(
    form,
    messageKeyPrefix,
    onwardRoute,
    None,
    Seq(("GB", "United Kingdom"))
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  "AmendAddressView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix)

        behave like pageWithBackLink(view)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))
  }
}
