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

package models.authOfficials

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait OfficialsPosition

object OfficialsPosition extends Enumerable.Implicits {

  case object BoardMember extends WithName("01") with OfficialsPosition
  case object Bursar extends WithName("02") with OfficialsPosition
  case object Chairman extends WithName("03") with OfficialsPosition
  case object ChiefExecutive extends WithName("04") with OfficialsPosition
  case object Director extends WithName("05") with OfficialsPosition
  case object Employee extends WithName("06") with OfficialsPosition
  case object FinanceManager extends WithName("07") with OfficialsPosition
  case object FinancialAccountant extends WithName("08") with OfficialsPosition
  case object GiftAidSecretary extends WithName("09") with OfficialsPosition
  case object Governor extends WithName("10") with OfficialsPosition
  case object HeadTeacher extends WithName("11") with OfficialsPosition
  case object AssistantHeadTeacher extends WithName("12") with OfficialsPosition
  case object HumanResourcesManager extends WithName("13") with OfficialsPosition
  case object InformationOfficer extends WithName("14") with OfficialsPosition
  case object MinisterOfReligion extends WithName("15") with OfficialsPosition
  case object Principal extends WithName("16") with OfficialsPosition
  case object Secretary extends WithName("17") with OfficialsPosition
  case object AssistantSecretary extends WithName("18") with OfficialsPosition
  case object Teacher extends WithName("19") with OfficialsPosition
  case object Treasurer extends WithName("20") with OfficialsPosition
  case object AssistantTreasurer extends WithName("21") with OfficialsPosition
  case object Trustee extends WithName("22") with OfficialsPosition
  case object UKAgent extends WithName("23") with OfficialsPosition

  val values: Seq[OfficialsPosition] = Seq(
    BoardMember, Bursar, Chairman, ChiefExecutive, Director, Employee, FinanceManager, FinancialAccountant,
    GiftAidSecretary, Governor, HeadTeacher, AssistantHeadTeacher, HumanResourcesManager, InformationOfficer,
    MinisterOfReligion, Principal, Secretary, AssistantSecretary, Teacher, Treasurer, AssistantTreasurer, Trustee, UKAgent
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(s"officialsPosition.${value.toString}")),
        checked = form("value").value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[OfficialsPosition] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
