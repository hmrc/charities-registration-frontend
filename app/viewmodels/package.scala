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

import pages.QuestionPage
import pages.addressLookup._
import pages.authorisedOfficials.{AuthorisedOfficialsNinoPage, AuthorisedOfficialsPassportPage}
import pages.nominees._
import pages.otherOfficials.{OtherOfficialsNinoPage, OtherOfficialsPassportPage}
import pages.sections.Section7Page

package object viewmodels {

  implicit class OfficialStatus(override val commonPages: Seq[QuestionPage[?]])(implicit
    sectionPage: QuestionPage[Boolean]
  ) extends StatusUtil {

    private def equivalentPages(index: Int): Map[String, (QuestionPage[?], QuestionPage[?])] = Map(
      ("nino", (AuthorisedOfficialsNinoPage(index), OtherOfficialsNinoPage(index))),
      ("passport", (AuthorisedOfficialsPassportPage(index), OtherOfficialsPassportPage(index))),
      (
        "previousAddress",
        (AuthorisedOfficialPreviousAddressLookupPage(index), OtherOfficialPreviousAddressLookupPage(index))
      )
    )

    private def getPage(section: QuestionPage[Boolean], page: String, index: Int): Seq[QuestionPage[?]] = Seq(
      if (section == Section7Page) {
        equivalentPages(index)(page)._1
      } else {
        equivalentPages(index)(page)._2
      }
    )

    def previousAddressEntry: (Boolean, Int) => Seq[QuestionPage[?]] = (isPreviousAddress: Boolean, index: Int) =>
      updateAvailablePages(
        isPreviousAddress,
        getPage(sectionPage, "previousAddress", index)
      )

    def indexedOfficialStartOfJourney: (Int, Boolean, Seq[QuestionPage[?]]) => Seq[QuestionPage[?]] =
      (index: Int, isNino: Boolean, commonPages: Seq[QuestionPage[?]]) =>
        updateAvailablePages(
          isNino,
          commonPages ++ getPage(sectionPage, "nino", index),
          commonPages ++ getPage(sectionPage, "passport", index)
        )

    def getOfficialPages(
      index: Int,
      isNino: Boolean,
      isPreviousAddress: Boolean,
      previousPages: Seq[QuestionPage[?]] = Seq.empty
    ): Seq[QuestionPage[?]] =
      commonPages
        .indexedOfficialStartOfJourney(index, isNino, previousPages)
        .previousAddressEntry(isPreviousAddress, index)

  }

  implicit class NomineeStatus(override val commonPages: Seq[QuestionPage[?]]) extends StatusUtil {

    def getIndividualNomineePages(
      isNino: Boolean,
      isPreviousAddress: Boolean,
      isPayment: Boolean
    ): Seq[QuestionPage[?]] =
      commonPages
        .updateAvailablePages(isNino, Seq(IndividualNomineesNinoPage), Seq(IndividualNomineesPassportPage))
        .updateAvailablePages(isPreviousAddress, Seq(NomineeIndividualPreviousAddressLookupPage))
        .updateAvailablePages(isPayment, Seq(IndividualNomineesBankDetailsPage))

    def getOrganisationNomineePages(
      isNino: Boolean,
      isPreviousAddress: Boolean,
      isPayment: Boolean
    ): Seq[QuestionPage[?]] =
      commonPages
        .updateAvailablePages(
          isNino,
          Seq(OrganisationAuthorisedPersonNinoPage),
          Seq(OrganisationAuthorisedPersonPassportPage)
        )
        .updateAvailablePages(isPreviousAddress, Seq(OrganisationNomineePreviousAddressLookupPage))
        .updateAvailablePages(isPayment, Seq(OrganisationNomineesBankDetailsPage))

  }
}
