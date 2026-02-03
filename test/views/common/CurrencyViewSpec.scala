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
import forms.common.CurrencyFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.common.CurrencyView

class CurrencyViewSpec extends QuestionViewBehaviours[BigDecimal] {

  private lazy val estimatedIncomePrefix: String = "estimatedIncome"
  private lazy val actualIncomePrefix: String    = "actualIncome"
  val form: Form[BigDecimal]                     = inject[CurrencyFormProvider].apply(estimatedIncomePrefix)
  val formActual: Form[BigDecimal]               = inject[CurrencyFormProvider].apply(actualIncomePrefix)

  private val view: CurrencyView = viewFor[CurrencyView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[BigDecimal], prefix: String): HtmlFormat.Appendable =
    view.apply(form, prefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[BigDecimal], prefix: String): HtmlFormat.Appendable =
    view.render(form, prefix, onwardRoute, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[BigDecimal], prefix: String): HtmlFormat.Appendable =
    view.f(form, prefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)

  private def viewTest(
    method: String,
    viewWithEstimated: HtmlFormat.Appendable,
    viewWithActual: HtmlFormat.Appendable
  ): Unit =
    s"$method" when {
      "with estimated income" must {
        behave like normalPage(
          viewWithEstimated,
          estimatedIncomePrefix,
          section = Some(messages("operationsAndFunds.section"))
        )

        behave like pageWithBackLink(viewWithEstimated)

        behave like pageWithSubmitButton(viewWithEstimated, BaseMessages.saveAndContinue)
      }

      "with actual income" must {
        behave like normalPage(
          viewWithActual,
          actualIncomePrefix,
          section = Some(messages("operationsAndFunds.section"))
        )

        behave like pageWithBackLink(viewWithActual)

        behave like pageWithSubmitButton(viewWithActual, BaseMessages.saveAndContinue)
      }
    }

  "CurrencyView" when {
    viewTest(".apply", viewViaApply(form, estimatedIncomePrefix), viewViaApply(formActual, actualIncomePrefix))
    viewTest(".render", viewViaRender(form, estimatedIncomePrefix), viewViaRender(formActual, actualIncomePrefix))
    viewTest(".f", viewViaF(form, estimatedIncomePrefix), viewViaF(formActual, actualIncomePrefix))
  }
}
