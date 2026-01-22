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

package models.operations

import models.Enumerable
import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

enum CharityEstablishedOptions(val name: String) {

  override def toString: String = name

  case England          extends CharityEstablishedOptions("0")
  case Wales            extends CharityEstablishedOptions("1")
  case Scotland         extends CharityEstablishedOptions("2")
  case NorthernIreland  extends CharityEstablishedOptions("3")
  case Overseas         extends CharityEstablishedOptions("4")
}

object CharityEstablishedOptions extends Enumerable.Implicits {

  def options(form: Form[?])(implicit messages: Messages): Seq[RadioItem] = values.toIndexedSeq.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"charityEstablishedIn.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[CharityEstablishedOptions] =
    Enumerable(values.map(v => v.toString -> v)*)

  implicit def reads: Reads[CharityEstablishedOptions] = Reads[CharityEstablishedOptions] {
    case JsString(England.name)         => JsSuccess(England)
    case JsString(Wales.name)           => JsSuccess(Wales)
    case JsString(Scotland.name)        => JsSuccess(Scotland)
    case JsString(NorthernIreland.name) => JsSuccess(NorthernIreland)
    case JsString(Overseas.name)        => JsSuccess(Overseas)
    case _                                  => JsError("error.invalid")
  }

  implicit def writes: Writes[CharityEstablishedOptions] =
    Writes(value => JsString(value.toString))
}
