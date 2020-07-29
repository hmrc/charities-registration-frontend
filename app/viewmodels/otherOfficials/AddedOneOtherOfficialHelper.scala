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

package viewmodels.otherOfficials

import controllers.otherOfficials.{routes => otherOfficialRoutes}
import models.{CheckMode, Index, UserAnswers}
import pages.addressLookup.OtherOfficialAddressLookupPage
import pages.otherOfficials._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class AddedOneOtherOfficialHelper(index: Index)(override val userAnswers: UserAnswers)
                                 (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {

  def otherOfficialNamesRow: Option[SummaryListRow] =
    answerFullame(OtherOfficialsNamePage(index),
                  otherOfficialRoutes.OtherOfficialsNameController.onPageLoad(CheckMode, index),
                  messagePrefix = "otherOfficialsName")

  def otherOfficialDobRow: Option[SummaryListRow] =
    answerPrefix(OtherOfficialsDOBPage(index),
                 otherOfficialRoutes.OtherOfficialsDOBController.onPageLoad(CheckMode, index),
                 messagePrefix = "otherOfficialsDOB")

  def otherOfficialMainPhoneNoRow: Option[SummaryListRow] =
    answerMainPhoneNo(OtherOfficialsPhoneNumberPage(index),
      otherOfficialRoutes.OtherOfficialsPhoneNumberController.onPageLoad(CheckMode, index),
      messagePrefix = "otherOfficialsPhoneNumber.mainPhoneNumber")

  def otherOfficialAlternativePhoneNoRow: Option[SummaryListRow] =
    answerAlternativePhoneNo(OtherOfficialsPhoneNumberPage(index),
      otherOfficialRoutes.OtherOfficialsPhoneNumberController.onPageLoad(CheckMode, index),
      messagePrefix = "otherOfficialsPhoneNumber.alternativePhoneNumber")

  def otherOfficialPositionRow: Option[SummaryListRow] =
    answerPrefix(OtherOfficialsPositionPage(index),
                 otherOfficialRoutes.OtherOfficialsPositionController.onPageLoad(CheckMode, index),
                 answerIsMsgKey = true,
                 messagePrefix = "officialsPosition")

  def otherOfficialHasNinoRow: Option[SummaryListRow] = {
    answerPrefix(IsOtherOfficialNinoPage(index),
                 otherOfficialRoutes.IsOtherOfficialNinoController.onPageLoad(CheckMode, index),
                 messagePrefix = "isOtherOfficialNino")
  }

  def otherOfficialNinoRow: Option[SummaryListRow] =
    answerPrefix(OtherOfficialsNinoPage(index),
                 otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(CheckMode, index),
                 messagePrefix = "otherOfficialsNino")

  def otherOfficialAddressRow: Option[SummaryListRow] =
    answerAddress(OtherOfficialAddressLookupPage(index),
                  controllers.addressLookup.routes.OtherOfficialsAddressLookupController.initializeJourney(index),
                  messagePrefix = "otherOfficialAddress")

  def otherOfficialHadPreviousAddressRow: Option[SummaryListRow] =
    answerPrefix(OtherOfficialsPreviousAddressPage(index),
                 otherOfficialRoutes.OtherOfficialsPreviousAddressController.onPageLoad(CheckMode, index),
                 messagePrefix = "otherOfficialsPreviousAddress")


  val rows: Seq[SummaryListRow] = Seq(
    otherOfficialNamesRow,
    otherOfficialDobRow,
    otherOfficialMainPhoneNoRow,
    otherOfficialAlternativePhoneNoRow,
    otherOfficialPositionRow,
    otherOfficialHasNinoRow,
    otherOfficialNinoRow,
    otherOfficialAddressRow,
    otherOfficialHadPreviousAddressRow
  ).flatten

}
