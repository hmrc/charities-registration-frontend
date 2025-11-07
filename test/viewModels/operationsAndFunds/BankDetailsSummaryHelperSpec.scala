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

import base.SpecBase
import base.data.messages.BaseMessages
import controllers.operationsAndFunds.{routes => operationFundsRoutes}
import models.{BankDetails, CheckMode, UserAnswers}
import pages.operationsAndFunds.BankDetailsPage
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.SummaryListRowHelper
import viewmodels.operationsAndFunds.BankDetailsSummaryHelper

class BankDetailsSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val helper = new BankDetailsSummaryHelper(
    UserAnswers("id")
      .set(
        BankDetailsPage,
        bankDetails
      )
      .success
      .value
  )

  "Bank Details Check Your Answers Helper" must {

    "For the Account Name answer" must {

      "have a correctly formatted summary list rows" in {

        helper.rows mustBe Seq(
          summaryListRow(
            messages("bankDetails.accountName.checkYourAnswersLabel"),
            HtmlContent(accountName),
            Some(messages("bankDetails.accountName.checkYourAnswersLabel")),
            operationFundsRoutes.BankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          ),
          summaryListRow(
            messages("bankDetails.sortCode.checkYourAnswersLabel"),
            HtmlContent(sortCode),
            Some(messages("bankDetails.sortCode.checkYourAnswersLabel")),
            operationFundsRoutes.BankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          ),
          summaryListRow(
            messages("bankDetails.accountNumber.checkYourAnswersLabel"),
            HtmlContent(accountNumber),
            Some(messages("bankDetails.accountNumber.checkYourAnswersLabel")),
            operationFundsRoutes.BankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          ),
          summaryListRow(
            messages("bankDetails.rollNumber.checkYourAnswersLabel"),
            HtmlContent(rollNumber),
            Some(messages("bankDetails.rollNumber.checkYourAnswersLabel")),
            operationFundsRoutes.BankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
