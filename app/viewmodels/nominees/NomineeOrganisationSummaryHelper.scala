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

package viewmodels.nominees

import controllers.nominees.routes
import models.nominees.NomineeSummary
import models.{CheckMode, UserAnswers}
import pages.addressLookup.OrganisationNomineeAddressLookupPage
import pages.nominees._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class NomineeOrganisationSummaryHelper(override val userAnswers: UserAnswers)
                                      (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {

  def nomineeName: Option[SummaryListRow] =
    answerPrefix(OrganisationNomineeNamePage,
      routes.OrganisationNomineeNameController.onPageLoad(CheckMode),
      messagePrefix = "nameOfOrganisation")

  def nomineeMainPhone: Option[SummaryListRow] =
    answerOrgPhoneNumber(OrganisationNomineeContactDetailsPage,
      routes.OrganisationNomineeContactDetailsController.onPageLoad(CheckMode),
      messagePrefix = "organisationContactDetails.phoneNumber")

  def nomineeEmailAddress: Option[SummaryListRow] =
    answerOrgEmailAddress(OrganisationNomineeContactDetailsPage,
      routes.OrganisationNomineeContactDetailsController.onPageLoad(CheckMode),
      messagePrefix = "organisationContactDetails.email")

  def nomineeAddress: Option[SummaryListRow] =
    answerAddress(OrganisationNomineeAddressLookupPage,
      controllers.addressLookup.routes.OrganisationNomineeAddressLookupController.initializeJourney(CheckMode),
      messagePrefix = "organisationNomineeAddress")

  def nomineeAddressChanged: Option[SummaryListRow] =
    answerPrefix(IsOrganisationNomineePreviousAddressPage,
      routes.IsOrganisationNomineePreviousAddressController.onPageLoad(CheckMode),
      messagePrefix = "isOrganisationNomineePreviousAddress")

  def nomineePreviousAddress: Option[SummaryListRow] = None // TODO organisation nominee prev address

  def nomineeCanBePaid: Option[SummaryListRow] =
    answerPrefix(IsOrganisationNomineePaymentsPage,
      routes.IsOrganisationNomineePaymentsController.onPageLoad(CheckMode),
      messagePrefix = "isOrganisationNomineePayments")

  def nomineeAccountName: Option[SummaryListRow] =
    answerAccountName(OrganisationNomineesBankDetailsPage,
      routes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "organisationNomineesBankDetails.accountName")

  def nomineeSortCode: Option[SummaryListRow] =
    answerSortCode(OrganisationNomineesBankDetailsPage,
      routes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "organisationNomineesBankDetails.sortCode")

  def nomineeAccountNumber: Option[SummaryListRow] =
    answerAccountNumber(OrganisationNomineesBankDetailsPage,
      routes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "organisationNomineesBankDetails.accountNumber")

  def nomineeBuildingRoll: Option[SummaryListRow] =
    answerRollNumber(OrganisationNomineesBankDetailsPage,
      routes.OrganisationNomineesBankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "organisationNomineesBankDetails.rollNumber")


  def authorisedPersonName: Option[SummaryListRow] =
    answerFullName(OrganisationAuthorisedPersonNamePage,
      routes.OrganisationAuthorisedPersonNameController.onPageLoad(CheckMode),
      messagePrefix = "organisationAuthorisedPersonName")

  def authorisedPersonDOB: Option[SummaryListRow] =
    answerPrefix(OrganisationAuthorisedPersonDOBPage,
      routes.OrganisationAuthorisedPersonDOBController.onPageLoad(CheckMode),
      messagePrefix = "organisationAuthorisedPersonDOB")

  def authorisedPersonHasNino: Option[SummaryListRow] =
    answerPrefix(IsOrganisationNomineeNinoPage,
      routes.IsOrganisationNomineeNinoController.onPageLoad(CheckMode),
      messagePrefix = "isOrganisationNomineeNino")

  def authorisedPersonNino: Option[SummaryListRow] =
    answerPrefix(OrganisationAuthorisedPersonNinoPage,
      routes.OrganisationAuthorisedPersonNinoController.onPageLoad(CheckMode),
      messagePrefix = "organisationAuthorisedPersonNino")

  def authorisedPersonPassportNumber: Option[SummaryListRow] = None // TODO organisation authorised person passport page

  def authorisedPersonPassportCountry: Option[SummaryListRow] = None // TODO organisation authorised person passport page

  def authorisedPersonPassportExpiry: Option[SummaryListRow] = None // TODO organisation authorised person passport page


  val rows: NomineeSummary = NomineeSummary(
    new NomineeTypeSummaryHelper(userAnswers).rows,
    Seq(
      nomineeName,
      nomineeMainPhone,
      nomineeEmailAddress,
      nomineeAddress,
      nomineeAddressChanged,
      nomineePreviousAddress,
      nomineeCanBePaid
    ).flatten,
    Seq(
      nomineeAccountName,
      nomineeSortCode,
      nomineeAccountNumber,
      nomineeBuildingRoll
    ).flatten,
    Seq(
      authorisedPersonName,
      authorisedPersonDOB,
      authorisedPersonHasNino,
      authorisedPersonNino,
      authorisedPersonPassportNumber,
      authorisedPersonPassportCountry,
      authorisedPersonPassportExpiry
    ).flatten,
    "nomineeDetailsSummary.checkYourAnswers.h2.organisationDetails",
    "nomineeDetailsSummary.checkYourAnswers.h2.organisationBank"
  )

}