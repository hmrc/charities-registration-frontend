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

package viewmodels.charityInformation

import models.addressLookup.AddressModel
import models.{CharityContactDetails, CharityName, CheckMode, UserAnswers}
import pages.addressLookup.CharityInformationAddressLookupPage
import pages.charityInformation.{CharityContactDetailsPage, CharityNamePage}
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.{CurrencyFormatter, ImplicitDateFormatter}
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class CharityInformationSummaryHelper(override val userAnswers: UserAnswers)
                                     (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper with CurrencyFormatter {


  def charityNameRows: Seq[SummaryListRow] =
    userAnswers.get(CharityNamePage).map{ name =>
      answerCharityName(name, controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode))
    }.fold(List[SummaryListRow]())(_.toList)

  def charityContactDetailsRows: Seq[SummaryListRow] =
    userAnswers.get(CharityContactDetailsPage).map{ contact =>
      answerCharityContactDetails(contact, controllers.charityInformation.routes.CharityContactDetailsController.onPageLoad(CheckMode))
    }.fold(List[SummaryListRow]())(_.toList)

  def officialAddressRow: Seq[SummaryListRow] =
    userAnswers.get(CharityInformationAddressLookupPage).map{ address =>
      answerOfficialAddress(address, controllers.addressLookup.routes.CharityInformationAddressLookupController.initializeJourney())
    }.fold(List[SummaryListRow]())(_.toList)

  private def answerCharityName(charityName: CharityName,
                               changeLinkCall: Call)( implicit messages: Messages): Seq[SummaryListRow] = Seq(

    Some(
      summaryListRow(
        label = messages("charityName.fullName.checkYourAnswersLabel"),
        value = charityName.fullName,
        visuallyHiddenText = Some(messages("charityName.fullName.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    ),

    charityName.operatingName.map( name =>
      summaryListRow(
        label = messages("charityName.operatingName.checkYourAnswersLabel"),
        value = name,
        visuallyHiddenText = Some(messages("charityName.operatingName.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerCharityContactDetails(charityContactDetails: CharityContactDetails,
                                  changeLinkCall: Call)( implicit messages: Messages): Seq[SummaryListRow] = Seq(

    Some(
      summaryListRow(
        label = messages("charityContactDetails.mainPhoneNumber.checkYourAnswersLabel"),
        value = charityContactDetails.daytimePhone,
        visuallyHiddenText = Some(messages("charityContactDetails.mainPhoneNumber.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    ),

    charityContactDetails.mobilePhone.map( alternativePhone =>
      summaryListRow(
        label = messages("charityContactDetails.alternativePhoneNumber.checkYourAnswersLabel"),
        value = alternativePhone,
        visuallyHiddenText = Some(messages("charityContactDetails.alternativePhoneNumber.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    ),

    Some(
      summaryListRow(
        label = messages("charityContactDetails.emailAddress.checkYourAnswersLabel"),
        value = charityContactDetails.emailAddress,
        visuallyHiddenText = Some(messages("charityContactDetails.emailAddress.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerOfficialAddress(addressModel: AddressModel,
                        changeLinkCall: Call)( implicit messages: Messages): Seq[SummaryListRow] = Seq(

    Some(
      summaryListRow(
        label = messages("charityInformation.addressLookup.checkYourAnswersLabel"),
        value = Seq(Some(addressModel.lines.mkString(", ")),
                    addressModel.postcode,
                    Some(addressModel.country.name)).flatten.mkString(", "),
        visuallyHiddenText = Some(messages("charityInformation.addressLookup.checkYourAnswersLabel")),
        actions = changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten


  val rows: Seq[SummaryListRow] = Seq(
    charityNameRows,
    charityContactDetailsRows,
    officialAddressRow
  ).flatten

}
