/*
 * Copyright 2023 HM Revenue & Customs
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
import models.{SaveStatus, UserAnswers}
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CharitiesConnector @Inject() (httpClient: HttpClient, implicit val appConfig: FrontendAppConfig) {

  private val logger = Logger(this.getClass)

  def registerCharities(registrationJson: JsValue, organizationId: Int)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[CharitiesRegistrationResponse] = {

    val charitiesRegistrationUrl: String =
      s"${appConfig.getCharitiesBackend}/org/$organizationId/submissions/application"

    httpClient.POST[JsValue, CharitiesRegistrationResponse](charitiesRegistrationUrl, registrationJson)(
      implicitly,
      CharitiesRegistrationResponseReads,
      hc,
      ec
    )
  }

  def getUserAnswers(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[UserAnswers]] = {

    val charitiesRegistrationUrl: String = s"${appConfig.getCharitiesBackend}/charities-registration/getUserAnswer/$id"

    httpClient.GET[HttpResponse](charitiesRegistrationUrl)(implicitly, hc, ec) map { response =>
      response.status match {
        case OK =>
          response.json.validate[UserAnswers] match {
            case JsSuccess(validResponse, _) => Some(validResponse)
            case JsError(errors)             =>
              logger.error(s"[CharitiesConnector][getUserAnswers]: Unexpected response, $errors returned")
              throw JsResultException(errors)
          }
        case _  =>
          logger.warn(s"[CharitiesConnector][getUserAnswers]: no data found for user $id")
          None
      }
    }
  }

  def saveUserAnswers(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {

    val charitiesRegistrationUrl: String =
      s"${appConfig.getCharitiesBackend}/charities-registration/saveUserAnswer/${userAnswers.id}"

    httpClient.POST[JsValue, HttpResponse](charitiesRegistrationUrl, Json.toJson(userAnswers))(
      implicitly,
      implicitly,
      hc,
      ec
    ) map {

      case HttpResponse(OK, responseBody, _) =>
        Json.parse(responseBody).validate[SaveStatus] match {
          case JsSuccess(value, _) => value.status
          case JsError(errors)     => throw JsResultException(errors)
        }

      case error =>
        logger.error(s"[CharitiesConnector][saveUserAnswers]: Unexpected response returned " + error)
        throw new RuntimeException("Unexpected response returned for saveUserAnswers")
    }
  }
}
