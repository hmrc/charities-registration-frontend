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

package viewmodels.authorisedOfficials

import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import models.{CheckMode, Index, Mode, PlaybackMode, UserAnswers}
import pages.addressLookup.AuthorisedOfficialAddressLookupPage
import pages.authorisedOfficials._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class AddedOneAuthorisedOfficialHelper(index: Index, mode: Mode) (override val userAnswers: UserAnswers)
                                     (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {

  def authOfficialNamesRow: Option[SummaryListRow] =
    answerFullName(AuthorisedOfficialsNamePage(index),
                  authOfficialRoutes.AuthorisedOfficialsNameController.onPageLoad(mode, index),
                  messagePrefix = "authorisedOfficialsName")

  def authOfficialDobRow: Option[SummaryListRow] =
    answerPrefix(AuthorisedOfficialsDOBPage(index),
                 authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(mode, index),
                 messagePrefix = "authorisedOfficialsDOB")

  def authOfficialMainPhoneNoRow: Option[SummaryListRow] =
    answerMainPhoneNo(AuthorisedOfficialsPhoneNumberPage(index),
                      authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(mode, index),
                      messagePrefix = "authorisedOfficialsPhoneNumber.mainPhoneNumber")

  def authOfficialAlternativePhoneNoRow: Option[SummaryListRow] =
    answerAlternativePhoneNo(AuthorisedOfficialsPhoneNumberPage(index),
                             authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(mode, index),
                             messagePrefix = "authorisedOfficialsPhoneNumber.alternativePhoneNumber")

  def authOfficialPositionRow: Option[SummaryListRow] =
    answerPrefix(AuthorisedOfficialsPositionPage(index),
                 authOfficialRoutes.AuthorisedOfficialsPositionController.onPageLoad(mode, index),
                 answerIsMsgKey = true,
                 messagePrefix = "officialsPosition")

  def authOfficialHasNinoRow: Option[SummaryListRow] = {
    answerPrefix(IsAuthorisedOfficialNinoPage(index),
                 authOfficialRoutes.IsAuthorisedOfficialNinoController.onPageLoad(mode, index),
                 messagePrefix = "isAuthorisedOfficialNino")
  }

  def authOfficialNinoRow: Option[SummaryListRow] =
    answerPrefix(AuthorisedOfficialsNinoPage(index),
                 authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(mode, index),
                 messagePrefix = "authorisedOfficialsNino")

  def authOfficialAddressRow: Option[SummaryListRow] =
    answerAddress(AuthorisedOfficialAddressLookupPage(index),
                  controllers.addressLookup.routes.AuthorisedOfficialsAddressLookupController.initializeJourney(index, mode),
                  messagePrefix = "authorisedOfficialAddress")

  def authOfficialHadPreviousAddressRow: Option[SummaryListRow] =
    answerPrefix(AuthorisedOfficialPreviousAddressPage(index),
                 authOfficialRoutes.AuthorisedOfficialPreviousAddressController.onPageLoad(mode, index),
                 messagePrefix = "authorisedOfficialPreviousAddress")


  val rows: Seq[SummaryListRow] = Seq(
    authOfficialNamesRow,
    authOfficialDobRow,
    authOfficialMainPhoneNoRow,
    authOfficialAlternativePhoneNoRow,
    authOfficialPositionRow,
    authOfficialHasNinoRow,
    authOfficialNinoRow,
    authOfficialAddressRow,
    authOfficialHadPreviousAddressRow
  ).flatten

}
