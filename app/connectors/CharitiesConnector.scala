/*
 * Copyright 2021 HM Revenue & Customs
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

package connectors

import config.FrontendAppConfig
import connectors.httpParsers.CharitiesRegistrationHttpParser.{CharitiesRegistrationResponse, CharitiesRegistrationResponseReads}
import javax.inject.Inject
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class CharitiesConnector @Inject()(httpClient: HttpClient, implicit val appConfig: FrontendAppConfig) {

  def registerCharities(registrationJson: JsValue, organizationId: Int)(
    implicit hc: HeaderCarrier, ec: ExecutionContext): Future[CharitiesRegistrationResponse] = {

    val charitiesRegistrationUrl: String = s"${appConfig.getCharitiesBackend}/org/$organizationId/submissions/application"

    httpClient.POST[JsValue, CharitiesRegistrationResponse](charitiesRegistrationUrl, registrationJson)(implicitly, CharitiesRegistrationResponseReads, hc, ec)
  }
}
