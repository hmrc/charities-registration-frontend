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

package transformers

import base.SpecBase
import models.oldCharities._
import org.joda.time.LocalDate
import play.api.libs.json.Json
import utils.TestData

class UserAnswerTransformerSpec extends SpecBase with TestData {
  //scalastyle:off magic.number
  val jsonTransformer: UserAnswerTransformer = new UserAnswerTransformer

  "UserAnswerTransformer" when {

    "toUserAnswerCharityContactDetails" must {

      "convert the ContactDetails" in {
        Json.obj("charityContactDetails" -> Json.toJson(contactDetails)).transform(
          jsonTransformer.toUserAnswerCharityContactDetails).asOpt.value mustBe Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false)
      }
    }

    "toUserAnswerCharityOfficialAddress" must {

      "convert the OfficialAddress" in {
        Json.obj("charityOfficialAddress" -> Json.toJson(charityAddress)).transform(
          jsonTransformer.toUserAnswerCharityOfficialAddress).asOpt.value mustBe Json.obj(
          "charityOfficialAddress" -> Json.parse("""{"postcode":"postcode","country":{"code":"GB","name":"United Kingdom"},"lines":["Test123","line2"]}"""))
      }
    }

    "toUserAnswerCorrespondenceAddress" must {

      "convert the CorrespondenceAddress" in {
        Json.obj("correspondenceAddress" -> Json.toJson(correspondenceAddress)).transform(
          jsonTransformer.toUserAnswerCorrespondenceAddress).asOpt.value mustBe Json.obj(
          "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"United Kingdom"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "canWeSendLettersToThisAddress" -> false
        )
      }
    }

    "toUserAnswersCharityRegulator" must {

      "convert to correct charityRegulator5" in {
        Json.obj("charityRegulator" -> Json.toJson(noCharityRegulator5)).transform(
          jsonTransformer.toUserAnswersCharityRegulator).asOpt.value mustBe Json.parse(
          """{"isSection2Completed":false,"isCharityRegulator":false,
						|"selectWhyNoRegulator":"1","whyNotRegisteredWithCharity":"Reason"}""".stripMargin)
      }

      "convert to correct charityRegulator6" in {
        Json.obj("charityRegulator" -> Json.toJson(noCharityRegulator6)).transform(
          jsonTransformer.toUserAnswersCharityRegulator).asOpt.value mustBe Json.parse(
          """{"isSection2Completed":false,"isCharityRegulator":false,
						|"selectWhyNoRegulator":"2","whyNotRegisteredWithCharity":"Reason"}""".stripMargin)
      }

      "convert to correct charityRegulator7" in {
        Json.obj("charityRegulator" -> Json.toJson(noCharityRegulator7)).transform(
          jsonTransformer.toUserAnswersCharityRegulator).asOpt.value mustBe Json.parse(
          """{"isSection2Completed":false,"isCharityRegulator":false,
						|"selectWhyNoRegulator":"3","whyNotRegisteredWithCharity":"Reason"}""".stripMargin)
      }

      "convert to correct charityRegulator8" in {
        Json.obj("charityRegulator" -> Json.toJson(noCharityRegulator8)).transform(
          jsonTransformer.toUserAnswersCharityRegulator).asOpt.value mustBe Json.parse(
          """{"isSection2Completed":false,"isCharityRegulator":false,
						|"selectWhyNoRegulator":"4","whyNotRegisteredWithCharity":"Reason"}""".stripMargin)
      }

      "convert to correct charityRegulator9" in {
        Json.obj("charityRegulator" -> Json.toJson(noCharityRegulator9)).transform(
          jsonTransformer.toUserAnswersCharityRegulator).asOpt.value mustBe Json.parse(
          """{"isSection2Completed":false,"isCharityRegulator":false,
						|"selectWhyNoRegulator":"5","whyNotRegisteredWithCharity":"Reason"}""".stripMargin)
      }

      "convert to correct charityRegulator10" in {
        Json.obj("charityRegulator" -> Json.toJson(noCharityRegulator10)).transform(
          jsonTransformer.toUserAnswersCharityRegulator).asOpt.value mustBe Json.parse(
          """{"isSection2Completed":false,"isCharityRegulator":false,
						|"selectWhyNoRegulator":"7","whyNotRegisteredWithCharity":"Reason"}""".stripMargin)
      }
    }

    "toUserAnswersCharityGoverningDocument" must {

      "convert to correct GoverningDocument for document 1" in {
        Json.obj("charityGoverningDocument" -> Json.toJson(charityGoverningDocument1)).transform(
          jsonTransformer.toUserAnswersCharityGoverningDocument).asOpt.value mustBe Json.parse(
          """{"whenGoverningDocumentApproved":"1990-11-11","isApprovedGoverningDocument":true,"isSection3Completed":false,
						|"sectionsChangedGoverningDocument":"test","governingDocumentName":"","selectGoverningDocument":"3"}""".stripMargin)
      }

      "convert to correct GoverningDocument for document 3" in {
        Json.obj("charityGoverningDocument" -> Json.toJson(charityGoverningDocument3)).transform(
          jsonTransformer.toUserAnswersCharityGoverningDocument).asOpt.value mustBe Json.parse(
          """{"whenGoverningDocumentApproved":"1990-11-11","isApprovedGoverningDocument":true,"isSection3Completed":false,
						|"sectionsChangedGoverningDocument":"test","governingDocumentName":"","selectGoverningDocument":"4"}""".stripMargin)
      }

      "convert to correct GoverningDocument for document 4" in {
        Json.obj("charityGoverningDocument" -> Json.toJson(charityGoverningDocument4)).transform(
          jsonTransformer.toUserAnswersCharityGoverningDocument).asOpt.value mustBe Json.parse(
          """{"whenGoverningDocumentApproved":"1990-11-11","isApprovedGoverningDocument":true,"isSection3Completed":false,
						|"sectionsChangedGoverningDocument":"test","governingDocumentName":"","selectGoverningDocument":"5"}""".stripMargin)
      }

      "convert to correct GoverningDocument for document 6" in {
        Json.obj("charityGoverningDocument" -> Json.toJson(charityGoverningDocument6)).transform(
          jsonTransformer.toUserAnswersCharityGoverningDocument).asOpt.value mustBe Json.parse(
          """{"whenGoverningDocumentApproved":"1990-11-11","isApprovedGoverningDocument":true,"isSection3Completed":false,
						|"sectionsChangedGoverningDocument":"test","governingDocumentName":"","selectGoverningDocument":"2"}""".stripMargin)
      }

      "convert to correct GoverningDocument for document 7" in {
        Json.obj("charityGoverningDocument" -> Json.toJson(charityGoverningDocument7)).transform(
          jsonTransformer.toUserAnswersCharityGoverningDocument).asOpt.value mustBe Json.parse(
          """{"whenGoverningDocumentApproved":"1990-11-11","isApprovedGoverningDocument":true,"isSection3Completed":false,
						|"sectionsChangedGoverningDocument":"test","governingDocumentName":"","selectGoverningDocument":"6"}""".stripMargin)
      }
    }

    "toUserAnswersCharityHowManyAuthOfficials" must {

      "convert to isAddAnotherOfficial: true with 2 auth officials" in {

        val howMany: CharityHowManyAuthOfficials = CharityHowManyAuthOfficials(Some(22))

        Json.obj("charityHowManyAuthOfficials" -> Json.toJson(howMany)).transform(
          jsonTransformer.toUserAnswersCharityHowManyAuthOfficials).asOpt.value mustBe Json.obj(
          "isAddAnotherOfficial" -> true,
          "isSection7Completed" -> false
        )
      }

      "convert to isAddAnotherOfficial: true with 1 auth officials" in {

        val howMany: CharityHowManyAuthOfficials = CharityHowManyAuthOfficials(Some(11))

        Json.obj("charityHowManyAuthOfficials" -> Json.toJson(howMany)).transform(
          jsonTransformer.toUserAnswersCharityHowManyAuthOfficials).asOpt.value mustBe Json.obj(
          "isAddAnotherOfficial" -> false,
          "isSection7Completed" -> false
        )
      }

      "do nothing if the question wasn't answered" in {

        Json.obj("charityHowManyAuthOfficials" -> Json.obj()).transform(
          jsonTransformer.toUserAnswersCharityHowManyAuthOfficials).asOpt.value mustBe Json.obj()
      }
    }

    "toUserAnswersCharityHowManyOtherOfficials" must {

      "convert to addAnotherOtherOfficial: true with 3 other officials" in {

        val howMany: CharityHowManyOtherOfficials = CharityHowManyOtherOfficials(Some(33))

        Json.obj("charityHowManyOtherOfficials" -> Json.toJson(howMany)).transform(
          jsonTransformer.toUserAnswersCharityHowManyOtherOfficials).asOpt.value mustBe Json.obj(
          "addAnotherOtherOfficial" -> true,
          "isSection8Completed" -> false
        )
      }

      "convert to addAnotherOtherOfficial: true with 2 other officials" in {

        val howMany: CharityHowManyOtherOfficials = CharityHowManyOtherOfficials(Some(22))

        Json.obj("charityHowManyOtherOfficials" -> Json.toJson(howMany)).transform(
          jsonTransformer.toUserAnswersCharityHowManyOtherOfficials).asOpt.value mustBe Json.obj(
          "addAnotherOtherOfficial" -> false,
          "isSection8Completed" -> false

        )
      }

      "do nothing if the question wasn't answered" in {

        Json.obj("charityHowManyOtherOfficials" -> Json.obj()).transform(
          jsonTransformer.toUserAnswersCharityHowManyOtherOfficials).asOpt.value mustBe Json.obj()
      }
    }

    "toOneOfficial" must {

      "convert to an authorised official with nino and previous address" in {

        val identity: OfficialIndividualIdentity = OfficialIndividualIdentity(Some("true"), "AB111111A", OfficialIndividualNationalIdentityCardDetails("", "", None))
        val currentAddress: CharityAddress = CharityAddress("current", "address", "", "", "AA1 1AA", "")
        val previousAddress: OptionalCharityAddress = OptionalCharityAddress(Some("true"), CharityAddress("previous", "address", "", "", "AA2 2AA", ""))
        val authOfficial: CharityAuthorisedOfficialIndividual = CharityAuthorisedOfficialIndividual("0001", "First", "Middle", "Last",
          LocalDate.parse("1990-01-01"), "01", "0123123123", Some("0123123124"), None, currentAddress, previousAddress, identity)

        Json.obj("authorisedOfficialIndividual1" -> Json.toJson(authOfficial)).transform(
          jsonTransformer.toOneOfficial(0, "authorised")).asOpt.value mustBe Json.parse(
          """{
            |    "isOfficialPreviousAddress": true,
            |    "officialsPhoneNumber": {
            |        "mobilePhone": "0123123124",
            |        "daytimePhone": "0123123123"
            |    },
            |    "officialsPosition": "01",
            |    "officialsDOB": "1990-01-01",
            |    "isOfficialNino": true,
            |    "officialAddress": {
            |        "country": {
            |            "code": "GB",
            |            "name": "United Kingdom"
            |        },
            |        "postcode": "AA1 1AA",
            |        "lines": [
            |            "current",
            |            "address"
            |        ]
            |    },
            |    "officialPreviousAddress": {
            |         "country": {
            |             "code": "GB",
            |             "name": "United Kingdom"
            |         },
            |         "postcode": "AA2 2AA",
            |         "lines": [
            |             "previous",
            |             "address"
            |         ]
            |    },
            |    "officialsName": {
            |        "firstName": "First",
            |        "lastName": "Last",
            |        "middleName": "Middle",
            |        "title": "0001"
            |    },
            |    "officialsNino": "AB111111A"
            |}""".stripMargin
        )
      }

      "convert to an authorised official with passport and no previous address" in {

        val identity: OfficialIndividualIdentity = OfficialIndividualIdentity(Some("false"), "",
          OfficialIndividualNationalIdentityCardDetails("number", "United Kingdom", Some(LocalDate.parse("2100-01-01"))))
        val currentAddress: CharityAddress = CharityAddress("current", "address", "", "", "AA1 1AA", "")
        val previousAddress: OptionalCharityAddress = OptionalCharityAddress(Some("false"), CharityAddress("", "", "", "", "", ""))
        val authOfficial: CharityAuthorisedOfficialIndividual = CharityAuthorisedOfficialIndividual("0020", "First", "Middle", "Last",
          LocalDate.parse("1990-01-01"), "01", "0123123123", Some("0123123124"), None, currentAddress, previousAddress, identity)

        Json.obj("authorisedOfficialIndividual1" -> Json.toJson(authOfficial)).transform(
          jsonTransformer.toOneOfficial(0, "authorised")).asOpt.value mustBe Json.parse(
          """{
            |    "isOfficialPreviousAddress": false,
            |    "officialsPhoneNumber": {
            |        "mobilePhone": "0123123124",
            |        "daytimePhone": "0123123123"
            |    },
            |    "officialsPosition": "01",
            |    "officialsDOB": "1990-01-01",
            |    "isOfficialNino": false,
            |    "officialAddress": {
            |        "country": {
            |            "code": "GB",
            |            "name": "United Kingdom"
            |        },
            |        "postcode": "AA1 1AA",
            |        "lines": [
            |            "current",
            |            "address"
            |        ]
            |    },
            |    "officialsName": {
            |        "firstName": "First",
            |        "lastName": "Last",
            |        "middleName": "Middle",
            |        "title": "unsupported"
            |    },
            |    "officialsPassport": {
            |        "passportNumber": "number",
            |        "expiryDate": "2100-01-01",
            |        "country": "United Kingdom"
            |    }
            |}""".stripMargin
        )
      }
    }

    "toUserAnswersCharityAddNominee" must {

      "convert to correct charityAddNominee" in {
        Json.obj("charityAddNominee" -> Json.toJson(CharityAddNominee(None))).transform(
          jsonTransformer.toUserAnswersCharityAddNominee).asOpt.value mustBe Json.parse(
          """{"isSection9Completed":false,"nominee":{"isAuthoriseNominee":false}}""".stripMargin)
      }
    }

    "toUserAnswersCharityNomineeStatus" must {

      "convert to correct charityNomineeStatus" in {
        Json.obj("charityNomineeStatus" -> Json.toJson(CharityNomineeStatus(None))).transform(
          jsonTransformer.toUserAnswersCharityNomineeStatus).asOpt.value mustBe Json.obj()
      }
    }

    "toUserAnswersAcknowledgement" must {

      "convert to correct Acknowledgement" in {
        Json.obj("acknowledgement-Reference" -> Json.toJson(acknowledgement)).transform(
          jsonTransformer.toUserAnswersOldAcknowledgement).asOpt.value mustBe Json.obj("oldAcknowledgement" -> Json.obj(
          "submissionDate" -> "9:56am, Tuesday 1 December 2020", "refNumber" -> "080582080582"))
      }
    }

    "toUserAnswersOperationAndFunds" must {

      "convert to the correct OperationAndFunds object with foreign countries of operation" in {
        Json.obj("operationAndFunds" -> Json.toJson(operationAndFundsFiveCountries)).transform(
          jsonTransformer.toUserAnswersOperationAndFunds).asOpt.value mustBe Json.parse(
          """
            |{
            |    "isSection5Completed": false,
            |    "operatingLocation": [
            |        "1",
            |        "2",
            |        "3",
            |        "4",
            |        "5"
            |    ],
            |    "actualIncome": 100,
            |    "selectFundRaising": [
            |        "donations"
            |    ],
            |    "whatCountryDoesTheCharityOperateIn": [
            |        {"overseasCountry": "AA"},
            |        {"overseasCountry": "BB"},
            |        {"overseasCountry": "CC"},
            |        {"overseasCountry": "DD"},
            |        {"overseasCountry": "EE"}
            |    ],
            |    "isBankStatements": true,
            |    "whyNoBankStatement": "noBankStatements",
            |    "accountingPeriodEndDate": "--1-1",
            |    "estimatedIncome": 100,
            |    "isFinancialAccounts": true,
            |    "otherFundRaising": "fundsOther"
            |}
            |""".stripMargin)
      }

      "convert to the correct OperationAndFunds object with foreign countries of operation and no UK-Wide" in {
        Json.obj("operationAndFunds" -> Json.toJson(operationAndFundsFiveCountriesNoUK)).transform(
          jsonTransformer.toUserAnswersOperationAndFunds).asOpt.value mustBe Json.parse(
          """
            |{
            |    "isSection5Completed": false,
            |    "operatingLocation": [
            |        "5"
            |    ],
            |    "actualIncome": 100,
            |    "selectFundRaising": [
            |        "donations"
            |    ],
            |    "whatCountryDoesTheCharityOperateIn": [
            |        {"overseasCountry": "AA"},
            |        {"overseasCountry": "BB"},
            |        {"overseasCountry": "CC"},
            |        {"overseasCountry": "DD"},
            |        {"overseasCountry": "EE"}
            |    ],
            |    "isBankStatements": true,
            |    "whyNoBankStatement": "noBankStatements",
            |    "accountingPeriodEndDate": "--1-1",
            |    "estimatedIncome": 100,
            |    "isFinancialAccounts": true,
            |    "otherFundRaising": "fundsOther"
            |}
            |""".stripMargin)
      }

      "convert to the correct OperationAndFunds object with no foreign countries of operation, UK-Wide" in {
        Json.obj("operationAndFunds" -> Json.toJson(operationAndFundsUKWide)).transform(
          jsonTransformer.toUserAnswersOperationAndFunds).asOpt.value mustBe Json.parse(
          """
            |{
            |    "isSection5Completed": false,
            |    "operatingLocation": [
            |        "1",
            |        "2",
            |        "3",
            |        "4"
            |    ],
            |    "actualIncome": 100,
            |    "selectFundRaising": [
            |        "donations"
            |    ],
            |    "isBankStatements": true,
            |    "whyNoBankStatement": "noBankStatements",
            |    "accountingPeriodEndDate": "--1-1",
            |    "estimatedIncome": 100,
            |    "isFinancialAccounts": true,
            |    "otherFundRaising": "fundsOther"
            |}
            |""".stripMargin)
      }
    }
  }

}
