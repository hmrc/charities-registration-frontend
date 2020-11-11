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

package config

import base.SpecBase
import play.api.i18n.Lang
import play.api.test.FakeRequest

class FrontendAppConfigSpec extends SpecBase {

  implicit val rq: FakeRequest[_] = fakeRequest

  "Application configuration" when {

    "contains correct configured values" must {

      "host" in {
        frontendAppConfig.host mustBe "http://localhost:9457"
      }

      "appName" in {
        frontendAppConfig.appName mustBe "charities-registration-frontend"
      }

      "govUK" in {
        frontendAppConfig.govUK mustBe "https://www.gov.uk"
      }

      "contactFormServiceIdentifier" in {
        frontendAppConfig.contactFormServiceIdentifier mustBe "iCharities"
      }

      "contactUrl" in {
        frontendAppConfig.contactUrl mustBe "http://localhost:9250/contact/contact-hmrc?service=iCharities"
      }

      "gtmContainer" in {
        frontendAppConfig.gtmContainer mustBe Some("GTM-NDJKHWK")
      }

      "feedbackUrl" in {
        frontendAppConfig.feedbackUrl mustBe "http://localhost:9250/contact/beta-feedback?service=iCharities"
      }

      "feedbackUnauthenticatedUrl" in {
        frontendAppConfig.feedbackUnauthenticatedUrl mustBe "http://localhost:9250/contact/beta-feedback-unauthenticated?service=iCharities"
      }

      "loginUrl" in {
        frontendAppConfig.loginUrl mustBe "http://localhost:9949/auth-login-stub/gg-sign-in"
      }

      "login continue key" in {
        frontendAppConfig.loginContinueKey mustBe "continue"
      }

      "signOutUrl" in {
        frontendAppConfig.signOutUrl mustBe "https://www.gov.uk/charity-recognition-hmrc"
      }

      "loginContinueUrl" in {
        frontendAppConfig.loginContinueUrl mustBe "http://localhost:9457/register-charity-hmrc/task-list"
      }

      "timeout" in {
        frontendAppConfig.timeout mustBe 900
      }

      "countdown" in {
        frontendAppConfig.countdown mustBe 120
      }

      "addressLookupFrontend" in {
        frontendAppConfig.addressLookupFrontend mustBe "http://localhost:9028"
      }

      "retrieveAddressUrl" in {
        frontendAppConfig.retrieveAddressUrl mustBe "http://localhost:9028/api/v2/confirmed"
      }

      "feedbackUrlAddressLookup" in {
        frontendAppConfig.feedbackUrlAddressLookup mustBe "http://localhost:9250/contact/beta-feedback?service=iCharities"
      }

      "cookies" in {
        frontendAppConfig.cookies mustBe "http://localhost:9457/help/cookies"
      }

      "privacy" in {
        frontendAppConfig.privacy mustBe "http://localhost:9457/help/privacy"
      }

      "termsConditions" in {
        frontendAppConfig.termsConditions mustBe "http://localhost:9457/help/terms-and-conditions"
      }

      "govUKHelp" in {
        frontendAppConfig.govUKHelp mustBe "https://www.gov.uk/help"
      }

      "languageTranslationEnabled" in {
        frontendAppConfig.languageTranslationEnabled mustBe true
      }

      "languageMap" in {
        frontendAppConfig.languageMap mustBe Map("en" -> Lang("en"), "cy" -> Lang("cy"))
      }

      "getRecognition" in {
        frontendAppConfig.getRecognition mustBe "https://www.gov.uk/charities-and-tax/get-recognition"
      }

      "getCharitiesBackend" in {
        frontendAppConfig.getCharitiesBackend mustBe "http://localhost:9329"
      }

      "isExternalTest" in {
        frontendAppConfig.isExternalTest mustBe false
      }
    }
  }

}
