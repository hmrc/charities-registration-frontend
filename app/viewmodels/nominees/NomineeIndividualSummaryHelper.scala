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

import controllers.nominees.routes
import models.nominees.NomineeSummary
import models.{CheckMode, UserAnswers}
import pages.addressLookup.{NomineeIndividualAddressLookupPage, NomineeIndividualPreviousAddressLookupPage}
import pages.nominees._
import play.api.i18n.Messages
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class NomineeIndividualSummaryHelper(countryService: CountryService)(override val userAnswers: UserAnswers)
                                    (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {
  def nomineeName: Option[SummaryListRow] =
    answerFullName(IndividualNomineeNamePage,
      routes.IndividualNomineeNameController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineeName")

  def nomineeDOB: Option[SummaryListRow] =
    answerPrefix(IndividualNomineeDOBPage,
    routes.IndividualNomineeDOBController.onPageLoad(CheckMode),
    messagePrefix = "individualNomineeDOB")

  def nomineeMainPhone: Option[SummaryListRow] =
    answerMainPhoneNo(IndividualNomineesPhoneNumberPage,
      routes.IndividualNomineesPhoneNumberController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesPhoneNumber.mainPhoneNumber")

  def nomineeAltPhone: Option[SummaryListRow] =
    answerAlternativePhoneNo(IndividualNomineesPhoneNumberPage,
      routes.IndividualNomineesPhoneNumberController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesPhoneNumber.alternativePhoneNumber")

  def nomineeHasNino: Option[SummaryListRow] =
    answerPrefix(IsIndividualNomineeNinoPage,
      routes.IsIndividualNomineeNinoController.onPageLoad(CheckMode),
      messagePrefix = "isIndividualNomineeNino")

  def nomineeNino: Option[SummaryListRow] =
    answerPrefix(IndividualNomineesNinoPage,
      routes.IndividualNomineesNinoController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesNino")

  def nomineePassportNumber: Option[SummaryListRow] =
    answerPassportNo(IndividualNomineesPassportPage,
      routes.IndividualNomineePassportController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesPassport")

  def nomineePassportCountry: Option[SummaryListRow] =
    answerCountryOfIssue(IndividualNomineesPassportPage,
      routes.IndividualNomineePassportController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesPassport",
      countryService)

  def nomineePassportExpiry: Option[SummaryListRow] =
    answerExpiryDate(IndividualNomineesPassportPage,
      routes.IndividualNomineePassportController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesPassport")

  def nomineeAddress: Option[SummaryListRow] =
    answerAddress(NomineeIndividualAddressLookupPage,
      controllers.addressLookup.routes.NomineeIndividualAddressLookupController.initializeJourney(CheckMode),
      messagePrefix = "nomineeIndividualAddress")

  def nomineeAddressChanged: Option[SummaryListRow] =
    answerPrefix(IsIndividualNomineePreviousAddressPage,
      routes.IsIndividualNomineePreviousAddressController.onPageLoad(CheckMode),
      messagePrefix = "isIndividualNomineePreviousAddress")

  def nomineePreviousAddress: Option[SummaryListRow] =
    answerAddress(NomineeIndividualPreviousAddressLookupPage,
      controllers.addressLookup.routes.NomineeIndividualPreviousAddressLookupController.initializeJourney(CheckMode),
      messagePrefix = "nomineeIndividualPreviousAddress")

  def nomineeCanBePaid: Option[SummaryListRow] =
    answerPrefix(IsIndividualNomineePaymentsPage,
    routes.IsIndividualNomineePaymentsController.onPageLoad(CheckMode),
    messagePrefix = "isIndividualNomineePayments")

  def nomineeAccountName: Option[SummaryListRow] =
    answerAccountName(IndividualNomineesBankDetailsPage,
    routes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode),
    messagePrefix = "individualNomineesBankDetails.accountName")

  def nomineeSortCode: Option[SummaryListRow] =
    answerSortCode(IndividualNomineesBankDetailsPage,
      routes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesBankDetails.sortCode")

  def nomineeAccountNumber: Option[SummaryListRow] =
    answerAccountNumber(IndividualNomineesBankDetailsPage,
      routes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesBankDetails.accountNumber")

  def nomineeBuildingRoll: Option[SummaryListRow] =
    answerRollNumber(IndividualNomineesBankDetailsPage,
      routes.IndividualNomineesBankDetailsController.onPageLoad(CheckMode),
      messagePrefix = "individualNomineesBankDetails.rollNumber")



  val rows: NomineeSummary = NomineeSummary(
    Seq(
      nomineeName,
      nomineeDOB,
      nomineeMainPhone,
      nomineeAltPhone,
      nomineeHasNino,
      nomineeNino,
      nomineePassportNumber,
      nomineePassportCountry,
      nomineePassportExpiry,
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
    authorisedPersonDetails = Seq.empty,
    h2Details = "nomineeDetailsSummary.checkYourAnswers.h2.individualDetails",
    h2BankDetails = "nomineeDetailsSummary.checkYourAnswers.h2.individualBank"
  )

}
