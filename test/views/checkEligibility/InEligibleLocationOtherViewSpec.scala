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

package views.checkEligibility

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.checkEligibility.InEligibleLocationOtherView

class InEligibleLocationOtherViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String   = "notEligible"
  private val messageLink: String        = messages("notEligible.p3.link")
  private val messageTabOrWindow: String = messages("site.opensInNewWindowOrTab")

  private val view: InEligibleLocationOtherView = viewFor[InEligibleLocationOtherView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable = view.apply()(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable = view.render(fakeRequest, messages, frontendAppConfig)

  private val viewViaF: HtmlFormat.Appendable = view.f()(fakeRequest, messages, frontendAppConfig)

  "InEligibleLocationOtherView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix)

        behave like pageWithAdditionalGuidance(view, messageKeyPrefix, "locationOther.p1", "p2", "p3", "p3.link")

        behave like pageWithHyperLink(
          view,
          "link",
          frontendAppConfig.getRecognition,
          messages(s"$messageLink $messageTabOrWindow")
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
