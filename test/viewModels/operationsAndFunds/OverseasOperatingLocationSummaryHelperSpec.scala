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

package viewModels.operationsAndFunds

import java.time.MonthDay

import base.SpecBase
import base.data.messages.BaseMessages
import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import models.operations.OperatingLocationOptions.{England, Overseas}
import models.operations.{FundRaisingOptions, OperatingLocationOptions}
import models.{Index, MongoDateTimeFormats, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{mock, when}
import pages.operationsAndFunds._
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import utils.CurrencyFormatter
import viewmodels.SummaryListRowHelper
import viewmodels.operationsAndFunds.OverseasOperatingLocationSummaryHelper

class OverseasOperatingLocationSummaryHelperSpec extends SpecBase with SummaryListRowHelper with CurrencyFormatter {

  private lazy val mockCountryService: CountryService = mock(classOf[CountryService])

  private val helper = new OverseasOperatingLocationSummaryHelper(
    UserAnswers("id")
      .set(FundRaisingPage, FundRaisingOptions.values.toSet)
      .flatMap(_.set(OperatingLocationPage, Set[OperatingLocationOptions](England, Overseas)))
      .flatMap(_.set(IsFinancialAccountsPage, true))
      .flatMap(_.set(EstimatedIncomePage, BigDecimal(1123.12)))
      .flatMap(_.set(ActualIncomePage, BigDecimal(11123.12)))
      .flatMap(
        _.set(WhatCountryDoesTheCharityOperateInPage(0), thCountryCode)
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), inCountryCode))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), frCountryCode))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(3), usCountryCode))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(4), chCountryCode))
      )
      .flatMap(_.set(IsBankStatementsPage, true))
      .flatMap(
        _.set(AccountingPeriodEndDatePage, MonthDay.from(correctFormatDate))(
          MongoDateTimeFormats.localDayMonthWrite
        )
      )
      .success
      .value,
    mockCountryService,
    NormalMode
  )

  when(mockCountryService.countries()(any()))
    .thenReturn(Seq(thCountryTuple, inCountryTuple, frCountryTuple, usCountryTuple, chCountryTuple))

  when(mockCountryService.find(meq(thCountryCode))(any())).thenReturn(Some(thCountry))
  when(mockCountryService.find(meq(inCountryCode))(any())).thenReturn(Some(inCountry))
  when(mockCountryService.find(meq(frCountryCode))(any())).thenReturn(Some(frCountry))
  when(mockCountryService.find(meq(usCountryCode))(any())).thenReturn(Some(usCountry))
  when(mockCountryService.find(meq(chCountryCode))(any())).thenReturn(Some(chCountry))

  "Overseas Operating Location Summary Helper" when {

    "overseas Operating Location Summary Row" must {

      "have a correctly formatted summary list row" in {

        helper.overseasOperatingLocationSummaryRow(0, onwardRoute) mustBe Some(
          summaryListRow(
            label = messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.1"),
            value = HtmlContent(thCountryName),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.1")),
            onwardRoute -> BaseMessages.delete
          )
        )
      }
    }

    "overseas Operating Location Summary multiple rows" must {

      "have a correctly formatted summary list row" in {

        helper.rows mustBe List(
          summaryListRow(
            label = messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.1"),
            value = HtmlContent(thCountryName),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.1")),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(0)) -> BaseMessages.delete
          ),
          summaryListRow(
            label = messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.2"),
            value = HtmlContent(inCountryName),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.2")),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(1)) -> BaseMessages.delete
          ),
          summaryListRow(
            label = messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.3"),
            value = HtmlContent(frCountryName),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.3")),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(2)) -> BaseMessages.delete
          ),
          summaryListRow(
            label = messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.4"),
            value = HtmlContent(usCountryName),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.4")),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(3)) -> BaseMessages.delete
          ),
          summaryListRow(
            label = messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.5"),
            value = HtmlContent(chCountryName),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel.5")),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(4)) -> BaseMessages.delete
          )
        )
      }
    }

    "overseas Operating Location Summary CYA Row" must {

      "have a correctly formatted summary list row" in {
        val countries =
          s"""<div>$thCountryName</div><div>$inCountryName</div><div>$frCountryName</div><div>$usCountryName</div><div>$chCountryName</div>"""

        helper.overseasOperatingLocationSummaryCYARow(onwardRoute) mustBe Some(
          summaryListRow(
            label = messages("overseasOperatingLocationSummary.checkYourAnswersLabel"),
            value = HtmlContent(countries),
            visuallyHiddenText = Some(messages(s"overseasOperatingLocationSummary.checkYourAnswersLabel")),
            onwardRoute -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
