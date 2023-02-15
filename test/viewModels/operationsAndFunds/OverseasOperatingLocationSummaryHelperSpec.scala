/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import base.data.messages.BaseMessages
import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import models.operations.OperatingLocationOptions.{England, Overseas}
import models.operations.{FundRaisingOptions, OperatingLocationOptions}
import models.{Country, Index, MongoDateTimeFormats, NormalMode, UserAnswers}
import org.joda.time.{LocalDate, MonthDay}
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.when
import org.mockito.MockitoSugar
import pages.operationsAndFunds._
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import utils.CurrencyFormatter
import viewmodels.SummaryListRowHelper
import viewmodels.operationsAndFunds.OverseasOperatingLocationSummaryHelper

class OverseasOperatingLocationSummaryHelperSpec extends SpecBase with SummaryListRowHelper with CurrencyFormatter {

  lazy val mockCountryService: CountryService = MockitoSugar.mock[CountryService]

  private val helper = new OverseasOperatingLocationSummaryHelper(
    UserAnswers("id")
      .set(FundRaisingPage, FundRaisingOptions.values.toSet)
      .flatMap(_.set(OperatingLocationPage, Set[OperatingLocationOptions](England, Overseas)))
      .flatMap(_.set(IsFinancialAccountsPage, true))
      .flatMap(_.set(EstimatedIncomePage, BigDecimal.valueOf(1123.12)))
      .flatMap(_.set(ActualIncomePage, BigDecimal.valueOf(11123.12)))
      .flatMap(
        _.set(WhatCountryDoesTheCharityOperateInPage(0), "TH")
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "IN"))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), "PT"))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(3), "PY"))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(4), "AF"))
      )
      .flatMap(_.set(IsBankStatementsPage, true))
      .flatMap(
        _.set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new LocalDate(2020, 10, 1).toDate))(
          MongoDateTimeFormats.localDayMonthWrite
        )
      )
      .success
      .value,
    mockCountryService,
    NormalMode
  )

  when(mockCountryService.countries()(any()))
    .thenReturn(Seq(("TH", "Thai"), ("IN", "India"), ("PT", "Portugal"), ("PY", "Paraguay"), ("AF", "Afghanistan")))

  when(mockCountryService.find(meq("TH"))(any())).thenReturn(Some(Country("TH", "Thai")))
  when(mockCountryService.find(meq("IN"))(any())).thenReturn(Some(Country("IN", "India")))
  when(mockCountryService.find(meq("PT"))(any())).thenReturn(Some(Country("PT", "Portugal")))
  when(mockCountryService.find(meq("PY"))(any())).thenReturn(Some(Country("PY", "Paraguay")))
  when(mockCountryService.find(meq("AF"))(any())).thenReturn(Some(Country("AF", "Afghanistan")))

  "Overseas Operating Location Summary Helper" must {

    "overseas Operating Location Summary Row" must {

      "have a correctly formatted summary list row" in {

        helper.overseasOperatingLocationSummaryRow(0, onwardRoute) mustBe Some(
          summaryListRow(
            label = messages("overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 1),
            value = HtmlContent("Thai"),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 1)),
            onwardRoute -> BaseMessages.delete
          )
        )
      }
    }

    "overseas Operating Location Summary multiple rows" must {

      "have a correctly formatted summary list row" in {

        helper.rows mustBe List(
          summaryListRow(
            label = messages("overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 1),
            value = HtmlContent("Thai"),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 1)),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(0)) -> BaseMessages.delete
          ),
          summaryListRow(
            label = messages("overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 2),
            value = HtmlContent("India"),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 2)),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(1)) -> BaseMessages.delete
          ),
          summaryListRow(
            label = messages("overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 3),
            value = HtmlContent("Portugal"),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 3)),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(2)) -> BaseMessages.delete
          ),
          summaryListRow(
            label = messages("overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 4),
            value = HtmlContent("Paraguay"),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 4)),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(3)) -> BaseMessages.delete
          ),
          summaryListRow(
            label = messages("overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 5),
            value = HtmlContent("Afghanistan"),
            visuallyHiddenText =
              Some(messages(s"overseasOperatingLocationSummary.addAnotherCountry.checkYourAnswersLabel", 5)),
            operationFundsRoutes.IsRemoveOperatingCountryController
              .onPageLoad(NormalMode, Index(4)) -> BaseMessages.delete
          )
        )
      }
    }

    "overseas Operating Location Summary CYA Row" must {

      "have a correctly formatted summary list row" in {
        val countries =
          s"""<div>Thai</div><div>India</div><div>Portugal</div><div>Paraguay</div><div>Afghanistan</div>"""

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
