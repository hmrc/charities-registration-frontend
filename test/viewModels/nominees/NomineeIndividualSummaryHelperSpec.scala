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

package viewModels.nominees

import base.SpecBase
import base.data.constants.ConfirmedAddressConstants
import base.data.messages.BaseMessages
import controllers.nominees.{routes => nomineesRoutes}
import models.{BankDetails, CheckMode, Country, Name, Passport, PhoneNumber, SelectTitle, UserAnswers}
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{mock, when}
import pages.addressLookup.{NomineeIndividualAddressLookupPage, NomineeIndividualPreviousAddressLookupPage}
import pages.nominees._
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.SummaryListRowHelper
import viewmodels.nominees.NomineeIndividualSummaryHelper

import java.time.LocalDate

class NomineeIndividualSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val year       = 1991
  private val month      = 1
  private val dayOfMonth = 2

  lazy val mockCountryService: CountryService = mock(classOf[CountryService])
  when(mockCountryService.find(meq("GB"))(any())).thenReturn(Some(Country("GB", "United Kingdom")))
  when(mockCountryService.find(meq("Unknown"))(any())).thenReturn(None)

  private val helper = new NomineeIndividualSummaryHelper(mockCountryService)(
    UserAnswers("id")
      .set(IndividualNomineeNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
      .flatMap(_.set(IndividualNomineeDOBPage, LocalDate.of(year, month, dayOfMonth)))
      .flatMap(_.set(IndividualNomineesPhoneNumberPage, PhoneNumber("0123123123", Some("0123123124"))))
      .flatMap(_.set(IsIndividualNomineeNinoPage, true))
      .flatMap(_.set(IndividualNomineesNinoPage, "AB123123A"))
      .flatMap(_.set(NomineeIndividualAddressLookupPage, ConfirmedAddressConstants.address))
      .flatMap(_.set(IsIndividualNomineePreviousAddressPage, true))
      .flatMap(_.set(NomineeIndividualPreviousAddressLookupPage, ConfirmedAddressConstants.address))
      .flatMap(_.set(IsIndividualNomineePaymentsPage, true))
      .flatMap(_.set(IndividualNomineesBankDetailsPage, bankDetails))
      .flatMap(_.set(IndividualNomineesPassportPage, Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))))
      .success
      .value
  )

  "Nominee Type Check Your Answers Helper" must {

    "For the nominee name answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeName mustBe Some(
          summaryListRow(
            messages("individualNomineeName.checkYourAnswersLabel"),
            HtmlContent("Mr John Jones"),
            Some(messages("individualNomineeName.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineeNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee DOB answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeDOB mustBe Some(
          summaryListRow(
            messages("individualNomineeDOB.checkYourAnswersLabel"),
            HtmlContent("2 January 1991"),
            Some(messages("individualNomineeDOB.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineeDOBController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee main phone answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeMainPhone mustBe Some(
          summaryListRow(
            messages("individualNomineesPhoneNumber.mainPhoneNumber.checkYourAnswersLabel"),
            HtmlContent("0123123123"),
            Some(messages("individualNomineesPhoneNumber.mainPhoneNumber.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineesPhoneNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee alternative phone answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAltPhone mustBe Some(
          summaryListRow(
            messages("individualNomineesPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel"),
            HtmlContent("0123123124"),
            Some(messages("individualNomineesPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineesPhoneNumberController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the does the nominee have a NINO answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeHasNino mustBe Some(
          summaryListRow(
            messages("isIndividualNomineeNino.checkYourAnswersLabel"),
            HtmlContent(messages("site.yes")),
            Some(messages("isIndividualNomineeNino.checkYourAnswersLabel")),
            nomineesRoutes.IsIndividualNomineeNinoController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Nominees Passport answers" must {

      "have a correctly formatted summary list rows for passport number" in {

        helper.nomineePassportNumber mustBe Some(
          summaryListRow(
            messages("individualNomineesPassport.passportNumber.checkYourAnswersLabel"),
            HtmlContent("GB12345"),
            Some(messages("individualNomineesPassport.passportNumber.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineePassportController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list rows for country of issue" in {

        helper.nomineePassportCountry mustBe Some(
          summaryListRow(
            messages("individualNomineesPassport.country.checkYourAnswersLabel"),
            HtmlContent("United Kingdom"),
            Some(messages("individualNomineesPassport.country.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineePassportController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list rows for expiry date" in {

        helper.nomineePassportExpiry mustBe Some(
          summaryListRow(
            messages("individualNomineesPassport.expiryDate.checkYourAnswersLabel"),
            HtmlContent("2 January 1991"),
            Some(messages("individualNomineesPassport.expiryDate.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineePassportController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the does the nominee's nino answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeNino mustBe Some(
          summaryListRow(
            messages("individualNomineesNino.checkYourAnswersLabel"),
            HtmlContent("AB123123A"),
            Some(messages("individualNomineesNino.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineesNinoController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee's address answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAddress mustBe Some(
          summaryListRow(
            messages("nomineeIndividualAddress.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, United Kingdom"),
            Some(messages("nomineeIndividualAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.NomineeIndividualAddressLookupController
              .initializeJourney(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the has the nominee's address changed answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAddressChanged mustBe Some(
          summaryListRow(
            messages("isIndividualNomineePreviousAddress.checkYourAnswersLabel"),
            HtmlContent(messages("site.yes")),
            Some(messages("isIndividualNomineePreviousAddress.checkYourAnswersLabel")),
            nomineesRoutes.IsIndividualNomineePreviousAddressController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee's previous address answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineePreviousAddress mustBe Some(
          summaryListRow(
            messages("nomineeIndividualPreviousAddress.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, United Kingdom"),
            Some(messages("nomineeIndividualPreviousAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.NomineeIndividualPreviousAddressLookupController
              .initializeJourney(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the can nominee be paid answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeCanBePaid mustBe Some(
          summaryListRow(
            messages("isIndividualNomineePayments.checkYourAnswersLabel"),
            HtmlContent(messages("site.yes")),
            Some(messages("isIndividualNomineePayments.checkYourAnswersLabel")),
            nomineesRoutes.IsIndividualNomineePaymentsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee account name answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAccountName mustBe Some(
          summaryListRow(
            messages("individualNomineesBankDetails.accountName.checkYourAnswersLabel"),
            HtmlContent(accountName),
            Some(messages("individualNomineesBankDetails.accountName.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee sort code answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeSortCode mustBe Some(
          summaryListRow(
            messages("individualNomineesBankDetails.sortCode.checkYourAnswersLabel"),
            HtmlContent(sortCode),
            Some(messages("individualNomineesBankDetails.sortCode.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee account number answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeAccountNumber mustBe Some(
          summaryListRow(
            messages("individualNomineesBankDetails.accountNumber.checkYourAnswersLabel"),
            HtmlContent(accountNumber),
            Some(messages("individualNomineesBankDetails.accountNumber.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee roll number answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeBuildingRoll mustBe Some(
          summaryListRow(
            messages("individualNomineesBankDetails.rollNumber.checkYourAnswersLabel"),
            HtmlContent(rollNumber),
            Some(messages("individualNomineesBankDetails.rollNumber.checkYourAnswersLabel")),
            nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
