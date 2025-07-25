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

package config

import base.SpecBase
import play.api.i18n.Lang
import play.api.test.FakeRequest

class FrontendAppConfigSpec extends SpecBase {

  implicit val rq: FakeRequest[?] = fakeRequest

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

      "exitSurveyUrl" in {
        frontendAppConfig.exitSurveyUrl mustBe "http://localhost:9514/feedback/CHARITIES"
      }

      "loginUrl" in {
        frontendAppConfig.loginUrl mustBe "http://localhost:9949/auth-login-stub/gg-sign-in"
      }

      "login continue key" in {
        frontendAppConfig.loginContinueKey mustBe "continue"
      }

      "signOutUrl" in {
        frontendAppConfig.signOutUrl mustBe frontendAppConfig.basGatewayBaseUrl + "/bas-gateway/sign-out-without-state"
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

      "cookies" in {
        frontendAppConfig.cookies mustBe "http://localhost:9457/help/cookies"
      }

      "accessibilityStatement" in {
        frontendAppConfig.accessibilityStatement mustBe "http://localhost:9457/accessibility-statement/register-charity-hmrc"
      }

      "platformHost" in {
        frontendAppConfig.platformHost mustBe ""
      }

      "accessibilityStatementFrontendUrl" in {
        frontendAppConfig
          .accessibilityStatementFrontendUrl() mustBe "http://localhost:9457/accessibility-statement/register-charity-hmrc?referrerUrl="
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

      "languageMap" in {
        frontendAppConfig.languageMap mustBe Map("en" -> Lang("en"), "cy" -> Lang("cy"))
      }

      "getRecognition" in {
        frontendAppConfig.getRecognition mustBe "https://www.gov.uk/charities-and-tax/get-recognition"
      }

      "getCharitiesBackend" in {
        frontendAppConfig.getCharitiesBackend mustBe "http://localhost:9329"
      }

      "noEmailPost" in {
        frontendAppConfig.noEmailPost mustBe true
      }

      "timeToLiveInDays" in {
        frontendAppConfig.timeToLiveInDays mustBe 28
      }
    }
  }

}
