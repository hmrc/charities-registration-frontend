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

package navigation

import java.time.LocalDate

import base.SpecBase
import controllers.addressLookup.{routes => addressLookupRoutes}
import controllers.nominees.{routes => nomineesRoutes}
import controllers.routes
import models._
import models.addressLookup.{AddressModel, CountryModel}
import models.nominees.OrganisationNomineeContactDetails
import pages.IndexPage
import pages.addressLookup.{NomineeIndividualAddressLookupPage, NomineeIndividualPreviousAddressLookupPage, OrganisationNomineeAddressLookupPage}
import pages.nominees._

class NomineesNavigatorSpec extends SpecBase {

  private val navigator: NomineesNavigator = inject[NomineesNavigator]
  private val nomineeName: Name = Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")
  private val IndividualNomineePhoneNumber: PhoneNumber = PhoneNumber("07700 900 982", "07700 900 982")
  private val minYear = 16
  private val address: AddressModel = AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))

  private val bankDetails =  BankDetails(
    accountName = "fullName",
    sortCode = "123456",
    accountNumber = "12345678",
    rollNumber = Some("operatingName")
  )

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      // Individual nominee
      // ----------------------------------------------------------------------------------------------

      "from the IsAuthoriseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the NomineeDetailsSummary page when No selected and clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, false).success.value) mustBe
            nomineesRoutes.NomineeDetailsSummaryController.onPageLoad()
        }

        "go to Is the nominee an individual or an organisation? page when yes selected clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, NormalMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, true).success.value) mustBe
            nomineesRoutes.ChooseNomineeController.onPageLoad(NormalMode)
        }
      }

      "from the ChooseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(ChooseNomineePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Individual Nominee Name page when selected person and clicked continue button" in {
          navigator.nextPage(ChooseNomineePage, NormalMode,
            emptyUserAnswers.set(ChooseNomineePage, true).success.value) mustBe
            nomineesRoutes.IndividualNomineeNameController.onPageLoad(NormalMode)
        }

        "go to What is the name of Organisation page when selected Organisation and clicked continue button" in {
          navigator.nextPage(ChooseNomineePage, NormalMode,
            emptyUserAnswers.set(ChooseNomineePage, false).success.value) mustBe
            nomineesRoutes.OrganisationNomineeNameController.onPageLoad(NormalMode)
        }

      }

      "from the IndividualNomineeNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineeNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to What is the nominee's date of birth page when clicked continue button" in {
          navigator.nextPage(IndividualNomineeNamePage, NormalMode,
            emptyUserAnswers.set(IndividualNomineeNamePage, nomineeName).success.value) mustBe
            nomineesRoutes.IndividualNomineeDOBController.onPageLoad(NormalMode)

        }
      }

      "from the IndividualNomineeDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineeDOBPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to What is the nominee's phone number page when clicked continue button" in {
          navigator.nextPage(IndividualNomineeDOBPage, NormalMode,
            emptyUserAnswers.set(IndividualNomineeDOBPage, LocalDate.now().minusYears(minYear)).success.value) mustBe
            nomineesRoutes.IndividualNomineesPhoneNumberController.onPageLoad(NormalMode)

        }
      }

      "from the IndividualNomineePhoneNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineesPhoneNumberPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Does the nominee have national insurance page when clicked continue button" in {
          navigator.nextPage(IndividualNomineesPhoneNumberPage, NormalMode,
            emptyUserAnswers.set(IndividualNomineesPhoneNumberPage, IndividualNomineePhoneNumber).success.value) mustBe
            nomineesRoutes.IsIndividualNomineeNinoController.onPageLoad(NormalMode)

        }
      }

      "from the IsIndividualNomineeNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsIndividualNomineeNinoPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Nominee National insurance number page when yes selected" in {
          navigator.nextPage(IsIndividualNomineeNinoPage, NormalMode,
            emptyUserAnswers.set(IsIndividualNomineeNinoPage, true).success.value) mustBe
           nomineesRoutes.IndividualNomineesNinoController.onPageLoad(NormalMode)
        }

        "go to the nominee passport or national identity card details page when No is selected" in {
          navigator.nextPage(IsIndividualNomineeNinoPage, NormalMode,
            emptyUserAnswers.set(IsIndividualNomineeNinoPage, false).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IndividualNomineeNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineesNinoPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Does the nominee have national insurance page when clicked continue button" in {
          navigator.nextPage(IndividualNomineesNinoPage, NormalMode,
            emptyUserAnswers.set(IndividualNomineesNinoPage, "QQ 12 34 56 C").success.value) mustBe
            addressLookupRoutes.NomineeIndividualAddressLookupController.initializeJourney(NormalMode)

        }
      }

      "from the IndividualNomineeLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(NomineeIndividualAddressLookupPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Has [Full name]’s home address changed in the last 12 months? page when clicked continue button" in {
          navigator.nextPage(NomineeIndividualAddressLookupPage, NormalMode,
            emptyUserAnswers.set(NomineeIndividualAddressLookupPage, address)
              .flatMap(_.set(NomineeIndividualAddressLookupPage, address))
              .success.value) mustBe
            nomineesRoutes.IsIndividualNomineePreviousAddressController.onPageLoad(NormalMode)
        }
      }

      "from the IsIndividualNomineePreviousAddressPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsIndividualNomineePreviousAddressPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Previous address lookup when yes selected" in {
          navigator.nextPage(IsIndividualNomineePreviousAddressPage, NormalMode,
            emptyUserAnswers.set(IsIndividualNomineePreviousAddressPage, true).success.value) mustBe
            addressLookupRoutes.NomineeIndividualPreviousAddressLookupController.initializeJourney(NormalMode)
        }

        "go to the Is nominee authorised to receive payments page when No is selected" in {
          navigator.nextPage(IsIndividualNomineePreviousAddressPage, NormalMode,
            emptyUserAnswers.set(IsIndividualNomineePreviousAddressPage, false).success.value) mustBe
            nomineesRoutes.IsIndividualNomineePaymentsController.onPageLoad(NormalMode)
        }
      }

      "from the IndividualNomineePreviousAddressLookup Page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(NomineeIndividualPreviousAddressLookupPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Is nominee authorised to receive payments page when clicked continue button" in {
          navigator.nextPage(NomineeIndividualPreviousAddressLookupPage, NormalMode,
            emptyUserAnswers.set(NomineeIndividualPreviousAddressLookupPage, address)
              .success.value) mustBe
            nomineesRoutes.IsIndividualNomineePaymentsController.onPageLoad(NormalMode)
        }
      }

      "from the IsIndividualNomineePaymentsPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsIndividualNomineePaymentsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Bank account details page when yes selected" in {
          navigator.nextPage(IsIndividualNomineePaymentsPage, NormalMode,
            emptyUserAnswers.set(IsIndividualNomineePaymentsPage, true).success.value) mustBe
            nomineesRoutes.IndividualNomineesBankDetailsController.onPageLoad(NormalMode)
        }

        "go to the Check your charity`s nominee details page when No is selected" in {
          navigator.nextPage(IsIndividualNomineePaymentsPage, NormalMode,
            emptyUserAnswers.set(IsIndividualNomineePaymentsPage, false).success.value) mustBe
            nomineesRoutes.NomineeDetailsSummaryController.onPageLoad()
        }
      }

      "from the IndividualNomineesBankDetails page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineesBankDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to summary page when clicked continue button" in {
          navigator.nextPage(IndividualNomineesBankDetailsPage, NormalMode,
            emptyUserAnswers.set(IndividualNomineesBankDetailsPage, bankDetails).success.value) mustBe
            nomineesRoutes.NomineeDetailsSummaryController.onPageLoad()
        }
      }

      // Organisation nominee
      // ----------------------------------------------------------------------------------------------

      "from the  OrganisationNomineeName page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationNomineeNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Organisation Nominee Contact Details page when clicked continue button" in {
          navigator.nextPage(OrganisationNomineeNamePage, NormalMode,
            emptyUserAnswers.set(OrganisationNomineeNamePage, "abc").success.value) mustBe
            nomineesRoutes.OrganisationNomineeContactDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the OrganisationNomineeContactDetails page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationNomineeContactDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Organisation Nominee Address Lookup page when clicked continue button" in {
          navigator.nextPage(OrganisationNomineeContactDetailsPage, NormalMode,
            emptyUserAnswers.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "test@email.com")).success.value) mustBe
            addressLookupRoutes.OrganisationNomineeAddressLookupController.initializeJourney(NormalMode)
        }
      }

      "from the OrganisationNomineeAddressLookup page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationNomineeAddressLookupPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Has the address changed page when continue button is clicked" in {
          navigator.nextPage(OrganisationNomineeAddressLookupPage, NormalMode,
            emptyUserAnswers.set(OrganisationNomineeAddressLookupPage, address).success.value) mustBe
            nomineesRoutes.IsOrganisationNomineePreviousAddressController.onPageLoad(NormalMode)
        }
      }

      "from the IsOrganisationNomineePreviousAddress page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsOrganisationNomineePreviousAddressPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Orgnisation previous address page when continue button is clicked" in {
          navigator.nextPage(IsOrganisationNomineePreviousAddressPage, NormalMode,
            emptyUserAnswers.set(IsOrganisationNomineePreviousAddressPage, true)
              .flatMap(_.set(IsOrganisationNomineePreviousAddressPage, true))
              .success.value) mustBe
            routes.DeadEndController.onPageLoad()//TODO when next page created

        }

        "go to the Has the address changed page when continue button is clicked" in {
          navigator.nextPage(IsOrganisationNomineePreviousAddressPage, NormalMode,
            emptyUserAnswers.set(IsOrganisationNomineePreviousAddressPage, false)
              .flatMap(_.set(IsOrganisationNomineePreviousAddressPage, false))
              .success.value) mustBe
          nomineesRoutes.IsOrganisationNomineePaymentsController.onPageLoad(NormalMode)
        }
      }

      "from the IsOrganisationNomineePayments Page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsOrganisationNomineePaymentsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What are Organisation's bank account details page when selected yes and clicked continue" in {
          navigator.nextPage(IsOrganisationNomineePaymentsPage, NormalMode,
            emptyUserAnswers.set(IsOrganisationNomineePaymentsPage, true).success.value) mustBe
            nomineesRoutes.OrganisationNomineesBankDetailsController.onPageLoad(NormalMode)//TODO when next page created
        }

        "go to the Adding Authorised person from organisation page when selected no and continue button is clicked" in {
          navigator.nextPage(IsOrganisationNomineePaymentsPage, NormalMode,
            emptyUserAnswers.set(IsOrganisationNomineePaymentsPage, false).success.value) mustBe
            nomineesRoutes.OrganisationNomineeAuthorisedPersonController.onPageLoad()//TODO when next page created
        }
      }

      "from the OrganisationNomineesBankContactDetails page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationNomineesBankDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Intro to Authorised Person page when clicked continue button" in {
          navigator.nextPage(OrganisationNomineesBankDetailsPage, NormalMode,
            emptyUserAnswers.set(OrganisationNomineesBankDetailsPage, bankDetails).success.value) mustBe
            nomineesRoutes.OrganisationNomineeAuthorisedPersonController.onPageLoad()
        }
      }

      "from the OrganisationAuthorisedPersonName page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationAuthorisedPersonNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the what is full authorised person's date of birth page when continue button is clicked" in {
          navigator.nextPage(OrganisationAuthorisedPersonNamePage, NormalMode,
            emptyUserAnswers.set(OrganisationAuthorisedPersonNamePage, nomineeName).success.value) mustBe
            nomineesRoutes.OrganisationAuthorisedPersonDOBController.onPageLoad(NormalMode)
        }
      }

      "from the OrganisationAuthorisedPersonDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationAuthorisedPersonDOBPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Does the nominee have national insurance number page when clicked continue button" in {
          navigator.nextPage(OrganisationAuthorisedPersonDOBPage, NormalMode,
            emptyUserAnswers.set(OrganisationAuthorisedPersonDOBPage, LocalDate.now().minusYears(minYear)).success.value) mustBe
            nomineesRoutes.IsOrganisationNomineeNinoController.onPageLoad(NormalMode)

        }
      }

      "from the IsOrganisationNomineeNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsOrganisationNomineeNinoPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to What is the nominee's National Insurance number page when Yes selected" in {
          navigator.nextPage(IsOrganisationNomineeNinoPage, NormalMode,
            emptyUserAnswers.set(IsOrganisationNomineeNinoPage, true).success.value) mustBe
            nomineesRoutes.OrganisationAuthorisedPersonNinoController.onPageLoad(NormalMode)

        }

        "go to What are the nominee's passport or national identity card details page when Yes selected" in {
          navigator.nextPage(IsOrganisationNomineeNinoPage, NormalMode,
            emptyUserAnswers.set(IsOrganisationNomineeNinoPage, false).success.value) mustBe
            routes.DeadEndController.onPageLoad()//TODO when next page is ready

        }
      }

      "from the OrganisationAuthorisedPersonNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationAuthorisedPersonNinoPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(OrganisationAuthorisedPersonNinoPage, NormalMode,
            emptyUserAnswers.set(OrganisationAuthorisedPersonNinoPage, "AA123456A").success.value) mustBe
            nomineesRoutes.NomineeDetailsSummaryController.onPageLoad()

        }
      }

      "from the NomineeDetailsSummaryPage" must {

        "go to the TaskList page when clicked continue button" in {
          navigator.nextPage(NomineeDetailsSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

    "in Check mode" when {

      // Individual nominee
      // ----------------------------------------------------------------------------------------------

      "from the IsAuthoriseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAuthoriseNomineePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the NomineeDetailsSummary page when No selected and clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, CheckMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, false).success.value) mustBe
            nomineesRoutes.NomineeDetailsSummaryController.onPageLoad()
        }

        "go to the DeadEnd page when clicked continue button" in {
          navigator.nextPage(IsAuthoriseNomineePage, CheckMode,
            emptyUserAnswers.set(IsAuthoriseNomineePage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the ChooseNomineePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(ChooseNomineePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(ChooseNomineePage, CheckMode,
            emptyUserAnswers.set(ChooseNomineePage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IndividualNomineeNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineeNamePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(IndividualNomineeNamePage, CheckMode,
            emptyUserAnswers.set(IndividualNomineeNamePage, nomineeName).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IndividualNomineeDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineeDOBPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to What is the nominee's phone number page when clicked continue button" in {
          navigator.nextPage(IndividualNomineeDOBPage, CheckMode,
            emptyUserAnswers.set(IndividualNomineeDOBPage,LocalDate.now().minusYears(minYear)).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready

        }
      }

      "from the IndividualNomineePhoneNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineesPhoneNumberPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Does the nominee have national insurance page when clicked continue button" in {
          navigator.nextPage(IndividualNomineesPhoneNumberPage, CheckMode,
            emptyUserAnswers.set(IndividualNomineesPhoneNumberPage, IndividualNomineePhoneNumber).success.value) mustBe
            routes.DeadEndController.onPageLoad()

        }
      }

      "from the IsIndividualNomineeNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsIndividualNomineeNinoPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Nominee National insurance number page when yes selected" in {
          navigator.nextPage(IsIndividualNomineeNinoPage, CheckMode,
            emptyUserAnswers.set(IsIndividualNomineeNinoPage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }

        "go to the nominee passport or national identity card details page when No is selected" in {
          navigator.nextPage(IsIndividualNomineeNinoPage, CheckMode,
            emptyUserAnswers.set(IsIndividualNomineeNinoPage, false).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IndividualNomineeNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineesNinoPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Does the nominee have national insurance page when clicked continue button" in {
          navigator.nextPage(IndividualNomineesNinoPage, CheckMode,
            emptyUserAnswers.set(IndividualNomineesNinoPage, "QQ 12 34 56 C").success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is created

        }
      }

      "from the IndividualNomineeLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(NomineeIndividualAddressLookupPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Has [Full name]’s home address changed in the last 12 months? page when clicked continue button" in {
          navigator.nextPage(NomineeIndividualAddressLookupPage, CheckMode,
            emptyUserAnswers.set(NomineeIndividualAddressLookupPage, address)
              .flatMap(_.set(NomineeIndividualAddressLookupPage, address))
              .success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IsIndividualNomineePreviousAddressPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsIndividualNomineePreviousAddressPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Previous address lookup when yes selected" in {
          navigator.nextPage(IsIndividualNomineePreviousAddressPage, CheckMode,
            emptyUserAnswers.set(IsIndividualNomineePreviousAddressPage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }

        "go to the Is nominee authorised to receive payments page when No is selected" in {
          navigator.nextPage(IsIndividualNomineePreviousAddressPage, CheckMode,
            emptyUserAnswers.set(IsIndividualNomineePreviousAddressPage, false).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IndividualNomineePreviousAddressLookup Page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(NomineeIndividualPreviousAddressLookupPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to summary page when clicked continue button" in {
          navigator.nextPage(NomineeIndividualPreviousAddressLookupPage, CheckMode,
            emptyUserAnswers.set(NomineeIndividualPreviousAddressLookupPage, address).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IsIndividualNomineePaymentsPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsIndividualNomineePaymentsPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Bank account details page when yes selected" in {
          navigator.nextPage(IsIndividualNomineePaymentsPage, CheckMode,
            emptyUserAnswers.set(IsIndividualNomineePaymentsPage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }

        "go to the Check your charity`s nominee details page when No is selected" in {
          navigator.nextPage(IsIndividualNomineePaymentsPage, CheckMode,
            emptyUserAnswers.set(IsIndividualNomineePaymentsPage, false).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IndividualNomineesBankContactDetails page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IndividualNomineesBankDetailsPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to summary page when clicked continue button" in {
          navigator.nextPage(IndividualNomineesBankDetailsPage, CheckMode,
            emptyUserAnswers.set(IndividualNomineesBankDetailsPage, bankDetails).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      // Organisation nominee
      // ----------------------------------------------------------------------------------------------

      "from the  OrganisationNomineeName page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationNomineeNamePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(OrganisationNomineeNamePage, CheckMode,
            emptyUserAnswers.set(OrganisationNomineeNamePage, "abc").success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the OrganisationNomineeContactDetails page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationNomineeContactDetailsPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(OrganisationNomineeContactDetailsPage, CheckMode,
            emptyUserAnswers.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "test@email.com")).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the OrganisationNomineeAddressLookup page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationNomineeAddressLookupPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(OrganisationNomineeAddressLookupPage, CheckMode,
            emptyUserAnswers.set(OrganisationNomineeAddressLookupPage, address).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IsOrganisationNomineePreviousAddressPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsOrganisationNomineePreviousAddressPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the payment received page when No is selected and continue button is clicked" in {
          navigator.nextPage(IsOrganisationNomineePreviousAddressPage, CheckMode,
            emptyUserAnswers.set(IsOrganisationNomineePreviousAddressPage, false).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }

        "go to the previous address page when Yes is selected and continue button is clicked" in {
          navigator.nextPage(IsOrganisationNomineePreviousAddressPage, CheckMode,
            emptyUserAnswers.set(IsOrganisationNomineePreviousAddressPage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }


      "from the OrganisationNomineesBankContactDetails page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationNomineesBankDetailsPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to summary page when clicked continue button" in {
          navigator.nextPage(OrganisationNomineesBankDetailsPage, CheckMode,
            emptyUserAnswers.set(OrganisationNomineesBankDetailsPage, bankDetails).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IsOrganisationNomineePayments Page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsOrganisationNomineePaymentsPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What are Organisation's bank account details page when selected yes and clicked continue" in {
          navigator.nextPage(IsOrganisationNomineePaymentsPage, CheckMode,
            emptyUserAnswers.set(IsOrganisationNomineePaymentsPage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad()//TODO when next page created
        }

        "go to the Adding Authorised person from organisation page when selected no and continue button is clicked" in {
          navigator.nextPage(IsOrganisationNomineePaymentsPage, CheckMode,
            emptyUserAnswers.set(IsOrganisationNomineePaymentsPage, false).success.value) mustBe
            routes.DeadEndController.onPageLoad()//TODO when next page created
        }
      }


      "from the OrganisationAuthorisedPersonName page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationAuthorisedPersonNamePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(OrganisationAuthorisedPersonNamePage, CheckMode,
            emptyUserAnswers.set(OrganisationAuthorisedPersonNamePage, nomineeName).success.value) mustBe
            routes.DeadEndController.onPageLoad()//TODO when next page created
        }
      }

      "from the OrganisationAuthorisedPersonDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationAuthorisedPersonDOBPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(OrganisationAuthorisedPersonDOBPage, CheckMode,
            emptyUserAnswers.set(OrganisationAuthorisedPersonDOBPage, LocalDate.now().minusYears(minYear)).success.value) mustBe
            routes.DeadEndController.onPageLoad()//TODO when next page is ready

        }
      }

      "from the IsOrganisationNomineeNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsOrganisationNomineeNinoPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when previous answer was Yes and changed answer is Yes" in {
          navigator.nextPage(IsOrganisationNomineeNinoPage, CheckMode,
            emptyUserAnswers.set(IsOrganisationNomineeNinoPage, true).success.value) mustBe //TODO add more behaviour once pages created
            routes.DeadEndController.onPageLoad() //TODO when next page is ready
        }

        "go to the summary page when previous answer was No and changed answer is No" in {
          navigator.nextPage(IsOrganisationNomineeNinoPage, CheckMode,
            emptyUserAnswers.set(IsOrganisationNomineeNinoPage, false).success.value) mustBe //TODO add more behaviour once pages created
            routes.DeadEndController.onPageLoad() //TODO when next page is ready
        }

        "go to the What is the nominee's National Insurance number page when previous answer was No and changed answer is Yes" in {
          navigator.nextPage(IsOrganisationNomineeNinoPage, CheckMode,
            emptyUserAnswers.set(IsOrganisationNomineeNinoPage, true).success.value) mustBe //TODO add more behaviour once pages created
            routes.DeadEndController.onPageLoad() //TODO when next page is ready
        }

        "go to the What are the nominee's passport or national identity card details page when previous answer was Yes and changed answer is No" in {
          navigator.nextPage(IsOrganisationNomineeNinoPage, CheckMode,
            emptyUserAnswers.set(IsOrganisationNomineeNinoPage, false).success.value) mustBe //TODO add more behaviour once pages created
            routes.DeadEndController.onPageLoad() //TODO when next page is ready
        }
      }

      "from the OrganisationAuthorisedPersonNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OrganisationAuthorisedPersonNinoPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(OrganisationAuthorisedPersonNinoPage, CheckMode,
            emptyUserAnswers.set(OrganisationAuthorisedPersonNinoPage, "AA123456A").success.value) mustBe
            routes.DeadEndController.onPageLoad()//TODO when next page is ready

        }
      }

      "from the NomineeDetailsSummaryPage" must {

        "go to the TaskList page when clicked continue button" in {
          navigator.nextPage(NomineeDetailsSummaryPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

    "in Playback mode" when {
      "attempting to go to any site" must {
        "go to the SessionExpiredController page" in {
          navigator.nextPage(ChooseNomineePage, PlaybackMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }

  }

}
