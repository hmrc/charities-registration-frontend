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

import models.addressLookup.AddressModel
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object ConfirmedAddressHttpParser {

  type ConfirmedAddressResponse = Either[ErrorResponse, AddressModel]

  private val logger = Logger(this.getClass)

  implicit object ConfirmedAddressReads extends HttpReads[ConfirmedAddressResponse] {

    def read(method: String, url: String, response: HttpResponse): ConfirmedAddressResponse = {
      response.status match {
        case OK =>
          response.json.validate[AddressModel](AddressModel.responseReads) match {
            case JsSuccess(address, _) => Right(address)
            case JsError(errors) =>
              logger.error(s"[ConfirmedAddressReads][read] Json validation errors $errors")
              Left(AddressMalformed)
          }
        case NOT_FOUND =>
          logger.error(s"[ConfirmedAddressReads][read] Address could not be found")
          Left(AddressNotFound)
        case status =>
          logger.warn(s"[ConfirmedAddressReads][read]: Unexpected response, status $status returned")
          Left(DefaultedUnexpectedFailure(status))
      }
    }
  }
}
