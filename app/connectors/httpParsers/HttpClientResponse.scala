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

package connectors.httpParsers

import com.google.inject.Inject
import play.api.Logging
import play.api.http.Status.*
import uk.gov.hmrc.http.{HttpException, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class HttpClientResponse @Inject() ()(implicit ec: ExecutionContext) extends Logging {
  private def logErrorResponses[A]: PartialFunction[Try[Either[UpstreamErrorResponse, A]], Unit] = {
    case Failure(ex: HttpException) => logger.error(s"HttpException thrown: ${ex.message}", ex)
    case Success(Left(r))           => logger.error(s"Response of ${r.statusCode} returned", r)
  }

  private def recoverHttpException[A]: PartialFunction[Throwable, Either[UpstreamErrorResponse, A]] = {
    case exception: HttpException =>
      Left(
        UpstreamErrorResponse(message = exception.message, statusCode = exception.responseCode, reportAs = BAD_GATEWAY)
      )
  }

  def read[A](
    response: Future[Either[UpstreamErrorResponse, A]]
  ): Future[Either[UpstreamErrorResponse, A]] =
    response andThen logErrorResponses recover recoverHttpException
  
  // TODO: Keep current WARN behaviour until we have error behaviour defined.
  //  However, log 404s at ERROR level.
  def readLogWarnExceptFor404[A](
    response: Future[Either[UpstreamErrorResponse, A]]
  ): Future[Either[UpstreamErrorResponse, A]] = {

    def logWarnResponses: PartialFunction[Try[Either[UpstreamErrorResponse, A]], Unit] = {
      case Failure(ex: HttpException)    => logger.warn(s"HttpException thrown: ${ex.message}", ex)
      case Success(Left(upstreamErrorResponse)) if upstreamErrorResponse.statusCode == NOT_FOUND =>
        logger.error(s"Response of ${upstreamErrorResponse.statusCode} returned", upstreamErrorResponse)
      case Success(Left(upstreamErrorResponse)) =>
        logger.warn(s"Response of ${upstreamErrorResponse.statusCode} returned", upstreamErrorResponse)
    }
  
    response andThen logWarnResponses recover recoverHttpException
  }
}
