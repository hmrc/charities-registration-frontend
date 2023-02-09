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
import utils.{ImplicitDateFormatter, TimeMachine}
import views.behaviours.ViewBehaviours
import views.html.ApplicationBeingProcessedView

class ApplicationBeingProcessedViewSpec extends ViewBehaviours with ImplicitDateFormatter {

  private val messageKeyPrefix = "beingProcessedOldSubmission"

  "ApplicationBeingProcessedView" must {

    def applyView(): HtmlFormat.Appendable = {
      val view = viewFor[ApplicationBeingProcessedView](Some(emptyUserAnswers))
      view.apply(dayToString(inject[TimeMachine].now(), dayOfWeek = false), "080582080582")(
        fakeRequest,
        messages,
        frontendAppConfig
      )
    }

    behave like normalPage(applyView(), messageKeyPrefix)

    behave like pageWithHyperLink(
      applyView(),
      "link",
      frontendAppConfig.exitSurveyUrl,
      messages("registrationSent.link")
    )

    "Contains the reference number" in {
      val doc = asDocument(applyView())
      assertContainsText(doc, "080582080582")
    }

  }
}
