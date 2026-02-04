/*
 * Copyright 2026 HM Revenue & Customs
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
import models.addressLookup.AddressModel
import models.authOfficials.OfficialsPosition
import models.operations.CharitablePurposes.{AmateurSport, AnimalWelfare}
import models.operations.{CharitablePurposes, CharityEstablishedOptions, FundRaisingOptions, OperatingLocationOptions}
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.regulators.SelectWhyNoRegulator
import models.{BankDetails, CharityContactDetails, CharityName, MongoDateTimeFormats, Name, PhoneNumber, UserAnswers}
import pages.addressLookup.*
import pages.authorisedOfficials.*
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import pages.operationsAndFunds.*
import pages.otherOfficials.*
import pages.regulatorsAndDocuments.*

import java.time.{LocalDate, MonthDay}
import scala.collection.immutable.SortedSet
import scala.util.Try

trait CharityTransformerConstants extends SpecBase {

  private val date = LocalDate.now()

  lazy val baseAnswers: Try[UserAnswers] = emptyUserAnswers
    .set(BankDetailsPage, bankDetailsWithoutRollNumber)
    .flatMap(
      _.set(
        CharityOfficialAddressLookupPage,
        address.copy(postcode = None, country = inCountryModel)
      )
    )
    .flatMap(_.set(CanWeSendToThisAddressPage, true))
    .flatMap(
      _.set(CharityContactDetailsPage, charityContactDetails)
    )
    .flatMap(_.set(CharityNamePage, charityNameNoOperatingName))
    .flatMap(_.set(IsCharityRegulatorPage, false))
    .flatMap(_.set(AuthorisedOfficialsNamePage(0), personNameWithMiddle))
    .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
    .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.parse("2000-12-11")))
    .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
    .flatMap(_.set(AuthorisedOfficialsNinoPage(0), nino))
    .flatMap(
      _.set(
        AuthorisedOfficialAddressLookupPage(0),
        addressAllLines
      )
    )
    .flatMap(_.set(OtherOfficialsNamePage(0), personName2WithMiddle))
    .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar))
    .flatMap(_.set(OtherOfficialsDOBPage(0), LocalDate.parse("2000-12-11")))
    .flatMap(_.set(OtherOfficialsPhoneNumberPage(0), phoneNumbers))
    .flatMap(_.set(OtherOfficialsNinoPage(0), nino))
    .flatMap(
      _.set(
        OtherOfficialAddressLookupPage(0),
        addressAllLines
      )
    )
    .flatMap(
      _.set(
        PublicBenefitsPage,
        "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
      )
    )
    .flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
    .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01")))

  val localUserAnswers: UserAnswers = baseAnswers
    .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold))
    .flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
    .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
    .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
    .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
    .flatMap(
      _.set(
        AccountingPeriodEndDatePage,
        MonthDay.from(LocalDate.parse("2020-01-01"))
      )(MongoDateTimeFormats.localDayMonthWrite)
        .flatMap(_.set(IsFinancialAccountsPage, true))
        .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
        .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
        .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
        .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales))
        .flatMap(_.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England)))
        .flatMap(_.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare)))
        .flatMap(
          _.set(
            CharitableObjectivesPage,
            "qwet\tqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre\r\n66"
          )
        )
    )
    .flatMap(_.set(OtherOfficialsNamePage(0), personNameWithoutMiddle))
    .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Director))
    .flatMap(
      _.set(
        OtherOfficialAddressLookupPage(0),
        addressWithTown.copy(postcode = None, country = itCountryModel)
      )
    )
    .success
    .value

  lazy val jsonAllFields: String =
    s"""{
       |  "charityRegistration": {
       |    "charity": {
       |      "charityOrganisation": {
       |        "registeredRegulator": true,
       |        "regulator": {
       |          "ccew": true,
       |          "ccewRegistrationNumber": "$charityCommissionRegistrationNumber",
       |          "oscr": true,
       |          "oscrRegistrationNumber": "$scottishRegulatorRegistrationNumber",
       |          "ccni": true,
       |          "ccniRegistrationNumber": "$niRegulatorRegistrationNumber",
       |          "otherRegulator": true,
       |          "otherRegulatorName": "$charityRegulatorName",
       |          "otherRegulatorRegistrationNumber": "$chartyRegulatorRegistrationNumber"
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
       |        "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
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
       |        "accountName": "$accountName",
       |        "rollNumber": "$rollNumber",
       |        "accountNumber": "$accountNumber",
       |        "sortCode": "$sortCode"
       |      },
       |      "declarationInfo": {
       |        "name": {
       |          "title": "0001",
       |          "firstName": "${personNameWithMiddle.firstName}",
       |          "lastName": "${personNameWithMiddle.lastName}",
       |          "middleName": "${personNameWithMiddle.middleName.get}"
       |
       |        },
       |        "position": "02",
       |        "postcode": "$ukPostcode",
       |        "telephoneNumber": "$daytimePhone",
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
       |        "emailAddress": "$charityEmail",
       |        "countryEstd": "1",
       |        "orgName": "$charityFullName",
       |        "telephoneNumber": "$daytimePhone",
       |        "mobileNumber": "$mobileNumber",
       |        "operatingName": "$charityOperatingName"
       |      },
       |      "addressDetails": {
       |        "differentCorrespondence": true,
       |        "officialAddress": {
       |          "postcode": "$ukPostcode",
       |          "addressLine1": "$line1",
       |          "addressLine2": "$line2",
       |          "addressLine3": "$line3",
       |          "addressLine4": "${town.get}",
       |          "nonUKAddress": false
       |        },
       |        "correspondenceAddress": {
       |          "postcode": "$ukPostcode",
       |          "addressLine1": "$line1",
       |          "addressLine2": "$line2",
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
       |            "firstName": "${personNameWithMiddle.firstName}",
       |            "middleName": "${personNameWithMiddle.middleName.get}",
       |            "lastName": "${personNameWithMiddle.lastName}"
       |          },
       |          "position": "02",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "$line3",
       |            "addressLine4": "${town.get}",
       |            "postcode": "$ukPostcode"
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
       |            "firstName": "${personNameWithoutMiddle.firstName}",
       |            "lastName": "${personNameWithoutMiddle.lastName}"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino2"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "${town.get}",
       |            "nonUKCountry": "$itCountryCode"
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
       |            "title": "0004",
       |            "firstName": "${personName2WithMiddle.firstName}",
       |            "middleName": "${personName2WithMiddle.middleName.get}",
       |            "lastName": "${personName2WithMiddle.lastName}"
       |          },
       |          "position": "02",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "$line3",
       |            "addressLine4": "${town.get}",
       |            "postcode": "$ukPostcode"
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
       |            "firstName": "${personNameWithoutMiddle.firstName}",
       |            "lastName": "${personNameWithoutMiddle.lastName}"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino2"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "${town.get}",
       |            "nonUKCountry": "$itCountryCode"
       |          }
       |        }
       |      }
       |    ]
       |  }
       |}""".stripMargin

  lazy val jsonMinFields: String =
    s"""{
       |  "charityRegistration": {
       |    "common": {
       |      "bankDetails": {
       |        "accountName": "$accountName",
       |        "accountNumber": "$accountNumber",
       |        "sortCode": "$sortCode"
       |      },
       |      "declarationInfo": {
       |        "name": {
       |          "title": "0001",
       |          "firstName": "${personNameWithMiddle.firstName}",
       |          "middleName": "${personNameWithMiddle.middleName.get}",
       |          "lastName": "${personNameWithMiddle.lastName}"
       |        },
       |        "position": "02",
       |        "telephoneNumber": "$daytimePhone",
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
       |        "emailAddress": "$charityEmail",
       |        "countryEstd": "1",
       |        "orgName": "$charityFullName",
       |        "telephoneNumber": "$daytimePhone",
       |        "mobileNumber": "$mobileNumber"
       |      },
       |      "addressDetails": {
       |        "differentCorrespondence": false,
       |        "officialAddress": {
       |          "addressLine1": "$line1",
       |          "addressLine2": "$line2",
       |          "nonUKCountry": "$inCountryCode",
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
       |        "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
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
       |            "firstName": "${personNameWithMiddle.firstName}",
       |            "middleName": "${personNameWithMiddle.middleName.get}",
       |            "lastName": "${personNameWithMiddle.lastName}"
       |          },
       |          "position": "02",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "$line3",
       |            "addressLine4": "${town.get}",
       |            "postcode": "$ukPostcode"
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
       |            "firstName": "${personNameWithoutMiddle.firstName}",
       |            "lastName": "${personNameWithoutMiddle.lastName}"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "${town.get}",
       |            "nonUKCountry": "$itCountryCode"
       |          }
       |        }
       |      }
       |    ]
       |  }
       |}
       |""".stripMargin

  lazy val jsonGeneral: String =
    s"""{
       |  "charityRegistration": {
       |    "common": {
       |      "bankDetails": {
       |        "accountName": "$accountName",
       |        "accountNumber": "$accountNumber",
       |        "sortCode": "$sortCode"
       |      },
       |      "declarationInfo": {
       |        "name": {
       |        "title": "0001",
       |          "firstName": "${personNameWithMiddle.firstName}",
       |          "middleName": "${personNameWithMiddle.middleName.get}",
       |          "lastName": "${personNameWithMiddle.lastName}"
       |        },
       |        "position": "23",
       |        "telephoneNumber": "$daytimePhone",
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
       |        "emailAddress": "$charityEmail",
       |        "countryEstd": "1",
       |        "orgName": "$charityFullName",
       |        "telephoneNumber": "$daytimePhone",
       |        "mobileNumber": "$mobileNumber"
       |      },
       |      "addressDetails": {
       |        "differentCorrespondence": false,
       |        "officialAddress": {
       |          "addressLine1": "$line1",
       |          "addressLine2": "$line2",
       |          "nonUKCountry": "$inCountryCode",
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
       |        "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
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
       |            "firstName": "${personNameWithMiddle.firstName}",
       |            "middleName": "${personNameWithMiddle.middleName.get}",
       |            "lastName": "${personNameWithMiddle.lastName}"
       |          },
       |          "position": "23",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "$line3",
       |            "addressLine4": "${town.get}",
       |            "postcode": "$ukPostcode"
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
       |            "firstName": "${personNameWithoutMiddle.firstName}",
       |            "lastName": "${personNameWithoutMiddle.lastName}"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino2"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "${town.get}",
       |            "nonUKCountry": "$itCountryCode"
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
       |            "title": "0004",
       |            "firstName": "${personName2WithMiddle.firstName}",
       |            "middleName": "${personName2WithMiddle.middleName.get}",
       |            "lastName": "${personName2WithMiddle.lastName}"
       |          },
       |          "position": "02",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": false,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "$line3",
       |            "addressLine4": "${town.get}",
       |            "postcode": "$ukPostcode"
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
       |            "firstName": "${personNameWithoutMiddle.firstName}",
       |            "lastName": "${personNameWithoutMiddle.lastName}"
       |          },
       |          "position": "05",
       |          "dateOfBirth": "2000-12-11",
       |          "dayPhoneNumber": "$daytimePhone",
       |          "mobilePhone": "$mobileNumber",
       |          "nino": "$nino2"
       |        },
       |        "addressDetails": {
       |          "currentAddress": {
       |            "nonUKAddress": true,
       |            "addressLine1": "$line1",
       |            "addressLine2": "$line2",
       |            "addressLine3": "${town.get}",
       |            "nonUKCountry": "$itCountryCode"
       |          }
       |        }
       |      }
       |    ]
       |  }
       |}""".stripMargin
}
