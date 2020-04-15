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

import forms.WebsiteAddressForm
import helpers.TestHelper
import org.jsoup.Jsoup
import views.html.home.websiteAddress


class WebsiteAddressControllerViewSpec extends TestHelper {

  "the WebSiteAddressView" should{
    val WebsiteAddressForm = forms.WebsiteAddressForm.WebsiteAddressForm.bind(Map("website" -> "www.google.com"))
    lazy val view = websiteAddress(WebsiteAddressForm)
    lazy val doc = Jsoup.parse(view.body)

    val errorForm = forms.WebsiteAddressForm.WebsiteAddressForm.bind(Map("website" -> "www.google"))
    lazy val errorView = websiteAddress(errorForm)
    lazy val errorDoc = Jsoup.parse(errorView.body)
    lazy val form = doc.select("form")

    "have the correct title" in{
      doc.title().toString shouldBe messages("charities_detail.title")
    }

    "has a valid form" in{
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe controllers.routes.WebsiteAddressController.onSubmit().url
    }

  "have a continue button" in{
      doc.select("button").attr("type") shouldBe "submit"
    }

    "have a back link" in{
      doc.select("#back-link").attr("href") shouldBe "#"
    }

  }
}
