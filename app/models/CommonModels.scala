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

package models

import java.time.LocalDate

import play.api.libs.json.{Json, OFormat}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait SelectTitle

object SelectTitle extends Enumerable.Implicits {

  case object Mr extends WithName("0001") with SelectTitle
  case object Mrs extends WithName("0002") with SelectTitle
  case object Miss extends WithName("0003") with SelectTitle
  case object Ms extends WithName("0004") with SelectTitle
  case object UnsupportedTitle extends WithName("unsupported") with SelectTitle

  val values: Seq[SelectTitle] = Seq(
    Mr, Mrs, Miss, Ms
  )

  val valuesAndUnsupported: Seq[SelectTitle] = values :+ UnsupportedTitle

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(s"nameTitle.${value.toString}")),
        checked = form("value").value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[SelectTitle] =
    Enumerable(valuesAndUnsupported.map(v => v.toString -> v): _*)
}

case class Name(title: SelectTitle, firstName: String, middleName: Option[String], lastName: String) {

  def getFullName: String = Seq(Some(firstName), middleName, Some(lastName)).flatten.mkString(" ")

  def getFullNameWithTitle(implicit messages: Messages): String = messages(s"nameTitle.${title.toString}") + s" $getFullName"

}

object Name {

  implicit val formats: OFormat[Name] = Json.format[Name]

  override def toString: String = "name"
}

case class PhoneNumber(daytimePhone: String, mobilePhone: Option[String])

object PhoneNumber {

  implicit val formats: OFormat[PhoneNumber] = Json.format[PhoneNumber]

  override def toString: String = "phoneNumber"
}

case class Passport(passportNumber: String, country: String, expiryDate: LocalDate)

object Passport {

  implicit val formats: OFormat[Passport] = Json.format[Passport]

  override def toString: String = "passport"
}

case class Country(code: String, name: String)

object Country {
  implicit val formats: OFormat[Country] = Json.format[Country]
}

case class FcoCountry(country: String, name: String)

object FcoCountry {
  implicit val formats: OFormat[FcoCountry] = Json.format[FcoCountry]
}
