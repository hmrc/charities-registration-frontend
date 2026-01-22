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

import models.{Enumerable, WithOrder}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

enum CharitablePurposes(val name: String, val order: Int)
  extends WithOrder {

  override def toString: String = name

  case AmateurSport
    extends CharitablePurposes("amateurSport", 1)

  case AnimalWelfare
    extends CharitablePurposes("animalWelfare", 2)

  private case ArtsCultureHeritageScience
    extends CharitablePurposes("artsCultureOrScience", 3)

  private case CitizenshipCommunity
    extends CharitablePurposes("citizenshipOrCommunityDevelopment", 4)

  private case Education
    extends CharitablePurposes("education", 5)

  private case EnvironmentalProtection
    extends CharitablePurposes("environmentalProtection", 6)

  private case Health
    extends CharitablePurposes("healthOrSavingOfLives", 7)

  private case HumanRights
    extends CharitablePurposes("humanRights", 8)

  private case PromotionOfEfficiency
    extends CharitablePurposes("armedForcesOfTheCrown", 9)

  private case ReliefOfPoverty
    extends CharitablePurposes("reliefOfPoverty", 10)

  private case ReliefOfThoseInNeed
    extends CharitablePurposes("reliefOfYouthAge", 11)

  private case Religion
    extends CharitablePurposes("religion", 12)

  case Other
    extends CharitablePurposes("other", 13)
}


object CharitablePurposes extends Enumerable.Implicits {

  def options(form: Form[?])(implicit messages: Messages): Seq[CheckboxItem] = values.toIndexedSeq.zipWithIndex.map {
    case (value, index) =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(if (index == 0) { "value" }
        else { "value-" + index.toString }),
        value = value.name,
        content = Text(messages(s"charitablePurposes.${value.name}")),
        checked = form.data.exists(_._2 == value.name)
      )
  }

  implicit val enumerable: Enumerable[CharitablePurposes] =
    Enumerable(values.map(v => v.name -> v)*)

  implicit def reads: Reads[CharitablePurposes] = Reads[CharitablePurposes] {
    case JsString(AmateurSport.name)               => JsSuccess(AmateurSport)
    case JsString(AnimalWelfare.name)              => JsSuccess(AnimalWelfare)
    case JsString(ArtsCultureHeritageScience.name) => JsSuccess(ArtsCultureHeritageScience)
    case JsString(CitizenshipCommunity.name)       => JsSuccess(CitizenshipCommunity)
    case JsString(Education.name)                  => JsSuccess(Education)
    case JsString(EnvironmentalProtection.name)    => JsSuccess(EnvironmentalProtection)
    case JsString(Health.name)                     => JsSuccess(Health)
    case JsString(HumanRights.name)                => JsSuccess(HumanRights)
    case JsString(PromotionOfEfficiency.name)      => JsSuccess(PromotionOfEfficiency)
    case JsString(ReliefOfPoverty.name)            => JsSuccess(ReliefOfPoverty)
    case JsString(ReliefOfThoseInNeed.name)        => JsSuccess(ReliefOfThoseInNeed)
    case JsString(Religion.name)                   => JsSuccess(Religion)
    case JsString(Other.name)                      => JsSuccess(Other)
    case _                                             => JsError("error.invalid")
  }

  implicit def writes: Writes[CharitablePurposes] =
    Writes(value => JsString(value.name))

}
