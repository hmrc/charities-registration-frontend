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

package transformers.submission

import base.SpecBase
import models.addressLookup.{AddressModel, CountryModel}
import models.{Name, PhoneNumber, SelectTitle, UserAnswers}
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, AuthorisedOfficialsPhoneNumberPage}
import play.api.libs.json.{Json, __}

class JsonTransformerSpec extends SpecBase {

  val jsonTransformer: JsonTransformer = new JsonTransformer {}

  "JsonTransformer" when {

    "replaceInvalidCharacters" must {

      "newlines form json with spaces" in {

        val input  = "Objects \r\n1.To transform. \r\n2. To make .\r\n3.To advance.\r\nchange"
        val output = "Objects  1.To transform.  2. To make . 3.To advance. change"

        jsonTransformer.replaceInvalidCharacters(input) mustBe output
      }

      "newlines and tabs form json with spaces" in {

        val input  = "1\tWorking.\r\n\r\n2\tEncourage.\r\n\r\n3\tPromote."
        val output = "1 Working.  2 Encourage.  3 Promote."

        jsonTransformer.replaceInvalidCharacters(input) mustBe output
      }

    }

    "Address" must {

      "convert the correct AddressModel" in {

        val userAnswers = emptyUserAnswers
          .set(CharityOfficialAddressLookupPage, addressAllLines)
          .success
          .value

        val expectedJson =
          s"""{
            |  "charityRegistration": {
            |   "common": {
            |    "addressDetails": {
            |      "officialAddress": {
            |        "organisation": "${addressAllLines.organisation.get}",
            |        "postcode": "${addressAllLines.postcode.get}",
            |        "addressLine1": "${addressAllLines.lines.head}",
            |        "addressLine2": "${addressAllLines.lines(1)}",
            |        "addressLine3": "${addressAllLines.lines(2)}",
            |        "addressLine4": "${addressAllLines.lines(3)}",
            |        "nonUKAddress": false
            |      }
            |     }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data
          .transform(
            jsonTransformer.getAddress(
              __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
              __ \ "charityOfficialAddress"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }

      "convert the correct AddressModel with mandatory fields only" in {

        val userAnswers = emptyUserAnswers
          .set(CharityOfficialAddressLookupPage, address.copy(organisation = None, postcode = None, country = thCountryModel))
          .success
          .value

        val expectedJson =
          s"""{
            |  "charityRegistration": {
            |   "common": {
            |    "addressDetails": {
            |      "officialAddress": {
            |        "addressLine1": "${address.lines.head}",
            |        "addressLine2": "${address.lines(1)}",
            |        "nonUKAddress": true,
            |        "nonUKCountry":"${thCountryModel.code}"
            |      }
            |     }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data
          .transform(
            jsonTransformer.getAddress(
              __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
              __ \ "charityOfficialAddress"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }

      "convert the correct AddressModel with UK address" must {

        "Postcode is defined along with line2, line3 and line4" in {

          val userAnswers = emptyUserAnswers
            .set(
              CharityOfficialAddressLookupPage,
              AddressModel(None, Seq("7 Test Street", " ", "  ", "   "), Some("NonUKCode"), CountryModel("IN", "INDIA"))
            )
            .success
            .value

          val expectedJson =
            """{
              |  "charityRegistration": {
              |   "common": {
              |    "addressDetails": {
              |      "officialAddress": {
              |        "addressLine1": "7 Test Street",
              |        "addressLine2": "NonUKCode",
              |        "nonUKAddress": true,
              |        "nonUKCountry":"IN"
              |      }
              |     }
              |    }
              |  }
              |}""".stripMargin

          userAnswers.data
            .transform(
              jsonTransformer.getAddress(
                __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
                __ \ "charityOfficialAddress"
              )
            )
            .asOpt
            .value mustBe Json.parse(expectedJson)
        }

        "Postcode is defined along with line3 and line4" in {

          val userAnswers = emptyUserAnswers
            .set(
              CharityOfficialAddressLookupPage,
              AddressModel(
                None,
                Seq("7", "Test Street", "address line 3", "address line 4"),
                Some("NonUKCode"),
                CountryModel("IN", "INDIA")
              )
            )
            .success
            .value

          val expectedJson =
            """{
              |  "charityRegistration": {
              |   "common": {
              |    "addressDetails": {
              |      "officialAddress": {
              |        "addressLine1": "7",
              |        "addressLine2": "Test Street",
              |        "addressLine3": "address line 3",
              |        "addressLine4": "address line 4, NonUKCode",
              |        "nonUKAddress": true,
              |        "nonUKCountry":"IN"
              |      }
              |     }
              |    }
              |  }
              |}""".stripMargin

          userAnswers.data
            .transform(
              jsonTransformer.getAddress(
                __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
                __ \ "charityOfficialAddress"
              )
            )
            .asOpt
            .value mustBe Json.parse(expectedJson)
        }

        "Postcode is defined along with line3 and line4 where line4 is more than 35 characters" in {

          val userAnswers = emptyUserAnswers
            .set(
              CharityOfficialAddressLookupPage,
              AddressModel(
                None,
                Seq("7", "Test Street", "address line 3", "address line 4 with more than 35 characters"),
                Some("NonUKCode"),
                CountryModel("IN", "INDIA")
              )
            )
            .success
            .value

          val expectedJson =
            """{
              |  "charityRegistration": {
              |   "common": {
              |    "addressDetails": {
              |      "officialAddress": {
              |        "addressLine1": "7",
              |        "addressLine2": "Test Street",
              |        "addressLine3": "address line 3",
              |        "addressLine4": "address line 4 with more than 35 characters",
              |        "nonUKAddress": true,
              |        "nonUKCountry":"IN"
              |      }
              |     }
              |    }
              |  }
              |}""".stripMargin

          userAnswers.data
            .transform(
              jsonTransformer.getAddress(
                __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
                __ \ "charityOfficialAddress"
              )
            )
            .asOpt
            .value mustBe Json.parse(expectedJson)
        }

        "Postcode is defined along with line3 and empty line4" in {

          val userAnswers = emptyUserAnswers
            .set(
              CharityOfficialAddressLookupPage,
              AddressModel(
                None,
                Seq("7", "Test Street", "address line 3", ""),
                Some("NonUKCode"),
                CountryModel("IN", "INDIA")
              )
            )
            .success
            .value

          val expectedJson =
            """{
              |  "charityRegistration": {
              |   "common": {
              |    "addressDetails": {
              |      "officialAddress": {
              |        "addressLine1": "7",
              |        "addressLine2": "Test Street",
              |        "addressLine3": "address line 3",
              |        "addressLine4": "NonUKCode",
              |        "nonUKAddress": true,
              |        "nonUKCountry":"IN"
              |      }
              |     }
              |    }
              |  }
              |}""".stripMargin

          userAnswers.data
            .transform(
              jsonTransformer.getAddress(
                __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
                __ \ "charityOfficialAddress"
              )
            )
            .asOpt
            .value mustBe Json.parse(expectedJson)
        }

        "Postcode is defined along with line3 only" in {

          val userAnswers = emptyUserAnswers
            .set(
              CharityOfficialAddressLookupPage,
              AddressModel(
                None,
                Seq("7", "Test Street", "address line 3"),
                Some("NonUKCode"),
                CountryModel("IN", "INDIA")
              )
            )
            .success
            .value

          val expectedJson =
            """{
              |  "charityRegistration": {
              |   "common": {
              |    "addressDetails": {
              |      "officialAddress": {
              |        "addressLine1": "7",
              |        "addressLine2": "Test Street",
              |        "addressLine3": "address line 3",
              |        "addressLine4": "NonUKCode",
              |        "nonUKAddress": true,
              |        "nonUKCountry":"IN"
              |      }
              |     }
              |    }
              |  }
              |}""".stripMargin

          userAnswers.data
            .transform(
              jsonTransformer.getAddress(
                __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
                __ \ "charityOfficialAddress"
              )
            )
            .asOpt
            .value mustBe Json.parse(expectedJson)
        }

        "Postcode is defined along with line4 without line3" in {

          val userAnswers = emptyUserAnswers
            .set(
              CharityOfficialAddressLookupPage,
              AddressModel(
                None,
                Seq("7", "Test Street", "", "address line 3"),
                Some("NonUKCode"),
                CountryModel("IN", "INDIA")
              )
            )
            .success
            .value

          val expectedJson =
            """{
              |  "charityRegistration": {
              |   "common": {
              |    "addressDetails": {
              |      "officialAddress": {
              |        "addressLine1": "7",
              |        "addressLine2": "Test Street",
              |        "addressLine3": "address line 3",
              |        "addressLine4": "NonUKCode",
              |        "nonUKAddress": true,
              |        "nonUKCountry":"IN"
              |      }
              |     }
              |    }
              |  }
              |}""".stripMargin

          userAnswers.data
            .transform(
              jsonTransformer.getAddress(
                __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
                __ \ "charityOfficialAddress"
              )
            )
            .asOpt
            .value mustBe Json.parse(expectedJson)
        }

        "Postcode is defined along with empty line4 without line3" in {

          val userAnswers = emptyUserAnswers
            .set(
              CharityOfficialAddressLookupPage,
              AddressModel(None, Seq("7", "Test Street", "", " "), Some("NonUKCode"), CountryModel("IN", "INDIA"))
            )
            .success
            .value

          val expectedJson =
            """{
              |  "charityRegistration": {
              |   "common": {
              |    "addressDetails": {
              |      "officialAddress": {
              |        "addressLine1": "7",
              |        "addressLine2": "Test Street",
              |        "addressLine3": "NonUKCode",
              |        "nonUKAddress": true,
              |        "nonUKCountry":"IN"
              |      }
              |     }
              |    }
              |  }
              |}""".stripMargin

          userAnswers.data
            .transform(
              jsonTransformer.getAddress(
                __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
                __ \ "charityOfficialAddress"
              )
            )
            .asOpt
            .value mustBe Json.parse(expectedJson)
        }

        "Postcode is defined along without line3 and line4" in {

          val userAnswers = emptyUserAnswers
            .set(
              CharityOfficialAddressLookupPage,
              AddressModel(None, Seq("7", "Test Street"), Some("NonUKCode"), CountryModel("IN", "INDIA"))
            )
            .success
            .value

          val expectedJson =
            """{
              |  "charityRegistration": {
              |   "common": {
              |    "addressDetails": {
              |      "officialAddress": {
              |        "addressLine1": "7",
              |        "addressLine2": "Test Street",
              |        "addressLine3": "NonUKCode",
              |        "nonUKAddress": true,
              |        "nonUKCountry":"IN"
              |      }
              |     }
              |    }
              |  }
              |}""".stripMargin

          userAnswers.data
            .transform(
              jsonTransformer.getAddress(
                __ \ "charityRegistration" \ "common" \ "addressDetails" \ "officialAddress",
                __ \ "charityOfficialAddress"
              )
            )
            .asOpt
            .value mustBe Json.parse(expectedJson)
        }
      }
    }

    "getOptionalAddress" must {

      "convert the correct OptionalAddress" in {

        val userAnswers = emptyUserAnswers
          .set(
            CharityOfficialAddressLookupPage,
            AddressModel(
              None,
              Seq("7", "Test Street", "line3", "line4"),
              Some("ZY11 1AA"),
              gbCountryModel
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |   "common": {
            |    "addressDetails": {
            |      "charityCorrespondenceAddress": {
            |        "postcode": "ZY11 1AA",
            |        "addressLine1": "7",
            |        "addressLine2": "Test Street",
            |        "addressLine3": "line3",
            |        "addressLine4": "line4",
            |        "nonUKAddress": false
            |      }
            |     }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data
          .transform(
            jsonTransformer.getOptionalAddress(
              __ \ "charityRegistration" \ "common" \ "addressDetails" \ "charityCorrespondenceAddress",
              __ \ "charityOfficialAddress"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }

      "return none if no address is defined" in {

        emptyUserAnswers.data
          .transform(
            jsonTransformer.getOptionalAddress(
              __ \ "charityRegistration" \ "common" \ "addressDetails" \ "charityCorrespondenceAddress",
              __ \ "charityOfficialAddress"
            )
          )
          .asOpt
          .value mustBe Json.parse("""{}""")
      }
    }

    "getPhone" must {

      "remove + from phone number " in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbersWithIntCode)
          .success
          .value

        val expectedJson = s"""{"individualDetails": {"dayPhoneNumber": "${phoneNumbersWithIntCode.daytimePhone.drop(1)}"}}"""

        userAnswers.data
          .transform(
            jsonTransformer.getPhone(
              __ \ "individualDetails" \ "dayPhoneNumber",
              __ \ "authorisedOfficials" \ 0 \ "officialsPhoneNumber" \ "daytimePhone"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)

      }

      "get the phone number as it is" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers)
          .success
          .value

        val expectedJson = s"""{"individualDetails": { "dayPhoneNumber": "${phoneNumbers.daytimePhone}" }}"""

        userAnswers.data
          .transform(
            jsonTransformer.getPhone(
              __ \ "individualDetails" \ "dayPhoneNumber",
              __ \ "authorisedOfficials" \ 0 \ "officialsPhoneNumber" \ "daytimePhone"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)

      }
    }

    "getOptionalPhone" must {

      "remove + from phone number " in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbersWithIntCode)
          .success
          .value

        val expectedJson = s"""{"individualDetails": {"mobilePhone": "${phoneNumbersWithIntCode.mobilePhone.drop(1)}"}}"""

        userAnswers.data
          .transform(
            jsonTransformer.getOptionalPhone(
              __ \ "individualDetails" \ "mobilePhone",
              __ \ "authorisedOfficials" \ 0 \ "officialsPhoneNumber" \ "mobilePhone"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)

      }

      "get the phone number as it is" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers)
          .success
          .value

        val expectedJson = s"""{ "individualDetails": { "mobilePhone": "${phoneNumbers.mobilePhone}" } }"""

        userAnswers.data
          .transform(
            jsonTransformer.getOptionalPhone(
              __ \ "individualDetails" \ "mobilePhone",
              __ \ "authorisedOfficials" \ 0 \ "officialsPhoneNumber" \ "mobilePhone"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)

      }

    }

    "getName" must {

      "convert the correct Name object" in {

        val userAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), personNameWithMiddle)
          .success
          .value

        val expectedJson =
          s"""{
            |  "charityRegistration": {
            |    "common": {
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "${personName3WithoutMiddle.firstName}",
            |          "lastName": "${personName3WithoutMiddle.lastName}",
            |          "lastName": "${personName3WithoutMiddle.middleName.get}",
            |          "title": "0001"
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data
          .transform(
            jsonTransformer.getName(
              __ \ "charityRegistration" \ "common" \ "declarationInfo" \ "name",
              __ \ "authorisedOfficials" \ 0 \ "officialsName"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }

      "convert the correct AddressModel with mandatory fields only" in {

        val userAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), personName3WithoutMiddle)
          .success
          .value

        val expectedJson =
          s"""{
            |  "charityRegistration": {
            |    "common": {
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "${personName3WithoutMiddle.firstName}",
            |          "lastName": "${personName3WithoutMiddle.lastName}",
            |          "title": "0002"
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data
          .transform(
            jsonTransformer.getName(
              __ \ "charityRegistration" \ "common" \ "declarationInfo" \ "name",
              __ \ "authorisedOfficials" \ 0 \ "officialsName"
            )
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)

      }
    }
  }

}
