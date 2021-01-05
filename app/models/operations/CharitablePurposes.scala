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

sealed trait CharitablePurposes extends WithOrder

object CharitablePurposes extends Enumerable.Implicits {

  case object AmateurSport extends WithName("amateurSport") with CharitablePurposes {
    override val order: Int = 1
  }

  case object AnimalWelfare extends WithName("animalWelfare") with CharitablePurposes {
    override val order: Int = 2
  }

  case object ArtsCultureHeritageScience extends WithName("artsCultureOrScience") with CharitablePurposes {
    override val order: Int = 3
  }

  case object CitizenshipCommunity extends WithName("citizenshipOrCommunityDevelopment") with CharitablePurposes {
    override val order: Int = 4
  }

  case object Education extends WithName("education") with CharitablePurposes {
    override val order: Int = 5
  }

  case object EnvironmentalProtection extends WithName("environmentalProtection") with CharitablePurposes {
    override val order: Int = 6
  }

  case object Health extends WithName("healthOrSavingOfLives") with CharitablePurposes {
    override val order: Int = 7
  }

  case object HumanRights extends WithName("humanRights") with CharitablePurposes {
    override val order: Int = 8
  }

  case object PromotionOfEfficiency extends WithName("armedForcesOfTheCrown") with CharitablePurposes {
    override val order: Int = 9
  }

  case object ReliefOfPoverty extends WithName("reliefOfPoverty") with CharitablePurposes {
    override val order: Int = 10
  }

  case object ReliefOfThoseInNeed extends WithName("reliefOfYouthAge") with CharitablePurposes {
    override val order: Int = 11
  }

  case object Religion extends WithName("religion") with CharitablePurposes {
    override val order: Int = 12
  }

  case object Other extends WithName("other") with CharitablePurposes {
    override val order: Int = 13
  }

  val values: Seq[CharitablePurposes] = Seq(
    AmateurSport, AnimalWelfare, ArtsCultureHeritageScience, CitizenshipCommunity, Education, EnvironmentalProtection,
    Health, HumanRights,PromotionOfEfficiency, ReliefOfPoverty, ReliefOfThoseInNeed, Religion, Other
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[CheckboxItem] = values.zipWithIndex.map {
    case (value, index) =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(if(index==0){ "value" } else { "value-" + index.toString }),
        value = value.toString,
        content = Text(messages(s"charitablePurposes.${value.toString}")),
        checked = form.data.exists(_._2 == value.toString)
      )
  }

  implicit val enumerable: Enumerable[CharitablePurposes] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
