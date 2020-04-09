/*
 * Copyright 2020 HM Revenue & Customs
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
import helpers.TestHelper
import org.jsoup.Jsoup
import play.api.i18n.Messages
import views.html.home.ineligible


class IneligibleForRegistrationControllerViewSpec extends TestHelper {

  "the IneligibilityView" should{
    lazy val view = ineligible()
    lazy val doc = Jsoup.parse(view.body)

    "have the correct title" in{
      doc.title().toString shouldBe messages("charities_detail.title")
    }

    "have the correct and properly formatted header"in{
      doc.select("h1").text shouldBe messages("charities_ineligible")
    }

    "have a back link" in{
      doc.select("#back-link").attr("href") shouldBe "#"

    }


  }
}
