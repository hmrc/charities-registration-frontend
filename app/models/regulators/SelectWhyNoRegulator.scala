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

package models.regulators

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import play.api.libs.json._

sealed trait SelectWhyNoRegulator

object SelectWhyNoRegulator extends Enumerable.Implicits {

  case object EnglandWalesUnderThreshold extends WithName("1") with SelectWhyNoRegulator
  case object ExemptOrExcepted extends WithName("5") with SelectWhyNoRegulator
  private case object NoRegulatorInCountry extends WithName("4") with SelectWhyNoRegulator
  private case object ParochialChurchCouncils extends WithName("2") with SelectWhyNoRegulator
  case object UniformedYouthGroup extends WithName("3") with SelectWhyNoRegulator
  case object Other extends WithName("7") with SelectWhyNoRegulator

  val values: Seq[SelectWhyNoRegulator] = Seq(
    EnglandWalesUnderThreshold,
    ExemptOrExcepted,
    NoRegulatorInCountry,
    ParochialChurchCouncils,
    UniformedYouthGroup,
    Other
  )

  def options(form: Form[?])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"selectWhyNoRegulator.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[SelectWhyNoRegulator] =
    Enumerable(values.map(v => v.toString -> v)*)

  implicit def reads: Reads[SelectWhyNoRegulator] = Reads[SelectWhyNoRegulator] {
    case JsString(EnglandWalesUnderThreshold.toString) => JsSuccess(EnglandWalesUnderThreshold)
    case JsString(ExemptOrExcepted.toString)           => JsSuccess(ExemptOrExcepted)
    case JsString(NoRegulatorInCountry.toString)       => JsSuccess(NoRegulatorInCountry)
    case JsString(ParochialChurchCouncils.toString)    => JsSuccess(ParochialChurchCouncils)
    case JsString(UniformedYouthGroup.toString)        => JsSuccess(UniformedYouthGroup)
    case JsString(Other.toString)                      => JsSuccess(Other)
    case _                                             => JsError("error.invalid")
  }

  implicit def writes: Writes[SelectWhyNoRegulator] =
    Writes(value => JsString(value.toString))

}
