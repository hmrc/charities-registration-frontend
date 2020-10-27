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

import pages.QuestionPage
import pages.addressLookup.AuthorisedOfficialPreviousAddressLookupPage
import pages.authorisedOfficials.{AuthorisedOfficialsNinoPage, AuthorisedOfficialsPassportPage}

package object viewmodels {

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

    def authorisedOfficialAnotherStartOfJourney: (Boolean, Seq[QuestionPage[_]]) => Seq[QuestionPage[_]] =
      (isNino: Boolean, authorisedOfficial2common: Seq[QuestionPage[_]]) => {
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
}
