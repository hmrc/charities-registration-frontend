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

package models.authOfficials

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import play.api.libs.json._

sealed trait OfficialsPosition

object OfficialsPosition extends Enumerable.Implicits {

  case object BoardMember extends WithName("01") with OfficialsPosition
  case object Bursar extends WithName("02") with OfficialsPosition
  private case object Chairman extends WithName("03") with OfficialsPosition
  private case object ChiefExecutive extends WithName("04") with OfficialsPosition
  case object Director extends WithName("05") with OfficialsPosition
  private case object Employee extends WithName("06") with OfficialsPosition
  private case object FinanceManager extends WithName("07") with OfficialsPosition
  private case object FinancialAccountant extends WithName("08") with OfficialsPosition
  private case object GiftAidSecretary extends WithName("09") with OfficialsPosition
  private case object Governor extends WithName("10") with OfficialsPosition
  private case object HeadTeacher extends WithName("11") with OfficialsPosition
  private case object AssistantHeadTeacher extends WithName("12") with OfficialsPosition
  private case object HumanResourcesManager extends WithName("13") with OfficialsPosition
  private case object InformationOfficer extends WithName("14") with OfficialsPosition
  private case object MinisterOfReligion extends WithName("15") with OfficialsPosition
  private case object Principal extends WithName("16") with OfficialsPosition
  private case object Secretary extends WithName("17") with OfficialsPosition
  private case object AssistantSecretary extends WithName("18") with OfficialsPosition
  private case object Teacher extends WithName("19") with OfficialsPosition
  private case object Treasurer extends WithName("20") with OfficialsPosition
  private case object AssistantTreasurer extends WithName("21") with OfficialsPosition
  private case object Trustee extends WithName("22") with OfficialsPosition
  case object UKAgent extends WithName("23") with OfficialsPosition

  val values: Seq[OfficialsPosition] = Seq(
    BoardMember,
    Bursar,
    Chairman,
    ChiefExecutive,
    Director,
    Employee,
    FinanceManager,
    FinancialAccountant,
    GiftAidSecretary,
    Governor,
    HeadTeacher,
    AssistantHeadTeacher,
    HumanResourcesManager,
    InformationOfficer,
    MinisterOfReligion,
    Principal,
    Secretary,
    AssistantSecretary,
    Teacher,
    Treasurer,
    AssistantTreasurer,
    Trustee,
    UKAgent
  )

  def options(form: Form[?])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"officialsPosition.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[OfficialsPosition] =
    Enumerable(values.map(v => v.toString -> v)*)

  implicit def reads: Reads[OfficialsPosition] = Reads[OfficialsPosition] {
    case JsString(BoardMember.toString)           => JsSuccess(BoardMember)
    case JsString(Bursar.toString)                => JsSuccess(Bursar)
    case JsString(Chairman.toString)              => JsSuccess(Chairman)
    case JsString(ChiefExecutive.toString)        => JsSuccess(ChiefExecutive)
    case JsString(Director.toString)              => JsSuccess(Director)
    case JsString(Employee.toString)              => JsSuccess(Employee)
    case JsString(FinanceManager.toString)        => JsSuccess(FinanceManager)
    case JsString(FinancialAccountant.toString)   => JsSuccess(FinancialAccountant)
    case JsString(GiftAidSecretary.toString)      => JsSuccess(GiftAidSecretary)
    case JsString(Governor.toString)              => JsSuccess(Governor)
    case JsString(HeadTeacher.toString)           => JsSuccess(HeadTeacher)
    case JsString(AssistantHeadTeacher.toString)  => JsSuccess(AssistantHeadTeacher)
    case JsString(HumanResourcesManager.toString) => JsSuccess(HumanResourcesManager)
    case JsString(InformationOfficer.toString)    => JsSuccess(InformationOfficer)
    case JsString(MinisterOfReligion.toString)    => JsSuccess(MinisterOfReligion)
    case JsString(Principal.toString)             => JsSuccess(Principal)
    case JsString(Secretary.toString)             => JsSuccess(Secretary)
    case JsString(AssistantSecretary.toString)    => JsSuccess(AssistantSecretary)
    case JsString(Teacher.toString)               => JsSuccess(Teacher)
    case JsString(Treasurer.toString)             => JsSuccess(Treasurer)
    case JsString(AssistantTreasurer.toString)    => JsSuccess(AssistantTreasurer)
    case JsString(Trustee.toString)               => JsSuccess(Trustee)
    case JsString(UKAgent.toString)               => JsSuccess(UKAgent)
    case _                                        => JsError("error.invalid")
  }

  implicit def writes: Writes[OfficialsPosition] =
    Writes(value => JsString(value.toString))

}
