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

import play.api.Logger
import play.api.http.HeaderNames
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object AddressLookupInitializationHttpParser {

  type AddressLookupInitializationResponse = Either[ErrorResponse, AddressLookupOnRamp]

  private val logger = Logger(this.getClass)

  implicit object AddressLookupInitializationReads extends HttpReads[AddressLookupInitializationResponse] {

    def read(method: String, url: String, response: HttpResponse): AddressLookupInitializationResponse = {
      response.status match {
        case ACCEPTED =>
          response.header(HeaderNames.LOCATION) match {
            case Some(url) =>
              Right(AddressLookupOnRamp(url))
            case None =>
              logger.warn(s"[AddressLookupInitializationReads][read]: No Location Header returned from Address Lookup")
              Left(NoLocationHeaderReturned)
          }
        case status =>
          logger.error(s"[AddressLookupInitializationReads][read]: Unexpected response, status $status returned")
          Left(DefaultedUnexpectedFailure(status))
      }
    }
  }

  case class AddressLookupOnRamp(url: String)
}
