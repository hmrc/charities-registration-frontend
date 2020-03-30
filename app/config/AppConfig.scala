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

import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(val runModeConfiguration: Configuration,
                          val environment: Environment,
                          servicesConfig: ServicesConfig) {

  lazy val mode: Mode = environment.mode;
  private def loadConfig(key: String) = servicesConfig.getConfString(key, throw new Exception(s"Missing key: $key"))

  private lazy val citizenAuthHost = loadConfig("govuk-tax.auth.citizen-auth.host")
  lazy val urBannerLink: String = loadConfig("urBanner.link")
  lazy val assetsPrefix: String = loadConfig("assets.url") + loadConfig("assets.version")
  private lazy val contactFrontendBaseUrl = loadConfig("microservice.services.contact-frontend.base-url")
  lazy val betaFeedbackUrl = s"$contactFrontendBaseUrl/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactFrontendBaseUrl/contact/beta-feedback-unauthenticated"
  lazy val analyticsToken = servicesConfig.getString("google-analytics.token")
  lazy val analyticsHost: String = servicesConfig.getConfString("google-analytics.host", "auto")
  lazy val reportAProblemPartialUrl = s"$contactFrontendBaseUrl/contact/problem_reports_ajax?secure=true"
  lazy val reportAProblemNonJsUrl = s"$contactFrontendBaseUrl/contact/problem_reports_nonjs?secure=true"
  lazy val citizenSwitchOffUrl = s"$citizenAuthHost/attorney/switch-off-act"

  lazy val companyAuthHost = servicesConfig.getConfString("govuk-tax.auth.company-auth.host", "http://localhost:9025")
  lazy val loginCallback =    servicesConfig.getConfString("govuk-tax.auth.login-callback.url", "http://localhost:9457/hmrc-register-charity-details/contact-details")
 // lazy val loginCallbackSave4Later = servicesConfig.getConfString("govuk-tax.auth.login-callback-s4l.url", routes.LoginController.retrieveSave4Later().url)
  lazy val loginPath = servicesConfig.getConfString("govuk-tax.auth.login_path", "sign-in")        //"sign-in-local"
  lazy val signIn = s"$companyAuthHost/gg/$loginPath"
  lazy val signInS4L = s"$companyAuthHost/gg/$loginPath"
  lazy val startURL = servicesConfig.getConfString("microservice.services.charities.start-url", "https://www.gov.uk/charity-recognition-hmrc")

  lazy val charitiesBasePath: String = servicesConfig.baseUrl("charities")

  lazy val useMinifiedAssets = servicesConfig.getConfBool("assets.minified", true)

  lazy val survey = servicesConfig.getConfString("microservice.services.feedback-frontend.host", "")
}

