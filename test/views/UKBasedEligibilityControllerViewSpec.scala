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

import forms.UKBasedEligibilityForm
import helpers.TestHelper
import org.jsoup.Jsoup
import views.html.home.ukBased


class UKBasedEligibilityControllerViewSpec extends TestHelper {

  "the EligibilityView" should{
    val eligibilityForm = UKBasedEligibilityForm.ukBasedForm.bind(Map("ukbased" -> "Yes"))
    lazy val view = ukBased(eligibilityForm)
    lazy val doc = Jsoup.parse(view.body)

    val errorForm = UKBasedEligibilityForm.ukBasedForm.bind(Map("ukbased" -> ""))
    lazy val errorView = ukBased(errorForm)
    lazy val errorDoc = Jsoup.parse(errorView.body)
    lazy val form = doc.select("form")

    "have the correct title" in{
      doc.title().toString shouldBe messages("charities_detail.title")
    }

    "have the correct and properly formatted header"in{
      doc.select("h1").text shouldBe messages("charities_elig.ukbased")
    }

    "has a valid form" in{
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe controllers.routes.UKBasedEligibilityController.onSubmit().url
      form.select("legend.visuallyhidden").text() shouldBe messages("charities_detail.title")
    }


    "have a continue button" in{
      doc.select("button").attr("type") shouldBe "submit"
    }

    "display the correct errors appropriately" in{
      errorDoc.select("h2#error-summary-heading").text shouldBe messages("charities.error")
      errorDoc.select("a#ukbased-error-summary").text shouldBe messages("charities_elig.confirm.location")
      errorDoc.select("span.error-notification").text shouldBe messages("charities_elig.confirm.location")
    }

    "not have errors on valid pages" in{
      eligibilityForm.hasErrors shouldBe false
      doc.select("a#ukbased-error-summary").text shouldBe ""
      doc.select("span.error-notification").text shouldBe ""
    }
  }
}
