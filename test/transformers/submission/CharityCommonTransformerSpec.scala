/*
 * Copyright 2023 HM Revenue & Customs
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
import models.authOfficials.OfficialsPosition
import models.operations.CharityEstablishedOptions
import models.{BankDetails, CharityContactDetails, CharityName, Name, PhoneNumber, SelectTitle}
import pages.addressLookup.{AuthorisedOfficialAddressLookupPage, CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, AuthorisedOfficialsPhoneNumberPage, AuthorisedOfficialsPositionPage}
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import pages.operationsAndFunds.{BankDetailsPage, CharityEstablishedInPage}
import play.api.libs.json.{Json, __}

class CharityCommonTransformerSpec extends SpecBase {

  val jsonTransformer: CharityCommonTransformer = new CharityCommonTransformer

  "CharityCommonTransformer" when {

    "userAnswersToAdmin" must {

      "convert the correct Admin object" in {

        val expectedJson =
          s"""{
            |  "charityRegistration": {
            |    "common": {
            |      "admin": {
            |        "acknowledgmentReference": "15 CHARACTERS S",
            |        "credentialID": "/newauth/credentialId/id",
            |        "sessionID": "$fakeSessionId",
            |        "welshIndicator": false,
            |        "applicationDate": "1970-01-01"
            |      }
            |    }
            |  }
            |}""".stripMargin

        emptyUserAnswers.data.transform(jsonTransformer.userAnswersToAdmin(fakeDataRequest)).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct Admin object when no session Id" in {
        val json      =
          emptyUserAnswers.data.transform(jsonTransformer.userAnswersToAdmin(fakeDataRequestNoSessionId)).asOpt.value
        val sessionId = (json \ "charityRegistration" \ "common" \ "admin" \ "sessionID").as[String]

        sessionId must have length 50
        sessionId must not be empty
      }

      "convert the correct Admin object when empty session Id" in {
        val json      =
          emptyUserAnswers.data.transform(jsonTransformer.userAnswersToAdmin(fakeDataRequestEmptySessionId)).asOpt.value
        val sessionId = (json \ "charityRegistration" \ "common" \ "admin" \ "sessionID").as[String]

        sessionId must have length 50
        sessionId must not be empty
      }

      "convert the correct Admin object when less than 50 is length of session Id" in {
        val json      =
          emptyUserAnswers.data.transform(jsonTransformer.userAnswersToAdmin(fakeDataRequestShortSessionId)).asOpt.value
        val sessionId = (json \ "charityRegistration" \ "common" \ "admin" \ "sessionID").as[String]

        sessionId must have length 50
        sessionId must not be empty
        sessionId must startWith("short id here not 50 chars")
      }

      "convert the correct Admin object when more than 50 is length of session Id" in {
        val json      = emptyUserAnswers.data
          .transform(jsonTransformer.userAnswersToAdmin(fakeDataRequestTooLongSessionId))
          .asOpt
          .value
        val sessionId = (json \ "charityRegistration" \ "common" \ "admin" \ "sessionID").as[String]

        sessionId must have length 50
        sessionId must not be empty
        sessionId mustBe ("short id here not 50 chars" * 10).take(50)
      }

    }

    "userAnswersToOrganisation" must {

      "convert the correct organisation" in {

        val userAnswers = emptyUserAnswers
          .set(
            CharityContactDetailsPage,
            CharityContactDetails("07700 900 982", Some("07700 000 111"), "abc@gmail.com")
          )
          .flatMap(_.set(CharityNamePage, CharityName("ABC", Some("OpName"))))
          .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Scotland))
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "organisation": {
            |        "applicationType": "0",
            |        "emailAddress": "abc@gmail.com",
            |        "countryEstd": "2",
            |        "orgName": "ABC",
            |        "telephoneNumber": "07700 900 982",
            |        "mobileNumber": "07700 000 111",
            |        "operatingName": "OpName"
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct organisation with mandatory fields only" in {
        val userAnswers = emptyUserAnswers
          .set(
            CharityContactDetailsPage,
            CharityContactDetails("07700 900 982", Some("07700 000 111"), "abc@gmail.com")
          )
          .flatMap(_.set(CharityNamePage, CharityName("ABC", None)))
          .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.England))
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "organisation": {
            |        "applicationType": "0",
            |        "emailAddress": "abc@gmail.com",
            |        "countryEstd": "1",
            |        "orgName": "ABC",
            |        "telephoneNumber": "07700 900 982",
            |        "mobileNumber": "07700 000 111"
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct organisation with mandatory fields only with Wales as country of establishment" in {
        val userAnswers = emptyUserAnswers
          .set(
            CharityContactDetailsPage,
            CharityContactDetails("07700 900 982", Some("07700 000 111"), "abc@gmail.com")
          )
          .flatMap(_.set(CharityNamePage, CharityName("ABC", None)))
          .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales))
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "organisation": {
            |        "applicationType": "0",
            |        "emailAddress": "abc@gmail.com",
            |        "countryEstd": "1",
            |        "orgName": "ABC",
            |        "telephoneNumber": "07700 900 982",
            |        "mobileNumber": "07700 000 111"
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToAddressDetailsCommon" must {

      "convert the correct AddressDetailsCommon without charityCorrespondenceAddress" in {

        val userAnswers = emptyUserAnswers
          .set(
            CharityOfficialAddressLookupPage,
            AddressModel(
              Seq("7", "Morrison street", "line3", "line4"),
              Some("G58AN"),
              CountryModel("GB", "United Kingdom")
            )
          )
          .flatMap(_.set(CanWeSendToThisAddressPage, true))
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "addressDetails": {
            |        "differentCorrespondence": false,
            |        "officialAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToAddressDetailsCommon).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct AddressDetailsCommon with charityCorrespondenceAddress" in {

        val userAnswers = emptyUserAnswers
          .set(
            CharityOfficialAddressLookupPage,
            AddressModel(
              Seq("7", "Morrison street", "line3", "line4"),
              Some("G58AN"),
              CountryModel("GB", "United Kingdom")
            )
          )
          .flatMap(_.set(CanWeSendToThisAddressPage, false))
          .flatMap(
            _.set(
              CharityPostalAddressLookupPage,
              AddressModel(Seq("1", "Morrison street"), Some("ZZ11ZZ"), CountryModel("GB", "United Kingdom"))
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "addressDetails": {
            |        "differentCorrespondence": true,
            |        "officialAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKAddress": false
            |        },
            |        "correspondenceAddress": {
            |          "postcode": "ZZ11ZZ",
            |          "addressLine1": "1",
            |          "addressLine2": "Morrison street",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToAddressDetailsCommon).asOpt.value mustBe Json.parse(
          expectedJson
        )

      }

      "convert the correct AddressDetailsCommon with charityCorrespondenceAddress and both addresses are same" in {

        val userAnswers = emptyUserAnswers
          .set(
            CharityOfficialAddressLookupPage,
            AddressModel(
              Seq("7", "Morrison street", "line3", "line4"),
              Some("G58AN"),
              CountryModel("GB", "United Kingdom")
            )
          )
          .flatMap(_.set(CanWeSendToThisAddressPage, false))
          .flatMap(
            _.set(
              CharityPostalAddressLookupPage,
              AddressModel(
                Seq("7", "Morrison street", "line3", "line4"),
                Some("G58AN"),
                CountryModel("GB", "United Kingdom")
              )
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "addressDetails": {
            |        "differentCorrespondence": false,
            |        "officialAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKAddress": false
            |        },
            |        "correspondenceAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToAddressDetailsCommon).asOpt.value mustBe Json.parse(
          expectedJson
        )

      }
    }

    "userAnswersToBankDetails" must {

      "convert the correct BankDetails" in {

        val userAnswers = emptyUserAnswers
          .set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName")))
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "bankDetails": {
            |        "accountName": "fullName",
            |        "rollNumber": "operatingName",
            |        "accountNumber": "12345678",
            |        "sortCode": "123456"
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToBankDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct AddressModel with mandatory fields only" in {

        val userAnswers =
          emptyUserAnswers.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", None)).success.value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "bankDetails": {
            |        "accountName": "fullName",
            |        "accountNumber": "12345678",
            |        "sortCode": "123456"
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToBankDetails).asOpt.value mustBe Json.parse(expectedJson)

      }
    }

    "userAnswersToIndDeclarationInfo" must {

      "convert the correct IndDeclarationInfo with UK data" in {

        val userAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", Some("John"), "Jones"))
          .flatMap(
            _.set(
              AuthorisedOfficialAddressLookupPage(0),
              AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("GB", "United Kingdom"))
            )
          )
          .flatMap(
            _.set(
              CharityOfficialAddressLookupPage,
              AddressModel(
                Seq("7", "Morrison street", "line3", "line4"),
                Some("G58AN"),
                CountryModel("GB", "United Kingdom")
              )
            )
          )
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("+44 7700 900 982", Some("07700 900 981"))))
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.UKAgent))
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "Jim",
            |          "lastName": "Jones",
            |          "middleName": "John",
            |          "title": "0001"
            |        },
            |        "position": "23",
            |        "postcode": "G58AN",
            |        "telephoneNumber": "44 7700 900 982",
            |        "declaration": true,
            |        "overseas": false
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToIndDeclarationInfo).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct IndDeclarationInfo with non UK data" in {

        val userAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", Some("John"), "Jones"))
          .flatMap(
            _.set(
              AuthorisedOfficialAddressLookupPage(0),
              AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))
            )
          )
          .flatMap(
            _.set(
              CharityOfficialAddressLookupPage,
              AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))
            )
          )
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981"))))
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.UKAgent))
          .success
          .value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "Jim",
            |          "lastName": "Jones",
            |          "middleName": "John",
            |          "title": "0001"
            |        },
            |        "position": "23",
            |        "telephoneNumber": "07700 900 982",
            |        "declaration": true,
            |        "overseas": true
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToIndDeclarationInfo).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

    }

    "userAnswersToCommon" must {

      "convert the correct Common object with all data" in {

        val userAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", Some("John"), "Jones"))
          .flatMap(
            _.set(
              AuthorisedOfficialAddressLookupPage(0),
              AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("GB", "United Kingdom"))
            )
          )
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.UKAgent))
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981"))))
          .flatMap(_.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName"))))
          .flatMap(
            _.set(
              CharityOfficialAddressLookupPage,
              AddressModel(
                Seq("7", "Morrison street", "line3", "line4"),
                Some("G58AN"),
                CountryModel("GB", "United Kingdom")
              )
            )
              .flatMap(_.set(CanWeSendToThisAddressPage, false))
          )
          .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales))
          .flatMap(
            _.set(
              CharityPostalAddressLookupPage,
              AddressModel(Seq("1", "Morrison street"), Some("ZZ11ZZ"), CountryModel("GB", "United Kingdom"))
            )
          )
          .flatMap(
            _.set(
              CharityContactDetailsPage,
              CharityContactDetails("07700 900 982", Some("07700 000 111"), "abc@gmail.com")
            )
              .flatMap(
                _.set(
                  CharityOfficialAddressLookupPage,
                  AddressModel(
                    Seq("7", "Morrison street", "line3", "line4"),
                    Some("G58AN"),
                    CountryModel("GB", "United Kingdom")
                  )
                )
              )
              .flatMap(_.set(CharityNamePage, CharityName("ABC", Some("OpName"))))
          )
          .success
          .value

        val expectedJson =
          s"""{
            |  "charityRegistration": {
            |    "common": {
            |      "bankDetails": {
            |        "accountName": "fullName",
            |        "rollNumber": "operatingName",
            |        "accountNumber": "12345678",
            |        "sortCode": "123456"
            |      },
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "Jim",
            |          "lastName": "Jones",
            |          "middleName": "John",
            |          "title": "0001"
            |        },
            |        "position": "23",
            |        "postcode": "G58AN",
            |        "telephoneNumber": "07700 900 982",
            |        "declaration": true,
            |        "overseas": false
            |      },
            |      "admin": {
            |        "acknowledgmentReference": "15 CHARACTERS S",
            |        "credentialID": "/newauth/credentialId/id",
            |        "sessionID": "$fakeSessionId",
            |        "welshIndicator": false,
            |        "applicationDate": "1970-01-01"
            |      },
            |      "organisation": {
            |        "applicationType": "0",
            |        "emailAddress": "abc@gmail.com",
            |        "countryEstd": "1",
            |        "orgName": "ABC",
            |        "telephoneNumber": "07700 900 982",
            |        "mobileNumber": "07700 000 111",
            |        "operatingName": "OpName"
            |      },
            |      "addressDetails": {
            |        "differentCorrespondence": true,
            |        "officialAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKAddress": false
            |        },
            |        "correspondenceAddress": {
            |          "postcode": "ZZ11ZZ",
            |          "addressLine1": "1",
            |          "addressLine2": "Morrison street",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToCommon(fakeDataRequest)).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct Common object when officialAddress and correspondenceAddress" in {

        val userAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", Some("John"), "Jones"))
          .flatMap(
            _.set(
              AuthorisedOfficialAddressLookupPage(0),
              AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("GB", "United Kingdom"))
            )
          )
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981"))))
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.UKAgent))
          .flatMap(_.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName"))))
          .flatMap(
            _.set(
              CharityOfficialAddressLookupPage,
              AddressModel(
                Seq("7", "Morrison street", "line3", "line4"),
                Some("G58AN"),
                CountryModel("GB", "United Kingdom")
              )
            )
          )
          .flatMap(_.set(CanWeSendToThisAddressPage, false))
          .flatMap(
            _.set(
              CharityPostalAddressLookupPage,
              AddressModel(
                Seq("7", "Morrison street", "line3", "line4"),
                Some("G58AN"),
                CountryModel("GB", "United Kingdom")
              )
            )
          )
          .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales))
          .flatMap(
            _.set(
              CharityContactDetailsPage,
              CharityContactDetails("07700 900 982", Some("07700 000 111"), "abc@gmail.com")
            )
              .flatMap(
                _.set(
                  CharityOfficialAddressLookupPage,
                  AddressModel(
                    Seq("7", "Morrison street", "line3", "line4"),
                    Some("G58AN"),
                    CountryModel("GB", "United Kingdom")
                  )
                )
              )
              .flatMap(_.set(CharityNamePage, CharityName("ABC", Some("OpName"))))
          )
          .success
          .value

        val expectedJson =
          s"""{
            |  "charityRegistration": {
            |    "common": {
            |      "bankDetails": {
            |        "accountName": "fullName",
            |        "rollNumber": "operatingName",
            |        "accountNumber": "12345678",
            |        "sortCode": "123456"
            |      },
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "Jim",
            |          "lastName": "Jones",
            |          "middleName": "John",
            |          "title": "0001"
            |        },
            |        "position": "23",
            |        "postcode": "G58AN",
            |        "telephoneNumber": "07700 900 982",
            |        "declaration": true,
            |        "overseas": false
            |      },
            |      "admin": {
            |        "acknowledgmentReference": "15 CHARACTERS S",
            |        "credentialID": "/newauth/credentialId/id",
            |        "sessionID": "$fakeSessionId",
            |        "welshIndicator": false,
            |        "applicationDate": "1970-01-01"
            |      },
            |      "organisation": {
            |        "applicationType": "0",
            |        "emailAddress": "abc@gmail.com",
            |        "countryEstd": "1",
            |        "orgName": "ABC",
            |        "telephoneNumber": "07700 900 982",
            |        "mobileNumber": "07700 000 111",
            |        "operatingName": "OpName"
            |      },
            |      "addressDetails": {
            |        "differentCorrespondence": false,
            |        "officialAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKAddress": false
            |        },
            |        "correspondenceAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToCommon(fakeDataRequest)).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct Common object with mandatory fields only" in {

        val userAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", None, "Jones"))
          .flatMap(
            _.set(
              AuthorisedOfficialAddressLookupPage(0),
              AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))
            )
          )
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.UKAgent))
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981"))))
          .flatMap(_.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", None)))
          .flatMap(
            _.set(
              CharityOfficialAddressLookupPage,
              AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))
            )
          )
          .flatMap(_.set(CanWeSendToThisAddressPage, true))
          .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales))
          .flatMap(
            _.set(
              CharityContactDetailsPage,
              CharityContactDetails("07700 900 982", Some("07700 000 111"), "abc@gmail.com")
            )
          )
          .flatMap(_.set(CharityNamePage, CharityName("ABC", None)))
          .success
          .value

        val expectedJson =
          s"""{
            |  "charityRegistration": {
            |    "common": {
            |      "bankDetails": {
            |        "accountName": "fullName",
            |        "accountNumber": "12345678",
            |        "sortCode": "123456"
            |      },
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "Jim",
            |          "lastName": "Jones",
            |          "title": "0001"
            |        },
            |        "position": "23",
            |        "telephoneNumber": "07700 900 982",
            |        "declaration": true,
            |        "overseas": true
            |      },
            |      "admin": {
            |        "acknowledgmentReference": "15 CHARACTERS S",
            |        "credentialID": "/newauth/credentialId/id",
            |        "sessionID": "$fakeSessionId",
            |        "welshIndicator": false,
            |        "applicationDate": "1970-01-01"
            |      },
            |      "organisation": {
            |        "applicationType": "0",
            |        "emailAddress": "abc@gmail.com",
            |        "countryEstd": "1",
            |        "orgName": "ABC",
            |        "telephoneNumber": "07700 900 982",
            |        "mobileNumber": "07700 000 111"
            |      },
            |      "addressDetails": {
            |        "differentCorrespondence": false,
            |        "officialAddress": {
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "nonUKCountry": "IN",
            |          "nonUKAddress": true
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToCommon(fakeDataRequest)).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }
  }

}
