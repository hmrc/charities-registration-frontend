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

package views.operationsAndFunds

import base.data.messages.BaseMessages
import forms.operationsAndFunds.WhatCountryDoesTheCharityOperateInFormProvider
import models.{Index, NormalMode}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.operationsAndFunds.WhatCountryDoesTheCharityOperateInView

class WhatCountryDoesTheCharityOperateInViewSpec extends QuestionViewBehaviours[String] {

  private val messageKeyPrefix: String = "whatCountryDoesTheCharityOperateIn"
  private val section: String          = messages("operationsAndFunds.section")
  val form: Form[String]               = inject[WhatCountryDoesTheCharityOperateInFormProvider].apply()

  private val view: WhatCountryDoesTheCharityOperateInView =
    viewFor[WhatCountryDoesTheCharityOperateInView](Some(emptyUserAnswers))

  private def viewViaApply(countriesList: Option[String] = None): HtmlFormat.Appendable = view.apply(
    form,
    NormalMode,
    Index(0),
    Seq(gbCountryTuple),
    countriesList
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private def viewViaRender(countriesList: Option[String] = None): HtmlFormat.Appendable = view.render(
    form,
    NormalMode,
    Index(0),
    Seq(gbCountryTuple),
    countriesList,
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private def viewViaF(countriesList: Option[String] = None): HtmlFormat.Appendable = view.f(
    form,
    NormalMode,
    Index(0),
    Seq(gbCountryTuple),
    countriesList
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  "WhatCountryDoesTheCharityOperateInView" when {
    def test(
      method: String,
      viewWithoutCountriesList: HtmlFormat.Appendable,
      viewWithCountriesList: HtmlFormat.Appendable
    ): Unit =
      s"$method" must {
        behave like normalPage(viewWithoutCountriesList, messageKeyPrefix, section = Some(section))

        behave like pageWithBackLink(viewWithoutCountriesList)

        behave like pageWithSubmitButton(viewWithoutCountriesList, BaseMessages.saveAndContinue)

        behave like pageWithAdditionalGuidance(viewWithoutCountriesList, messageKeyPrefix, "hint")

        "display the correct guidance" when {
          "countries are populated" in {
            val doc: Document = asDocument(viewWithCountriesList)
            assertContainsText(doc, messages(s"$messageKeyPrefix.countries.hint", gbCountryName))
          }
        }
      }

    val input: Seq[(String, HtmlFormat.Appendable, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(), viewViaApply(Some(gbCountryName))),
      (".render", viewViaRender(), viewViaRender(Some(gbCountryName))),
      (".f", viewViaF(), viewViaF(Some(gbCountryName)))
    )

    input.foreach(args => test.tupled(args))
  }
}
