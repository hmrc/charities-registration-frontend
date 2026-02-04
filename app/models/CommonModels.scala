/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

import java.time.LocalDate


enum SelectTitle(val code: String) {
  case Mr extends SelectTitle("0001")
  case Mrs extends SelectTitle("0002")
  private case Miss extends SelectTitle("0003")
  case Ms extends SelectTitle("0004")
  case UnsupportedTitle extends SelectTitle("unsupported")

  override def toString: String = code
}

object SelectTitle extends Enumerable.Implicits {

  val supportedValues: Seq[SelectTitle] =
    SelectTitle.values.toSeq.filterNot(_ == UnsupportedTitle)

  def options(form: Form[?])(implicit messages: Messages): Seq[RadioItem] = supportedValues.map { value =>
    RadioItem(
      value = Some(value.code),
      content = Text(messages(s"nameTitle.${value.code}")),
      checked = form("value").value.contains(value.code)
    )
  }

  implicit val enumerable: Enumerable[SelectTitle] =
    Enumerable(values.map(v => v.code -> v)*)

  implicit val format: Format[SelectTitle] = Format(
    Reads {
      case JsString(s) =>
        SelectTitle.values.find(_.code == s)
          .orElse(SelectTitle.values.find(_.toString == s))
          .map(JsSuccess(_))
          .getOrElse(JsError("error.invalid"))
      case _ => JsError("error.invalid")
    },
    Writes(t => JsString(t.code))
  )
}

case class Name(title: SelectTitle, firstName: String, middleName: Option[String], lastName: String) {

  def getFullName: String = Seq(Some(firstName), middleName, Some(lastName)).flatten.mkString(" ")

  def getFullNameWithTitle(implicit messages: Messages): String =
    messages(s"nameTitle.${title.code}") + s" $getFullName"

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

object Passport extends MongoDateTimeFormats {

  implicit val format: OFormat[Passport] = Json.format[Passport]

  override def toString: String = "passport"
}

case class Country(code: String, name: String)

case class FcoCountry(country: String, name: String)

object FcoCountry {
  implicit val formats: OFormat[FcoCountry] = Json.format[FcoCountry]
}

case class SaveStatus(status: Boolean)

object SaveStatus {
  implicit val formats: OFormat[SaveStatus] = Json.format[SaveStatus]
}
