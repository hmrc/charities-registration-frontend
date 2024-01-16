/*
 * Copyright 2024 HM Revenue & Customs
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

import forms.common.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.common.OfficialsSummaryView

class OfficialsSummaryViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String = "authorisedOfficialsSummary.checkYourAnswers"
  private val section: String          = messages("officialsAndNominees.section")
  val form: Form[Boolean]              = inject[YesNoFormProvider].apply("authorisedOfficialsSummary")

  private val view: OfficialsSummaryView = viewFor[OfficialsSummaryView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable = view.apply(
    form,
    "authorisedOfficialsSummary",
    2,
    controllers.routes.IndexController.onPageLoad(None),
    Seq.empty
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaRender: HtmlFormat.Appendable = view.render(
    form,
    "authorisedOfficialsSummary",
    2,
    controllers.routes.IndexController.onPageLoad(None),
    Seq.empty,
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaF: HtmlFormat.Appendable = view.f(
    form,
    "authorisedOfficialsSummary",
    2,
    controllers.routes.IndexController.onPageLoad(None),
    Seq.empty
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  "OfficialsSummaryView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(
          view,
          messageKeyPrefix,
          Seq(),
          section = Some(section),
          postHeadingString = ".addedOne"
        )

        behave like pageWithBackLink(view)

        behave like pageWithSubmitButton(view, messages("site.confirmAndContinue"))
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
