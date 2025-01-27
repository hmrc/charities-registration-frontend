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

package views

import play.twirl.api.HtmlFormat
import utils.{ImplicitDateFormatter, TimeMachine}
import views.behaviours.ViewBehaviours
import views.html.ApplicationBeingProcessedView

class ApplicationBeingProcessedViewSpec extends ViewBehaviours with ImplicitDateFormatter {

  private val messageKeyPrefix: String = "beingProcessedOldSubmission"

  private val view: ApplicationBeingProcessedView = viewFor[ApplicationBeingProcessedView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable =
    view.apply(dayToString(inject[TimeMachine].now(), dayOfWeek = false), "080582080582")(
      fakeRequest,
      messages,
      frontendAppConfig
    )

  private val viewViaRender: HtmlFormat.Appendable =
    view.render(
      dayToString(inject[TimeMachine].now(), dayOfWeek = false),
      "080582080582",
      fakeRequest,
      messages,
      frontendAppConfig
    )

  private val viewViaF: HtmlFormat.Appendable =
    view.f(
      dayToString(inject[TimeMachine].now(), dayOfWeek = false),
      "080582080582"
    )(fakeRequest, messages, frontendAppConfig)

  "ApplicationBeingProcessedView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix)

        behave like pageWithHyperLink(
          view,
          "link",
          frontendAppConfig.exitSurveyUrl,
          messages("registrationSent.link")
        )

        "contain the reference number" in {
          val doc = asDocument(view)
          assertContainsText(doc, "080582080582")
        }
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
