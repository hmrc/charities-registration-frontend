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

import java.time.LocalDate

import models.addressLookup.AddressModel
import models.{CheckMode, Index, UserAnswers}
import models.AuthorisedOfficialsName
import models.AuthOfficials.AuthorisedOfficialsPosition
import pages.addressLookup.AuthorisedOfficialAddressLookupPage
import pages.authorisedOfficials._
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}
import controllers.authorisedOfficials.{routes => authOfficialRoutes}

class AddedOneAuthorisedOfficialHelper(index: Index) (override val userAnswers: UserAnswers)
                                     (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {

  def authOfficialNamesRows: List[SummaryListRow] =
    userAnswers.get(AuthorisedOfficialsNamePage(0)).map{ name =>
      answerAuthOfficialsName(name, authOfficialRoutes.AuthorisedOfficialsNameController.onPageLoad(CheckMode, index))
    }.fold(List[SummaryListRow]())(_.toList)

  def authOfficialDobRows: List[SummaryListRow] =
    userAnswers.get(AuthorisedOfficialsDOBPage(0)).map{ LocalDate =>
      answerAuthOfficialsDOB(LocalDate, authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(CheckMode, index))
    }.fold(List[SummaryListRow]())(_.toList)

  def authOfficialPositionRows: List[SummaryListRow] =
    userAnswers.get(AuthorisedOfficialsPositionPage(0)).map{ position =>
      answerAuthOfficialPosition(position, authOfficialRoutes.AuthorisedOfficialsPositionController.onPageLoad(CheckMode, index))
    }.fold(List[SummaryListRow]())(_.toList)

  def authOfficialHasNINORow: List[SummaryListRow] =
    userAnswers.get(IsAuthorisedOfficialPositionPage(0)).map{ boolean =>
      answerAuthOfficialHasNINO(boolean, authOfficialRoutes.IsAuthorisedOfficialPositionController.onPageLoad(CheckMode, index))
    }.fold(List[SummaryListRow]())(_.toList)

  def authOfficialNINoRows: List[SummaryListRow] =
    userAnswers.get(AuthorisedOfficialsNINOPage(0)).map{ NINO =>
      answerAuthOfficialsNINO(NINO, authOfficialRoutes.AuthorisedOfficialsNINOController.onPageLoad(CheckMode, index))
    }.fold(List[SummaryListRow]())(_.toList)

  def authOfficialAddressRow: List[SummaryListRow] =
    userAnswers.get(AuthorisedOfficialAddressLookupPage(0)).map{ address =>
      answerAuthOfficialAddress(address, controllers.addressLookup.routes.AuthorisedOfficialsAddressLookupController.initializeJourney(index))
    }.fold(List[SummaryListRow]())(_.toList)

  def authOfficialHadPreviousAddressRow: List[SummaryListRow] =
    userAnswers.get(AuthorisedOfficialPreviousAddressPage(0)).map{ boolean =>
      answerAuthOfficialsPreviousAddress(boolean, authOfficialRoutes.AuthorisedOfficialPreviousAddressController.onPageLoad(CheckMode, index))
    }.fold(List[SummaryListRow]())(_.toList)

  private def answerAuthOfficialsName(authorisedOfficialsName: AuthorisedOfficialsName,
                                changeLinkCall: Call)(implicit messages: Messages): Seq[SummaryListRow] = Seq(
    Some(
      summaryListRow(
        label = messages("authorisedOfficialsName.checkYourAnswersLabel"),
        value = authorisedOfficialsName.fullName,
        visuallyHiddenText = Some(messages("authorisedOfficialsName.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerAuthOfficialsDOB(authorisedOfficialsDOB: LocalDate,
                                     changeLinkCall: Call)(implicit messages: Messages): Seq[SummaryListRow] = Seq(
    Some(
      summaryListRow(
        label = messages("authorisedOfficialsDOB.checkYourAnswersLabel"),
        value = authorisedOfficialsDOB,
        visuallyHiddenText = Some(messages("authorisedOfficialsDOB.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerAuthOfficialPosition(authorisedOfficialsPosition: AuthorisedOfficialsPosition,
                                          changeLinkCall: Call)( implicit messages: Messages): Seq[SummaryListRow] = Seq(
    Some(
      summaryListRow(
        label = messages("authorisedOfficialsPosition.checkYourAnswersLabel"),
        value = messages(s"authorisedOfficialsPosition.$authorisedOfficialsPosition"),
        visuallyHiddenText = Some(messages("authorisedOfficialsPosition.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerAuthOfficialHasNINO(authorisedOfficialHasNino: Boolean,
                                     changeLinkCall: Call)(implicit messages: Messages): Seq[SummaryListRow] = Seq(
    Some(
      summaryListRow(
        label = messages("isAuthorisedOfficialPosition.checkYourAnswersLabel"),
        value = authorisedOfficialHasNino,
        visuallyHiddenText = Some(messages("isAuthorisedOfficialPosition.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerAuthOfficialsNINO(authorisedOfficialsNino: String,
                                        changeLinkCall: Call)(implicit messages: Messages): Seq[SummaryListRow] = Seq(
    Some(
      summaryListRow(
        label = messages("authorisedOfficialsNINO.checkYourAnswersLabel"),
        value = authorisedOfficialsNino,
        visuallyHiddenText = Some(messages("authorisedOfficialsNINO.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerAuthOfficialAddress(addressModel: AddressModel,
                                    changeLinkCall: Call)( implicit messages: Messages): Seq[SummaryListRow] = Seq(
    Some(
      summaryListRow(
        label = messages("authorisedOfficialAddress.checkYourAnswersLabel"),
        value = Seq(Some(addressModel.lines.mkString(", ")),
          addressModel.postcode,
          Some(addressModel.country.name)).flatten.mkString(", "),
        visuallyHiddenText = Some(messages("authorisedOfficialAddress.checkYourAnswersLabel")),
        actions = changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerAuthOfficialsPreviousAddress(authorisedOfficialPreviousAddress: Boolean,
                                      changeLinkCall: Call)(implicit messages: Messages): Seq[SummaryListRow] = Seq(
    Some(
      summaryListRow(
        label = messages("authorisedOfficialPreviousAddress.checkYourAnswersLabel"),
        value = authorisedOfficialPreviousAddress,
        visuallyHiddenText = Some(messages("authorisedOfficialPreviousAddress.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  val rows: Seq[SummaryListRow] = Seq(
    authOfficialNamesRows,
    authOfficialDobRows,
    authOfficialPositionRows,
    authOfficialHasNINORow,
    authOfficialNINoRows,
    authOfficialAddressRow,
    authOfficialHadPreviousAddressRow
  ).flatten

}
