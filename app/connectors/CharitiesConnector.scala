/*
 * Copyright 2026 HM Revenue & Customs
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
import models.{RegistrationResponse, SaveStatus, UserAnswers}
import play.api.Logger
import play.api.http.Status.*
import play.api.libs.json.*
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CharitiesConnector @Inject() (httpClient: HttpClientV2, implicit val appConfig: FrontendAppConfig) {

  private val logger = Logger(this.getClass)
  def registerCharities(id: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Option[RegistrationResponse]] =
    httpClient
      .post(url"${appConfig.getCharitiesBackend}/submissions/application/$id")
      .withBody(Json.obj())
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK    =>
            response.json.validate[RegistrationResponse] match {
              case JsSuccess(validResponse, _) => Some(validResponse)
              case JsError(errors)             =>
                logger.warn(s"[CharitiesConnector][registerCharities]: Unexpected response, $errors returned")
                throw JsResultException(errors)
            }
          case notOk =>
            logger.error(
              s"[CharitiesConnector][registerCharities]: Registration unsuccessful with status $notOk and message ${response.body}"
            )
            None
        }
      }

  def getUserAnswers(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[UserAnswers]] =
    httpClient
      .get(url"${appConfig.getCharitiesBackend}/charities-registration/getUserAnswer/$id")
      .execute[HttpResponse]
      .map { response =>
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

  def saveUserAnswers(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    httpClient
      .post(url"${appConfig.getCharitiesBackend}/charities-registration/saveUserAnswer/${userAnswers.id}")
      .withBody(Json.toJson(userAnswers))
      .execute[HttpResponse]
      .map {
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
