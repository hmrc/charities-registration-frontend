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
 // private def loadConfig(key: String) = servicesConfig.getConfString(key, throw new Exception(s"Missing key: $key"))

  private lazy val citizenAuthHost = servicesConfig.getString("govuk-tax.auth.citizen-auth.host")
  lazy val urBannerLink: String = servicesConfig.getString("urBanner.link")
  lazy val assetsPrefix: String = servicesConfig.getString("assets.url") + servicesConfig.getString("assets.version")
  private lazy val contactFrontendBaseUrl = servicesConfig.getString("microservice.services.contact-frontend.base-url")
  lazy val betaFeedbackUrl = s"$contactFrontendBaseUrl/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactFrontendBaseUrl/contact/beta-feedback-unauthenticated"
  lazy val analyticsToken = servicesConfig.getString("google-analytics.token")
  lazy val analyticsHost: String = servicesConfig.getString("google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactFrontendBaseUrl/contact/problem_reports_ajax?secure=true"
  lazy val reportAProblemNonJsUrl = s"$contactFrontendBaseUrl/contact/problem_reports_nonjs?secure=true"
  lazy val citizenSwitchOffUrl = s"$citizenAuthHost/attorney/switch-off-act"

  lazy val companyAuthHost = servicesConfig.getString("govuk-tax.auth.company-auth.host")
  lazy val loginCallback =    servicesConfig.getString("govuk-tax.auth.login-callback.url")
  // lazy val loginCallbackSave4Later = servicesConfig.getConfString("govuk-tax.auth.login-callback-s4l.url", routes.LoginController.retrieveSave4Later().url)
  lazy val loginPath = servicesConfig.getString("govuk-tax.auth.login_path")  //"sign-in-local"
  lazy val signIn = s"$companyAuthHost/gg/$loginPath"
  lazy val signInS4L = s"$companyAuthHost/gg/$loginPath"
  lazy val startURL = servicesConfig.getString("microservice.services.charities.start-url")

  lazy val charitiesBasePath: String = servicesConfig.baseUrl("charities")

  lazy val useMinifiedAssets = servicesConfig.getConfBool("assets.minified", true)

  lazy val survey = servicesConfig.getString("microservice.services.feedback-frontend.host")
}