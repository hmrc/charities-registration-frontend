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
import models.{RegistrationResponse, UserAnswers}
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
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[UpstreamErrorResponse, Option[UserAnswers]]] = {
    // This is required because backend returns NO_CONTENT instead of NOT_FOUND for when no user answers data is found.
    implicit def readOptionOfNoContent[A: HttpReads]: HttpReads[Option[A]] =
      HttpReads[HttpResponse]
        .flatMap(_.status match {
          case NO_CONTENT => HttpReads.pure(None)
          case _          => HttpReads[A].map(Some.apply)
        })
    httpClientResponse.readLogWarn(
      httpClient
        .get(url"${appConfig.getCharitiesBackend}/charities-registration/getUserAnswer/$id")
        .execute[Either[UpstreamErrorResponse, Option[UserAnswers]]]
    )
  }

  def saveUserAnswers(
    userAnswers: UserAnswers
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[UpstreamErrorResponse, Unit]] = {
    httpClientResponse
      .read(
        httpClient
          .post(url"${appConfig.getCharitiesBackend}/charities-registration/saveUserAnswer/${userAnswers.id}")
          .withBody(Json.toJson(userAnswers))
          .execute[Either[UpstreamErrorResponse, Unit]]
      )
      // Below to keep existing behaviour for now. However, the exception is logged twice (here and error handler). 
      // TODO: Logging twice is not ideal and we should change by future re-design of error handling
      .map {
        case Left(UpstreamErrorResponse(message, status, _, _)) =>
          throw new RuntimeException(s"Unexpected response returned for saveUserAnswers $message and status $status")
        case v @ Right(_)                            => v
      }

  }
}
