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

import models.Enumerable
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import play.api.libs.json._

enum SelectWhyNoRegulator(val name: String) {

  override def toString: String = name

  case EnglandWalesUnderThreshold
    extends SelectWhyNoRegulator("1")

  case ExemptOrExcepted
    extends SelectWhyNoRegulator("5")

  private case NoRegulatorInCountry
    extends SelectWhyNoRegulator("4")

  private case ParochialChurchCouncils
    extends SelectWhyNoRegulator("2")

  case UniformedYouthGroup
    extends SelectWhyNoRegulator("3")

  case Other
    extends SelectWhyNoRegulator("7")
}
object SelectWhyNoRegulator extends Enumerable.Implicits {


  def options(form: Form[?])(implicit messages: Messages): Seq[RadioItem] = values.toIndexedSeq.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"selectWhyNoRegulator.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[SelectWhyNoRegulator] =
    Enumerable(values.map(v => v.toString -> v)*)

  implicit def reads: Reads[SelectWhyNoRegulator] = Reads[SelectWhyNoRegulator] {
    case JsString(EnglandWalesUnderThreshold.name) => JsSuccess(EnglandWalesUnderThreshold)
    case JsString(ExemptOrExcepted.name)           => JsSuccess(ExemptOrExcepted)
    case JsString(NoRegulatorInCountry.name)       => JsSuccess(NoRegulatorInCountry)
    case JsString(ParochialChurchCouncils.name)    => JsSuccess(ParochialChurchCouncils)
    case JsString(UniformedYouthGroup.name)        => JsSuccess(UniformedYouthGroup)
    case JsString(Other.name)                      => JsSuccess(Other)
    case _                                             => JsError("error.invalid")
  }

  implicit def writes: Writes[SelectWhyNoRegulator] =
    Writes(value => JsString(value.toString))

}
