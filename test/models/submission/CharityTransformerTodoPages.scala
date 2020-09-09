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

import java.time.LocalDate

import pages.QuestionPage
import play.api.libs.json.{JsPath, JsValue, Json}

trait CharityTransformerTodoPages {
  private val date = LocalDate.now()

  // TODO when page created
  case object GoverningDocumentChangePage extends QuestionPage[String] {

    override def path: JsPath = JsPath \ toString

    override def toString: String = "governingDocumentChanges"
  }

  // TODO when page created
  case object HasFinancialAccountsPage extends QuestionPage[Boolean] {

    override def path: JsPath = JsPath \ toString

    override def toString: String = "hasFinancialAccounts"
  }

  // TODO when page created
  case object NoBankStatementPage extends QuestionPage[String] {

    override def path: JsPath = JsPath \ toString

    override def toString: String = "noBankStatement"
  }

  // TODO when page created
  case class OverseasCountryPage(index: Int) extends QuestionPage[String] {

    override def path: JsPath = JsPath \ toString \ index

    override def toString: String = "overseas"
  }

  // TODO when page created
  case object EstimatedIncomePage extends QuestionPage[Double] {

    override def path: JsPath = JsPath \ toString

    override def toString: String = "estimatedIncome"
  }

  // TODO when page created
  case object GrossIncomePage extends QuestionPage[Double] {

    override def path: JsPath = JsPath \ toString

    override def toString: String = "grossIncome"
  }

  lazy val jsonAllFields: JsValue = Json.parse(
    s"""
       |{
       |    "charityRegistration": {
       |        "charity": {
       |            "charityOrganisation": {
       |                "registeredRegulator": true,
       |                "regulator": {
       |                    "ccew": true,
       |                    "ccewRegistrationNumber": "123456",
       |                    "oscr": true,
       |                    "oscrRegistrationNumber": "SC123456",
       |                    "ccni": true,
       |                    "ccniRegistrationNumber": "ABCDEFGHIJ1234567890",
       |                    "otherRegulator": true,
       |                    "otherRegulatorName": "Other Regulator Name",
       |                    "otherRegulatorRegistrationNumber": "12345678901234567890"
       |                }
       |            },
       |            "aboutOrganisation": {
       |                "aboutOrgCommon": {
       |                    "otherDocument": "Other Documents for Charity percent",
       |                    "effectiveDate": "2014-07-01"
       |                },
       |                "documentEnclosed": "1",
       |                "governingApprovedDoc": true,
       |                "governingApprovedWords": false,
       |                "governingApprovedChanges": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
       |                "governingApprovedChangesB": "11223344556677889900"
       |            },
       |            "operationAndFunds": {
       |                "operationAndFundsCommon": {
       |                    "accountPeriodEnd": "0101",
       |                    "financialAccounts": true,
       |                    "noBankStatements": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
       |                    "noBankStatementsB": "11223344556677889900"
       |                },
       |                "estimatedGrossIncome": 2000.00,
       |                "incomeReceivedToDate": 19999.99,
       |                "futureFunds": "other, donations, tradingSubsidiaries, tradingIncome, fundraising, grants, membershipSubscriptions, investmentIncome",
       |                "otherAreaOperation": true,
       |                "englandAndWales": true,
       |                "scotland": true,
       |                "northernIreland": true,
       |                "ukWide": true,
       |                "overseas": true,
       |                "otherCountriesOfOperation": {
       |                    "overseas1": "Country 1",
       |                    "overseas2": "Country 2",
       |                    "overseas3": "Country 3",
       |                    "overseas4": "Country 4",
       |                    "overseas5": "Country 5"
       |                }
       |            },
       |            "orgPurpose": {
       |                "charitableObjectives": {
       |                    "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
       |                    "objectivesB": "11223344556677889900"
       |                },
       |                "charitablePurposes": {
       |                    "reliefOfPoverty": true,
       |                    "education": true,
       |                    "religion": true,
       |                    "healthOrSavingOfLives": true,
       |                    "citizenshipOrCommunityDevelopment": true,
       |                    "artsCultureOrScience": true,
       |                    "amateurSport": true,
       |                    "humanRights": true,
       |                    "environmentalProtection": true,
       |                    "reliefOfYouthAge": true,
       |                    "animalWelfare": true,
       |                    "armedForcesOfTheCrown": true,
       |                    "other": true
       |                },
       |                "publicBenefit": {
       |                    "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
       |                    "publicBenefitB": "11223344556677889900"
       |                }
       |            }
       |        },
       |        "common": {
       |            "bankDetails": {
       |                "accountName": "fullName",
       |                "rollNumber": "operatingName",
       |                "accountNumber": "12345678",
       |                "sortCode": "123456"
       |            },
       |            "declarationInfo": {
       |                "name": {
       |                    "firstName": "Albert",
       |                    "lastName": "Einstien",
       |                    "middleName": "G",
       |                    "title": "0001"
       |                },
       |                "position": "02",
       |                "declaration": true,
       |                "overseas": false
       |            },
       |            "admin": {
       |                "acknowledgmentReference": "15 CHARACTERS S",
       |                "credentialID": "/newauth/credentialId/id",
       |                "sessionID": "50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA",
       |                "welshIndicator": false,
       |                "applicationDate": "1970-01-01"
       |            },
       |            "organisation": {
       |                "applicationType": "0",
       |                "emailAddress": "abc@gmail.com",
       |                "countryEstd": "1",
       |                "orgName": "ABC",
       |                "telephoneNumber": "07700 900 982",
       |                "mobileNumber": "07700 000 111",
       |                "operatingName": "OpName"
       |            },
       |            "addressDetails": {
       |                "differentCorrespondence": true,
       |                "officialAddress": {
       |                    "postcode": "G58AN",
       |                    "addressLine1": "7",
       |                    "addressLine2": "Morrison street",
       |                    "addressLine3": "line3",
       |                    "addressLine4": "line4",
       |                    "nonUKCountry": "United Kingdom",
       |                    "nonUKAddress": false
       |                },
       |                "correspondenceAddress": {
       |                    "postcode": "ZZ11ZZ",
       |                    "addressLine1": "1",
       |                    "addressLine2": "Morrison street",
       |                    "nonUKCountry": "United Kingdom",
       |                    "nonUKAddress": false
       |                }
       |            }
       |        }
       |    },
       |    "partner": [
       |        {
       |            "responsiblePerson": {
       |                "action": "1",
       |                "relation": "2"
       |            },
       |            "type": "1",
       |            "addPartner": {
       |                "effectiveDateOfChange": "$date"
       |            },
       |            "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "Albert",
       |                    "middleName": "G",
       |                    "lastName": "Einstien"
       |                },
       |                "position": "02",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456C"
       |            },
       |            "addressDetails": {
       |                "currentAddress": {
       |                    "nonUKAddress": false,
       |                    "addressLine1": "2",
       |                    "addressLine2": "Dubai Main Road",
       |                    "addressLine3": "line3",
       |                    "addressLine4": "line4",
       |                    "postcode": "G27JD",
       |                    "nonUKCountry": "United Kingdom"
       |                }
       |            }
       |        },
       |        {
       |            "responsiblePerson": {
       |                "action": "1",
       |                "relation": "2"
       |            },
       |            "type": "1",
       |            "addPartner": {
       |                "effectiveDateOfChange": "$date"
       |            },
       |            "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "David",
       |                    "lastName": "Beckham"
       |                },
       |                "position": "05",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456A"
       |            },
       |            "addressDetails": {
       |                "currentAddress": {
       |                    "nonUKAddress": true,
       |                    "addressLine1": "3",
       |                    "addressLine2": "Morrison Street",
       |                    "addressLine3": "Bill Tower",
       |                    "nonUKCountry": "Italy"
       |                }
       |            }
       |        },
       |        {
       |            "responsiblePerson": {
       |                "action": "1",
       |                "relation": "1"
       |            },
       |            "type": "1",
       |            "addPartner": {
       |                "effectiveDateOfChange": "$date"
       |            },
       |            "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "Albert",
       |                    "middleName": "G",
       |                    "lastName": "Einstien"
       |                },
       |                "position": "02",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456C"
       |            },
       |            "addressDetails": {
       |                "currentAddress": {
       |                    "nonUKAddress": false,
       |                    "addressLine1": "2",
       |                    "addressLine2": "Dubai Main Road",
       |                    "addressLine3": "line3",
       |                    "addressLine4": "line4",
       |                    "postcode": "G27JD",
       |                    "nonUKCountry": "United Kingdom"
       |                }
       |            }
       |        },
       |        {
       |            "responsiblePerson": {
       |                "action": "1",
       |                "relation": "1"
       |            },
       |            "type": "1",
       |            "addPartner": {
       |                "effectiveDateOfChange": "$date"
       |            },
       |            "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "David",
       |                    "lastName": "Beckham"
       |                },
       |                "position": "05",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456A"
       |            },
       |            "addressDetails": {
       |                "currentAddress": {
       |                    "nonUKAddress": true,
       |                    "addressLine1": "3",
       |                    "addressLine2": "Morrison Street",
       |                    "addressLine3": "Bill Tower",
       |                    "nonUKCountry": "Italy"
       |                }
       |            }
       |        }
       |    ]
       |}""".stripMargin)

  lazy val jsonMinFields: JsValue = Json.parse(
    s"""
       |{
       |    "charityRegistration": {
       |      "common": {
       |        "bankDetails": {
       |          "accountName": "fullName",
       |          "accountNumber": "12345678",
       |          "sortCode": "123456"
       |        },
       |        "declarationInfo": {
       |          "name": {
       |            "firstName": "Albert",
       |            "middleName": "G",
       |            "lastName": "Einstien",
       |            "title": "0001"
       |          },
       |          "position": "02",
       |          "declaration": true,
       |          "overseas": false
       |        },
       |        "admin": {
       |          "acknowledgmentReference": "15 CHARACTERS S",
       |          "credentialID": "/newauth/credentialId/id",
       |          "sessionID": "50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA",
       |          "welshIndicator": false,
       |          "applicationDate": "1970-01-01"
       |        },
       |        "organisation": {
       |          "applicationType": "0",
       |          "emailAddress": "abc@gmail.com",
       |          "countryEstd": "1",
       |          "orgName": "ABC",
       |          "telephoneNumber": "07700 900 982",
       |          "mobileNumber": "07700 000 111"
       |        },
       |        "addressDetails": {
       |          "differentCorrespondence": false,
       |          "officialAddress": {
       |            "addressLine1": "7",
       |            "addressLine2": "Morrison street",
       |            "nonUKCountry": "India",
       |            "nonUKAddress": true
       |          }
       |        }
       |      },
       |      "charity": {
       |        "charityOrganisation": {
       |          "registeredRegulator": false,
       |          "nonRegReason": "1"
       |        },
       |        "aboutOrganisation": {
       |          "aboutOrgCommon": {
       |            "otherDocument": "Other Documents for Charity",
       |            "effectiveDate": "2014-07-01"
       |          },
       |          "documentEnclosed": "1",
       |          "governingApprovedDoc": false,
       |          "governingApprovedWords": false
       |        },
       |        "operationAndFunds": {
       |          "operationAndFundsCommon": {
       |            "accountPeriodEnd": "0101",
       |            "financialAccounts": true
       |          },
       |          "futureFunds": "other, donations, tradingSubsidiaries, tradingIncome, fundraising, grants, membershipSubscriptions, investmentIncome",
       |          "otherAreaOperation": true,
       |          "englandAndWales": true,
       |          "scotland": false,
       |          "northernIreland": false,
       |          "ukWide": false,
       |          "overseas": false
       |        },
       |        "orgPurpose": {
       |          "charitableObjectives": {
       |            "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
       |          },
       |          "charitablePurposes": {
       |            "reliefOfPoverty": false,
       |            "education": false,
       |            "religion": false,
       |            "healthOrSavingOfLives": false,
       |            "citizenshipOrCommunityDevelopment": false,
       |            "artsCultureOrScience": false,
       |            "amateurSport": true,
       |            "humanRights": false,
       |            "environmentalProtection": false,
       |            "reliefOfYouthAge": false,
       |            "animalWelfare": true,
       |            "armedForcesOfTheCrown": false,
       |            "other": false
       |          },
       |          "publicBenefit": {
       |            "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
       |          }
       |        }
       |      }
       |    },
       |    "partner":[{
       |        "responsiblePerson": {
       |              "action": "1",
       |              "relation": "2"
       |        },
       |        "type": "1",
       |               "addPartner": {
       |              "effectiveDateOfChange": "$date"
       |        },
       |               "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "Albert",
       |                    "middleName": "G",
       |                    "lastName": "Einstien"
       |                },
       |                "position": "02",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456C"
       |        },
       |        "addressDetails": {
       |              "currentAddress": {
       |                    "nonUKAddress": false,
       |                    "addressLine1": "2",
       |                    "addressLine2": "Dubai Main Road",
       |                    "addressLine3": "line3",
       |                    "addressLine4": "line4",
       |                    "postcode": "G27JD",
       |                    "nonUKCountry": "United Kingdom"
       |               }
       |        }
       |    },
       |    {
       |        "responsiblePerson": {
       |              "action": "1",
       |              "relation": "1"
       |        },
       |        "type": "1",
       |               "addPartner": {
       |              "effectiveDateOfChange": "$date"
       |        },
       |               "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "David",
       |                    "lastName": "Beckham"
       |                },
       |                "position": "05",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456C"
       |        },
       |        "addressDetails": {
       |              "currentAddress": {
       |                    "nonUKAddress": true,
       |                    "addressLine1": "3",
       |                    "addressLine2": "Morrison Street",
       |                    "addressLine3": "Bill Tower",
       |                    "nonUKCountry": "Italy"
       |               }
       |        }
       |
       |    }]
       |  }
       |""".stripMargin)

  lazy val jsonGeneral: JsValue = Json.parse(
    s"""{
       |    "charityRegistration": {
       |        "common": {
       |            "bankDetails": {
       |                "accountName": "fullName",
       |                "accountNumber": "12345678",
       |                "sortCode": "123456"
       |            },
       |            "declarationInfo": {
       |                "name": {
       |                    "firstName": "Albert",
       |                    "middleName": "G",
       |                    "lastName": "Einstien",
       |                    "title": "0001"
       |                },
       |                "position": "23",
       |                "declaration": true,
       |                "overseas": false
       |            },
       |            "admin": {
       |                "acknowledgmentReference": "15 CHARACTERS S",
       |                "credentialID": "/newauth/credentialId/id",
       |                "sessionID": "50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA",
       |                "welshIndicator": false,
       |                "applicationDate": "1970-01-01"
       |            },
       |            "organisation": {
       |                "applicationType": "0",
       |                "emailAddress": "abc@gmail.com",
       |                "countryEstd": "1",
       |                "orgName": "ABC",
       |                "telephoneNumber": "07700 900 982",
       |                "mobileNumber": "07700 000 111"
       |            },
       |            "addressDetails": {
       |                "differentCorrespondence": false,
       |                "officialAddress": {
       |                    "addressLine1": "7",
       |                    "addressLine2": "Morrison street",
       |                    "nonUKCountry": "India",
       |                    "nonUKAddress": true
       |                }
       |            }
       |        },
       |        "charity": {
       |            "charityOrganisation": {
       |                "registeredRegulator": false,
       |                "nonRegReason": "1"
       |            },
       |            "aboutOrganisation": {
       |                "aboutOrgCommon": {
       |                    "otherDocument": "Other Documents for Charity",
       |                    "effectiveDate": "2014-07-01"
       |                },
       |                "documentEnclosed": "1",
       |                "governingApprovedDoc": false,
       |                "governingApprovedWords": false
       |            },
       |            "operationAndFunds": {
       |                "operationAndFundsCommon": {
       |                    "accountPeriodEnd": "0101",
       |                    "financialAccounts": true
       |                },
       |                "futureFunds": "other, donations, tradingSubsidiaries, tradingIncome, fundraising, grants, membershipSubscriptions, investmentIncome",
       |                "otherAreaOperation": true,
       |                "englandAndWales": true,
       |                "scotland": false,
       |                "northernIreland": false,
       |                "ukWide": false,
       |                "overseas": false
       |            },
       |            "orgPurpose": {
       |                "charitableObjectives": {
       |                    "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
       |                },
       |                "charitablePurposes": {
       |                    "reliefOfPoverty": false,
       |                    "education": false,
       |                    "religion": false,
       |                    "healthOrSavingOfLives": false,
       |                    "citizenshipOrCommunityDevelopment": false,
       |                    "artsCultureOrScience": false,
       |                    "amateurSport": true,
       |                    "humanRights": false,
       |                    "environmentalProtection": false,
       |                    "reliefOfYouthAge": false,
       |                    "animalWelfare": true,
       |                    "armedForcesOfTheCrown": false,
       |                    "other": false
       |                },
       |                "publicBenefit": {
       |                    "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
       |                }
       |            }
       |        }
       |    },
       |    "partner": [
       |        {
       |            "responsiblePerson": {
       |                "action": "1",
       |                "relation": "2"
       |            },
       |            "type": "1",
       |            "addPartner": {
       |                "effectiveDateOfChange": "$date"
       |            },
       |            "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "Albert",
       |                    "middleName": "G",
       |                    "lastName": "Einstien"
       |                },
       |                "position": "23",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456C"
       |            },
       |            "addressDetails": {
       |                "currentAddress": {
       |                    "nonUKAddress": false,
       |                    "addressLine1": "2",
       |                    "addressLine2": "Dubai Main Road",
       |                    "addressLine3": "line3",
       |                    "addressLine4": "line4",
       |                    "postcode": "G27JD",
       |                    "nonUKCountry": "United Kingdom"
       |                }
       |            }
       |        },
       |        {
       |            "responsiblePerson": {
       |                "action": "1",
       |                "relation": "2"
       |            },
       |            "type": "1",
       |            "addPartner": {
       |                "effectiveDateOfChange": "$date"
       |            },
       |            "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "David",
       |                    "lastName": "Beckham"
       |                },
       |                "position": "05",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456A"
       |            },
       |            "addressDetails": {
       |                "currentAddress": {
       |                    "nonUKAddress": true,
       |                    "addressLine1": "3",
       |                    "addressLine2": "Morrison Street",
       |                    "addressLine3": "Bill Tower",
       |                    "nonUKCountry": "Italy"
       |                }
       |            }
       |        },
       |        {
       |            "responsiblePerson": {
       |                "action": "1",
       |                "relation": "1"
       |            },
       |            "type": "1",
       |            "addPartner": {
       |                "effectiveDateOfChange": "$date"
       |            },
       |            "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "Albert",
       |                    "middleName": "G",
       |                    "lastName": "Einstien"
       |                },
       |                "position": "02",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456C"
       |            },
       |            "addressDetails": {
       |                "currentAddress": {
       |                    "nonUKAddress": false,
       |                    "addressLine1": "2",
       |                    "addressLine2": "Dubai Main Road",
       |                    "addressLine3": "line3",
       |                    "addressLine4": "line4",
       |                    "postcode": "G27JD",
       |                    "nonUKCountry": "United Kingdom"
       |                }
       |            }
       |        },
       |        {
       |            "responsiblePerson": {
       |                "action": "1",
       |                "relation": "1"
       |            },
       |            "type": "1",
       |            "addPartner": {
       |                "effectiveDateOfChange": "$date"
       |            },
       |            "individualDetails": {
       |                "name": {
       |                    "title": "0001",
       |                    "firstName": "David",
       |                    "lastName": "Beckham"
       |                },
       |                "position": "05",
       |                "dateOfBirth": "2000-12-11",
       |                "dayPhoneNumber": "07700 900 982",
       |                "mobilePhone": "07700 900 981",
       |                "nino": "QQ123456A"
       |            },
       |            "addressDetails": {
       |                "currentAddress": {
       |                    "nonUKAddress": true,
       |                    "addressLine1": "3",
       |                    "addressLine2": "Morrison Street",
       |                    "addressLine3": "Bill Tower",
       |                    "nonUKCountry": "Italy"
       |                }
       |            }
       |        }
       |    ]
       |}""".stripMargin
  )
}

