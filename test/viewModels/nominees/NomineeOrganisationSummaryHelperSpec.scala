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
import models.nominees.OrganisationNomineeContactDetails
import models.{BankDetails, CheckMode, Country, Name, Passport, SelectTitle, UserAnswers}
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.addressLookup.{OrganisationNomineeAddressLookupPage, OrganisationNomineePreviousAddressLookupPage}
import pages.nominees._
import service.CountryService
import viewmodels.SummaryListRowHelper
import viewmodels.nominees.NomineeOrganisationSummaryHelper

class NomineeOrganisationSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val year = 1991
  private val month = 1
  private val dayOfMonth = 2

  lazy val mockCountryService: CountryService = MockitoSugar.mock[CountryService]
  when(mockCountryService.find(meq("GB"))(any())).thenReturn(Some(Country("GB", "United Kingdom")))

  private lazy val baseUserAnswers: UserAnswers = UserAnswers("id")
    .set(OrganisationNomineeNamePage, "Company Inc")
    .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "company@inc.com")))
    .flatMap(_.set(OrganisationNomineeAddressLookupPage, ConfirmedAddressConstants.address))
    .flatMap(_.set(IsOrganisationNomineePreviousAddressPage, false))
    .flatMap(_.set(OrganisationNomineePreviousAddressLookupPage, ConfirmedAddressConstants.address))
    .flatMap(_.set(IsOrganisationNomineePaymentsPage, true))
    .flatMap(_.set(OrganisationNomineesBankDetailsPage, BankDetails(accountName = "PM Cares",
      sortCode = "176534",
      accountNumber = "43444546",
      rollNumber = Some("765431234"))))
    .flatMap(_.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones")))
    .flatMap(_.set(OrganisationAuthorisedPersonDOBPage, LocalDate.of(year, month, dayOfMonth))).success.value

  private val helperNino = new NomineeOrganisationSummaryHelper(mockCountryService)(baseUserAnswers
    .set(IsOrganisationNomineeNinoPage, true)
    .flatMap(_.set(OrganisationAuthorisedPersonNinoPage, "AB123123A"))
    .success.value)

  private val helperPassport = new NomineeOrganisationSummaryHelper(mockCountryService)(baseUserAnswers
    .set(IsOrganisationNomineeNinoPage, false)
    .flatMap(_.set(OrganisationAuthorisedPersonPassportPage, Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))))
    .success.value)


  "Nominee Type Check Your Answers Helper" must {

    "For the organisation name answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeName mustBe Some(summaryListRow(
          messages("nameOfOrganisation.checkYourAnswersLabel"),
          "Company Inc",
          Some(messages("nameOfOrganisation.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationNomineeNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }


    "For the organisation phone number answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeMainPhone mustBe Some(summaryListRow(
          messages("organisationContactDetails.phoneNumber.checkYourAnswersLabel"),
          "0123123123",
          Some(messages("organisationContactDetails.phoneNumber.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationNomineeContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation email address answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeEmailAddress mustBe Some(summaryListRow(
          messages("organisationContactDetails.email.checkYourAnswersLabel"),
          "company@inc.com",
          Some(messages("organisationContactDetails.email.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationNomineeContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation's address answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeAddress mustBe Some(summaryListRow(
          messages("organisationNomineeAddress.checkYourAnswersLabel"),
          "Test 1, Test 2, AA00 0AA, United Kingdom",
          Some(messages("organisationNomineeAddress.checkYourAnswersLabel")),
          controllers.addressLookup.routes.OrganisationNomineeAddressLookupController.initializeJourney(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the has the organisation's address changed answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeAddressChanged mustBe Some(summaryListRow(
          messages("isOrganisationNomineePreviousAddress.checkYourAnswersLabel"),
          messages("site.no"),
          Some(messages("isOrganisationNomineePreviousAddress.checkYourAnswersLabel")),
          nomineesRoutes.IsOrganisationNomineePreviousAddressController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the nominee's previous address answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineePreviousAddress mustBe Some(summaryListRow(
          messages("nomineeOrganisationPreviousAddress.checkYourAnswersLabel"),
          "Test 1, Test 2, AA00 0AA, United Kingdom",
          Some(messages("nomineeOrganisationPreviousAddress.checkYourAnswersLabel")),
          controllers.addressLookup.routes.OrganisationNomineePreviousAddressLookupController.initializeJourney(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the can organisation be paid answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeCanBePaid mustBe Some(summaryListRow(
          messages("isOrganisationNomineePayments.checkYourAnswersLabel"),
          messages("site.yes"),
          Some(messages("isOrganisationNomineePayments.checkYourAnswersLabel")),
          nomineesRoutes.IsOrganisationNomineePaymentsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation account name answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeAccountName mustBe Some(summaryListRow(
          messages("organisationNomineesBankDetails.accountName.checkYourAnswersLabel"),
          "PM Cares",
          Some(messages("organisationNomineesBankDetails.accountName.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation sort code answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeSortCode mustBe Some(summaryListRow(
          messages("organisationNomineesBankDetails.sortCode.checkYourAnswersLabel"),
          "176534",
          Some(messages("organisationNomineesBankDetails.sortCode.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation account number answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeAccountNumber mustBe Some(summaryListRow(
          messages("organisationNomineesBankDetails.accountNumber.checkYourAnswersLabel"),
          "43444546",
          Some(messages("organisationNomineesBankDetails.accountNumber.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation roll number answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeBuildingRoll mustBe Some(summaryListRow(
          messages("organisationNomineesBankDetails.rollNumber.checkYourAnswersLabel"),
          "765431234",
          Some(messages("organisationNomineesBankDetails.rollNumber.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation authorised person name answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.authorisedPersonName mustBe Some(summaryListRow(
          messages("organisationAuthorisedPersonName.checkYourAnswersLabel"),
          "Mr John Jones",
          Some(messages("organisationAuthorisedPersonName.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationAuthorisedPersonNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation authorised person DOB answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.authorisedPersonDOB mustBe Some(summaryListRow(
          messages("organisationAuthorisedPersonDOB.checkYourAnswersLabel"),
          "2 January 1991",
          Some(messages("organisationAuthorisedPersonDOB.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationAuthorisedPersonDOBController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the does the organisation authorised person have a NINO answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.authorisedPersonHasNino mustBe Some(summaryListRow(
          messages("isOrganisationNomineeNino.checkYourAnswersLabel"),
          messages("site.yes"),
          Some(messages("isOrganisationNomineeNino.checkYourAnswersLabel")),
          nomineesRoutes.IsOrganisationNomineeNinoController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation authorised person's nino answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.authorisedPersonNino mustBe Some(summaryListRow(
          messages("organisationAuthorisedPersonNino.checkYourAnswersLabel"),
          "AB123123A",
          Some(messages("organisationAuthorisedPersonNino.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationAuthorisedPersonNinoController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

    "For the organisation authorised person's passport answer" must {

      "have a correctly formatted summary list row for country of issue" in {
        helperPassport.authorisedPersonPassportCountry mustBe Some(summaryListRow(
          messages("organisationAuthorisedPersonPassport.country.checkYourAnswersLabel"),
          "United Kingdom",
          Some(messages("organisationAuthorisedPersonPassport.country.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationAuthorisedPersonPassportController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }

      "have a correctly formatted summary list row for passport number" in {
        helperPassport.authorisedPersonPassportNumber mustBe Some(summaryListRow(
          messages("organisationAuthorisedPersonPassport.passportNumber.checkYourAnswersLabel"),
          "GB12345",
          Some(messages("organisationAuthorisedPersonPassport.passportNumber.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationAuthorisedPersonPassportController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }

      "have a correctly formatted summary list row for expiry date" in {
        helperPassport.authorisedPersonPassportExpiry mustBe Some(summaryListRow(
          messages("organisationAuthorisedPersonPassport.expiryDate.checkYourAnswersLabel"),
          "2 January 1991",
          Some(messages("organisationAuthorisedPersonPassport.expiryDate.checkYourAnswersLabel")),
          nomineesRoutes.OrganisationAuthorisedPersonPassportController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

  }
}
