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

package viewmodels.operationsAndFunds

import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import models.{Index, NormalMode, UserAnswers}
import pages.operationsAndFunds.WhatCountryDoesTheCharityOperateInPage
import play.api.i18n.Messages
import play.api.mvc.Call
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.{CurrencyFormatter, ImplicitDateFormatter}
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class OverseasOperatingLocationSummaryHelper(override val userAnswers: UserAnswers, val countryService: CountryService)
                                            (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
    with SummaryListRowHelper with CurrencyFormatter {

    def overseasOperatingLocationSummaryRow(page: WhatCountryDoesTheCharityOperateInPage, index: Int,
                                            changeLinkCall: Call): Option[SummaryListRow] = {
      userAnswers.get(page).map{ code =>
        summaryListRow(
          label = messages("overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", index + 1),
          value = countryService.find(code).fold(code)(_.name),
          visuallyHiddenText = Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", index + 1)),
          changeLinkCall -> messages("site.delete")
        )
      }
    }

    lazy val rows: Seq[SummaryListRow] = {
      val result = for(i <- 0 to 4) yield  {
        overseasOperatingLocationSummaryRow(WhatCountryDoesTheCharityOperateInPage(i), i,
          operationFundsRoutes.WhatCountryDoesTheCharityOperateInController.onRemove(NormalMode, Index(i)))
      }
      result.flatten
    }


  def overseasOperatingLocationSummaryCYARow(page: WhatCountryDoesTheCharityOperateInPage,
                                          changeLinkCall: Call): Option[SummaryListRow] = {

    val result1 = for(i <- 0 to 4) yield userAnswers.get(WhatCountryDoesTheCharityOperateInPage(i))
    val ans = result1.filter(_.nonEmpty).flatten

    userAnswers.get(page).map{ _ =>
      summaryListRow(
        label = messages("overseasOperatingLocationSummary.checkYourAnswersLabel"),
        value = ans.foldLeft("")(
          (accumulator,code) => accumulator + "<div>" + countryService.find(code).fold(code)(_.name) + "</div>"),
        visuallyHiddenText = Some(messages(s"overseasOperatingLocationSummary.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }
  }
}
