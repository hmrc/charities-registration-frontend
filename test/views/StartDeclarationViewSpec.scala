/*
 * Copyright 2023 HM Revenue & Customs
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

package views

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.StartDeclarationView

class StartDeclarationViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String = "startDeclaration"

  private val view: StartDeclarationView = viewFor[StartDeclarationView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable = view.apply()(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable = view.render(fakeRequest, messages, frontendAppConfig)

  private val viewViaF: HtmlFormat.Appendable = view.f()(fakeRequest, messages, frontendAppConfig)

  "StartDeclarationView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(messages("declaration.section")))

        behave like pageWithAdditionalGuidance(view, messageKeyPrefix, "p")

        behave like pageWithBackLink(view)

        behave like pageWithHyperLink(
          view,
          "declarationLink",
          controllers.routes.DeclarationController.onPageLoad.url,
          messages("site.continue")
        )
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
