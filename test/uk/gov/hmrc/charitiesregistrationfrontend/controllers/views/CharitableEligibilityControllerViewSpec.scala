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

package uk.gov.hmrc.charitiesregistrationfrontend.controllers.views

import org.jsoup.Jsoup
import uk.gov.hmrc.charitiesregistrationfrontend.forms.EligibilityForm
import uk.gov.hmrc.charitiesregistrationfrontend.views.html.home.eligibility
import uk.gov.hmrc.charitiesregistrationfrontend.controllers
class CharitableEligibilityControllerViewSpec extends CommonViewSpecHelper with CharitiesViewMessages{

  "the EligibilityView" should{
    val eligibilityForm = EligibilityForm.charitableForm.bind(Map("charitable" -> "Yes"))
    lazy val view = eligibility(eligibilityForm)
    lazy val doc = Jsoup.parse(view.body)

    val errorForm = EligibilityForm.charitableForm.bind(Map("charitable" -> ""))
    lazy val errorView = eligibility(errorForm)
    lazy val errorDoc = Jsoup.parse(errorView.body)
    lazy val form = doc.select("form")

    "have the correct title" in{
      doc.title().toString shouldBe charitiesDetailTitle
    }

    "have the correct and properly formatted header"in{
      doc.select("h1").text shouldBe charitiesEligCharitable
    }

    "has a valid form" in{
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe controllers.routes.CharitableEligibilityController.onSubmit().url
      form.select("legend.visuallyhidden").text() shouldBe charitiesDetailTitleLegendText
    }


    "have a continue button" in{
      doc.select("button").attr("type") shouldBe "submit"
    }

    "display the correct errors appropriately" in{
      errorDoc.select("h2#error-summary-heading").text shouldBe charitiesErrorLable
      errorDoc.select("a#charitable-error-summary").text shouldBe errorReal
      errorDoc.select("span.error-notification").text shouldBe errorReal
    }

    "not have errors on valid pages" in{
      eligibilityForm.hasErrors shouldBe false
      doc.select("a#charitable-error-summary").text shouldBe ""
      doc.select("span.error-notification").text shouldBe ""
    }
  }
}
