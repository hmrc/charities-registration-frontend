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

enum SelectGoverningDocument(val name: String) {

  override def toString: String = name

  case MemorandumArticlesAssociation
    extends SelectGoverningDocument("2")

  case RoyalCharacter
    extends SelectGoverningDocument("6")

  case RulesConstitution
    extends SelectGoverningDocument("1")

  case TrustDeed
    extends SelectGoverningDocument("3")

  case Will
    extends SelectGoverningDocument("4")

  case Other
    extends SelectGoverningDocument("7")
}
object SelectGoverningDocument extends Enumerable.Implicits {
  

  def options(form: Form[?])(implicit messages: Messages): Seq[RadioItem] = values.toIndexedSeq.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"selectGoverningDocument.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[SelectGoverningDocument] =
    Enumerable(values.map(v => v.toString -> v)*)

  implicit def reads: Reads[SelectGoverningDocument] = Reads[SelectGoverningDocument] {
    case JsString(MemorandumArticlesAssociation.name) => JsSuccess(MemorandumArticlesAssociation)
    case JsString(RoyalCharacter.name)                => JsSuccess(RoyalCharacter)
    case JsString(RulesConstitution.name)             => JsSuccess(RulesConstitution)
    case JsString(TrustDeed.name)                     => JsSuccess(TrustDeed)
    case JsString(Will.name)                          => JsSuccess(Will)
    case JsString(Other.name)                         => JsSuccess(Other)
    case _                                                => JsError("error.invalid")
  }

  implicit def writes: Writes[SelectGoverningDocument] =
    Writes(value => JsString(value.toString))

}
