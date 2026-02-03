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
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

enum FundRaisingOptions(val name: String, val order: Int) extends WithOrder {
  override def toString: String = name
  case Donations                 extends FundRaisingOptions("donations", 1)
  private[FundRaisingOptions]
  case Fundraising               extends FundRaisingOptions("fundraising", 2)
  private[FundRaisingOptions]
  case Grants                    extends FundRaisingOptions("grants", 3)
  private[FundRaisingOptions]
  case MembershipSubscriptions   extends FundRaisingOptions("membershipSubscriptions", 4)
  private[FundRaisingOptions]
  case TradingIncome             extends FundRaisingOptions("tradingIncome", 5)
  private[FundRaisingOptions]
  case TradingSubsidiaries       extends FundRaisingOptions("tradingSubsidiaries", 6)
  private[FundRaisingOptions]
  case InvestmentIncome          extends FundRaisingOptions("investmentIncome", 7)
  case Other                     extends FundRaisingOptions("other", 8)
}

object FundRaisingOptions extends Enumerable.Implicits {

  given Ordering[FundRaisingOptions] =
    Ordering.by(_.ordinal)

  val valuesIndexed: Seq[FundRaisingOptions] = values.toIndexedSeq

  def options(form: Form[?])(implicit messages: Messages): Seq[CheckboxItem] =
    valuesIndexed.zipWithIndex.map { case (value, index) =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(if (index == 0) {
          "value"
        } else {
          "value-" + index.toString
        }),
        value = value.toString,
        content = Text(messages(s"selectFundRaising.${value.toString}")),
        checked = form.data.exists(_._2 == value.toString)
      )
    }

  implicit val enumerable: Enumerable[FundRaisingOptions] =
    Enumerable(valuesIndexed.map(v => v.toString -> v)*)
}
