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
import models.oldCharities.{CharityAddress, CharityAuthorisedOfficialIndividual, CharityContactDetails, CharityHowManyAuthOfficials, OfficialIndividualIdentity, OfficialIndividualNationalIdentityCardDetails, OptionalCharityAddress}
import org.joda.time.LocalDate
import play.api.libs.json.Json

class UserAnswerTransformerSpec extends SpecBase {
  //scalastyle:off magic.number
  val jsonTransformer: UserAnswerTransformer = new UserAnswerTransformer

  "UserAnswerTransformer" when {

    "toUserAnswerCharityContactDetails" must {

      "convert the ContactDetails" in {

        val contactDetails: CharityContactDetails = CharityContactDetails("Test123", None, "1234567890", None, None, None)

        Json.obj("charityContactDetails" -> Json.toJson(contactDetails)).transform(
          jsonTransformer.toUserAnswerCharityContactDetails).asOpt.value mustBe Json.obj(
           "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
           "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
           "isSection1Completed" -> false)
      }
    }

    "toUserAnswerCharityOfficialAddress" must {

      "convert the OfficialAddress" in {

        val charityAddress: CharityAddress = CharityAddress("Test123", "line2", "", "", "postcode", "")

        Json.obj("charityOfficialAddress" -> Json.toJson(charityAddress)).transform(
          jsonTransformer.toUserAnswerCharityOfficialAddress).asOpt.value mustBe Json.obj(
           "charityOfficialAddress" -> Json.parse("""{"postcode":"postcode","country":{"code":"GB","name":"GB"},"lines":["Test123","line2"]}"""))
      }
    }

    "toUserAnswerCorrespondenceAddress" must {

      "convert the CorrespondenceAddress" in {

        val correspondenceAddress: OptionalCharityAddress = OptionalCharityAddress(Some("true"), CharityAddress("Test123", "line2", "", "", "postcode", ""))

        Json.obj("correspondenceAddress" -> Json.toJson(correspondenceAddress)).transform(
          jsonTransformer.toUserAnswerCorrespondenceAddress).asOpt.value mustBe Json.obj(
           "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "canWeSendLettersToThisAddress" -> false
        )
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
    }

    "toOneOfficial" must {

      "convert to an authorised official with nino and previous address" in {

        val identity: OfficialIndividualIdentity = OfficialIndividualIdentity(Some("true"), "AB111111A", OfficialIndividualNationalIdentityCardDetails("", "", None))
        val currentAddress: CharityAddress = CharityAddress("current", "address", "", "", "AA1 1AA", "")
        val previousAddress: OptionalCharityAddress = OptionalCharityAddress(Some("true"), CharityAddress("previous", "address", "", "", "AA2 2AA", ""))
        val authOfficial: CharityAuthorisedOfficialIndividual = CharityAuthorisedOfficialIndividual("0001", "First", "Middle", "Last",
          LocalDate.parse("1990-01-01"), "01", "0123123123", Some("0123123124"), None, currentAddress, previousAddress, identity)

        Json.obj("authorisedOfficialIndividual1" -> Json.toJson(authOfficial)).transform(
          jsonTransformer.toOneOfficial(0)).asOpt.value mustBe Json.parse(
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
            |            "name": "GB"
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
            |             "name": "GB"
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
          jsonTransformer.toOneOfficial(0)).asOpt.value mustBe Json.parse(
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
            |            "name": "GB"
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

  }

}
