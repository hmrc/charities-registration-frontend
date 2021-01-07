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

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait CharityEstablishedOptions

object CharityEstablishedOptions extends Enumerable.Implicits {

  case object England extends WithName("0") with CharityEstablishedOptions
  case object Wales extends WithName("1") with CharityEstablishedOptions
  case object Scotland extends WithName("2") with CharityEstablishedOptions
  case object NorthernIreland extends WithName("3") with CharityEstablishedOptions
  case object Overseas extends WithName("4") with CharityEstablishedOptions

  val values: Seq[CharityEstablishedOptions] = Seq(
    England, Wales, Scotland, NorthernIreland, Overseas
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(s"charityEstablishedIn.${value.toString}")),
        checked = form("value").value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[CharityEstablishedOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)
}

