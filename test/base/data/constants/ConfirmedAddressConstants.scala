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

package base.data.constants

import base.SpecBase
import models.addressLookup.AddressModel
import play.api.libs.json.{JsObject, Json}

object ConfirmedAddressConstants extends SpecBase {

  val addressLookupResponse: JsObject = Json.obj(
    "auditRef" -> "a1fe6969-e3fd-421b-a5fb-c9458c9cfd22",
    "id"       -> "GB690091234501",
    "address"  -> Json.toJson(address)
  )

  val addressJson: JsObject = Json.obj(
    "lines"    -> Json.toJson(Seq(line1, line2)),
    "postcode" -> ukPostcode,
    "country"  -> Json.obj(
      "code" -> gbCountryCode,
      "name" -> gbCountryName
    )
  )
}
