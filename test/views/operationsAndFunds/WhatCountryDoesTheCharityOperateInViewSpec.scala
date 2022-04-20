/*
 * Copyright 2022 HM Revenue & Customs
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

import assets.messages.BaseMessages
import forms.operationsAndFunds.WhatCountryDoesTheCharityOperateInFormProvider
import models.{Index, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.operationsAndFunds.WhatCountryDoesTheCharityOperateInView


class WhatCountryDoesTheCharityOperateInViewSpec extends QuestionViewBehaviours[String]  {

  private val messageKeyPrefix: String = "whatCountryDoesTheCharityOperateIn"
  private val section: String = messages("operationsAndFunds.section")
  val form: Form[String] = inject[WhatCountryDoesTheCharityOperateInFormProvider].apply()

    "WhatCountryDoesTheCharityOperateInView" must {

      def applyView(form: Form[String], countriesList: Option[String] = None): HtmlFormat.Appendable = {
          val view = viewFor[WhatCountryDoesTheCharityOperateInView](Some(emptyUserAnswers))
          view.apply(form, NormalMode,Index(0),Seq(("GB", "United Kingdom")), countriesList)(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix, section = Some(section))

      behave like pageWithBackLink(applyView(form))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

      behave like pageWithAdditionalGuidance(applyView(form), messageKeyPrefix, "hint")

      "When Countries are populated" must {
        "display the correct guidance" in {

          val doc = asDocument(applyView(form, Some("United Kingdom")))
          assertContainsText(doc, messages(s"$messageKeyPrefix.countries.hint", "United Kingdom"))
        }
      }
  }
}
