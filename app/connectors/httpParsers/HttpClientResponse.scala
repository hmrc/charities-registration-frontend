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

class HttpClientResponse[A] @Inject() ()(implicit ec: ExecutionContext) extends Logging {
  private val logErrorResponses: PartialFunction[Try[Either[UpstreamErrorResponse, A]], Unit] = {
    case Failure(exception: HttpException) => logger.error(exception.message)
    case Success(Left(error))              => logger.error(error.message, error)
  }

  private val logWarnResponses: PartialFunction[Try[Either[UpstreamErrorResponse, A]], Unit] = {
    case Failure(exception: HttpException) => logger.error(exception.message)
    case Success(Left(error))              => logger.warn(error.message, error)
  }

  private val recoverHttpException: PartialFunction[Throwable, Either[UpstreamErrorResponse, A]] = {
    case exception: HttpException =>
      Left(UpstreamErrorResponse(exception.message, BAD_GATEWAY, BAD_GATEWAY))
  }

  def read(
    response: Future[Either[UpstreamErrorResponse, A]]
  ): Future[Either[UpstreamErrorResponse, A]] =
    response andThen logErrorResponses recover recoverHttpException
    
  // TODO: This is a temporary method to match current behaviour which will change on new ticket
  def readLogWarn(
    response: Future[Either[UpstreamErrorResponse, A]]
  ): Future[Either[UpstreamErrorResponse, A]] =
    response recover recoverHttpException andThen logWarnResponses
}
