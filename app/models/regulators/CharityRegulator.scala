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

package models.regulators

import models.{Enumerable, WithOrder}
import pages.QuestionPage
import pages.regulatorsAndDocuments.*
import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

enum CharityRegulator(val name: String, val order: Int)
  extends WithOrder {

  override def toString: String = name

  case EnglandWales
    extends CharityRegulator("ccew", 1)

  case Scottish
    extends CharityRegulator("oscr", 2)

  case NorthernIreland
    extends CharityRegulator("ccni", 3)

  case Other
    extends CharityRegulator("otherRegulator", 4)
}

object CharityRegulator extends Enumerable.Implicits {

  val pageMap: Map[CharityRegulator, QuestionPage[?]] = Map(
    EnglandWales    -> CharityCommissionRegistrationNumberPage,
    Scottish        -> ScottishRegulatorRegNumberPage,
    NorthernIreland -> NIRegulatorRegNumberPage,
    Other           -> CharityOtherRegulatorDetailsPage
  )

  def options(form: Form[?])(implicit messages: Messages): Seq[CheckboxItem] = values.toIndexedSeq.zipWithIndex.map {
    case (value, index) =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(if (index == 0) { "value" }
        else { "value-" + index.toString }),
        value = value.toString,
        content = Text(messages(s"charityRegulator.${value.toString}")),
        checked = form.data.exists(_._2 == value.toString)
      )
  }

  implicit val enumerable: Enumerable[CharityRegulator] =
    Enumerable(values.map(v => v.toString -> v)*)

  implicit def reads: Reads[CharityRegulator] = Reads[CharityRegulator] {
    case JsString(EnglandWales.name)    => JsSuccess(EnglandWales)
    case JsString(Scottish.name)        => JsSuccess(Scottish)
    case JsString(NorthernIreland.name) => JsSuccess(NorthernIreland)
    case JsString(Other.name)           => JsSuccess(Other)
    case _                                  => JsError("error.invalid")
  }

  implicit def writes: Writes[CharityRegulator] =
    Writes(value => JsString(value.toString))

}
