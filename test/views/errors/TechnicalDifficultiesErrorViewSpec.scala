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

package views.errors

import org.jsoup.Jsoup
import views.behaviours.ViewBehaviours
import views.html.errors.TechnicalDifficultiesErrorView

class TechnicalDifficultiesErrorViewSpec extends ViewBehaviours {

  "Technical Difficulties Error view" must {

    val view = inject[TechnicalDifficultiesErrorView]

    val applyView = view.apply("title", "heading", "content")(fakeRequest, messages, frontendAppConfig)
    lazy val document = Jsoup.parse(applyView.toString)

    "Have the correct pageTitle" in {
      document.title mustBe title("Sorry, there is a problem with the service - Error")
    }

    "Have the correct heading" in {
      document.select("h1").text mustBe "Sorry, there is a problem with the service"
    }

    "Have the correct content" in {
      document.select("main p.govuk-body").text mustBe "Try again later. We saved your answers. They will be available for 28 days."
    }
  }
}
