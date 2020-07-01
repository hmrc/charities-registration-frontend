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

package views.components

import assets.messages.SiteHeaderMessages
import base.SpecBase
import org.jsoup.Jsoup
import play.twirl.api.Html
import views.html.components.siteHeader

class SiteHeaderSpec extends SpecBase {

  lazy val siteHeader: siteHeader = inject[siteHeader]
  lazy val html: Html = siteHeader()(messages)

  object Selectors {
    val govUkHomeLink = "a.govuk-header__link--homepage"
    val serviceLink = "a.govuk-header__link--service-name"
    val signOutLink = "ul.govuk-header__navigation li:nth-of-type(1) a"
  }
  s"siteHeader component" must {

    lazy val document = Jsoup.parse(html.toString)

    "Have the correct govUk home link" in {
      document.select(Selectors.govUkHomeLink).attr("href") mustBe "https://www.gov.uk"
      document.select(Selectors.govUkHomeLink).text mustBe SiteHeaderMessages.govUk
    }

    "Have the correct Service Name and Link" in {
      document.select(Selectors.serviceLink).attr("href") mustBe controllers.routes.IndexController.onPageLoad().url
      document.select(Selectors.serviceLink).text mustBe SiteHeaderMessages.serviceName
    }

    "Have the correct Sign Out Link" in {
      document.select(Selectors.signOutLink).attr("href") mustBe controllers.routes.SignOutController.signOut().url
      document.select(Selectors.signOutLink).text mustBe SiteHeaderMessages.signOut
    }
  }
}
