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

package viewmodels

import models.addressLookup.AddressModel
import models.{Name, PhoneNumber, UserAnswers, WithOrder}
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.{CurrencyFormatter, ImplicitDateFormatter}

trait CheckYourAnswersHelper extends ImplicitDateFormatter with SummaryListRowHelper with CurrencyFormatter {

  implicit val messages: Messages

  val userAnswers: UserAnswers

  def answer[A](page: QuestionPage[A],
                changeLinkCall: Call,
                answerIsMsgKey: Boolean = false,
                headingMessageArgs: Seq[String] = Seq(),
                idx: Option[Int] = None)
               (implicit messages: Messages, reads: Reads[A], conversion: A => String): Option[SummaryListRow] =
    userAnswers.get(page, idx) map { ans =>
      summaryListRow(
        label = messages(s"$page.checkYourAnswersLabel", headingMessageArgs: _*),
        value = if (answerIsMsgKey) messages(s"$page.$ans") else ans,
        visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel", headingMessageArgs: _*)),
        changeLinkCall -> messages("site.edit")
      )
    }

  def multiLineAnswer[A<: WithOrder](page: QuestionPage[Set[A]],
                                     changeLinkCall: Call)
                                    (implicit reads: Reads[A],
                               conversion: A => String): Option[SummaryListRow] =
    userAnswers.get(page).map { ans =>

      summaryListRow(
        label = messages(s"$page.checkYourAnswersLabel"),
        ans.toList.sortBy(_.order).foldLeft("")((accumulator,item) => accumulator + "<div>" + messages(s"$page.$item") + "</div>"),
        visuallyHiddenText = Some(messages(s"$page.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerFullName(page: QuestionPage[Name],
                     changeLinkCall: Call,
                     messagePrefix: String) : Option[SummaryListRow] =
    userAnswers.get(page) map { ans =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = ans.getFullNameWithTitle(messages),
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerPrefix[A](page: QuestionPage[A],
                changeLinkCall: Call,
                answerIsMsgKey: Boolean = false,
                messagePrefix: String,
                headingMessageArgs: Seq[String] = Seq(),
                idx: Option[Int] = None)
               (implicit messages: Messages, reads: Reads[A], conversion: A => String): Option[SummaryListRow] =
    userAnswers.get(page, idx) map { ans =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel", headingMessageArgs: _*),
        value = if (answerIsMsgKey) messages(s"$messagePrefix.$ans") else ans,
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel", headingMessageArgs: _*)),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerMainPhoneNo(page: QuestionPage[PhoneNumber],
                        changeLinkCall: Call,
                        messagePrefix: String): Option[SummaryListRow] =

    userAnswers.get(page).map { contactDetails =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = contactDetails.daytimePhone,
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

  def answerAlternativePhoneNo(page: QuestionPage[PhoneNumber],
                               changeLinkCall: Call,
                               messagePrefix: String): Option[SummaryListRow] =

    userAnswers.get(page).map { contactDetails =>
      summaryListRow(
        label = messages(s"$messagePrefix.checkYourAnswersLabel"),
        value = contactDetails.mobilePhone,
        visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
        changeLinkCall -> messages("site.edit")
      )
    }

   def answerAddress(page: QuestionPage[AddressModel],
                     changeLinkCall: Call,
                     messagePrefix: String): Option[SummaryListRow] =

     userAnswers.get(page) map { address =>
       summaryListRow(
         label = messages(s"$messagePrefix.checkYourAnswersLabel"),
         value = Seq(Some(address.lines.mkString(", ")),
                     address.postcode,
                     Some(address.country.name)).flatten.mkString(", "),
         visuallyHiddenText = Some(messages(s"$messagePrefix.checkYourAnswersLabel")),
         changeLinkCall -> messages("site.edit")
       )
     }

  implicit val yesNoValue: Boolean => String = {
    case true => messages("site.yes")
    case _ => messages("site.no")
  }

}
