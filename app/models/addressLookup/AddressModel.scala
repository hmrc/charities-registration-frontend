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

package models.addressLookup

import play.api.i18n.Messages
import play.api.libs.json.{Format, JsPath, Json, Reads}
import service.CountryService

case class CountryModel(code: String, name: String)

object CountryModel {
  implicit val fmt: Format[CountryModel] = Json.format[CountryModel]
}

case class AddressModel(lines: Seq[String], postcode: Option[String], country: CountryModel) {

  def toEdit: AmendAddressModel = {
    val el = editLines
    AmendAddressModel(
      el._1,
      el._2,
      el._3,
      el._4,
      postcode.getOrElse(""),
      country.code
    )
  }

  private def editLines: (String, Option[String], Option[String], String) = {

    val l1 = lines.headOption.getOrElse("")
    val l2 = if (lines.length > 2) lines.lift(1) else None
    val l3 = if (lines.length > 3) lines.lift(2) else None
    val l4 = lines.lastOption.getOrElse("")

    (l1, l2, l3, l4)
  }
}

object AddressModel {
  val responseReads: Reads[AddressModel] = Reads { json =>
    Reads.at[AddressModel](JsPath \ "address").reads(json)
  }
  implicit val fmt: Format[AddressModel] = Json.format[AddressModel]

}

object AmendAddressModel {

  implicit val fmt: Format[AmendAddressModel] = Json.format[AmendAddressModel]
}

case class AmendAddressModel(
  line1: String,
  line2: Option[String],
  line3: Option[String],
  town: String,
  postcode: String,
  country: String
) {

  def toConfirmableAddress(implicit messages: Messages): AddressModel =
    AddressModel(
      Seq(line1) ++ line2.toSeq ++ line3.toSeq ++ Seq(town),
      if (postcode.isEmpty) {
        None
      } else {
        Some(postcode)
      },
      CountryModel(country, CountryService.find(country).map(_.name).getOrElse("United Kingdom"))
    )

}
