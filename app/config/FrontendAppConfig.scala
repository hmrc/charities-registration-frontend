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

import com.google.inject.{Inject, Singleton}
import play.api.Environment
import play.api.i18n.Lang
import play.api.mvc.{Call, RequestHeader}
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.util.Try

@Singleton
class FrontendAppConfig @Inject()(val servicesConfig: ServicesConfig, environment: Environment) {

  lazy val host: String = servicesConfig.getString("host")
  lazy val appName: String = servicesConfig.getString("appName")

  private val contactHost: String = servicesConfig.getString("contact-frontend.host")
  val contactFormServiceIdentifier: String = "charities"

  lazy val contactUrl: String = s"$contactHost/contact/contact-hmrc?service=$contactFormServiceIdentifier"

  lazy val contactHmrcUrl: String = servicesConfig.getString("urls.contactHmrc")

  val gtmContainer: Option[String] = (Try {
    servicesConfig.getString("gtm.container")
  } map {
    case "main" => Some("GTM-NDJKHWK")
    case "transitional" => Some("GTM-TSFTCWZ")
  }) getOrElse(None)


  private def requestUri(implicit request: RequestHeader): String = SafeRedirectUrl(host + request.uri).encodedUrl
  def feedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback"


  private lazy val exitSurveyBaseUrl = servicesConfig.getString("feedback-frontend.host") + servicesConfig.getString("feedback-frontend.url")
  lazy val exitSurveyUrl = s"$exitSurveyBaseUrl/$contactFormServiceIdentifier"

  lazy val loginUrl: String = servicesConfig.getString("urls.login")
  lazy val signOutUrl: String = servicesConfig.getString("urls.signOut")
  lazy val loginContinueUrl: String = servicesConfig.getString("urls.loginContinue")
  lazy val appSgnOutUrl: String = host + controllers.routes.SignOutController.signOutNoSurvey().url

  lazy val timeout: Int = servicesConfig.getInt("timeout.timeout")
  lazy val countdown: Int = servicesConfig.getInt("timeout.countdown")

  lazy val addressLookupFrontend: String = servicesConfig.baseUrl("address-lookup-frontend")

  //Footer Links
  lazy val cookies: String = host + servicesConfig.getString("urls.footer.cookies")
  lazy val privacy: String = host + servicesConfig.getString("urls.footer.privacy")
  lazy val termsConditions: String = host + servicesConfig.getString("urls.footer.termsConditions")
  lazy val govUKHelp: String = servicesConfig.getString("urls.footer.govukHelp")

  def languageTranslationEnabled: Boolean = servicesConfig.getBoolean("features.welshLanguage")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => controllers.routes.LanguageSwitchController.switchToLanguage(lang)

  lazy val encryptionKey: String = servicesConfig.getString("mongodb.encryption.key")

  object GovukGuidance {
    val supportForBusiness: String = servicesConfig.getString("urls.govukGuidance.supportForBusiness")
  }
}
