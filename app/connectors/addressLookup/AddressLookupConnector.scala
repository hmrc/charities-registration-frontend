/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors.addressLookup

import config.FrontendAppConfig
import connectors.httpParsers.AddressLookupInitializationHttpParser.{AddressLookupInitializationReads, AddressLookupInitializationResponse}
import connectors.httpParsers.ConfirmedAddressHttpParser.{ConfirmedAddressReads, ConfirmedAddressResponse}
import models.addressLookup.AddressLookupConfigurationModel
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector @Inject() (httpClient: HttpClientV2, implicit val appConfig: FrontendAppConfig) {

  private[connectors] lazy val addressLookupInitUrl: String = s"${appConfig.addressLookupFrontend}/api/v2/init"

  def initialize(
    callbackUrl: String,
    messagePrefix: String,
    fullName: Option[String],
    allowedCountryCodes: Option[Set[String]]
  )(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    messages: MessagesApi
  ): Future[AddressLookupInitializationResponse] = {

    val body = new AddressLookupConfiguration(callbackUrl, messagePrefix, fullName, allowedCountryCodes).apply

    httpClient
      .post(url"$addressLookupInitUrl")
      .withBody(Json.toJson(body)(AddressLookupConfigurationModel.writes))
      .execute[AddressLookupInitializationResponse]
  }

  def retrieveAddress(
    id: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ConfirmedAddressResponse] = {
    val fullUrl = s"${appConfig.retrieveAddressUrl}?id=$id"
    httpClient.get(url"$fullUrl").execute[ConfirmedAddressResponse]
  }

}
