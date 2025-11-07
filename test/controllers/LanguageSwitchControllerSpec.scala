/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers

import base.SpecBase
import play.api.Application
import play.api.http.Status.SEE_OTHER
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.Helpers.{cookies, defaultAwaitTimeout, status, writeableOf_AnyContentAsEmpty}
import play.api.test.{FakeRequest, Helpers}

import scala.concurrent.Future

class LanguageSwitchControllerSpec extends SpecBase {

  implicit lazy val appWithWelshTranslation: Application = GuiceApplicationBuilder()
    .configure("play.i18n.langs" -> List("cy"))
    .build()

  implicit lazy val appWithEnglishTranslation: Application = GuiceApplicationBuilder()
    .configure("play.i18n.langs" -> List("en"))
    .build()

  def getLanguageCookies(future: Future[Result]): String =
    cookies(future).get("PLAY_LANG").get.value

  "LanguageSwitchController" when {
    "translation is enabled switching language" must {
      "set the language to Cymraeg" in {
        val request = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage("cy").url)

        val result = Helpers.route(appWithWelshTranslation, request).get

        status(result) mustBe SEE_OTHER
        getLanguageCookies(result) mustBe "cy"
      }

      "set the language to English" in {
        val request = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage("en").url)

        val result = Helpers.route(appWithEnglishTranslation, request).get

        status(result) mustBe SEE_OTHER
        getLanguageCookies(result) mustBe "en"
      }
    }

    "translation is disabled  switching language" must {
      "should set the language to English regardless of what is requested" in {
        val cymraegRequest = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage("cy").url)
        val englishRequest = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage("en").url)

        val cymraegResult = Helpers.route(appWithEnglishTranslation, cymraegRequest).get
        val englishResult = Helpers.route(appWithEnglishTranslation, englishRequest).get

        status(cymraegResult) mustBe SEE_OTHER
        getLanguageCookies(cymraegResult) mustBe "en"

        status(englishResult) mustBe SEE_OTHER
        getLanguageCookies(englishResult) mustBe "en"
      }
    }
  }
}
