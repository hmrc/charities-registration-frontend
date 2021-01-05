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

package models.operations

import models.{Enumerable, WithName, WithOrder}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

sealed trait OperatingLocationOptions extends WithOrder

object OperatingLocationOptions extends Enumerable.Implicits {

  case object England extends WithName("1") with OperatingLocationOptions {
    override val order: Int = 1
  }
  case object Wales extends WithName("2") with OperatingLocationOptions {
    override val order: Int = 2
  }
  case object Scotland extends WithName("3") with OperatingLocationOptions {
    override val order: Int = 3
  }
  case object NorthernIreland extends WithName("4") with OperatingLocationOptions {
    override val order: Int = 4
  }
  case object Overseas extends WithName("5") with OperatingLocationOptions {
    override val order: Int = 5
  }

  val values: Seq[OperatingLocationOptions] = Seq(
    England, Wales, Scotland, NorthernIreland, Overseas
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[CheckboxItem] = values.zipWithIndex.map {
    case (value, index) =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(if(index==0){ "value" } else { "value-" + index.toString }),
        value = value.toString,
        content = Text(messages(s"operatingLocation.${value.toString}")),
        checked = form.data.exists(_._2 == value.toString)
      )
  }

  implicit val enumerable: Enumerable[OperatingLocationOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
