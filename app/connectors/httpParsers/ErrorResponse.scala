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

import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_ACCEPTABLE, NOT_FOUND}

trait ErrorResponse {
  val status: Int
  val body: String
}

case class DefaultedUnexpectedFailure(override val status: Int) extends ErrorResponse {
  override val body: String = s"Unexpected response, status $status returned"
}

case class UnexpectedFailureException(message: String) extends RuntimeException(message)

object AddressNotFound extends ErrorResponse {
  override val status: Int  = NOT_FOUND
  override val body: String = "Address not found"
}

object AddressMalformed extends ErrorResponse {
  override val status: Int  = INTERNAL_SERVER_ERROR
  override val body: String = "Address returned from address lookup was malformed"
}

object NoLocationHeaderReturned extends ErrorResponse {
  override val status: Int  = INTERNAL_SERVER_ERROR
  override val body: String = "Address Lookup returned ACCEPTED (202) but did not provide a Location header"
}

object EtmpFailed extends ErrorResponse {
  override val status: Int  = BAD_REQUEST
  override val body: String = "Charities returned unexpected Response from ETMP"
}

object CharitiesInvalidJson extends ErrorResponse {
  override val status: Int  = NOT_ACCEPTABLE
  override val body: String = "Charities returned Json parsing error for invalid json input"
}
