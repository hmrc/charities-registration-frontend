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

package transformers.submission

import base.SpecBase
import models.addressLookup.{AddressModel, CountryModel}
import models.authOfficials.OfficialsPosition
import models.operations.CharitablePurposes.{AmateurSport, AnimalWelfare}
import models.operations.{CharitablePurposes, CharityEstablishedOptions, FundRaisingOptions, OperatingLocationOptions}
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.regulators.SelectWhyNoRegulator
import models.{BankDetails, CharityContactDetails, CharityName, MongoDateTimeFormats, Name, PhoneNumber, SelectTitle, UserAnswers}
import org.joda.time.{MonthDay, LocalDate => JLocalDate}
import pages.addressLookup._
import pages.authorisedOfficials._
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import pages.operationsAndFunds._
import pages.otherOfficials._
import pages.regulatorsAndDocuments._
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate
import scala.util.Try

trait CharityTransformerConstants extends SpecBase{
  //scalastyle:off magic.number

  private val date = LocalDate.now()

  val day: Int = 11
  val month: Int = 12
  val year: Int = 2000

  lazy val baseAnswers: Try[UserAnswers] = emptyUserAnswers.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", None))
    .flatMap(_.set(CharityOfficialAddressLookupPage,
      AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))))
    .flatMap(_.set(CanWeSendToThisAddressPage, true))
    .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", Some("07700 000 111"), "abc@gmail.com")))
    .flatMap(_.set(CharityNamePage, CharityName("ABC", None)))
    .flatMap(_.set(IsCharityRegulatorPage, false))
    .flatMap(_.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")))
    .flatMap(
      _.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
    .flatMap(
      _.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day)))
    .flatMap(
      _.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981"))))
    .flatMap(
      _.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C"))
    .flatMap(
      _.set(AuthorisedOfficialAddressLookupPage(0),
        AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))))
    .flatMap(
      _.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien"))).flatMap(
    _.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
    _.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
    _.set(OtherOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
    _.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C")).flatMap(
    _.set(OtherOfficialAddressLookupPage(0),
      AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))))
    .flatMap(
      _.set(PublicBenefitsPage,
        "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"))
    .flatMap(
      _.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
    .flatMap(
      _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))

  val localUserAnswers: UserAnswers = baseAnswers
    .flatMap(
      _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)).flatMap(
    _.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation)).flatMap(
    _.set(GoverningDocumentNamePage, "Other Documents for Charity")).flatMap(
    _.set(IsApprovedGoverningDocumentPage, false)).flatMap(
    _.set(HasCharityChangedPartsOfGoverningDocumentPage, false)).flatMap(
    _.set(AccountingPeriodEndDatePage,
      MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
      _.set(IsFinancialAccountsPage, true)).flatMap(
      _.set(EstimatedIncomePage, BigDecimal(123))).flatMap(
      _.set(ActualIncomePage, BigDecimal(121))).flatMap(
      _.set(FundRaisingPage, FundRaisingOptions.values.toSet)).flatMap(
      _.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales)).flatMap(
      _.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England))).flatMap(
      _.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare))).flatMap(
      _.set(CharitableObjectivesPage,
        "qwet\tqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre\r\n66"
      ))
  )
    .flatMap(
      _.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr,"David", None, "Beckham"))).flatMap(
    _.set(OtherOfficialsPositionPage(0), OfficialsPosition.Director)).flatMap(
    _.set(OtherOfficialAddressLookupPage(0),
      AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, CountryModel("IT", "Italy")))).success.value

  lazy val jsonAllFields: JsValue = Json.parse(
    s"""{
       |  "charityRegistration": {
       |    "charity": {
       |      "charityOrganisation": {
       |        "registeredRegulator": true,
       |        "regulator": {
       |          "ccew": true,
       |          "ccewRegistrationNumber": "123456",
       |          "oscr": true,
       |          "oscrRegistrationNumber": "SC123456",
       |          "ccni": true,
       |          "ccniRegistrationNumber": "ABCDEFGHIJ1234567890",
       |          "otherRegulator": true,
       |          "otherRegulatorName": "Other Regulator Name",
       |          "otherRegulatorRegistrationNumber": "12345678901234567890"
       |        }
       |      },
       |      "aboutOrganisation": {
       |        "aboutOrgCommon": {
       |          "otherDocument": "Other Documents for Charity",
       |          "effectiveDate": "2014-07-01"
       |        },
       |        "documentEnclosed": "2",
       |        "governingApprovedDoc": true,
       |        "governingApprovedWords": true,
       |        "governingApprovedChanges": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
       |        "governingApprovedChangesB": "11223344556677889900"
       |      },
       |      "operationAndFunds": {
       |        "operationAndFundsCommon": {
       |          "accountPeriodEnd": "0101",
       |          "financialAccounts": true,
       |          "noBankStatements": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
       |          "noBankStatementsB": "11223344556677889900"
       |        },
       |        "estimatedGrossIncome": 2000.00,
       |        "incomeReceivedToDate": 19999.99,
       |        "futureFunds": "other, donations, tradingSubsidiaries, tradingIncome, fundraising, grants, membershipSubscriptions, investmentIncome",
       |        "otherAreaOperation": true,
       |        "englandAndWales": true,
       |        "scotland": true,
       |        "northernIreland": true,
       |        "ukWide": true,
       |        "overseas": true,
       |        "otherCountriesOfOperation": {
       |          "overseas1": "Country 1",
       |          "overseas2": "Country 2",
       |          "overseas3": "Country 3",
       |          "overseas4": "Country 4",
       |          "overseas5": "Country 5"
       |        }
       |      },
       |      "orgPurpose": {
       |        "charitableObjectives": {
       |          "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
       |          "objectivesB": "11223344556677889900"
       |        },
       |        "charitablePurposes": {
       |          "reliefOfPoverty": true,
       |          "education": true,
       |          "religion": true,
       |          "healthOrSavingOfLives": true,
       |          "citizenshipOrCommunityDevelopment": true,
       |          "artsCultureOrScience": true,
       |          "amateurSport": true,
       |          "humanRights": true,
       |          "environmentalProtection": true,
       |          "reliefOfYouthAge": true,
       |          "animalWelfare": true,
       |          "armedForcesOfTheCrown": true,
       |          "other": true
       |        },
       |        "publicBenefit": {
       |          "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
       |          "publicBenefitB": "11223344556677889900"
       |        }
       |      }
       |    },
       |    "common": {
       |      "bankDetails": {
       |        "accountName": "fullName",
       |        "rollNumber": "operatingName",
       |        "accountNumber": 12345678,
       |        "sortCode": 123456
       |      },
       |      "declarationInfo": {
       |        "name": {
       |          "firstName": "Albert",
       |          "lastName": "Einstien",
       |          "middleName": "G",
       |          "title": "0001"
       |        },
       |        "position": "02",
       |        "postcode": "G58AN",
       |        "telephoneNumber": "07700 900 982",
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
       |          "nonUKAddress": false
       |        },
       |        "correspondenceAddress": {
       |          "postcode": "ZZ11ZZ",
       |          "addressLine1": "1",
       |          "addressLine2": "Morrison street",
       |          "nonUKAddress": false
       |        }
       |      }
       |    },
       |    "partner": [
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "2"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "Albert",
       |            "middleName": "G",
       |            "lastName": "Einstien"
       |          },
       |          "position": "02",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456C"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "2",
       |            "addressLine2": "Dubai Main Road",
       |            "addressLine3": "line3",
       |            "addressLine4": "line4",
       |            "postcode": "G27JD"
       |          }
       |        }
       |      },
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "2"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "David",
       |            "lastName": "Beckham"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456A"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "3",
       |            "addressLine2": "Morrison Street",
       |            "addressLine3": "Bill Tower",
       |            "nonUKCountry": "IT"
       |          }
       |        }
       |      },
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "1"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "Albert",
       |            "middleName": "G",
       |            "lastName": "Einstien"
       |          },
       |          "position": "02",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456C"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "2",
       |            "addressLine2": "Dubai Main Road",
       |            "addressLine3": "line3",
       |            "addressLine4": "line4",
       |            "postcode": "G27JD"
       |          }
       |        }
       |      },
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "1"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "David",
       |            "lastName": "Beckham"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456A"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "3",
       |            "addressLine2": "Morrison Street",
       |            "addressLine3": "Bill Tower",
       |            "nonUKCountry": "IT"
       |          }
       |        }
       |      }
       |    ]
       |  }
       |}""".stripMargin)

  lazy val jsonMinFields: JsValue = Json.parse(
    s"""{
       |  "charityRegistration": {
       |    "common": {
       |      "bankDetails": {
       |        "accountName": "fullName",
       |        "accountNumber": 12345678,
       |        "sortCode": 123456
       |      },
       |      "declarationInfo": {
       |        "name": {
       |          "firstName": "Albert",
       |          "middleName": "G",
       |          "lastName": "Einstien",
       |          "title": "0001"
       |        },
       |        "position": "02",
       |        "telephoneNumber": "07700 900 982",
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
       |    },
       |    "charity": {
       |      "charityOrganisation": {
       |        "registeredRegulator": false,
       |        "nonRegReason": "1"
       |      },
       |      "aboutOrganisation": {
       |        "aboutOrgCommon": {
       |          "otherDocument": "Other Documents for Charity",
       |          "effectiveDate": "2014-07-01"
       |        },
       |        "documentEnclosed": "2",
       |        "governingApprovedDoc": false,
       |        "governingApprovedWords": true
       |      },
       |      "operationAndFunds": {
       |        "operationAndFundsCommon": {
       |          "accountPeriodEnd": "0101",
       |          "financialAccounts": true
       |        },
       |        "estimatedGrossIncome": 123.00,
       |        "incomeReceivedToDate": 121.00,
       |        "futureFunds": "other, donations, tradingSubsidiaries, tradingIncome, fundraising, grants, membershipSubscriptions, investmentIncome",
       |        "otherAreaOperation": true,
       |        "englandAndWales": true,
       |        "scotland": false,
       |        "northernIreland": false,
       |        "ukWide": false,
       |        "overseas": false
       |      },
       |      "orgPurpose": {
       |        "charitableObjectives": {
       |          "objectivesA": "qwet qwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre 66"
       |        },
       |        "charitablePurposes": {
       |          "reliefOfPoverty": false,
       |          "education": false,
       |          "religion": false,
       |          "healthOrSavingOfLives": false,
       |          "citizenshipOrCommunityDevelopment": false,
       |          "artsCultureOrScience": false,
       |          "amateurSport": true,
       |          "humanRights": false,
       |          "environmentalProtection": false,
       |          "reliefOfYouthAge": false,
       |          "animalWelfare": true,
       |          "armedForcesOfTheCrown": false,
       |          "other": false
       |        },
       |        "publicBenefit": {
       |          "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
       |        }
       |      }
       |    },
       |    "partner": [
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "2"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "Albert",
       |            "middleName": "G",
       |            "lastName": "Einstien"
       |          },
       |          "position": "02",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456C"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "2",
       |            "addressLine2": "Dubai Main Road",
       |            "addressLine3": "line3",
       |            "addressLine4": "line4",
       |            "postcode": "G27JD"
       |          }
       |        }
       |      },
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "1"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "David",
       |            "lastName": "Beckham"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456C"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "3",
       |            "addressLine2": "Morrison Street",
       |            "addressLine3": "Bill Tower",
       |            "nonUKCountry": "IT"
       |          }
       |        }
       |      }
       |    ]
       |  }
       |}
       |""".stripMargin)

  lazy val jsonGeneral: JsValue = Json.parse(
    s"""{
       |  "charityRegistration": {
       |    "common": {
       |      "bankDetails": {
       |        "accountName": "fullName",
       |        "accountNumber": 12345678,
       |        "sortCode": 123456
       |      },
       |      "declarationInfo": {
       |        "name": {
       |          "firstName": "Albert",
       |          "middleName": "G",
       |          "lastName": "Einstien",
       |          "title": "0001"
       |        },
       |        "position": "23",
       |        "telephoneNumber": "07700 900 982",
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
       |    },
       |    "charity": {
       |      "charityOrganisation": {
       |        "registeredRegulator": false,
       |        "nonRegReason": "1"
       |      },
       |      "aboutOrganisation": {
       |        "aboutOrgCommon": {
       |          "otherDocument": "Other Documents for Charity",
       |          "effectiveDate": "2014-07-01"
       |        },
       |        "documentEnclosed": "2",
       |        "governingApprovedDoc": false,
       |        "governingApprovedWords": true
       |      },
       |      "operationAndFunds": {
       |        "operationAndFundsCommon": {
       |          "accountPeriodEnd": "0101",
       |          "financialAccounts": true
       |        },
       |        "estimatedGrossIncome": 123.00,
       |        "incomeReceivedToDate": 121.00,
       |        "futureFunds": "other, donations, tradingSubsidiaries, tradingIncome, fundraising, grants, membershipSubscriptions, investmentIncome",
       |        "otherAreaOperation": true,
       |        "englandAndWales": true,
       |        "scotland": false,
       |        "northernIreland": false,
       |        "ukWide": false,
       |        "overseas": false
       |      },
       |      "orgPurpose": {
       |        "charitableObjectives": {
       |          "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
       |        },
       |        "charitablePurposes": {
       |          "reliefOfPoverty": false,
       |          "education": false,
       |          "religion": false,
       |          "healthOrSavingOfLives": false,
       |          "citizenshipOrCommunityDevelopment": false,
       |          "artsCultureOrScience": false,
       |          "amateurSport": true,
       |          "humanRights": false,
       |          "environmentalProtection": false,
       |          "reliefOfYouthAge": false,
       |          "animalWelfare": true,
       |          "armedForcesOfTheCrown": false,
       |          "other": false
       |        },
       |        "publicBenefit": {
       |          "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
       |        }
       |      }
       |    },
       |    "partner": [
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "2"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "Albert",
       |            "middleName": "G",
       |            "lastName": "Einstien"
       |          },
       |          "position": "23",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456C"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "2",
       |            "addressLine2": "Dubai Main Road",
       |            "addressLine3": "line3",
       |            "addressLine4": "line4",
       |            "postcode": "G27JD"
       |          }
       |        }
       |      },
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "2"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "David",
       |            "lastName": "Beckham"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456A"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "3",
       |            "addressLine2": "Morrison Street",
       |            "addressLine3": "Bill Tower",
       |            "nonUKCountry": "IT"
       |          }
       |        }
       |      },
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "1"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "Albert",
       |            "middleName": "G",
       |            "lastName": "Einstien"
       |          },
       |          "position": "02",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456C"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "2",
       |            "addressLine2": "Dubai Main Road",
       |            "addressLine3": "line3",
       |            "addressLine4": "line4",
       |            "postcode": "G27JD"
       |          }
       |        }
       |      },
       |      {
       |        "responsiblePerson": {
       |          "action": "1",
       |          "relation": "1"
       |        },
       |        "type": "1",
       |        "addPartner": {
       |          "effectiveDateOfChange": "$date"
       |        },
       |        "individualDetails": {
       |          "name": {
       |            "title": "0001",
       |            "firstName": "David",
       |            "lastName": "Beckham"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "07700 900 982",
       |          "mobilePhone": "07700 900 981",
       |          "nino": "QQ123456A"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "3",
       |            "addressLine2": "Morrison Street",
       |            "addressLine3": "Bill Tower",
       |            "nonUKCountry": "IT"
       |          }
       |        }
       |      }
       |    ]
       |  }
       |}""".stripMargin
  )
}

