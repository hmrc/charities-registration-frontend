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

package viewModels

import base.SpecBase
import models.addressLookup.{AddressModel, CountryModel}
import models.{Name, SelectTitle}
import pages.addressLookup._
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import pages.nominees.{IndividualNomineeNamePage, OrganisationAuthorisedPersonNamePage}
import pages.otherOfficials.OtherOfficialsNamePage
import pages.regulatorsAndDocuments.IsCharityRegulatorPage
import play.api.i18n.Messages
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import viewmodels.RequiredDocumentsHelper

class RequiredDocumentsHelperSpec extends SpecBase{

  private val john: Name = Name(SelectTitle.Mr, "John", None, "Smith")

  private val userAnswersForeignAuthOfficial1 = emptyUserAnswers
    .set(AuthorisedOfficialsNamePage(0), john)
    .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0), AddressModel(Seq("aa", "bb"), postcode = None, country = CountryModel("AA", "Aaa"))))
    .flatMap(_.set(IsCharityRegulatorPage, true)).success.value

  private val userAnswersUKAuthOfficial1 = emptyUserAnswers
    .set(AuthorisedOfficialAddressLookupPage(0), AddressModel(Seq("aa", "bb"), postcode = None, country = CountryModel("GB", "United Kingdom"))).success.value

  private val localRequest: FakeRequest[_] = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "cy"))
  private lazy val localMessages: Messages = messagesApi.preferred(localRequest)

  "RequiredDocumentsHelper" must {

    "checkPageCondition" should {

      "return the correct message if page condition is as expected" in {

        RequiredDocumentsHelper.checkPageCondition(userAnswersForeignAuthOfficial1, IsCharityRegulatorPage, true) mustBe
          Some("requiredDocuments.isCharityRegulator.answerTrue")
      }

      "return None if page condition is not as expected" in {

        RequiredDocumentsHelper.checkPageCondition(userAnswersForeignAuthOfficial1, IsCharityRegulatorPage, false) mustBe
          None
      }
    }

    "getNamePageFromAddressPage" should {

      "return auth official's name page if given auth official's address page" in {

        RequiredDocumentsHelper.getNamePageFromAddressPage(AuthorisedOfficialAddressLookupPage(0)) mustBe
          AuthorisedOfficialsNamePage(0)
      }

      "return other official's name page if given other official's address page" in {

        RequiredDocumentsHelper.getNamePageFromAddressPage(OtherOfficialAddressLookupPage(0)) mustBe
          OtherOfficialsNamePage(0)
      }

      "return individual nominee's name page if given individual nominee's address page" in {

        RequiredDocumentsHelper.getNamePageFromAddressPage(NomineeIndividualAddressLookupPage) mustBe
          IndividualNomineeNamePage
      }

      "return organisation nominee's name page if given organisation nominee's address page" in {

        RequiredDocumentsHelper.getNamePageFromAddressPage(OrganisationNomineeAddressLookupPage) mustBe
          OrganisationAuthorisedPersonNamePage
      }
    }

    "getOfficialsAndNomineesNames" should {

      "return an empty sequence if none of the officials live abroad" in {

        RequiredDocumentsHelper.getOfficialsAndNomineesNames(userAnswersUKAuthOfficial1) mustBe Seq.empty
      }

      "return a non-empty sequence if at least one official lives abroad" in {

        RequiredDocumentsHelper.getOfficialsAndNomineesNames(userAnswersForeignAuthOfficial1) mustBe
          Seq(john)
      }
    }

    "formatNames" should {

      "format correctly for 1 name" in {
        RequiredDocumentsHelper.formatNames(Seq(john)) mustBe "John Smith"
      }

      "format correctly for 2 names" in {
        RequiredDocumentsHelper.formatNames(Seq(john, john)) mustBe "John Smith and John Smith"

      }

      "format correctly for 3 names" in {
        RequiredDocumentsHelper.formatNames(Seq(john, john, john)) mustBe "John Smith, John Smith and John Smith"

      }

      "format correctly for 4 or more names" in {
        RequiredDocumentsHelper.formatNames(Seq(john, john, john, john)) mustBe "John Smith, John Smith, John Smith and John Smith"

      }

      "format correctly for 2 names in Welsh" in {
        RequiredDocumentsHelper.formatNames(Seq(john, john))(localMessages) mustBe "John Smith, John Smith"

      }

      "format correctly for 3 names in Welsh" in {
        RequiredDocumentsHelper.formatNames(Seq(john, john, john))(localMessages) mustBe "John Smith, John Smith, John Smith"

      }

      "format correctly for 4 or more names in Welsh" in {
        RequiredDocumentsHelper.formatNames(Seq(john, john, john, john))(localMessages) mustBe "John Smith, John Smith, John Smith, John Smith"

      }
    }

    "getRequiredDocuments" should {

      "return the correct sequence" in {

        RequiredDocumentsHelper.getRequiredDocuments(userAnswersForeignAuthOfficial1) mustBe
          Seq("requiredDocuments.governingDocumentName.answerTrue", "requiredDocuments.isCharityRegulator.answerTrue")
      }
    }

    "getForeignOfficialsMessages" should {

      "return a tuple if there is an official or nominee living abroad" in {
        RequiredDocumentsHelper.getForeignOfficialsMessages(userAnswersForeignAuthOfficial1) mustBe
          Some(("requiredDocuments.foreignAddresses.answerTrue", "John Smith"))
      }

      "return a None if none of the officials or nominees live abroad" in {
        RequiredDocumentsHelper.getForeignOfficialsMessages(userAnswersUKAuthOfficial1) mustBe None
      }
    }
  }

}
