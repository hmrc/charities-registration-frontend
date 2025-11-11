/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.http.Status.{ACCEPTED, BAD_REQUEST}
import play.api.libs.json.{JsError, JsResultException, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object CharitiesRegistrationHttpParser {

  type CharitiesRegistrationResponse = Either[ErrorResponse, RegistrationResponse]

  private val logger = Logger(this.getClass)

  implicit object CharitiesRegistrationResponseReads extends HttpReads[CharitiesRegistrationResponse] {

    def read(method: String, url: String, response: HttpResponse): CharitiesRegistrationResponse =
      response.status match {
        case ACCEPTED =>
          response.json.validate[RegistrationResponse] match {
            case JsSuccess(validResponse, _) => Right(validResponse)
            case JsError(errors)             =>
              throw JsResultException(errors)
          }

        case BAD_REQUEST =>
          Left(RequestNotAccepted)

        case status =>
          Left(UnexpectedFailure(status))
      }
  }
}
