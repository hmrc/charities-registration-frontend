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

package viewmodels.otherOfficials

import controllers.otherOfficials.{routes => otherOfficialRoutes}
import models.{Index, Mode, UserAnswers}
import pages.addressLookup.{OtherOfficialAddressLookupPage, OtherOfficialPreviousAddressLookupPage}
import pages.authorisedOfficials.AuthorisedOfficialsPassportPage
import pages.otherOfficials._
import play.api.i18n.Messages
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class AddedOtherOfficialHelper(index: Index, mode: Mode, countryService: CountryService)(override val userAnswers: UserAnswers)
                                 (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {

  def otherOfficialNamesRow: Option[SummaryListRow] =
    answerFullName(OtherOfficialsNamePage(index),
                  otherOfficialRoutes.OtherOfficialsNameController.onPageLoad(mode, index),
                  messagePrefix = "otherOfficialsName")

  def otherOfficialDobRow: Option[SummaryListRow] =
    answerPrefix(OtherOfficialsDOBPage(index),
                 otherOfficialRoutes.OtherOfficialsDOBController.onPageLoad(mode, index),
                 messagePrefix = "otherOfficialsDOB")

  def otherOfficialMainPhoneNoRow: Option[SummaryListRow] =
    answerMainPhoneNo(OtherOfficialsPhoneNumberPage(index),
      otherOfficialRoutes.OtherOfficialsPhoneNumberController.onPageLoad(mode, index),
      messagePrefix = "otherOfficialsPhoneNumber.mainPhoneNumber")

  def otherOfficialAlternativePhoneNoRow: Option[SummaryListRow] =
    answerAlternativePhoneNo(OtherOfficialsPhoneNumberPage(index),
      otherOfficialRoutes.OtherOfficialsPhoneNumberController.onPageLoad(mode, index),
      messagePrefix = "otherOfficialsPhoneNumber.alternativePhoneNumber")


  def otherOfficialPositionRow: Option[SummaryListRow] =
    answerPrefix(OtherOfficialsPositionPage(index),
                 otherOfficialRoutes.OtherOfficialsPositionController.onPageLoad(mode, index),
                 answerIsMsgKey = true,
                 messagePrefix = "officialsPosition")

  def otherOfficialHasNinoRow: Option[SummaryListRow] = {
    answerPrefix(IsOtherOfficialNinoPage(index),
                 otherOfficialRoutes.IsOtherOfficialNinoController.onPageLoad(mode, index),
                 messagePrefix = "isOtherOfficialNino")
  }

  def otherOfficialNinoRow: Option[SummaryListRow] =
    answerPrefix(OtherOfficialsNinoPage(index),
                 otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(mode, index),
                 messagePrefix = "otherOfficialsNino")

  def otherOfficialPassportNumberRow: Option[SummaryListRow] =
    answerPassportNo(OtherOfficialsPassportPage(index),
      otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(mode, index),
      messagePrefix = "otherOfficialsPassport")

  def otherOfficialCountryOfIssueRow: Option[SummaryListRow] =
    answerCountryOfIssue(OtherOfficialsPassportPage(index),
      otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(mode, index),
      messagePrefix = "otherOfficialsPassport",
      countryService)

  def otherOfficialExpiryDateRow: Option[SummaryListRow] =
    answerExpiryDate(OtherOfficialsPassportPage(index),
      otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(mode, index),
      messagePrefix = "otherOfficialsPassport")


  def otherOfficialAddressRow: Option[SummaryListRow] =
    answerAddress(OtherOfficialAddressLookupPage(index),
                  controllers.addressLookup.routes.OtherOfficialsAddressLookupController.initializeJourney(index, mode),
                  messagePrefix = "otherOfficialAddress")

  def otherOfficialHadPreviousAddressRow: Option[SummaryListRow] =
    answerPrefix(IsOtherOfficialsPreviousAddressPage(index),
                 otherOfficialRoutes.IsOtherOfficialsPreviousAddressController.onPageLoad(mode, index),
                 messagePrefix = "isOtherOfficialsPreviousAddress")

  def otherOfficialPreviousAddressRow: Option[SummaryListRow] =
    answerAddress(OtherOfficialPreviousAddressLookupPage(index),
      controllers.addressLookup.routes.OtherOfficialsPreviousAddressLookupController.initializeJourney(index, mode),
      messagePrefix = "otherOfficialPreviousAddress")


  val rows: Seq[SummaryListRow] = Seq(
    otherOfficialNamesRow,
    otherOfficialDobRow,
    otherOfficialMainPhoneNoRow,
    otherOfficialAlternativePhoneNoRow,
    otherOfficialPositionRow,
    otherOfficialHasNinoRow,
    otherOfficialNinoRow,
    otherOfficialPassportNumberRow,
    otherOfficialCountryOfIssueRow,
    otherOfficialExpiryDateRow,
    otherOfficialAddressRow,
    otherOfficialHadPreviousAddressRow,
    otherOfficialPreviousAddressRow
  ).flatten

}
