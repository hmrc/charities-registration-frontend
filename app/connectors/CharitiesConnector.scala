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
import connectors.httpParsers.HttpClientResponse
import models.{RegistrationResponse, SaveStatus, UserAnswers}
import play.api.Logger
import play.api.http.Status.*
import play.api.libs.json.*
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CharitiesConnector @Inject() (
  httpClient: HttpClientV2,
  httpClientResponse: HttpClientResponse,
  implicit val appConfig: FrontendAppConfig
) {
  // This is required because backend returns NO_CONTENT instead of NOT_FOUND for when no user answers data is found.
  private implicit def readOptionOfNoContent[A: HttpReads]: HttpReads[Option[A]] =
    HttpReads[HttpResponse]
      .flatMap(_.status match {
        case NO_CONTENT => HttpReads.pure(None)
        case _          => HttpReads[A].map(Some.apply)
      })

  private val logger = Logger(this.getClass)
  def registerCharities(id: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[UpstreamErrorResponse, Option[RegistrationResponse]]] =
    httpClientResponse.read(
      httpClient
        .post(url"${appConfig.getCharitiesBackend}/submissions/application/$id")
        .withBody(Json.obj())
        .execute[Either[UpstreamErrorResponse, Option[RegistrationResponse]]]
    )

  def getUserAnswers(
    id: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[UpstreamErrorResponse, Option[UserAnswers]]] =
    httpClientResponse.readLogWarn(
      httpClient
        .get(url"${appConfig.getCharitiesBackend}/charities-registration/getUserAnswer/$id")
        .execute[Either[UpstreamErrorResponse, Option[UserAnswers]]]
    )

//    def saveUserAnswers(
//      userAnswers: UserAnswers
//    )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[UpstreamErrorResponse, Unit]] =
//      httpClientResponseUserAnswers.read(
//        httpClient
//          .post(url"${appConfig.getCharitiesBackend}/charities-registration/saveUserAnswer/${userAnswers.id}")
//          .withBody(Json.toJson(userAnswers))
//          .execute[Either[UpstreamErrorResponse, Unit]]
//      )

  def saveUserAnswers(
    userAnswers: UserAnswers
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    httpClient
      .post(url"${appConfig.getCharitiesBackend}/charities-registration/saveUserAnswer/${userAnswers.id}")
      .withBody(Json.toJson(userAnswers))
      .execute[HttpResponse]
      .map {
        case HttpResponse(OK, responseBody, _) =>
          Json.parse(responseBody).validate[SaveStatus] match {
            case JsSuccess(_, _) => (): Unit
            case JsError(errors) => throw JsResultException(errors)
          }

        case error =>
          logger.error(s"[CharitiesConnector][saveUserAnswers]: Unexpected response returned " + error)
          throw new RuntimeException("Unexpected response returned for saveUserAnswers")
      }
}
