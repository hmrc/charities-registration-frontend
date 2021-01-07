/*
 * Copyright 2021 HM Revenue & Customs
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

import assets.messages.BaseMessages
import forms.common.CurrencyFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.common.CurrencyView

class CurrencyViewSpec extends QuestionViewBehaviours[BigDecimal] {

  private lazy val estimatedIncomePrefix = "estimatedIncome"
  private lazy val actualIncomePrefix = "actualIncome"
  val form: Form[BigDecimal] = inject[CurrencyFormProvider].apply(estimatedIncomePrefix)
  val formActual: Form[BigDecimal] = inject[CurrencyFormProvider].apply(actualIncomePrefix)

  def applyView(form: Form[_], prefix: String): HtmlFormat.Appendable = {
    val view = viewFor[CurrencyView](Some(emptyUserAnswers))
    view.apply(form, prefix, onwardRoute)(
      fakeRequest, messages, frontendAppConfig)
  }

  "Charity's Estimated income view" must {


    behave like normalPage(applyView(form, estimatedIncomePrefix), estimatedIncomePrefix, section = Some(messages("operationsAndFunds.section")))

    behave like pageWithBackLink(applyView(form, estimatedIncomePrefix))

    behave like pageWithSubmitButton(applyView(form, estimatedIncomePrefix), BaseMessages.saveAndContinue)
  }

  "Charity's Actual income view" must {

    behave like normalPage(applyView(formActual, actualIncomePrefix), actualIncomePrefix, section = Some(messages("operationsAndFunds.section")))

    behave like pageWithBackLink(applyView(formActual, actualIncomePrefix))

    behave like pageWithSubmitButton(applyView(formActual, actualIncomePrefix), BaseMessages.saveAndContinue)
  }

}
