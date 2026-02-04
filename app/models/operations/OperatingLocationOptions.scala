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

package models.operations

import models.{Enumerable, WithOrder}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

enum OperatingLocationOptions extends WithOrder {

  case England
  case Wales
  case Scotland
  case NorthernIreland
  case Overseas
  
  override def toString: String = (ordinal + 1).toString
  override val order: Int = ordinal + 1
}

object OperatingLocationOptions extends Enumerable.Implicits {
  

  def options(form: Form[?])(implicit messages: Messages): Seq[CheckboxItem] = values.toIndexedSeq.zipWithIndex.map {
    case (value, index) =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(if (index == 0) { "value" }
        else { "value-" + index.toString }),
        value = value.toString,
        content = Text(messages(s"operatingLocation.${value.toString}")),
        checked = form.data.exists(_._2 == value.toString)
      )
  }

  implicit val enumerable: Enumerable[OperatingLocationOptions] =
    Enumerable(values.map(v => v.toString -> v)*)

}
