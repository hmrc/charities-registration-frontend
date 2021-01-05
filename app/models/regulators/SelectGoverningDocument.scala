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

package models.regulators

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait SelectGoverningDocument

object SelectGoverningDocument extends Enumerable.Implicits {

  case object MemorandumArticlesAssociation extends WithName("1") with SelectGoverningDocument
  case object RoyalCharacter extends WithName("2") with SelectGoverningDocument
  case object RulesConstitution extends WithName("3") with SelectGoverningDocument
  case object TrustDeed extends WithName("4") with SelectGoverningDocument
  case object Will extends WithName("5") with SelectGoverningDocument
  case object Other extends WithName("6") with SelectGoverningDocument

  val values: Seq[SelectGoverningDocument] = Seq(
    MemorandumArticlesAssociation, RoyalCharacter, RulesConstitution, TrustDeed, Will, Other
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(s"selectGoverningDocument.${value.toString}")),
        checked = form("value").value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[SelectGoverningDocument] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
