/*
 * Copyright 2025 HM Revenue & Customs
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

package viewmodels

import models.addressLookup.AddressModel
import models.nominees.OrganisationNomineeContactDetails
import models.{BankDetails, Name, Passport, PhoneNumber, SelectTitle, UserAnswers, WithOrder}
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.Call
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.{CurrencyFormatter, ImplicitDateFormatter}

import java.text.DecimalFormat

trait CheckYourAnswersHelper extends ImplicitDateFormatter with SummaryListRowHelper with CurrencyFormatter {

  implicit val messages: Messages

  val userAnswers: UserAnswers

  def answer[A](
    page: QuestionPage[A],
    changeLinkCall: Call,
    answerIsMsgKey: Boolean = false,
    headingMessageArgs: Seq[String] = Seq(),
    idx: Option[Int] = None
  )(implicit messages: Messages, reads: Reads[A], conversion: A => String): Option[SummaryListRow] =
    userAnswers.get(page, idx) map { ans =>
      summaryListRow(
        label = messages(s"$page.checkYourAnswersLabel", headingMessageArgs*),
        value = if (answerIsMsgKey) HtmlContent(messages(s"$page.$ans")) else HtmlContent(conversion(ans)),
        visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel", headingMessageArgs*)),
        changeLinkCall -> messages("site.edit")
      )
    }

  def textBoxAnswer[A](
    page: QuestionPage[A],
    changeLinkCall: Call,
    headingMessageArgs: Seq[String] = Seq(),
    idx: Option[Int] = None
  )(implicit messages: Messages, reads: Reads[A], conversion: A => String): Option[SummaryListRow] =
    userAnswers.get(page, idx) map { ans =>
      summaryListRow(
        label = messages(s"$page.checkYourAnswersLabel", headingMessageArgs*),
        value = HtmlContent(conversion(ans).replaceAll("\r\n", "<br>")),
        visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel", headingMessageArgs*)),
        changeLinkCall -> messages("site.edit")
      )
    }

  def multiLineAnswer[A <: WithOrder](page: QuestionPage[Set[A]], changeLinkCall: Call)(implicit
    reads: Reads[A]
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { ans =>
      summaryListRow(
        label = messages(s"$page.checkYourAnswersLabel"),
        HtmlContent(
          ans.toList
            .sortBy(_.order)
            .foldLeft("")((accumulator, item) => accumulator + "<div>" + messages(s"$page.$item") + "</div>")
        ),
        visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerFullName(page: QuestionPage[Name], changeLinkCall: Call, messagePrefix: String): Option[SummaryListRow] =
    userAnswers.get(page) map { ans =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = HtmlContent(
          if (ans.title == SelectTitle.UnsupportedTitle) ans.getFullName else ans.getFullNameWithTitle(messages)
        ),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerPrefix[A](
    page: QuestionPage[A],
    changeLinkCall: Call,
    answerIsMsgKey: Boolean = false,
    messagePrefix: String,
    headingMessageArgs: Seq[String] = Seq(),
    idx: Option[Int] = None
  )(implicit messages: Messages, reads: Reads[A], conversion: A => String): Option[SummaryListRow] =
    userAnswers.get(page, idx) map { ans =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel", headingMessageArgs*),
        value = if (answerIsMsgKey) HtmlContent(messages(s"$messagePrefix.$ans")) else HtmlContent(conversion(ans)),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel", headingMessageArgs*)),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerMainPhoneNo(
    page: QuestionPage[PhoneNumber],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { contactDetails =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = HtmlContent(contactDetails.daytimePhone),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerAlternativePhoneNo(
    page: QuestionPage[PhoneNumber],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).flatMap { contactDetails =>
      contactDetails.mobilePhone.map { mobilePhone =>
        summaryListRow(
          label = messages(s"$messagePrefix.checkYourAnswersLabel"),
          value = HtmlContent(mobilePhone),
          visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      }
    }

  def answerOrgPhoneNumber(
    page: QuestionPage[OrganisationNomineeContactDetails],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { contactDetails =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = HtmlContent(contactDetails.phoneNumber),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerOrgEmailAddress(
    page: QuestionPage[OrganisationNomineeContactDetails],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { contactDetails =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = HtmlContent(contactDetails.email),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerAccountName(
    page: QuestionPage[BankDetails],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { contactDetails =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = HtmlContent(contactDetails.accountName),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerSortCode(
    page: QuestionPage[BankDetails],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { contactDetails =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = HtmlContent(contactDetails.sortCode),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerAccountNumber(
    page: QuestionPage[BankDetails],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { contactDetails =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = HtmlContent(contactDetails.accountNumber),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerRollNumber(
    page: QuestionPage[BankDetails],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).flatMap { contactDetails =>
      contactDetails.rollNumber.map { rollNumber =>
        summaryListRow(
          label = messages(s"$messagePrefix.checkYourAnswersLabel"),
          value = HtmlContent(rollNumber),
          visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
          changeLinkCall -> messages("site.edit")
        )
      }
    }

  def answerAddress(
    page: QuestionPage[AddressModel],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page) map { address =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = Text(
          Seq(
            Some(address.lines.mkString(", ")),
            address.postcode,
            CountryService.find(address.country.code).map(c => c.name)
          ).flatten.mkString(", ")
        ),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerPassportNo(
    page: QuestionPage[Passport],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { passport =>
      summaryListRow(
        label = messages(s"$messagePrefix.passportNumber.checkYourAnswersLabel"),
        value = HtmlContent(passport.passportNumber),
        visuallyHiddenText = Some(messages(s"$messagePrefix.passportNumber.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerCountryOfIssue(
    page: QuestionPage[Passport],
    changeLinkCall: Call,
    messagePrefix: String,
    countryService: CountryService
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { passport =>
      summaryListRow(
        label = messages(s"$messagePrefix.country.checkYourAnswersLabel"),
        value = HtmlContent(countryService.find(passport.country).fold(passport.country)(_.name)),
        visuallyHiddenText = Some(messages(s"$messagePrefix.country.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerExpiryDate(
    page: QuestionPage[Passport],
    changeLinkCall: Call,
    messagePrefix: String
  ): Option[SummaryListRow] =
    userAnswers.get(page).map { passport =>
      summaryListRow(
        label = messages(s"$messagePrefix.expiryDate.checkYourAnswersLabel"),
        value = HtmlContent(passport.expiryDate),
        visuallyHiddenText = Some(messages(s"$messagePrefix.expiryDate.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  implicit val yesNoValue: Boolean => String = {
    case true => messages("site.yes")
    case _    => messages("site.no")
  }

  implicit def bigDecToString: BigDecimal => String = number => {
    val format = new DecimalFormat
    "£" + format.format(number)
  }

}
