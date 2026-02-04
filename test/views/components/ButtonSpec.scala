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

package views.components

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.{Html, HtmlFormat}
import views.html.components.button

class ButtonSpec extends ComponentsViewSpecBase {

  override val viewViaApply: HtmlFormat.Appendable  = inject[button].apply("site.continue")
  override val viewViaRender: HtmlFormat.Appendable = inject[button].render("site.continue", messages)
  override val viewViaF: HtmlFormat.Appendable      = inject[button].f("site.continue")(messages)

  "Button" when {
    getViews.foreach { (title, view) =>
      s"$title" must {
        "Have the correct class" in {
          asDocument(view).select("button").hasClass("govuk-button") mustBe true
        }

        "Have the correct button text" in {
          asDocument(view).select("button").text mustBe messages("site.continue")
        }
      }
    }
  }

}
