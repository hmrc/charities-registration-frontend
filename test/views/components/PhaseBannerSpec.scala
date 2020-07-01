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

import assets.messages.PhaseBannerMessages
import base.SpecBase
import org.jsoup.Jsoup
import play.twirl.api.Html
import views.html.components.phaseBanner

class PhaseBannerSpec extends SpecBase {

  lazy val phaseBannerView: phaseBanner = inject[phaseBanner]
  lazy val html: Html = phaseBannerView("alpha")(fakeRequest, messages, frontendAppConfig)

  object Selectors {
    val link = "a"
    val content = "span.govuk-phase-banner__text"
    val phase = "strong.govuk-tag"
  }

  s"phaseBanner component" must {

    lazy val document = Jsoup.parse(html.toString)

    "Have the correct phase tag" in {
      document.select(Selectors.phase).text mustBe PhaseBannerMessages.tag
    }

    "Have a link to the contact-frontend service" in {
      document.select(Selectors.link).attr("href") mustBe frontendAppConfig.feedbackUrl(fakeRequest)
      document.select(Selectors.link).text mustBe PhaseBannerMessages.link
    }

    "Have a unauthenticated link to the contact-frontend service" in {
      lazy val html: Html = phaseBannerView("alpha", false)(fakeRequest, messages, frontendAppConfig)
      lazy val document = Jsoup.parse(html.toString)
      document.select(Selectors.link).attr("href") mustBe frontendAppConfig.feedbackUnauthenticatedUrl(fakeRequest)
      document.select(Selectors.link).text mustBe PhaseBannerMessages.link
    }

    "Have the correct message for the content of the banner" in {
      document.select(Selectors.content).text mustBe PhaseBannerMessages.content
    }
  }
}
