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

package viewModels.nominees

import java.time.LocalDate

import assets.constants.ConfirmedAddressConstants
import assets.messages.BaseMessages
import base.SpecBase
import controllers.nominees.{routes => nomineesRoutes}
import models.{BankDetails, CheckMode, Name, PhoneNumber, SelectTitle, UserAnswers}
import pages.addressLookup.NomineeIndividualAddressLookupPage
import pages.nominees._
import viewmodels.SummaryListRowHelper
import viewmodels.nominees.NomineeIndividualSummaryHelper

class NomineeIndividualSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val year = 1991
  private val month = 1
  private val dayOfMonth = 2

  private val helper = new NomineeIndividualSummaryHelper(UserAnswers("id")
    .set(IndividualNomineeNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
    .flatMap(_.set(IndividualNomineeDOBPage, LocalDate.of(year, month, dayOfMonth)))
    .flatMap(_.set(IndividualNomineesPhoneNumberPage, PhoneNumber("0123123123", "0123123124")))
    .flatMap(_.set(IsIndividualNomineeNinoPage, true))
    .flatMap(_.set(IndividualNomineesNinoPage, "AB123123A"))
    .flatMap(_.set(NomineeIndividualAddressLookupPage, ConfirmedAddressConstants.address))
    .flatMap(_.set(IsIndividualNomineePreviousAddressPage, false))
    .flatMap(_.set(IsIndividualNomineePaymentsPage, true))
    .flatMap(_.set(IndividualNomineesBankDetailsPage, BankDetails(accountName = "PM Cares",
      sortCode = "176534",
      accountNumber = "43444546",
      rollNumber = Some("765431234"))))
    .success.value)


  "Nominee Type Check Your Answers Helper" must {

    "For the nominee name answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeName mustBe Some(summaryListRow(
          messages("individualNomineeName.checkYourAnswersLabel"),
          "Mr John Jones",
          Some(messages("individualNomineeName.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineeNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee DOB answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeDOB mustBe Some(summaryListRow(
          messages("individualNomineeDOB.checkYourAnswersLabel"),
          "2 January 1991",
          Some(messages("individualNomineeDOB.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineeDOBController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee main phone answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeMainPhone mustBe Some(summaryListRow(
          messages("individualNomineesPhoneNumber.mainPhoneNumber.checkYourAnswersLabel"),
          "0123123123",
          Some(messages("individualNomineesPhoneNumber.mainPhoneNumber.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineesPhoneNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee alternative phone answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAltPhone mustBe Some(summaryListRow(
          messages("individualNomineesPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel"),
          "0123123124",
          Some(messages("individualNomineesPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineesPhoneNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the does the nominee have a NINO answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeHasNino mustBe Some(summaryListRow(
          messages("isIndividualNomineeNino.checkYourAnswersLabel"),
          messages("site.yes"),
          Some(messages("isIndividualNomineeNino.checkYourAnswersLabel")),
          nomineesRoutes.IsIndividualNomineeNinoController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the does the nominee's nino answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeNino mustBe Some(summaryListRow(
          messages("individualNomineesNino.checkYourAnswersLabel"),
          "AB123123A",
          Some(messages("individualNomineesNino.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineesNinoController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee's address answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAddress mustBe Some(summaryListRow(
          messages("nomineeIndividualAddress.checkYourAnswersLabel"),
          "Test 1, Test 2, AA00 0AA, United Kingdom",
          Some(messages("nomineeIndividualAddress.checkYourAnswersLabel")),
          controllers.addressLookup.routes.NomineeIndividualAddressLookupController.initializeJourney(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the has the nominee's address changed answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAddressChanged mustBe Some(summaryListRow(
          messages("isIndividualNomineePreviousAddress.checkYourAnswersLabel"),
          messages("site.no"),
          Some(messages("isIndividualNomineePreviousAddress.checkYourAnswersLabel")),
          nomineesRoutes.IsIndividualNomineePreviousAddressController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the can nominee be paid answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeCanBePaid mustBe Some(summaryListRow(
          messages("isIndividualNomineePayments.checkYourAnswersLabel"),
          messages("site.yes"),
          Some(messages("isIndividualNomineePayments.checkYourAnswersLabel")),
          nomineesRoutes.IsIndividualNomineePaymentsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee account name answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAccountName mustBe Some(summaryListRow(
          messages("individualNomineesBankDetails.accountName.checkYourAnswersLabel"),
          "PM Cares",
          Some(messages("individualNomineesBankDetails.accountName.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee sort code answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeSortCode mustBe Some(summaryListRow(
          messages("individualNomineesBankDetails.sortCode.checkYourAnswersLabel"),
          "176534",
          Some(messages("individualNomineesBankDetails.sortCode.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee account number answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAccountNumber mustBe Some(summaryListRow(
          messages("individualNomineesBankDetails.accountNumber.checkYourAnswersLabel"),
          "43444546",
          Some(messages("individualNomineesBankDetails.accountNumber.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee roll number answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeBuildingRoll mustBe Some(summaryListRow(
          messages("individualNomineesBankDetails.rollNumber.checkYourAnswersLabel"),
          "765431234",
          Some(messages("individualNomineesBankDetails.rollNumber.checkYourAnswersLabel")),
          nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }
  }
}
