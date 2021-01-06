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

import models.{Index, SelectTitle, UserAnswers}
import pages.QuestionPage
import pages.addressLookup.{OtherOfficialAddressLookupPage, OtherOfficialPreviousAddressLookupPage}
import pages.otherOfficials._
import pages.sections.Section8Page
import viewmodels.StatusHelper
import viewmodels._

object OtherOfficialStatusHelper extends StatusHelper {

  implicit val sectionPage: QuestionPage[Boolean] = Section8Page

  private def journeyCommon(index: Int): Seq[QuestionPage[_]] = Seq(
    OtherOfficialsNamePage(index),
    OtherOfficialsDOBPage(index),
    OtherOfficialsPhoneNumberPage(index),
    OtherOfficialsPositionPage(index),
    IsOtherOfficialNinoPage(index),
    OtherOfficialAddressLookupPage(index),
    IsOtherOfficialsPreviousAddressPage(index)
  )

  private def remainingJourneyPages(index: Int): Seq[QuestionPage[_]] = Seq(
    OtherOfficialsNinoPage(index),
    OtherOfficialsPassportPage(index),
    OtherOfficialPreviousAddressLookupPage(index)
  )

  private val otherOfficial1common: Seq[QuestionPage[_]] = journeyCommon(0)

  private val otherOfficial2common: Seq[QuestionPage[_]] = journeyCommon(1) ++ Seq(IsAddAnotherOtherOfficialPage)

  private val otherOfficial3common: Seq[QuestionPage[_]] = journeyCommon(2)

  private val allPages: Seq[QuestionPage[_]] = {
    otherOfficial1common ++ otherOfficial2common ++ otherOfficial3common ++
      remainingJourneyPages(0) ++ remainingJourneyPages(1) ++ remainingJourneyPages(2)
  }

  private def officialsTitleIsLegal(userAnswers: UserAnswers, index: Int): Boolean =
    userAnswers.get(OtherOfficialsNamePage(index)) match {
      case Some(name) if name.title == SelectTitle.UnsupportedTitle => false
      case _ => true
    }

  def validateDataFromOldService(userAnswers: UserAnswers): Boolean = {
    val range = if (userAnswers.get(IsAddAnotherOtherOfficialPage).contains(true)) Seq(0, 1, 2) else Seq(0, 1)

    range.forall(index => officialsTitleIsLegal(userAnswers, index))
  }

  override def checkComplete(userAnswers: UserAnswers): Boolean = {

    def noAdditionalPagesDefined(list: Seq[QuestionPage[_]]): Boolean = userAnswers.unneededPagesNotPresent(list, allPages)

    (userAnswers.get(IsOtherOfficialNinoPage(0)), userAnswers.get(IsOtherOfficialsPreviousAddressPage(0)),
      userAnswers.get(IsOtherOfficialNinoPage(1)), userAnswers.get(IsOtherOfficialsPreviousAddressPage(1))) match {
      case (Some(isNino1), Some(isPreviousAddress1), Some(isNino2), Some(isPreviousAddress2)) =>

        userAnswers.get(IsAddAnotherOtherOfficialPage) match {

          case Some(false) =>
            val newPages = otherOfficial1common
              .getOfficialPages(0, isNino1, isPreviousAddress1)
              .getOfficialPages(1, isNino2, isPreviousAddress2, otherOfficial2common)

            userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages) && validateDataFromOldService(userAnswers)

          case Some(true) =>

            (userAnswers.get(IsOtherOfficialNinoPage(2)), userAnswers.get(IsOtherOfficialsPreviousAddressPage(2))) match {
              case (Some(isNino3), Some(isPreviousAddress3)) =>
                val newPages = otherOfficial1common
                  .getOfficialPages(0, isNino1, isPreviousAddress1)
                  .getOfficialPages(1, isNino2, isPreviousAddress2, otherOfficial2common)
                  .getOfficialPages(2, isNino3, isPreviousAddress3, otherOfficial3common)

                userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages) && validateDataFromOldService(userAnswers)

              case _ => false
            }
          case _ => false
        }
      case _ => false
    }
  }

  def otherOfficialCompleted(index: Index, userAnswers: UserAnswers): Boolean = {
    (userAnswers.get(IsOtherOfficialNinoPage(index)), userAnswers.get(IsOtherOfficialsPreviousAddressPage(index))) match {
      case (Some(isNino), Some(isPreviousAddress)) =>
        val list  = journeyCommon(index).getOfficialPages(index, isNino, isPreviousAddress)
        userAnswers.arePagesDefined(list) &&
          userAnswers.unneededPagesNotPresent(list, remainingJourneyPages(index)) &&
          officialsTitleIsLegal(userAnswers, index)

      case _ => false
    }
  }
}
