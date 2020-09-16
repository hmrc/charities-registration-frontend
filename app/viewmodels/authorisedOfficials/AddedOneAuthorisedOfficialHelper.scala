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
import models.{CheckMode, Index, UserAnswers}
import pages.addressLookup.AuthorisedOfficialAddressLookupPage
import pages.authorisedOfficials._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class AddedOneAuthorisedOfficialHelper(index: Index) (override val userAnswers: UserAnswers)
                                     (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {

  def authOfficialNamesRow: Option[SummaryListRow] =
    answerFullName(AuthorisedOfficialsNamePage(index),
                  authOfficialRoutes.AuthorisedOfficialsNameController.onPageLoad(CheckMode, index),
                  messagePrefix = "authorisedOfficialsName")

  def authOfficialDobRow: Option[SummaryListRow] =
    answerPrefix(AuthorisedOfficialsDOBPage(index),
                 authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(CheckMode, index),
                 messagePrefix = "authorisedOfficialsDOB")

  def authOfficialMainPhoneNoRow: Option[SummaryListRow] =
    answerMainPhoneNo(AuthorisedOfficialsPhoneNumberPage(index),
                      authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(CheckMode, index),
                      messagePrefix = "authorisedOfficialsPhoneNumber.mainPhoneNumber")

  def authOfficialAlternativePhoneNoRow: Option[SummaryListRow] =
    answerAlternativePhoneNo(AuthorisedOfficialsPhoneNumberPage(index),
                             authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(CheckMode, index),
                             messagePrefix = "authorisedOfficialsPhoneNumber.alternativePhoneNumber")

  def authOfficialPositionRow: Option[SummaryListRow] =
    answerPrefix(AuthorisedOfficialsPositionPage(index),
                 authOfficialRoutes.AuthorisedOfficialsPositionController.onPageLoad(CheckMode, index),
                 answerIsMsgKey = true,
                 messagePrefix = "officialsPosition")

  def authOfficialHasNinoRow: Option[SummaryListRow] = {
    answerPrefix(IsAuthorisedOfficialNinoPage(index),
                 authOfficialRoutes.IsAuthorisedOfficialNinoController.onPageLoad(CheckMode, index),
                 messagePrefix = "isAuthorisedOfficialNino")
  }

  def authOfficialNinoRow: Option[SummaryListRow] =
    answerPrefix(AuthorisedOfficialsNinoPage(index),
                 authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(CheckMode, index),
                 messagePrefix = "authorisedOfficialsNino")

  def authOfficialAddressRow: Option[SummaryListRow] =
    answerAddress(AuthorisedOfficialAddressLookupPage(index),
                  controllers.addressLookup.routes.AuthorisedOfficialsAddressLookupController.initializeJourney(index),
                  messagePrefix = "authorisedOfficialAddress")

  def authOfficialHadPreviousAddressRow: Option[SummaryListRow] =
    answerPrefix(AuthorisedOfficialPreviousAddressPage(index),
                 authOfficialRoutes.AuthorisedOfficialPreviousAddressController.onPageLoad(CheckMode, index),
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
