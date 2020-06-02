/*
 * Copyright 2020 HM Revenue & Customs
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
import pages.QuestionPage
import pages.regulatorsAndDocuments._
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

sealed trait CharityRegulator

object CharityRegulator extends Enumerable.Implicits {

  case object EnglandWales extends WithName("ccew") with CharityRegulator
  case object Scottish extends WithName("oscr") with CharityRegulator
  case object NorthernIreland extends WithName("ccni") with CharityRegulator
  case object Other extends WithName("other") with CharityRegulator

  val values: Seq[CharityRegulator] = Seq(
    EnglandWales, Scottish, NorthernIreland, Other
  )

  val pageMap: Map[CharityRegulator, QuestionPage[_]] = Map(
    EnglandWales -> CharityCommissionRegistrationNumberPage,
    Scottish -> ScottishRegulatorRegNumberPage,
    NorthernIreland -> NIRegulatorRegNumberPage,
    Other -> CharityOtherRegulatorDetailsPage
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[CheckboxItem] = values.map {
    value =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(value.toString),
        value = value.toString,
        content = Text(messages(s"charityRegulator.${value.toString}")),
        checked = form.data.exists(_._2 == value.toString)
      )
  }

  implicit val enumerable: Enumerable[CharityRegulator] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
