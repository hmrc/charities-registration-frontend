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

import models.UserAnswers
import pages.QuestionPage
import pages.addressLookup._
import pages.authorisedOfficials._
import viewmodels.StatusHelper

object AuthorisedOfficialsStatusHelper extends StatusHelper {

  implicit class AuthorisedOfficialStatus(common: Seq[QuestionPage[_]]) {

    def authorisedOfficial1StartOfJourney: Boolean => Seq[QuestionPage[_]] = (isNino: Boolean) => {
      updateList(
        isNino,
        Seq(AuthorisedOfficialsNinoPage(0)),
        Seq(AuthorisedOfficialsPassportPage(0))
      )
    }

    def previousAddressEntry: (Boolean, Int) => Seq[QuestionPage[_]] = (isPreviousAddress: Boolean, index: Int) => {
      updateList(
        isPreviousAddress,
        Seq(AuthorisedOfficialPreviousAddressLookupPage(index))
      )
    }

    def authorisedOfficial2StartOfJourney: Boolean => Seq[QuestionPage[_]] = (isNino: Boolean) => {
      updateList(
        isNino,
        authorisedOfficial2common ++ Seq(AuthorisedOfficialsNinoPage(1)),
        authorisedOfficial2common ++ Seq(AuthorisedOfficialsPassportPage(1))
      )
    }

    def updateList(condition: Boolean, addIfTrue: Seq[QuestionPage[_]], elseAdd: Seq[QuestionPage[_]]= Seq.empty): Seq[QuestionPage[_]] = {
      if (condition) common ++ addIfTrue else common ++ elseAdd
    }
  }


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

  private val allPages: Seq[QuestionPage[_]] =  {
    authorisedOfficial1common ++ authorisedOfficial2common ++
      remainingJourneyPages(0) ++ remainingJourneyPages(1)
  }


  override def checkComplete(userAnswers: UserAnswers): Boolean = {

    def noAdditionalPagesDefined(list: Seq[QuestionPage[_]]): Boolean = userAnswers.unneededPagesNotPresent(list, allPages)

    (userAnswers.get(IsAuthorisedOfficialNinoPage(0)), userAnswers.get(IsAuthorisedOfficialPreviousAddressPage(0))) match {
      case (Some(isNino1), Some(isPreviousAddress1)) =>

        userAnswers.get(IsAddAnotherAuthorisedOfficialPage) match {
          case Some(false) =>
            val newPages = authorisedOfficial1common.authorisedOfficial1StartOfJourney(isNino1).previousAddressEntry(isPreviousAddress1, 0)

            userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages)
          case Some(true) =>

            (userAnswers.get(IsAuthorisedOfficialNinoPage(1)), userAnswers.get(IsAuthorisedOfficialPreviousAddressPage(1))) match {
              case (Some(isNino2), Some(isPreviousAddress2)) =>
                val newPages = authorisedOfficial1common.authorisedOfficial1StartOfJourney(isNino1)
                  .previousAddressEntry(isPreviousAddress1, 0)
                  .authorisedOfficial2StartOfJourney(isNino2)
                  .previousAddressEntry(isPreviousAddress2, 1)

                userAnswers.arePagesDefined(newPages) && noAdditionalPagesDefined(newPages)
              case _ => false
            }
          case _ => false
        }
      case _ => false
    }
  }

}
