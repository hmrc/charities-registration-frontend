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

package viewmodels.authorisedOfficials

import models.{Index, SelectTitle, UserAnswers}
import pages.QuestionPage
import pages.addressLookup._
import pages.authorisedOfficials._
import pages.sections.Section7Page
import viewmodels.StatusHelper
import viewmodels._

object AuthorisedOfficialsStatusHelper extends StatusHelper {

  implicit val sectionPage: QuestionPage[Boolean] = Section7Page

  private def journeyCommon(index: Int): Seq[QuestionPage[_]] = Seq(
    AuthorisedOfficialsNamePage(index),
    AuthorisedOfficialsDOBPage(index),
    AuthorisedOfficialsPhoneNumberPage(index),
    AuthorisedOfficialsPositionPage(index),
    IsAuthorisedOfficialNinoPage(index),
    AuthorisedOfficialAddressLookupPage(index),
    IsAuthorisedOfficialPreviousAddressPage(index)
  )

  private def remainingJourneyPages(index: Int): Seq[QuestionPage[_]] = Seq(
    AuthorisedOfficialsNinoPage(index),
    AuthorisedOfficialsPassportPage(index),
    AuthorisedOfficialPreviousAddressLookupPage(index)
  )

  private val authorisedOfficial1common: Seq[QuestionPage[_]] = journeyCommon(0) ++ Seq(IsAddAnotherAuthorisedOfficialPage)

  private val authorisedOfficial2common: Seq[QuestionPage[_]] = journeyCommon(1)

  private val allPages: Seq[QuestionPage[_]] = {
    authorisedOfficial1common ++ authorisedOfficial2common ++
      remainingJourneyPages(0) ++ remainingJourneyPages(1)
  }

  private def officialsTitleIsLegal(userAnswers: UserAnswers, index: Int): Boolean =
    userAnswers.get(AuthorisedOfficialsNamePage(index)) match {
    case Some(name) if name.title == SelectTitle.UnsupportedTitle => false
    case _ => true
  }

  def validateDataFromOldService(userAnswers: UserAnswers): Boolean = {
    val range = if (userAnswers.get(IsAddAnotherAuthorisedOfficialPage).contains(true)) Seq(0, 1) else Seq(0)

    range.forall(index => officialsTitleIsLegal(userAnswers, index))
  }


  override def checkComplete(userAnswers: UserAnswers): Boolean = {

    def noAdditionalPagesDefined(list: Seq[QuestionPage[_]]): Boolean = userAnswers.unneededPagesNotPresent(list, allPages)

    (userAnswers.get(IsAuthorisedOfficialNinoPage(0)), userAnswers.get(IsAuthorisedOfficialPreviousAddressPage(0))) match {
      case (Some(isNino1), Some(isPreviousAddress1)) =>

        userAnswers.get(IsAddAnotherAuthorisedOfficialPage) match {
          case Some(false) =>
            val newPages = authorisedOfficial1common.getOfficialPages(0, isNino1, isPreviousAddress1)

            userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages) && validateDataFromOldService(userAnswers)
          case Some(true) =>

            (userAnswers.get(IsAuthorisedOfficialNinoPage(1)), userAnswers.get(IsAuthorisedOfficialPreviousAddressPage(1))) match {
              case (Some(isNino2), Some(isPreviousAddress2)) =>
                val newPages = authorisedOfficial1common
                  .getOfficialPages(0, isNino1, isPreviousAddress1)
                  .getOfficialPages(1, isNino2, isPreviousAddress2, authorisedOfficial2common)

                userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages) && validateDataFromOldService(userAnswers)
              case _ => false
            }
          case _ => false
        }
      case _ => false
    }
  }


  def authorisedOfficialCompleted(index: Index, userAnswers: UserAnswers): Boolean = {
    (userAnswers.get(IsAuthorisedOfficialNinoPage(index)), userAnswers.get(IsAuthorisedOfficialPreviousAddressPage(index))) match {
      case (Some(isNino), Some(isPreviousAddress)) =>
        val list  = journeyCommon(index).getOfficialPages(index, isNino, isPreviousAddress)
        userAnswers.arePagesDefined(list) &&
          userAnswers.unneededPagesNotPresent(list, remainingJourneyPages(index)) &&
          officialsTitleIsLegal(userAnswers, index)

      case _ => false
    }
  }

}
