/*
 * Copyright 2021 HM Revenue & Customs
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
import base.SpecBase
import models.nominees.OrganisationNomineeContactDetails
import models.{BankDetails, Name, Passport, PhoneNumber, SelectTitle, UserAnswers}
import pages.addressLookup._
import pages.nominees._
import viewmodels.nominees.NomineeStatusHelper

class NomineeStatusHelperSpec extends SpecBase {

  implicit class NomineeStatusPageSetter(userAnswers: UserAnswers) {

    def addIndividualAddress: UserAnswers =
      userAnswers.set(IsIndividualNomineePreviousAddressPage, true)
        .flatMap(_.set(NomineeIndividualPreviousAddressLookupPage, ConfirmedAddressConstants.address)).success.value

    def addPassport: UserAnswers =
      userAnswers.set(IsIndividualNomineeNinoPage, false)
        .flatMap(_.set(IndividualNomineesPassportPage, Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth)))).success.value

    def addPayment: UserAnswers =
      userAnswers.set(IsIndividualNomineePaymentsPage, true)
        .flatMap(_.set(IndividualNomineesBankDetailsPage, BankDetails(accountName = "PM Cares",
          sortCode = "176534",
          accountNumber = "43444546",
          rollNumber = Some("765431234")))).success.value

    def removeNino: UserAnswers =
      userAnswers.remove(IsIndividualNomineeNinoPage).success.value

    def addOrganizationAddress: UserAnswers =
      userAnswers.set(IsOrganisationNomineePreviousAddressPage, true)
        .flatMap(_.set(OrganisationNomineePreviousAddressLookupPage, ConfirmedAddressConstants.address)).success.value

    def addOrganisationPayment: UserAnswers =
      userAnswers.set(IsOrganisationNomineePaymentsPage, true)
        .flatMap(_.set(OrganisationNomineesBankDetailsPage, BankDetails(accountName = "PM Cares",
          sortCode = "176534",
          accountNumber = "43444546",
          rollNumber = Some("765431234")))).success.value

    def removeOrganisationNomineeNino: UserAnswers =
      userAnswers.remove(IsOrganisationNomineeNinoPage).success.value

    def addOrganisationNomineePassport: UserAnswers =
      userAnswers.set(IsOrganisationNomineeNinoPage, false)
        .flatMap(_.set(OrganisationAuthorisedPersonPassportPage, Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth)))).success.value
  }

  private val year = 2000
  private val month = 1
  private val dayOfMonth = 2

  private val noNominee = UserAnswers("id").set(IsAuthoriseNomineePage, false).success.value

  private val nomineeIndividual: UserAnswers = UserAnswers("id").set(IsAuthoriseNomineePage, true)
    .flatMap(_.set(ChooseNomineePage, true))
    .flatMap(_.set(IndividualNomineeNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones")))
    .flatMap(_.set(IndividualNomineeDOBPage, LocalDate.of(year, month, dayOfMonth)))
    .flatMap(_.set(IndividualNomineesPhoneNumberPage, PhoneNumber("0123123123", Some("0123123124"))))
    .flatMap(_.set(IsIndividualNomineeNinoPage, true))
    .flatMap(_.set(IndividualNomineesNinoPage, "AB123123A"))
    .flatMap(_.set(NomineeIndividualAddressLookupPage, ConfirmedAddressConstants.address))
    .flatMap(_.set(IsIndividualNomineePreviousAddressPage, false))
    .flatMap(_.set(IsIndividualNomineePaymentsPage, false)).success.value

  private val nomineeOrganisation: UserAnswers  = UserAnswers("id").set(IsAuthoriseNomineePage, true)
    .flatMap(_.set(ChooseNomineePage, false))
    .flatMap(_.set(OrganisationNomineeNamePage, "Company Inc"))
    .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "company@inc.com")))
    .flatMap(_.set(OrganisationNomineeAddressLookupPage, ConfirmedAddressConstants.address))
    .flatMap(_.set(IsOrganisationNomineePreviousAddressPage, false))
    .flatMap(_.set(IsOrganisationNomineePaymentsPage, false))
    .flatMap(_.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones")))
    .flatMap(_.set(OrganisationAuthorisedPersonDOBPage, LocalDate.of(year, month, dayOfMonth)))
    .flatMap(_.set(IsOrganisationNomineeNinoPage, true))
    .flatMap(_.set(OrganisationAuthorisedPersonNinoPage, "AB123123A")).success.value

  private val helper = NomineeStatusHelper

  "OtherOfficialsStatusHelper" must {

    "when verifying section 8 answers" must {

      "return false when user answers is empty" in {

        helper.checkComplete(emptyUserAnswers) mustBe false
      }

      "return true if only no nominee selected" in {

        helper.checkComplete(noNominee) mustBe true
      }

      "return false nominee details are not defined" in {

        helper.checkComplete(UserAnswers("id")
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, false)).success.value) mustBe false
      }

      "Individual Nominee" must {

        "return true when individual nominee without nino, previous address and payment details (Scenario 10)" in {

          helper.checkComplete(nomineeIndividual.removeNino.addPassport) mustBe true
        }

        "return false when individual nominee without nino, passport, previous address and payment details (Scenario 10)" in {

          helper.checkComplete(nomineeIndividual.removeNino) mustBe false
        }

        "return true when individual nominee with payment details and without nino, previous address (Scenario 9)" in {

          helper.checkComplete(nomineeIndividual.removeNino.addPassport.addPayment) mustBe true
        }

        "return false when individual nominee with payment and without nino, previous address and payment details (Scenario 9)" in {

          helper.checkComplete(nomineeIndividual.removeNino.addPassport
            .set(IsIndividualNomineePaymentsPage, true).success.value) mustBe false
        }

        "return true when individual nominee with previous address and without nino, payment details (Scenario 8)" in {

          helper.checkComplete(nomineeIndividual.removeNino.addPassport.addIndividualAddress) mustBe true
        }

        "return false when individual nominee with previous address selected and without nino, payment and previous address details (Scenario 8)" in {

          helper.checkComplete(nomineeIndividual.removeNino.addPassport
            .set(IsIndividualNomineePreviousAddressPage, true).success.value) mustBe false
        }

        "return true when individual nominee with payment, previous address and without nino (Scenario 7)" in {

          helper.checkComplete(nomineeIndividual.removeNino.addPassport.addIndividualAddress.addPayment) mustBe true
        }

        "return false when individual nominee with payment, previous address selected and without nino, previous address details (Scenario 7)" in {

          helper.checkComplete(nomineeIndividual.removeNino.addPassport.addPayment
            .set(IsIndividualNomineePreviousAddressPage, true).success.value) mustBe false
        }

        "return true when individual nominee with nino nd without previous address, payment details (Scenario 6)" in {

          helper.checkComplete(nomineeIndividual) mustBe true
        }

        "return false when individual nominee with nino and without previous address, payment details(Scenario 6)" in {

          helper.checkComplete(nomineeIndividual.removeNino) mustBe false
        }

        "return true when individual nominee with nino, payment details and without previous address (Scenario 5)" in {

          helper.checkComplete(nomineeIndividual.addPayment) mustBe true
        }

        "return false when individual nominee with nino, payment and without previous address, payment details (Scenario 5)" in {

          helper.checkComplete(nomineeIndividual.set(IsIndividualNomineePaymentsPage, true).success.value) mustBe false
        }

        "return true when individual nominee with nino, previous address and without previous address payment details(Scenario 4)" in {

          helper.checkComplete(nomineeIndividual.addIndividualAddress) mustBe true
        }

        "return false when individual nominee with nino, previous address and without previous address details, payment details (Scenario 4)" in {

          helper.checkComplete(nomineeIndividual.set(IsIndividualNomineePreviousAddressPage, true).success.value) mustBe false
        }

        "return true when individual nominee with nino, previous address and payment details(Scenario 3)" in {

          helper.checkComplete(nomineeIndividual.addIndividualAddress.addPayment) mustBe true
        }

        "return false when individual nominee with nino, previous address, payment details and without previous address details (Scenario 3)" in {

          helper.checkComplete(nomineeIndividual.addPayment.set(IsIndividualNomineePreviousAddressPage, true).success.value) mustBe false
        }

        "return false when individual nominee with organisation nominee details" in {

          helper.checkComplete(nomineeIndividual.addOrganizationAddress) mustBe false
        }
      }

      "Organisation nominee" must {

        "return true when organisation nominee with nino, previous address and payment details(Scenario 11)" in {

          helper.checkComplete(nomineeOrganisation.addOrganizationAddress.addOrganisationPayment) mustBe true
        }

        "return false when organisation nominee with nino, previous address, payment details and without previous address details (Scenario 11)" in {

          helper.checkComplete(nomineeOrganisation.addOrganisationPayment
            .set(IsOrganisationNomineePreviousAddressPage, true).success.value) mustBe false
        }

        "return true when organisation nominee with previous address, payment details and Passport (Scenario 12)" in {

          helper.checkComplete(nomineeOrganisation.addOrganizationAddress
            .addOrganisationPayment.addOrganisationNomineePassport) mustBe true
        }

        "return false when organisation nominee with previous address, payment details and without passport details (Scenario 12)" in {

          helper.checkComplete(nomineeOrganisation.addOrganizationAddress
            .addOrganisationPayment.set(IsOrganisationNomineeNinoPage, false).success.value) mustBe false
        }

        "return true when organisation nominee with previous address, nino and without payment (Scenario 13)" in {

          helper.checkComplete(nomineeOrganisation.addOrganizationAddress) mustBe true
        }

        "return false when organisation nominee with previous address, nino and without payment, passport details (Scenario 13)" in {

          helper.checkComplete(nomineeOrganisation.addOrganizationAddress.removeOrganisationNomineeNino) mustBe false
        }

        "return true when organisation nominee with previous address, Passport and without payment (Scenario 14)" in {

          helper.checkComplete(nomineeOrganisation.addOrganizationAddress.addOrganisationNomineePassport) mustBe true
        }

        "return false when organisation nominee with previous address, passport and without payment, passport details (Scenario 14)" in {

          helper.checkComplete(nomineeOrganisation.addOrganizationAddress.set(IsOrganisationNomineeNinoPage, false).success.value) mustBe false
        }

        "return true when organisation nominee with nino, payment details without previous address(Scenario 15)" in {

          helper.checkComplete(nomineeOrganisation.addOrganisationPayment) mustBe true
        }

        "return false when organisation nominee with nino, payment details and without previous address, nino details (Scenario 15)" in {

          helper.checkComplete(nomineeOrganisation.addOrganisationPayment.removeOrganisationNomineeNino) mustBe false
        }

        "return true when organisation nominee with payment details and Passport (Scenario 16)" in {

          helper.checkComplete(nomineeOrganisation.addOrganisationPayment.addOrganisationNomineePassport) mustBe true
        }

        "return false when organisation nominee with payment details and without passport details (Scenario 16)" in {

          helper.checkComplete(nomineeOrganisation.addOrganisationPayment.set(IsOrganisationNomineeNinoPage, false).success.value) mustBe false
        }

        "return true when organisation nominee with passport and without payment, previous address (Scenario 17)" in {

          helper.checkComplete(nomineeOrganisation.addOrganisationNomineePassport) mustBe true
        }

        "return false when organisation nominee with passport and without payment, previous address passport details (Scenario 17)" in {

          helper.checkComplete(nomineeOrganisation.set(IsOrganisationNomineeNinoPage, false).success.value) mustBe false
        }

        "return true when organisation nominee with nino and without previous address, payment (Scenario 18)" in {

          helper.checkComplete(nomineeOrganisation) mustBe true
        }

        "return false when organisation nominee with nino and without previous address, payment, nino details (Scenario 18)" in {

          helper.checkComplete(nomineeOrganisation.removeOrganisationNomineeNino) mustBe false
        }

        "return false when organisation nominee with individual nominee details" in {

          helper.checkComplete(nomineeOrganisation.addIndividualAddress) mustBe false
        }
      }

      "validateDataFromOldService" must {

        "return true when for empty user Answers" in {

          helper.validateDataFromOldService(emptyUserAnswers) mustBe true
        }

        "return true when for no nominee" in {

          helper.validateDataFromOldService(noNominee) mustBe true
        }

        "return true when for nominee with correct title" in {
          val nomineeIndividual: UserAnswers = UserAnswers("id").set(IsAuthoriseNomineePage, true)
            .flatMap(_.set(ChooseNomineePage, true)
              .flatMap(_.set(IndividualNomineeNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones")))).success.value

          helper.validateDataFromOldService(nomineeIndividual) mustBe true
        }

        "return false when for nominee with unsupported title" in {
          val nomineeIndividual: UserAnswers = UserAnswers("id").set(IsAuthoriseNomineePage, true)
            .flatMap(_.set(ChooseNomineePage, true)
              .flatMap(_.set(IndividualNomineeNamePage, Name(SelectTitle.UnsupportedTitle, firstName = "John", None, lastName = "Jones")))).success.value

          helper.validateDataFromOldService(nomineeIndividual) mustBe false
        }

        "return true when for nominee with correct title and email" in {
          val nomineeOrganisation: UserAnswers  = UserAnswers("id").set(IsAuthoriseNomineePage, true)
            .flatMap(_.set(ChooseNomineePage, false))
            .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "company@inc.com")))
            .flatMap(_.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))).success.value

          helper.validateDataFromOldService(nomineeOrganisation) mustBe true
        }

        "return false when for nominee with unsupported title and correct email" in {
          val nomineeOrganisation: UserAnswers  = UserAnswers("id").set(IsAuthoriseNomineePage, true)
            .flatMap(_.set(ChooseNomineePage, false))
            .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "company@inc.com")))
            .flatMap(_.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.UnsupportedTitle, firstName = "John", None, lastName = "Jones"))).success.value

          helper.validateDataFromOldService(nomineeOrganisation) mustBe false
        }

        "return false when for nominee with unsupported title and blank email" in {
          val nomineeOrganisation: UserAnswers  = UserAnswers("id").set(IsAuthoriseNomineePage, true)
            .flatMap(_.set(ChooseNomineePage, false))
            .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "")))
            .flatMap(_.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.UnsupportedTitle, firstName = "John", None, lastName = "Jones"))).success.value

          helper.validateDataFromOldService(nomineeOrganisation) mustBe false
        }

        "return false when for nominee with correct title and blank email" in {
          val nomineeOrganisation: UserAnswers  = UserAnswers("id").set(IsAuthoriseNomineePage, true)
            .flatMap(_.set(ChooseNomineePage, false))
            .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "")))
            .flatMap(_.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))).success.value

          helper.validateDataFromOldService(nomineeOrganisation) mustBe false
        }

      }
    }
  }
}
