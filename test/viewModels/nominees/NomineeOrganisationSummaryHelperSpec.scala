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
import controllers.nominees.routes as nomineesRoutes
import models.nominees.OrganisationNomineeContactDetails
import models.{BankDetails, CheckMode, Country, Name, Passport, SelectTitle, UserAnswers}
import org.mockito.ArgumentMatchers.{any, eq as meq}
import org.mockito.Mockito.{mock, when}
import pages.addressLookup.{OrganisationNomineeAddressLookupPage, OrganisationNomineePreviousAddressLookupPage}
import pages.nominees.*
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.SummaryListRowHelper
import viewmodels.nominees.NomineeOrganisationSummaryHelper

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NomineeOrganisationSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  private val year       = 1991
  private val month      = 1
  private val dayOfMonth = 2

  lazy val mockCountryService: CountryService = mock(classOf[CountryService])
  when(mockCountryService.find(meq("GB"))(any())).thenReturn(Some(Country("GB", "United Kingdom")))

  private lazy val baseUserAnswers: UserAnswers = UserAnswers("id")
    .set(OrganisationNomineeNamePage, "Company Inc")
    .flatMap(
      _.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", organisationEmail))
    )
    .flatMap(_.set(OrganisationNomineeAddressLookupPage, ConfirmedAddressConstants.address))
    .flatMap(_.set(IsOrganisationNomineePreviousAddressPage, false))
    .flatMap(_.set(OrganisationNomineePreviousAddressLookupPage, ConfirmedAddressConstants.address))
    .flatMap(_.set(IsOrganisationNomineePaymentsPage, true))
    .flatMap(_.set(OrganisationNomineesBankDetailsPage, bankDetails))
    .flatMap(
      _.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
    )
    .flatMap(_.set(OrganisationAuthorisedPersonDOBPage, LocalDate.of(year, month, dayOfMonth)))
    .success
    .value

  private val helperNino = new NomineeOrganisationSummaryHelper(mockCountryService)(
    baseUserAnswers
      .set(IsOrganisationNomineeNinoPage, true)
      .flatMap(_.set(OrganisationAuthorisedPersonNinoPage, "AB123123A"))
      .success
      .value
  )

  private val helperPassport = new NomineeOrganisationSummaryHelper(mockCountryService)(
    baseUserAnswers
      .set(IsOrganisationNomineeNinoPage, false)
      .flatMap(
        _.set(
          OrganisationAuthorisedPersonPassportPage,
          passport
        )
      )
      .success
      .value
  )

  "Nominee Type Check Your Answers Helper" must {

    "For the organisation name answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeName mustBe Some(
          summaryListRow(
            messages("nameOfOrganisation.checkYourAnswersLabel"),
            HtmlContent("Company Inc"),
            Some(messages("nameOfOrganisation.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationNomineeNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation phone number answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeMainPhone mustBe Some(
          summaryListRow(
            messages("organisationContactDetails.phoneNumber.checkYourAnswersLabel"),
            HtmlContent("0123123123"),
            Some(messages("organisationContactDetails.phoneNumber.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationNomineeContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation email address answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeEmailAddress mustBe Some(
          summaryListRow(
            messages("organisationContactDetails.email.checkYourAnswersLabel"),
            HtmlContent(organisationEmail),
            Some(messages("organisationContactDetails.email.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationNomineeContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation's address answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeAddress mustBe Some(
          summaryListRow(
            messages("organisationNomineeAddress.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, United Kingdom"),
            Some(messages("organisationNomineeAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.OrganisationNomineeAddressLookupController
              .initializeJourney(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the has the organisation's address changed answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeAddressChanged mustBe Some(
          summaryListRow(
            messages("isOrganisationNomineePreviousAddress.checkYourAnswersLabel"),
            HtmlContent(messages("site.no")),
            Some(messages("isOrganisationNomineePreviousAddress.checkYourAnswersLabel")),
            nomineesRoutes.IsOrganisationNomineePreviousAddressController.onPageLoad(
              CheckMode
            ) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee's previous address answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineePreviousAddress mustBe Some(
          summaryListRow(
            messages("nomineeOrganisationPreviousAddress.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, United Kingdom"),
            Some(messages("nomineeOrganisationPreviousAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.OrganisationNomineePreviousAddressLookupController
              .initializeJourney(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the can organisation be paid answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeCanBePaid mustBe Some(
          summaryListRow(
            messages("isOrganisationNomineePayments.checkYourAnswersLabel"),
            HtmlContent(messages("site.yes")),
            Some(messages("isOrganisationNomineePayments.checkYourAnswersLabel")),
            nomineesRoutes.IsOrganisationNomineePaymentsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation account name answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeAccountName mustBe Some(
          summaryListRow(
            messages("organisationNomineesBankDetails.accountName.checkYourAnswersLabel"),
            HtmlContent(accountName),
            Some(messages("organisationNomineesBankDetails.accountName.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation sort code answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeSortCode mustBe Some(
          summaryListRow(
            messages("organisationNomineesBankDetails.sortCode.checkYourAnswersLabel"),
            HtmlContent(sortCode),
            Some(messages("organisationNomineesBankDetails.sortCode.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation account number answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeAccountNumber mustBe Some(
          summaryListRow(
            messages("organisationNomineesBankDetails.accountNumber.checkYourAnswersLabel"),
            HtmlContent(accountNumber),
            Some(messages("organisationNomineesBankDetails.accountNumber.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation roll number answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.nomineeBuildingRoll mustBe Some(
          summaryListRow(
            messages("organisationNomineesBankDetails.rollNumber.checkYourAnswersLabel"),
            HtmlContent(rollNumber),
            Some(messages("organisationNomineesBankDetails.rollNumber.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation authorised person name answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.authorisedPersonName mustBe Some(
          summaryListRow(
            messages("organisationAuthorisedPersonName.checkYourAnswersLabel"),
            HtmlContent("Mr John Jones"),
            Some(messages("organisationAuthorisedPersonName.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationAuthorisedPersonNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation authorised person DOB answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.authorisedPersonDOB mustBe Some(
          summaryListRow(
            messages("organisationAuthorisedPersonDOB.checkYourAnswersLabel"),
            HtmlContent("2 January 1991"),
            Some(messages("organisationAuthorisedPersonDOB.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationAuthorisedPersonDOBController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the does the organisation authorised person have a NINO answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.authorisedPersonHasNino mustBe Some(
          summaryListRow(
            messages("isOrganisationNomineeNino.checkYourAnswersLabel"),
            HtmlContent(messages("site.yes")),
            Some(messages("isOrganisationNomineeNino.checkYourAnswersLabel")),
            nomineesRoutes.IsOrganisationNomineeNinoController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation authorised person's nino answer" must {

      "have a correctly formatted summary list row" in {
        helperNino.authorisedPersonNino mustBe Some(
          summaryListRow(
            messages("organisationAuthorisedPersonNino.checkYourAnswersLabel"),
            HtmlContent("AB123123A"),
            Some(messages("organisationAuthorisedPersonNino.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationAuthorisedPersonNinoController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the organisation authorised person's passport answer" must {

      "have a correctly formatted summary list row for country of issue" in {
        helperPassport.authorisedPersonPassportCountry mustBe Some(
          summaryListRow(
            messages("organisationAuthorisedPersonPassport.country.checkYourAnswersLabel"),
            HtmlContent("United Kingdom"),
            Some(messages("organisationAuthorisedPersonPassport.country.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationAuthorisedPersonPassportController.onPageLoad(
              CheckMode
            ) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list row for passport number" in {
        helperPassport.authorisedPersonPassportNumber mustBe Some(
          summaryListRow(
            messages("organisationAuthorisedPersonPassport.passportNumber.checkYourAnswersLabel"),
            HtmlContent(passportNumber),
            Some(messages("organisationAuthorisedPersonPassport.passportNumber.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationAuthorisedPersonPassportController.onPageLoad(
              CheckMode
            ) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list row for expiry date" in {
        helperPassport.authorisedPersonPassportExpiry mustBe Some(
          summaryListRow(
            messages("organisationAuthorisedPersonPassport.expiryDate.checkYourAnswersLabel"),
            HtmlContent(passport.expiryDate.format(dateFormatter)),
            Some(messages("organisationAuthorisedPersonPassport.expiryDate.checkYourAnswersLabel")),
            nomineesRoutes.OrganisationAuthorisedPersonPassportController.onPageLoad(
              CheckMode
            ) -> BaseMessages.changeLink
          )
        )
      }
    }

  }
}
