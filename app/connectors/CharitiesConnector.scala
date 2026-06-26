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
  // TODO: Keep close to current behaviour until we have error behaviour defined. Should possibly return
  //  5xx if any 2xx other than 200 returned.
  def registerCharities(id: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[UpstreamErrorResponse, RegistrationResponse]] =
    httpClientResponse.read(
      httpClient
        .post(url"${appConfig.getCharitiesBackend}/submissions/application/$id")
        .withBody(Json.obj())
        .execute[Either[UpstreamErrorResponse, RegistrationResponse]]
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
    // TODO: Keep current behaviour until we have error behaviour defined.
    //  Current behaviour is: 2 exceptions are logged (1 in error handler and 1 here).
    //  I suggest that we remove convertLeftToException and redirect upstream errors in saveUserAnswers
    //  in situ (i.e. in calling code) based on context and whether it's a 4xx or 5xx response.
    def convertLeftToException[A](
      response: Future[Either[UpstreamErrorResponse, A]]
    )(implicit ec: ExecutionContext): Future[Either[UpstreamErrorResponse, A]] =
      response.map {
        case Left(UpstreamErrorResponse(message, status, _, _)) =>
          throw new RuntimeException(s"Unexpected response returned $message and status $status")
        case rightUpstreamErrorResponse @ Right(_)              => rightUpstreamErrorResponse
      }

    convertLeftToException(
      httpClientResponse
        .read(
          httpClient
            .post(url"${appConfig.getCharitiesBackend}/charities-registration/saveUserAnswer/${userAnswers.id}")
            .withBody(Json.toJson(userAnswers))
            .execute[Either[UpstreamErrorResponse, Unit]]
        )
    )
  }

}
