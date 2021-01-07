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

package viewmodels.nominees

import models.{SelectTitle, UserAnswers}
import pages.QuestionPage
import pages.addressLookup._
import pages.nominees._
import viewmodels.{StatusHelper, _}

object NomineeStatusHelper extends StatusHelper {

  private val commonJourneyPages: Seq[QuestionPage[_]] = Seq(
    IsAuthoriseNomineePage,
    ChooseNomineePage
  )

  private val individualCommonJourneyPages: Seq[QuestionPage[_]] = Seq(
    IndividualNomineeNamePage,
    IndividualNomineeDOBPage,
    IndividualNomineesPhoneNumberPage,
    IsIndividualNomineeNinoPage,
    NomineeIndividualAddressLookupPage,
    IsIndividualNomineePreviousAddressPage,
    IsIndividualNomineePaymentsPage
  )

  private val remainingIndividualJourneyPages: Seq[QuestionPage[_]] = Seq(
    IndividualNomineesNinoPage,
    IndividualNomineesPassportPage,
    NomineeIndividualPreviousAddressLookupPage,
    IndividualNomineesBankDetailsPage
  )

  private val organisationCommonJourneyPages: Seq[QuestionPage[_]] = Seq(
    OrganisationNomineeNamePage,
    OrganisationNomineeContactDetailsPage,
    OrganisationNomineeAddressLookupPage,
    IsOrganisationNomineePreviousAddressPage,
    IsOrganisationNomineePaymentsPage,
    OrganisationAuthorisedPersonNamePage,
    OrganisationAuthorisedPersonDOBPage,
    IsOrganisationNomineeNinoPage
  )

  private val remainingOrganisationJourneyPages: Seq[QuestionPage[_]] = Seq(
    OrganisationNomineePreviousAddressLookupPage,
    OrganisationNomineesBankDetailsPage
  )

  private val remainingOrganisationIndividualJourneyPages: Seq[QuestionPage[_]] = Seq(
    OrganisationAuthorisedPersonNinoPage,
    OrganisationAuthorisedPersonPassportPage
  )

  private val individualJourneyPages: Seq[QuestionPage[_]] = individualCommonJourneyPages ++ remainingIndividualJourneyPages

  private val organisationJourneyPages: Seq[QuestionPage[_]] = {
    organisationCommonJourneyPages ++ remainingOrganisationJourneyPages ++ remainingOrganisationIndividualJourneyPages
  }

  private val allPages: Seq[QuestionPage[_]] = commonJourneyPages ++ individualJourneyPages ++ organisationJourneyPages

  override def checkComplete(userAnswers: UserAnswers): Boolean = {

    def noAdditionalPagesDefined(list: Seq[QuestionPage[_]]): Boolean = userAnswers.unneededPagesNotPresent(list, allPages)

    (userAnswers.get(IsAuthoriseNomineePage), userAnswers.get(ChooseNomineePage)) match {

      case(Some(false), None) => noAdditionalPagesDefined(Seq(IsAuthoriseNomineePage))

      case(Some(true), Some(true)) =>
        (userAnswers.get(IsIndividualNomineeNinoPage), userAnswers.get(IsIndividualNomineePreviousAddressPage),
          userAnswers.get(IsIndividualNomineePaymentsPage)) match {
          case (Some(isNino), Some(isPreviousAddress), Some(isPayment)) =>

            val newPages = commonJourneyPages ++ individualCommonJourneyPages
              .getIndividualNomineePages(isNino, isPreviousAddress, isPayment)

            userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages)

          case _ => false
        }

      case(Some(true), Some(false)) =>
        (userAnswers.get(IsOrganisationNomineeNinoPage), userAnswers.get(IsOrganisationNomineePreviousAddressPage),
          userAnswers.get(IsOrganisationNomineePaymentsPage)) match {
          case (Some(isNino), Some(isPreviousAddress), Some(isPayment)) =>
            val newPages = commonJourneyPages ++ organisationCommonJourneyPages
              .getOrganisationNomineePages(isNino, isPreviousAddress, isPayment)

            userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages)

          case _ => false
        }

      case _ => false
    }
  }

  def validateDataFromOldService(userAnswers: UserAnswers): Boolean = {
    (userAnswers.get(IsAuthoriseNomineePage), userAnswers.get(ChooseNomineePage)) match {
      case(Some(true), Some(true)) =>
        userAnswers.get(IndividualNomineeNamePage) match {
          case Some(name) if name.title == SelectTitle.UnsupportedTitle => false
          case _ => true
        }
      case(Some(true), Some(false)) =>
        (userAnswers.get(OrganisationAuthorisedPersonNamePage), userAnswers.get(OrganisationNomineeContactDetailsPage)) match {
          case (Some(name), Some(contact)) => name.title != SelectTitle.UnsupportedTitle && contact.email.nonEmpty
          case _ => true
        }
      case _ => true
    }
  }
}
