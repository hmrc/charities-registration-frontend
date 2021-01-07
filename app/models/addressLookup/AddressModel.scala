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

package models.addressLookup

import play.api.libs.json.{Format, JsPath, Json, Reads}

case class CountryModel(code: String, name: String)

object CountryModel {
  implicit val fmt: Format[CountryModel] = Json.format[CountryModel]
}

case class AddressModel(lines: Seq[String],
                        postcode: Option[String],
                        country: CountryModel)

object AddressModel {
  val responseReads: Reads[AddressModel] = Reads { json =>
    Reads.at[AddressModel](JsPath \ "address").reads(json)
  }
  implicit val fmt: Format[AddressModel] = Json.format[AddressModel]

}
