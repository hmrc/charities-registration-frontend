/*
 * Copyright 2020 HM Revenue & Customs
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

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

sealed trait OperatingLocationOptions

object OperatingLocationOptions extends Enumerable.Implicits {

  case object EnglandAndWales extends WithName("englandAndWales") with OperatingLocationOptions
  case object Scotland extends WithName("scotland") with OperatingLocationOptions
  case object NorthernIreland extends WithName("northernIreland") with OperatingLocationOptions
  case object UKWide extends WithName("ukWide") with OperatingLocationOptions
  case object Overseas extends WithName("overseas") with OperatingLocationOptions

  val values: Seq[OperatingLocationOptions] = Seq(
    EnglandAndWales, Scotland, NorthernIreland, UKWide, Overseas
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[CheckboxItem] = values.map {
    value =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(value.toString),
        value = value.toString,
        content = Text(messages(s"operatingLocation.${value.toString}")),
        checked = form.data.exists(_._2 == value.toString)
      )
  }

  implicit val enumerable: Enumerable[OperatingLocationOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
