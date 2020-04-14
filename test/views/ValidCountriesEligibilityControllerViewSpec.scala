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

import forms.EligibilityForm
import helpers.TestHelper
import org.jsoup.Jsoup
import views.html.home.validCountries


class ValidCountriesEligibilityControllerViewSpec extends TestHelper {

  "the EligibilityView" should{
    val eligibilityForm = EligibilityForm.validCountriesForm.bind(Map("charitable" -> "Yes"))
    lazy val view = validCountries(eligibilityForm)
    lazy val doc = Jsoup.parse(view.body)

    val errorForm = EligibilityForm.validCountriesForm.bind(Map("charitable" -> ""))
    lazy val errorView = validCountries(errorForm)
    lazy val errorDoc = Jsoup.parse(errorView.body)
    lazy val form = doc.select("form")

    "have the correct title" in{
      doc.title().toString shouldBe messages("charities_elig.title")
    }

    "have the correct and properly formatted header"in{
      doc.select("h2#page-heading").text() shouldBe messages("charities_elig.check_eligibility")
      doc.select("h1").text shouldBe messages("charities_elig.valid_countries")
    }

    "have valid details text link to expand countries" in {
      doc.select("span.govuk-details__summary-text").text shouldBe messages("charities_elig.countries.details")
    }

    "display valid countries list" in {
      doc.select("div.govuk-details__text").text shouldBe messages("charities_elig.countries.list")
    }

    "has a valid form" in{
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe controllers.routes.ValidCountriesEligibilityController.onSubmit().url
      form.select("legend.visuallyhidden").text() shouldBe messages("charities_elig.title")
    }


    "have a continue button" in{
      doc.select("button").attr("type") shouldBe "submit"
    }

    "display the correct errors appropriately" in{
      errorDoc.select("h2#error-summary-heading").text shouldBe messages("charities.error")
      errorDoc.select("a#charitable-error-summary").text shouldBe messages("charities_elig.valid_countries.confirm")
      errorDoc.select("span.error-notification").text shouldBe messages("charities_elig.valid_countries.confirm")
    }

    "not have errors on valid pages" in{
      eligibilityForm.hasErrors shouldBe false
      doc.select("a#charitable-error-summary").text shouldBe ""
      doc.select("span.error-notification").text shouldBe ""
    }
  }
}
