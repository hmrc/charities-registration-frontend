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

package views.components

import base.SpecBase
import org.jsoup.Jsoup
import play.twirl.api.Html
import views.html.components.button

class ButtonSpec extends SpecBase {

  lazy val buttonComponent: button = inject[button]
  lazy val html: Html              = buttonComponent("site.continue")

  object Selectors {
    val button = "button"
  }

  s"button component" must {

    lazy val document = Jsoup.parse(html.toString)

    "Have the correct class" in {
      document.select(Selectors.button).hasClass("govuk-button") mustBe true
    }

    "Have the correct button text" in {
      document.select(Selectors.button).text mustBe messages("site.continue")
    }
  }
}
