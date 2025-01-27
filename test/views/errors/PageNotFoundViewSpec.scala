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

package views.errors

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.errors.PageNotFoundView

class PageNotFoundViewSpec extends ViewBehaviours {

  private val view: PageNotFoundView = inject[PageNotFoundView]

  private val viewViaApply: HtmlFormat.Appendable =
    view.apply(signedIn = false)(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable =
    view.render(signedIn = false, fakeRequest, messages, frontendAppConfig)

  private val viewViaF: HtmlFormat.Appendable = view.f(false)(fakeRequest, messages, frontendAppConfig)

  "PageNotFoundView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, "pageNotFound")

        behave like pageWithHyperLink(
          view,
          "startLink",
          controllers.routes.PageNotFoundController.redirectToStartOfJourney().url,
          messages("pageNotFound.back.start.link")
        )
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))

    ".apply" when {
      "reportLink" must {
        behave like pageWithHyperLink(
          viewViaApply,
          "reportLink",
          frontendAppConfig.contactUrl,
          messages("pageNotFound.p3.link")
        )
      }
    }
  }
}
