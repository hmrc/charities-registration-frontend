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

package connectors.httpParsers

import models.RegistrationResponse
import play.api.Logger
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, NOT_ACCEPTABLE}
import play.api.libs.json.{JsError, JsResultException, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object CharitiesRegistrationHttpParser {

  type CharitiesRegistrationResponse = Either[ErrorResponse, RegistrationResponse]

  private val logger = Logger(this.getClass)

  implicit object CharitiesRegistrationResponseReads extends HttpReads[CharitiesRegistrationResponse] {

    def read(method: String, url: String, response: HttpResponse): CharitiesRegistrationResponse = {
      response.status match {
        case ACCEPTED => response.json.validate[RegistrationResponse] match {
          case JsSuccess(validResponse, _) => Right(validResponse)
          case JsError(errors) => logger.error(s"[CharitiesRegistrationResponseReads][read]: Unexpected response, $errors returned")
            throw JsResultException(errors)
        }

        case NOT_ACCEPTABLE => Logger.error(s"[CharitiesRegistrationResponseReads][read]: can not process json, invalid json posted, $NOT_ACCEPTABLE returned")
          Left(CharitiesInvalidJson)

        case BAD_REQUEST => logger.error(s"[CharitiesRegistrationResponseReads][read]: Unexpected response, $BAD_REQUEST returned")
          Left(EtmpFailed)

        case status =>
          logger.error(s"[CharitiesRegistrationResponseReads][read]: Unexpected response, status $status returned")
          Left(DefaultedUnexpectedFailure(status))
      }
    }
  }
}
