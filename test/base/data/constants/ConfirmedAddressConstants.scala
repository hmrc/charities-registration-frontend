/*
 * Copyright 2023 HM Revenue & Customs
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

package base.data.constants

import models.addressLookup.{AddressModel, CountryModel}
import play.api.libs.json.{JsObject, Json}


object ConfirmedAddressConstants {

  val lines: Seq[String]    = Seq("Test 1", "Test 2")
  val postcode: String      = "AA00 0AA"
  val country: CountryModel = CountryModel(
    code = "GB",
    name = "United Kingdom"
  )

  val address: AddressModel = AddressModel(
    lines,
    postcode = Some(postcode),
    country
  )

  val addressLookupResponse: JsObject = Json.obj(
    "auditRef" -> "a1fe6969-e3fd-421b-a5fb-c9458c9cfd22",
    "id"       -> "GB690091234501",
    "address"  -> Json.toJson(address)
  )

  val addressJson: JsObject = Json.obj(
    "lines"    -> Json.toJson(lines),
    "postcode" -> "AA00 0AA",
    "country"  -> Json.obj(
      "code" -> "GB",
      "name" -> "United Kingdom"
    )
  )
}
