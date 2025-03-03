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

package navigation

import controllers.addressLookup.{routes => addressLookupRoutes}
import controllers.nominees.{routes => nomineeRoutes}
import controllers.routes
import models._
import pages.Page
import pages.addressLookup._
import pages.nominees._
import play.api.mvc.Call

class NomineesNavigator extends BaseNavigator {

  override val normalRoutes: Page => UserAnswers => Call = {

    case IsAuthoriseNomineePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsAuthoriseNomineePage) match {
          case Some(true)  => nomineeRoutes.ChooseNomineeController.onPageLoad(NormalMode)
          case Some(false) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case ChooseNomineePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(ChooseNomineePage) match {
          case Some(true)  => nomineeRoutes.IndividualNomineeNameController.onPageLoad(NormalMode)
          case Some(false) => nomineeRoutes.OrganisationNomineeNameController.onPageLoad(NormalMode)
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineeNamePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineeNamePage) match {
          case Some(_) => nomineeRoutes.IndividualNomineeDOBController.onPageLoad(NormalMode)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineeDOBPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineeDOBPage) match {
          case Some(_) => nomineeRoutes.IndividualNomineesPhoneNumberController.onPageLoad(NormalMode)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineesPhoneNumberPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineesPhoneNumberPage) match {
          case Some(_) => nomineeRoutes.IsIndividualNomineeNinoController.onPageLoad(NormalMode)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IsIndividualNomineeNinoPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsIndividualNomineeNinoPage) match {
          case Some(true)  => nomineeRoutes.IndividualNomineesNinoController.onPageLoad(NormalMode)
          case Some(false) => nomineeRoutes.IndividualNomineePassportController.onPageLoad(NormalMode)
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineesPassportPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineesPassportPage) match {
          case Some(_) =>
            userAnswers.get(NomineeIndividualAddressLookupPage) match {
              case Some(_) => nomineeRoutes.ConfirmNomineeIndividualAddressController.onPageLoad()
              case _       =>
                controllers.addressLookup.routes.NomineeIndividualAddressLookupController
                  .initializeJourney(NormalMode)
            }
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineesNinoPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineesNinoPage) match {
          case Some(_) =>
            userAnswers.get(NomineeIndividualAddressLookupPage) match {
              case Some(_) => nomineeRoutes.ConfirmNomineeIndividualAddressController.onPageLoad()
              case _       =>
                controllers.addressLookup.routes.NomineeIndividualAddressLookupController
                  .initializeJourney(NormalMode)
            }
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IsIndividualNomineePaymentsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsIndividualNomineePaymentsPage) match {
          case Some(true)  => nomineeRoutes.IndividualNomineesBankDetailsController.onPageLoad(NormalMode)
          case Some(false) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineesBankDetailsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineesBankDetailsPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case NomineeIndividualAddressLookupPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(NomineeIndividualAddressLookupPage) match {
          case Some(address) if isNotValidAddress(address) =>
            nomineeRoutes.AmendNomineeIndividualAddressController.onPageLoad(NormalMode)
          case Some(_)                                     =>
            nomineeRoutes.IsIndividualNomineePreviousAddressController.onPageLoad(NormalMode)
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IsIndividualNomineePreviousAddressPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsIndividualNomineePreviousAddressPage) match {
          case Some(true) if userAnswers.get(NomineeIndividualPreviousAddressLookupPage).isDefined =>
            nomineeRoutes.ConfirmNomineeIndividualPreviousAddressController.onPageLoad()
          case Some(true)                                                                          =>
            controllers.addressLookup.routes.NomineeIndividualPreviousAddressLookupController
              .initializeJourney(NormalMode)
          case Some(false)                                                                         =>
            nomineeRoutes.IsIndividualNomineePaymentsController.onPageLoad(NormalMode)
          case _                                                                                   =>
            routes.PageNotFoundController.onPageLoad()
        }

    case NomineeIndividualPreviousAddressLookupPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(NomineeIndividualPreviousAddressLookupPage) match {
          case Some(address) if isNotValidAddress(address) =>
            nomineeRoutes.AmendNomineeIndividualPreviousAddressController.onPageLoad(NormalMode)
          case Some(_)                                     =>
            nomineeRoutes.IsIndividualNomineePaymentsController.onPageLoad(NormalMode)
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineeNamePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineeNamePage) match {
          case Some(_) => nomineeRoutes.OrganisationNomineeContactDetailsController.onPageLoad(NormalMode)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineeContactDetailsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineeContactDetailsPage) match {
          case Some(_) =>
            userAnswers.get(OrganisationNomineeAddressLookupPage) match {
              case Some(_) =>
                nomineeRoutes.ConfirmOrganisationNomineeAddressController.onPageLoad()
              case _       =>
                addressLookupRoutes.OrganisationNomineeAddressLookupController.initializeJourney(NormalMode)
            }
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineeAddressLookupPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineeAddressLookupPage) match {
          case Some(address) if isNotValidAddress(address) =>
            nomineeRoutes.AmendNomineeOrganisationAddressController.onPageLoad(NormalMode)
          case Some(_)                                     =>
            nomineeRoutes.IsOrganisationNomineePreviousAddressController.onPageLoad(NormalMode)
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IsOrganisationNomineePreviousAddressPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOrganisationNomineePreviousAddressPage) match {
          case Some(true) if userAnswers.get(OrganisationNomineePreviousAddressLookupPage).isDefined =>
            nomineeRoutes.ConfirmOrganisationNomineePreviousAddressController.onPageLoad()
          case Some(true)                                                                            =>
            addressLookupRoutes.OrganisationNomineePreviousAddressLookupController.initializeJourney(NormalMode)
          case Some(false)                                                                           =>
            nomineeRoutes.IsOrganisationNomineePaymentsController.onPageLoad(NormalMode)
          case _                                                                                     =>
            routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineePreviousAddressLookupPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineePreviousAddressLookupPage) match {
          case Some(address) if isNotValidAddress(address) =>
            nomineeRoutes.AmendNomineeOrganisationPreviousAddressController.onPageLoad(NormalMode)
          case Some(_)                                     =>
            nomineeRoutes.IsOrganisationNomineePaymentsController.onPageLoad(NormalMode)
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IsOrganisationNomineePaymentsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOrganisationNomineePaymentsPage) match {
          case Some(true)  => nomineeRoutes.OrganisationNomineesBankDetailsController.onPageLoad(NormalMode)
          case Some(false) => nomineeRoutes.OrganisationNomineeAuthorisedPersonController.onPageLoad()
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineesBankDetailsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineesBankDetailsPage) match {
          case Some(_) => nomineeRoutes.OrganisationNomineeAuthorisedPersonController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationAuthorisedPersonNamePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationAuthorisedPersonNamePage) match {
          case Some(_) => nomineeRoutes.OrganisationAuthorisedPersonDOBController.onPageLoad(NormalMode)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationAuthorisedPersonDOBPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationAuthorisedPersonDOBPage) match {
          case Some(_) => nomineeRoutes.IsOrganisationNomineeNinoController.onPageLoad(NormalMode)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IsOrganisationNomineeNinoPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOrganisationNomineeNinoPage) match {
          case Some(true)  => nomineeRoutes.OrganisationAuthorisedPersonNinoController.onPageLoad(NormalMode)
          case Some(false) => nomineeRoutes.OrganisationAuthorisedPersonPassportController.onPageLoad(NormalMode)
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationAuthorisedPersonNinoPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationAuthorisedPersonNinoPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationAuthorisedPersonPassportPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationAuthorisedPersonPassportPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case NomineeDetailsSummaryPage => _ => routes.IndexController.onPageLoad(None)

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val checkRouteMap: Page => UserAnswers => Call = {

    case IsAuthoriseNomineePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsAuthoriseNomineePage) match {
          case Some(true)  => nomineeRoutes.ChooseNomineeController.onPageLoad(CheckMode)
          case Some(false) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case ChooseNomineePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(ChooseNomineePage) match {
          case Some(true)  => nomineeRoutes.IndividualNomineeNameController.onPageLoad(NormalMode)
          case Some(false) => nomineeRoutes.OrganisationNomineeNameController.onPageLoad(NormalMode)
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineeNamePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineeNamePage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineeDOBPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineeDOBPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineesPhoneNumberPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineesPhoneNumberPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IsIndividualNomineeNinoPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsIndividualNomineeNinoPage) match {
          case Some(true) if userAnswers.get(IndividualNomineesNinoPage).isDefined      =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case Some(true)                                                               =>
            nomineeRoutes.IndividualNomineesNinoController.onPageLoad(CheckMode)
          case Some(false) if userAnswers.get(IndividualNomineesPassportPage).isDefined =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case Some(false)                                                              =>
            nomineeRoutes.IndividualNomineePassportController.onPageLoad(CheckMode)
          case _                                                                        =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineesPassportPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineesPassportPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineesNinoPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineesNinoPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case NomineeIndividualAddressLookupPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(NomineeIndividualAddressLookupPage) match {
          case Some(address) if isNotValidAddress(address) =>
            nomineeRoutes.AmendNomineeIndividualAddressController.onPageLoad(CheckMode)
          case Some(_)                                     =>
            nomineeRoutes.IsIndividualNomineePreviousAddressController.onPageLoad(CheckMode)
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IsIndividualNomineePreviousAddressPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsIndividualNomineePreviousAddressPage) match {
          case Some(true) if userAnswers.get(NomineeIndividualPreviousAddressLookupPage).isDefined =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case Some(true)                                                                          =>
            addressLookupRoutes.NomineeIndividualPreviousAddressLookupController.initializeJourney(CheckMode)
          case Some(false)                                                                         =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _                                                                                   =>
            routes.PageNotFoundController.onPageLoad()
        }

    case NomineeIndividualPreviousAddressLookupPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(NomineeIndividualPreviousAddressLookupPage) match {
          case Some(address) if isNotValidAddress(address) =>
            nomineeRoutes.AmendNomineeIndividualPreviousAddressController.onPageLoad(CheckMode)
          case Some(_)                                     => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _                                           => routes.PageNotFoundController.onPageLoad()
        }

    case IsIndividualNomineePaymentsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsIndividualNomineePaymentsPage) match {
          case Some(true) if userAnswers.get(IndividualNomineesBankDetailsPage).isDefined =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case Some(true)                                                                 =>
            nomineeRoutes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode)
          case Some(false)                                                                =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _                                                                          =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IndividualNomineesBankDetailsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IndividualNomineesBankDetailsPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineeNamePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineeNamePage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineeContactDetailsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineeContactDetailsPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineeAddressLookupPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineeAddressLookupPage) match {
          case Some(address) if isNotValidAddress(address) =>
            nomineeRoutes.AmendNomineeOrganisationAddressController.onPageLoad(CheckMode)
          case Some(_)                                     =>
            nomineeRoutes.IsOrganisationNomineePreviousAddressController.onPageLoad(CheckMode)
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IsOrganisationNomineePreviousAddressPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOrganisationNomineePreviousAddressPage) match {
          case Some(true) if userAnswers.get(OrganisationNomineePreviousAddressLookupPage).isDefined =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case Some(true)                                                                            =>
            addressLookupRoutes.OrganisationNomineePreviousAddressLookupController.initializeJourney(CheckMode)
          case Some(false)                                                                           =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _                                                                                     =>
            routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineePreviousAddressLookupPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineePreviousAddressLookupPage) match {
          case Some(address) if isNotValidAddress(address) =>
            nomineeRoutes.AmendNomineeOrganisationPreviousAddressController.onPageLoad(CheckMode)
          case Some(_)                                     =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IsOrganisationNomineePaymentsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOrganisationNomineePaymentsPage) match {
          case Some(true) if userAnswers.get(OrganisationNomineesBankDetailsPage).isDefined =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case Some(true)                                                                   =>
            nomineeRoutes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode)
          case Some(false)                                                                  =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _                                                                            =>
            routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationNomineesBankDetailsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationNomineesBankDetailsPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationAuthorisedPersonNamePage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationAuthorisedPersonNamePage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationAuthorisedPersonDOBPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationAuthorisedPersonDOBPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IsOrganisationNomineeNinoPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOrganisationNomineeNinoPage) match {
          case Some(true) if userAnswers.get(OrganisationAuthorisedPersonNinoPage).isDefined      =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case Some(true)                                                                         =>
            nomineeRoutes.OrganisationAuthorisedPersonNinoController.onPageLoad(CheckMode)
          case Some(false) if userAnswers.get(OrganisationAuthorisedPersonPassportPage).isDefined =>
            nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case Some(false)                                                                        =>
            nomineeRoutes.OrganisationAuthorisedPersonPassportController.onPageLoad(CheckMode)
          case _                                                                                  =>
            routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationAuthorisedPersonNinoPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationAuthorisedPersonNinoPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OrganisationAuthorisedPersonPassportPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OrganisationAuthorisedPersonPassportPage) match {
          case Some(_) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case NomineeDetailsSummaryPage => _ => routes.IndexController.onPageLoad(None)

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

}
