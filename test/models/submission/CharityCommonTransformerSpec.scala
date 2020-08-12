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

package models.submission

import base.SpecBase
import models.AuthOfficials.OfficialsPosition
import models.{BankDetails, CharityContactDetails, CharityName, Name}
import models.addressLookup.{AddressModel, CountryModel}
import pages.QuestionPage
import pages.addressLookup.{AuthorisedOfficialAddressLookupPage, CharityOfficialAddressLookupPage}
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, AuthorisedOfficialsPositionPage}
import pages.charityInformation.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import pages.operationsAndFunds.BankDetailsPage
import play.api.libs.json.{JsPath, Json, __}

class CharityCommonTransformerSpec extends SpecBase {

  val jsonTransformer: CharityCommonTransformer = new CharityCommonTransformer

  //TODO remove this once CharityCorrespondenceAddressLookupPage id created
  case object CharityCorrespondenceAddressLookupPage extends QuestionPage[AddressModel] {
    override def path: JsPath = JsPath \ toString
    override def toString: String = "charityCorrespondenceAddress"
  }

  "CharityCommonTransformer" when {

    "userAnswersToAdmin" must {

      "convert the correct Admin object" in {

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "admin": {
            |        "acknowledgmentReference": "15 CHARACTERS S",
            |        "credentialID": "/newauth/credentialId/id",
            |        "sessionID": "50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA",
            |        "welshIndicator": false,
            |        "applicationDate": "1970-01-01"
            |      }
            |    }
            |  }
            |}""".stripMargin

        emptyUserAnswers.data.transform(jsonTransformer.userAnswersToAdmin(fakeDataRequest)).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToOrganisation" must {

      "convert the correct organisation" in {

        val userAnswers = emptyUserAnswers.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", Some("07700 000 111"),"abc@gmail.com"))
          .flatMap(_.set(CharityNamePage, CharityName("ABC", Some("OpName")))).success.value

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
            |        "mobileNumber": "07700 000 111",
            |        "operatingName": "OpName"
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct organisation with mandatory fields only" in {
        val userAnswers = emptyUserAnswers.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982",None,"abc@gmail.com"))
          .flatMap(_.set(CharityNamePage, CharityName("ABC", None))).success.value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "organisation": {
            |        "applicationType": "0",
            |        "emailAddress": "abc@gmail.com",
            |        "countryEstd": "1",
            |        "orgName": "ABC",
            |        "telephoneNumber": "07700 900 982"
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToAddressDetailsCommon" must {

      "convert the correct AddressDetailsCommon without charityCorrespondenceAddress" in {

        val userAnswers = emptyUserAnswers.set(CharityOfficialAddressLookupPage,
          AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("UK", "United Kingdom")))
          .flatMap(_.set(CanWeSendToThisAddressPage, true)).success.value

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
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToAddressDetailsCommon).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct AddressDetailsCommon with charityCorrespondenceAddress" in {

        val userAnswers = emptyUserAnswers.set(CharityOfficialAddressLookupPage,
          AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("UK", "United Kingdom")))
          .flatMap(_.set(CanWeSendToThisAddressPage, false))
          .flatMap(_.set(CharityCorrespondenceAddressLookupPage,
          AddressModel(Seq("1", "Morrison street"), Some("ZZ11ZZ"), CountryModel("UK", "United Kingdom")))).success.value

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
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        },
            |        "correspondenceAddress": {
            |          "postcode": "ZZ11ZZ",
            |          "addressLine1": "1",
            |          "addressLine2": "Morrison street",
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToAddressDetailsCommon).asOpt.value mustBe Json.parse(expectedJson)

      }

      "convert the correct AddressDetailsCommon with charityCorrespondenceAddress and both addresses are same" in {

        val userAnswers = emptyUserAnswers.set(CharityOfficialAddressLookupPage,
          AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("UK", "United Kingdom")))
          .flatMap(_.set(CanWeSendToThisAddressPage, false))
          .flatMap(_.set(CharityCorrespondenceAddressLookupPage,
            AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("UK", "United Kingdom")))).success.value

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
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        },
            |        "correspondenceAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToAddressDetailsCommon).asOpt.value mustBe Json.parse(expectedJson)

      }
    }

    "userAnswersToBankDetails" must {

      "convert the correct BankDetails" in {

        val userAnswers = emptyUserAnswers.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName"))).success.value

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

        val userAnswers = emptyUserAnswers.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", None)).success.value

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

        val userAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name("Jim", Some("John"), "Jones"))
          .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0),
            AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))))
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0),  OfficialsPosition.UKAgent)).success.value

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
            |        "declaration": true,
            |        "overseas": false
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToIndDeclarationInfo).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct IndDeclarationInfo with non UK data" in {

        val userAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name("Jim", Some("John"), "Jones"))
          .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0),
            AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))))
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0),  OfficialsPosition.UKAgent)).success.value

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
            |        "declaration": true,
            |        "overseas": true
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToIndDeclarationInfo).asOpt.value mustBe Json.parse(expectedJson)
      }

    }

    "userAnswersToCommon" must {

      "convert the correct Common object with all data" in {

        val userAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name("Jim", Some("John"), "Jones"))
          .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0),
            AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))))
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0),  OfficialsPosition.UKAgent))
          .flatMap(_.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName"))))
          .flatMap(_.set(CharityOfficialAddressLookupPage,
          AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("UK", "United Kingdom")))
          .flatMap(_.set(CanWeSendToThisAddressPage, false)))
          .flatMap(_.set(CharityCorrespondenceAddressLookupPage,
            AddressModel(Seq("1", "Morrison street"), Some("ZZ11ZZ"), CountryModel("UK", "United Kingdom"))))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", Some("07700 000 111"),"abc@gmail.com"))
            .flatMap(_.set(CharityNamePage, CharityName("ABC", Some("OpName"))))).success.value

        val expectedJson =
          """{
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
            |        "declaration": true,
            |        "overseas": false
            |      },
            |      "admin": {
            |        "acknowledgmentReference": "15 CHARACTERS S",
            |        "credentialID": "/newauth/credentialId/id",
            |        "sessionID": "50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA",
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
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        },
            |        "correspondenceAddress": {
            |          "postcode": "ZZ11ZZ",
            |          "addressLine1": "1",
            |          "addressLine2": "Morrison street",
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToCommon(fakeDataRequest)).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct Common object when officialAddress and correspondenceAddress" in {

        val userAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name("Jim", Some("John"), "Jones"))
          .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0),
            AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))))
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0),  OfficialsPosition.UKAgent))
          .flatMap(_.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName"))))
          .flatMap(_.set(CharityOfficialAddressLookupPage,
            AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("UK", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, false))
          .flatMap(_.set(CharityCorrespondenceAddressLookupPage,
            AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("UK", "United Kingdom"))))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", Some("07700 000 111"),"abc@gmail.com"))
            .flatMap(_.set(CharityNamePage, CharityName("ABC", Some("OpName"))))).success.value

        val expectedJson =
          """{
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
            |        "declaration": true,
            |        "overseas": false
            |      },
            |      "admin": {
            |        "acknowledgmentReference": "15 CHARACTERS S",
            |        "credentialID": "/newauth/credentialId/id",
            |        "sessionID": "50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA",
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
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        },
            |        "correspondenceAddress": {
            |          "postcode": "G58AN",
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "addressLine3": "line3",
            |          "addressLine4": "line4",
            |          "nonUKCountry": "United Kingdom",
            |          "nonUKAddress": false
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToCommon(fakeDataRequest)).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct Common object with mandatory fields only" in {

        val userAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name("Jim", None, "Jones"))
          .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0),
            AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))))
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0),  OfficialsPosition.UKAgent))
          .flatMap(_.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", None)))
          .flatMap(_.set(CharityOfficialAddressLookupPage,
            AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))))
            .flatMap(_.set(CanWeSendToThisAddressPage, true))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", None, "abc@gmail.com")))
            .flatMap(_.set(CharityNamePage, CharityName("ABC", None))).success.value

        val expectedJson =
          """{
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
            |        "declaration": true,
            |        "overseas": true
            |      },
            |      "admin": {
            |        "acknowledgmentReference": "15 CHARACTERS S",
            |        "credentialID": "/newauth/credentialId/id",
            |        "sessionID": "50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA",
            |        "welshIndicator": false,
            |        "applicationDate": "1970-01-01"
            |      },
            |      "organisation": {
            |        "applicationType": "0",
            |        "emailAddress": "abc@gmail.com",
            |        "countryEstd": "1",
            |        "orgName": "ABC",
            |        "telephoneNumber": "07700 900 982"
            |      },
            |      "addressDetails": {
            |        "differentCorrespondence": false,
            |        "officialAddress": {
            |          "addressLine1": "7",
            |          "addressLine2": "Morrison street",
            |          "nonUKCountry": "India",
            |          "nonUKAddress": true
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.userAnswersToCommon(fakeDataRequest)).asOpt.value mustBe Json.parse(expectedJson)
      }
    }
  }

}
