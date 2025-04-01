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

import com.google.inject.{Inject, Singleton}
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URLEncoder
import scala.util.Try

@Singleton
class FrontendAppConfig @Inject() (val servicesConfig: ServicesConfig) {

  lazy val host: String              = servicesConfig.getString("host")
  lazy val appName: String           = servicesConfig.getString("appName")
  lazy val govUK: String             = servicesConfig.getString("urls.govUK")
  lazy val basGatewayBaseUrl: String = servicesConfig.getString("bas-gateway.host")
  lazy val feedbackFrontend: String  = servicesConfig.getString("feedback-frontend.host")

  private val contactHost: String = servicesConfig.getString("contact-frontend.host")

  val contactFormServiceIdentifier: String = "iCharities"

  lazy val contactUrl: String = s"$contactHost/contact/contact-hmrc?service=$contactFormServiceIdentifier"

  lazy val userAnswersTimeToLive: Long =
    servicesConfig.getInt("mongodb.user-eligibility-answers.timeToLiveInSeconds").toLong

  def exitSurveyUrl: String = s"$feedbackFrontend/feedback/CHARITIES"

  lazy val loginUrl: String                         = servicesConfig.getString("urls.login")
  lazy val registerUrl: String                      = servicesConfig.getString("urls.register")
  lazy val signOutUrl: String                       = s"$basGatewayBaseUrl/bas-gateway/sign-out-without-state"
  lazy val loginContinueUrl: String                 = servicesConfig.getString("urls.loginContinue")
  lazy val incorrectDetailsLoginContinueUrl: String = servicesConfig.getString("urls.incorrectDetailsLoginContinue")
  lazy val loginContinueKey: String                 = servicesConfig.getString("urls.continue")
  lazy val registrationContinueKey: String          = servicesConfig.getString("urls.registration")
  lazy val timeout: Int                             = servicesConfig.getInt("timeout.timeout")
  lazy val countdown: Int                           = servicesConfig.getInt("timeout.countdown")

  // Address lookup
  lazy val addressLookupFrontend: String = servicesConfig.baseUrl("address-lookup-frontend")
  lazy val retrieveAddressUrl: String    = addressLookupFrontend + "/api/v2/confirmed"

  // Footer Links
  lazy val cookies: String                = host + servicesConfig.getString("urls.footer.cookies")
  lazy val privacy: String                = host + servicesConfig.getString("urls.footer.privacy")
  lazy val termsConditions: String        = host + servicesConfig.getString("urls.footer.termsConditions")
  lazy val govUKHelp: String              = servicesConfig.getString("urls.footer.govukHelp")
  lazy val accessibilityStatement: String = host + servicesConfig.getString("urls.footer.accessibilityStatement")
  lazy val platformHost: String           = Try(servicesConfig.getString("platform.frontend.host")).getOrElse("")

  def accessibilityStatementFrontendUrl()(implicit request: RequestHeader): String =
    s"$accessibilityStatement?referrerUrl=${URLEncoder.encode(s"$platformHost${request.path}", "UTF-8")}"

  lazy val noEmailPost: Boolean = servicesConfig.getBoolean("features.noEmailPost")

  def languageMap: Map[String, Lang] = Map("en" -> Lang("en"), "cy" -> Lang("cy"))

  lazy val getRecognition: String = servicesConfig.getString("urls.getRecognition")

  lazy val getCharitiesBackend: String = servicesConfig.baseUrl("charities")

  lazy val timeToLiveInDays: Int = servicesConfig.getInt("user-answers.timeToLiveInDays")

}
