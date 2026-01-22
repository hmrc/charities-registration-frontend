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

package models.authOfficials

import models.Enumerable
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import play.api.libs.json._

enum OfficialsPosition(val code: String) {
  override def toString: String = code

  case BoardMember extends OfficialsPosition("01")
  case Bursar extends OfficialsPosition("02")
  private case Chairman extends OfficialsPosition("03")
  private case ChiefExecutive extends OfficialsPosition("04")
  case Director extends OfficialsPosition("05")
  private case Employee extends OfficialsPosition("06")
  private case FinanceManager extends OfficialsPosition("07")
  private case FinancialAccountant extends OfficialsPosition("08")
  private case GiftAidSecretary extends OfficialsPosition("09")
  private case Governor extends OfficialsPosition("10")
  private case HeadTeacher extends OfficialsPosition("11")
  private case AssistantHeadTeacher extends OfficialsPosition("12")
  private case HumanResourcesManager extends OfficialsPosition("13")
  private case InformationOfficer extends OfficialsPosition("14")
  private case MinisterOfReligion extends OfficialsPosition("15")
  private case Principal extends OfficialsPosition("16")
  private case Secretary extends OfficialsPosition("17")
  private case AssistantSecretary extends OfficialsPosition("18")
  private case Teacher extends OfficialsPosition("19")
  private case Treasurer extends OfficialsPosition("20")
  private case AssistantTreasurer extends OfficialsPosition("21")
  private case Trustee extends OfficialsPosition("22")
  case UKAgent extends OfficialsPosition("23")
}


object OfficialsPosition extends Enumerable.Implicits {
  
  def options(form: Form[?])(implicit messages: Messages): Seq[RadioItem] = values.toIndexedSeq.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"officialsPosition.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[OfficialsPosition] =
    Enumerable(values.map(v => v.toString -> v)*)

  implicit def reads: Reads[OfficialsPosition] = Reads[OfficialsPosition] {
    case JsString(BoardMember.code)           => JsSuccess(BoardMember)
    case JsString(Bursar.code)                => JsSuccess(Bursar)
    case JsString(Chairman.code)              => JsSuccess(Chairman)
    case JsString(ChiefExecutive.code)        => JsSuccess(ChiefExecutive)
    case JsString(Director.code)              => JsSuccess(Director)
    case JsString(Employee.code)              => JsSuccess(Employee)
    case JsString(FinanceManager.code)        => JsSuccess(FinanceManager)
    case JsString(FinancialAccountant.code)   => JsSuccess(FinancialAccountant)
    case JsString(GiftAidSecretary.code)      => JsSuccess(GiftAidSecretary)
    case JsString(Governor.code)              => JsSuccess(Governor)
    case JsString(HeadTeacher.code)           => JsSuccess(HeadTeacher)
    case JsString(AssistantHeadTeacher.code)  => JsSuccess(AssistantHeadTeacher)
    case JsString(HumanResourcesManager.code) => JsSuccess(HumanResourcesManager)
    case JsString(InformationOfficer.code)    => JsSuccess(InformationOfficer)
    case JsString(MinisterOfReligion.code)    => JsSuccess(MinisterOfReligion)
    case JsString(Principal.code)             => JsSuccess(Principal)
    case JsString(Secretary.code)             => JsSuccess(Secretary)
    case JsString(AssistantSecretary.code)    => JsSuccess(AssistantSecretary)
    case JsString(Teacher.code)               => JsSuccess(Teacher)
    case JsString(Treasurer.code)             => JsSuccess(Treasurer)
    case JsString(AssistantTreasurer.code)    => JsSuccess(AssistantTreasurer)
    case JsString(Trustee.code)               => JsSuccess(Trustee)
    case JsString(UKAgent.code)               => JsSuccess(UKAgent)
    case _                                        => JsError("error.invalid")
  }

  implicit def writes: Writes[OfficialsPosition] =
    Writes(value => JsString(value.toString))

}
