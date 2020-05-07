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

import forms.CharityDetailsFormProvider
import helpers.TestHelper
import org.jsoup.Jsoup
import views.html.CharityNameDetailsView

class CharityNameDetailsViewSpec extends TestHelper {

  "the CharityNameDetailsView" should{

    val charityNamesForm = CharityDetailsFormProvider.charityNamesForm().bind(Map("charityFullName" -> "Good Samaritan", "charityOperatingName" -> ""))
    lazy val view = CharityNameDetailsView(charityNamesForm)
    lazy val doc = Jsoup.parse(view.body)

    val errorForm = CharityDetailsFormProvider.charityNamesForm().bind(Map("charityFullName" -> "", "charityOperatingName" -> ""))
    lazy val errorView = CharityNameDetailsView(errorForm)
    lazy val errorDoc = Jsoup.parse(errorView.body)
    lazy val form = doc.select("form")

    "have the correct title" in{
      doc.title() shouldBe messages("charitiesDetails.title")
    }

    "have the correct and properly formatted header"in{
      doc.select("h1").text shouldBe messages("charityDetails.name.heading")
    }

    "has a valid form" in{
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe controllers.routes.CharityNameDetailsController.onSubmit().url
    }


    "have a continue button" in{
      doc.select("button").attr("type") shouldBe "submit"
    }

    "display the correct errors appropriately" in{
      errorDoc.select("h2#error-summary-heading").text shouldBe messages("charities.error")
      errorDoc.select("a#charityFullName-error-summary").text shouldBe messages("charityDetails.error.fullName.required")
      errorDoc.select("span.error-notification").text shouldBe messages("charityDetails.error.fullName.required")
    }

    "not have errors on valid pages" in{
      charityNamesForm.hasErrors shouldBe false
      doc.select("a#charitable-error-summary").text shouldBe ""
      doc.select("span.error-notification").text shouldBe ""
    }

    "have a back link" in{
      doc.select("#back-link").attr("href") shouldBe "javascript:history.go(-1)"
    }

  }
}
