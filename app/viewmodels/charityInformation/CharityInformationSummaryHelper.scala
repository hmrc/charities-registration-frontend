/*
 * Copyright 2024 HM Revenue & Customs
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
import pages.QuestionPage
import pages.addressLookup.{CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class CharityInformationSummaryHelper(override val userAnswers: UserAnswers)(implicit val messages: Messages)
    extends ImplicitDateFormatter
    with CheckYourAnswersHelper
    with SummaryListRowHelper {

  def charityNameRows: Seq[SummaryListRow] =
    userAnswers
      .get(CharityNamePage)
      .map { name =>
        answerCharityName(name, controllers.contactDetails.routes.CharityNameController.onPageLoad(CheckMode))
      }
      .fold(List[SummaryListRow]())(_.toList)

  def charityContactDetailsRows: Seq[SummaryListRow] =
    userAnswers
      .get(CharityContactDetailsPage)
      .map { contact =>
        answerCharityContactDetails(
          contact,
          controllers.contactDetails.routes.CharityContactDetailsController.onPageLoad(CheckMode)
        )
      }
      .fold(List[SummaryListRow]())(_.toList)

  def officialAddressRow: Seq[SummaryListRow] =
    answerAddress(
      CharityOfficialAddressLookupPage,
      controllers.addressLookup.routes.CharityOfficialAddressLookupController.initializeJourney,
      s"$CharityOfficialAddressLookupPage.addressLookup"
    )

  def canWeSendToThisAddressRow: Seq[SummaryListRow] =
    userAnswers
      .get(CanWeSendToThisAddressPage)
      .map { boolean =>
        answerCanWeSendToThisAddress(
          boolean,
          controllers.contactDetails.routes.CanWeSendToThisAddressController.onPageLoad(CheckMode)
        )
      }
      .fold(List[SummaryListRow]())(_.toList)

  def postalAddressRow: Seq[SummaryListRow] =
    answerAddress(
      CharityPostalAddressLookupPage,
      controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney,
      s"$CharityPostalAddressLookupPage.addressLookup"
    )

  private def answerCharityName(charityName: CharityName, changeLinkCall: Call)(implicit
    messages: Messages
  ): Seq[SummaryListRow] = Seq(
    Some(
      summaryListRow(
        label = messages("charityName.fullName.checkYourAnswersLabel"),
        value = HtmlContent(charityName.fullName),
        visuallyHiddenText = Some(messages("charityName.fullName.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    ),
    charityName.operatingName.map(name =>
      summaryListRow(
        label = messages("charityName.operatingName.checkYourAnswersLabel"),
        value = HtmlContent(name),
        visuallyHiddenText = Some(messages("charityName.operatingName.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    )
  ).flatten

  private def answerCharityContactDetails(charityContactDetails: CharityContactDetails, changeLinkCall: Call)(implicit
    messages: Messages
  ): Seq[SummaryListRow] =
    Seq(
      Some(
        summaryListRow(
          label = messages("charityContactDetails.mainPhoneNumber.checkYourAnswersLabel"),
          value = HtmlContent(charityContactDetails.daytimePhone),
          visuallyHiddenText = Some(messages("charityContactDetails.mainPhoneNumber.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      ),
      charityContactDetails.mobilePhone.map { mobilePhone =>
        summaryListRow(
          label = messages("charityContactDetails.alternativePhoneNumber.checkYourAnswersLabel"),
          value = HtmlContent(mobilePhone),
          visuallyHiddenText = Some(messages("charityContactDetails.alternativePhoneNumber.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      },
      Some(
        summaryListRow(
          label = messages("charityContactDetails.emailAddress.checkYourAnswersLabel"),
          value = HtmlContent(charityContactDetails.emailAddress),
          visuallyHiddenText = Some(messages("charityContactDetails.emailAddress.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      )
    ).flatten

  private def answerAddress(page: QuestionPage[AddressModel], changeLinkCall: Call, messagePrefix: String)(implicit
    messages: Messages
  ): Seq[SummaryListRow] = Seq(
    userAnswers
      .get(page)
      .flatMap(address =>
        Some(
          summaryListRow(
            label = messages(s"$messagePrefix.checkYourAnswersLabel"),
            value = Text(
              Seq(Some(address.lines.mkString(", ")), address.postcode, Some(address.country.name)).flatten
                .mkString(", ")
            ),
            visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
            actions = changeLinkCall -> messages("site.edit")
          )
        )
      )
  ).flatten

  private def answerCanWeSendToThisAddress(canWeSendToThisAddress: Boolean, changeLinkCall: Call)(implicit
    messages: Messages
  ): Seq[SummaryListRow] = Seq(
    if (canWeSendToThisAddress) {
      userAnswers.get(CharityOfficialAddressLookupPage).map { address =>
        summaryListRow(
          label = messages("canWeSendLettersToThisAddress.checkYourAnswersLabel"),
          value = HtmlContent(
            s"<div>${messages("site.yes")}</div>" +
              HtmlFormat.escape(
                Seq(Some(address.lines.mkString(", ")), address.postcode, Some(address.country.name)).flatten
                  .mkString(", ")
              )
          ),
          visuallyHiddenText = Some(messages("canWeSendLettersToThisAddress.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      }
    } else {
      Some(
        summaryListRow(
          label = messages("canWeSendLettersToThisAddress.checkYourAnswersLabel"),
          value = HtmlContent(s"${messages("site.no")}<div>${messages("canWeSendLettersToThisAddress.no.hint")}</div>"),
          visuallyHiddenText = Some(messages("canWeSendLettersToThisAddress.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      )
    }
  ).flatten

  val rows: Seq[SummaryListRow] = Seq(
    charityNameRows,
    charityContactDetailsRows,
    officialAddressRow,
    canWeSendToThisAddressRow,
    postalAddressRow
  ).flatten

}
