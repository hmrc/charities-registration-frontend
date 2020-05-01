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

import forms.{CharitiesContactDetailsForm, EligibilityForm}
import helpers.TestHelper
import org.jsoup.Jsoup
import play.api.i18n.Messages
import views.html.home.{contactDetails, eligibility}


class CharitiesContactDetailsControllerViewSpec extends TestHelper {

  "the ContactDetailsView" should{
    val ContactDetailsForm = CharitiesContactDetailsForm.contactDetailsForm.bind(Map("daytimePhone" -> "01632 960 001","mobilePhone"->"01632 960 001","emailAddress"->"name@example.com"))
    lazy val view = contactDetails(ContactDetailsForm)
    lazy val doc = Jsoup.parse(view.body)

    val errorForm = CharitiesContactDetailsForm.contactDetailsForm.bind(Map("daytimePhone" -> "01632 960 001","mobilePhone"->"01632 960 001","emailAddress"->"name@example.com"))
    lazy val errorView = contactDetails(errorForm)
    lazy val errorDoc = Jsoup.parse(errorView.body)
    lazy val form = doc.select("form")

    "have the correct title" in{
      doc.title().toString shouldBe messages("charities.title")
    }

    "have the correct and properly formatted header"in{
      doc.select("h1").text shouldBe messages("charities_contact_details")
    }

    "have a continue button" in{
      doc.select("button").attr("type") shouldBe "submit"
    }

    "display the daytimePhone name label and text input" in {
      doc.text() should include(Messages("charities_lbl.daytimePhoneNo"))
      doc.getElementsByAttributeValueMatching("name", "daytimePhone") shouldNot  be(empty)

    }

    "display the mobilePhone label and text input" in {

      doc.text() should  include(messages("charities_lbl.mobilePhoneNo"))
      doc.getElementsByAttributeValueMatching("name", "mobilePhone") shouldNot  be(empty)
    }
    "display the emailAddress label and text input" in {

      doc.text() should  include(messages("charities_lbl.emailAddress"))
      doc.getElementsByAttributeValueMatching("name", "emailAddress") shouldNot  be(empty)
    }
    "display the save and continue button" in {
      doc.getElementById("continue-button").text shouldBe  messages("common.continue")
    }

    "have a back link" in{
      doc.select("#back-link").attr("href") shouldBe "javascript:history.go(-1)"
    }
  }
}
