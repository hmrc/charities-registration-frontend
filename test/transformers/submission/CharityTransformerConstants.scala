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

trait CharityTransformerConstants extends SpecBase {

  private val date = LocalDate.now()

  val day: Int   = 11
  val month: Int = 12
  val year: Int  = 2000

  lazy val baseAnswers: Try[UserAnswers] = emptyUserAnswers
    .set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", None))
    .flatMap(
      _.set(
        CharityOfficialAddressLookupPage,
        AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "India"))
      )
    )
    .flatMap(_.set(CanWeSendToThisAddressPage, true))
    .flatMap(
      _.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", Some("07700 000 111"), "abc@gmail.com"))
    )
    .flatMap(_.set(CharityNamePage, CharityName("ABC", None)))
    .flatMap(_.set(IsCharityRegulatorPage, false))
    .flatMap(_.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")))
    .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
    .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day)))
    .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981"))))
    .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C"))
    .flatMap(
      _.set(
        AuthorisedOfficialAddressLookupPage(0),
        AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))
      )
    )
    .flatMap(_.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")))
    .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar))
    .flatMap(_.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day)))
    .flatMap(_.set(OtherOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981"))))
    .flatMap(_.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C"))
    .flatMap(
      _.set(
        OtherOfficialAddressLookupPage(0),
        AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))
      )
    )
    .flatMap(
      _.set(
        PublicBenefitsPage,
        "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
      )
    )
    .flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
    .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))

  val localUserAnswers: UserAnswers = baseAnswers
    .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold))
    .flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
    .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
    .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
    .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
    .flatMap(
      _.set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(
        MongoDateTimeFormats.localDayMonthWrite
      ).flatMap(_.set(IsFinancialAccountsPage, true))
        .flatMap(_.set(EstimatedIncomePage, BigDecimal(123)))
        .flatMap(_.set(ActualIncomePage, BigDecimal(121)))
        .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
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
    .flatMap(_.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "David", None, "Beckham")))
    .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Director))
    .flatMap(
      _.set(
        OtherOfficialAddressLookupPage(0),
        AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, CountryModel("IT", "Italy"))
      )
    )
    .success
    .value

  lazy val jsonAllFields: JsValue = Json.parse(
    s"""
       |{"charityRegistration":{"common":{"bankDetails":{"accountName":"fullName","rollNumber":"operatingName","accountNumber":12345678,"sortCode":123456},"declarationInfo":{"telephoneNumber":"07700 900 982","name":{"lastName":"Einstien","firstName":"Albert","middleName":"G","title":"0001"},"postcode":"G58AN","position":"02","declaration":true,"overseas":false},"admin":{"acknowledgmentReference":"15 CHARACTERS S","credentialID":"/newauth/credentialId/id","sessionID":"50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA","welshIndicator":false,"applicationDate":"1970-01-01"},"organisation":{"countryEstd":"1","applicationType":"0","emailAddress":"abc@gmail.com","orgName":"ABC","telephoneNumber":"07700 900 982","mobileNumber":"07700 000 111","operatingName":"OpName"},"addressDetails":{"differentCorrespondence":true,"officialAddress":{"postcode":"G58AN","addressLine1":"7","addressLine2":"Morrison street","addressLine3":"line3","addressLine4":"line4","nonUKAddress":false},"correspondenceAddress":{"postcode":"ZZ11ZZ","addressLine1":"1","addressLine2":"Morrison street","nonUKAddress":false}}},"partner":[{"responsiblePerson":{"action":"1","relation":"2"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Einstien","firstName":"Albert","middleName":"G","title":"0001"},"dateOfBirth":"2000-12-11","position":"02","nino":"QQ123456C"},"addressDetails":{"currentAddress":{"postcode":"G27JD","addressLine1":"2","addressLine2":"Dubai Main Road","addressLine3":"line3","addressLine4":"line4","nonUKAddress":false}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}},{"responsiblePerson":{"action":"1","relation":"2"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Beckham","firstName":"David","title":"0001"},"dateOfBirth":"2000-12-11","position":"05","nino":"QQ123456A"},"addressDetails":{"currentAddress":{"addressLine1":"3","addressLine2":"Morrison Street","addressLine3":"Bill Tower","nonUKCountry":"IT","nonUKAddress":true}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}},{"responsiblePerson":{"action":"1","relation":"1"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Einstien","firstName":"Albert","middleName":"G","title":"0001"},"dateOfBirth":"2000-12-11","position":"02","nino":"QQ123456C"},"addressDetails":{"currentAddress":{"postcode":"G27JD","addressLine1":"2","addressLine2":"Dubai Main Road","addressLine3":"line3","addressLine4":"line4","nonUKAddress":false}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}},{"responsiblePerson":{"action":"1","relation":"1"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Beckham","firstName":"David","title":"0001"},"dateOfBirth":"2000-12-11","position":"05","nino":"QQ123456A"},"addressDetails":{"currentAddress":{"addressLine1":"3","addressLine2":"Morrison Street","addressLine3":"Bill Tower","nonUKCountry":"IT","nonUKAddress":true}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}}],"charity":{"operationAndFunds":{"incomeReceivedToDate":19999.99,"northernIreland":true,"otherCountriesOfOperation":{"overseas1":"Country 1","overseas2":"Country 2","overseas3":"Country 3","overseas4":"Country 4","overseas5":"Country 5"},"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":true,"operationAndFundsCommon":{"noBankStatementsB":"11223344556677889900","financialAccounts":true,"accountPeriodEnd":"0101","noBankStatements":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643"},"englandAndWales":true,"estimatedGrossIncome":2000,"scotland":true,"overseas":true},"charityOrganisation":{"registeredRegulator":true,"regulator":{"otherRegulatorName":"Other Regulator Name","ccni":true,"oscr":true,"ccewRegistrationNumber":"123456","oscrRegistrationNumber":"SC123456","ccew":true,"otherRegulator":true,"ccniRegistrationNumber":"ABCDEFGHIJ1234567890","otherRegulatorRegistrationNumber":"12345678901234567890"}},"orgPurpose":{"charitablePurposes":{"other":true,"education":true,"animalWelfare":true,"humanRights":true,"healthOrSavingOfLives":true,"religion":true,"artsCultureOrScience":true,"amateurSport":true,"citizenshipOrCommunityDevelopment":true,"environmentalProtection":true,"reliefOfPoverty":true,"armedForcesOfTheCrown":true,"reliefOfYouthAge":true},"charitableObjectives":{"objectivesB":"11223344556677889900","objectivesA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643"},"publicBenefit":{"publicBenefitB":"11223344556677889900","publicBenefitA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643"}},"aboutOrganisation":{"governingApprovedDoc":true,"governingApprovedChangesB":"11223344556677889900","documentEnclosed":"2","aboutOrgCommon":{"otherDocument":"Other Documents for Charity","effectiveDate":"2014-07-01"},"governingApprovedChanges":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643","governingApprovedWords":true}}}}
       |""".stripMargin)

  lazy val jsonMinFields: JsValue = Json.parse(
    s"""{
       |"charityRegistration":{
       |  "common":{
       |    "bankDetails":{
       |      "accountName":"fullName",
       |      "accountNumber":12345678,
       |      "sortCode":123456
       |    },
       |    "declarationInfo":{
       |        "telephoneNumber":"07700 900 982",
       |        "name":{
       |            "lastName":"Einstien",
       |            "firstName":"Albert",
       |            "middleName":"G",
       |            "title":"0001"
       |        },
       |        "position":"02",
       |        "declaration":true,
       |        "overseas":false
       |     },
       |     "admin":{
       |        "acknowledgmentReference":"15 CHARACTERS S",
       |        "credentialID":"/newauth/credentialId/id",
       |        "sessionID":"50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA",
       |        "welshIndicator":false,
       |        "applicationDate":"1970-01-01"
       |      },
       |      "organisation":{
       |          "countryEstd":"1",
       |          "applicationType":"0",
       |          "emailAddress":"abc@gmail.com",
       |          "orgName":"ABC",
       |          "telephoneNumber":"07700 900 982",
       |          "mobileNumber":"07700 000 111"
       |      },
       |      "addressDetails":{
       |        "differentCorrespondence":false,
       |        "officialAddress":{
       |            "addressLine1":"7",
       |            "addressLine2":"Morrison street",
       |            "nonUKCountry":"IN",
       |            "nonUKAddress":true
       |        }
       |       }
       |       },
       |       "partner":[{"responsiblePerson":{"action":"1","relation":"2"},
       |       "individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Einstien","firstName":"Albert","middleName":"G","title":"0001"},"dateOfBirth":"2000-12-11","position":"02","nino":"QQ123456C"},"addressDetails":{"currentAddress":{"postcode":"G27JD","addressLine1":"2","addressLine2":"Dubai Main Road","addressLine3":"line3","addressLine4":"line4","nonUKAddress":false}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}},{"responsiblePerson":{"action":"1","relation":"1"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Beckham","firstName":"David","title":"0001"},"dateOfBirth":"2000-12-11","position":"05","nino":"QQ123456C"},"addressDetails":{"currentAddress":{"addressLine1":"3","addressLine2":"Morrison Street","addressLine3":"Bill Tower","nonUKCountry":"IT","nonUKAddress":true}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}}],"charity":{"operationAndFunds":{"incomeReceivedToDate":121,"northernIreland":false,"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":false,"operationAndFundsCommon":{"financialAccounts":true,"accountPeriodEnd":"0101"},"englandAndWales":true,"estimatedGrossIncome":123,"scotland":false,"overseas":false},"charityOrganisation":{"nonRegReason":"1","registeredRegulator":false},"orgPurpose":{"charitablePurposes":{"other":false,"education":false,"animalWelfare":true,"humanRights":false,"healthOrSavingOfLives":false,"religion":false,"artsCultureOrScience":false,"amateurSport":true,"citizenshipOrCommunityDevelopment":false,"environmentalProtection":false,"reliefOfPoverty":false,"armedForcesOfTheCrown":false,"reliefOfYouthAge":false},"charitableObjectives":{"objectivesA":"qwet qwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre 66"},"publicBenefit":{"publicBenefitA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"}},"aboutOrganisation":{"governingApprovedDoc":false,"documentEnclosed":"2","aboutOrgCommon":{"otherDocument":"Other Documents for Charity","effectiveDate":"2014-07-01"},"governingApprovedWords":true}}}}
       |""".stripMargin)

  lazy val jsonGeneral: JsValue = Json.parse(
    s"""
       |{"charityRegistration":{"common":{"bankDetails":{"accountName":"fullName","accountNumber":12345678,"sortCode":123456},"declarationInfo":{"telephoneNumber":"07700 900 982","name":{"lastName":"Einstien","firstName":"Albert","middleName":"G","title":"0001"},"position":"23","declaration":true,"overseas":false},"admin":{"acknowledgmentReference":"15 CHARACTERS S","credentialID":"/newauth/credentialId/id","sessionID":"50 CHARACTERS STRING 50 CHARACTERS STRING 50 CHARA","welshIndicator":false,"applicationDate":"1970-01-01"},"organisation":{"countryEstd":"1","applicationType":"0","emailAddress":"abc@gmail.com","orgName":"ABC","telephoneNumber":"07700 900 982","mobileNumber":"07700 000 111"},"addressDetails":{"differentCorrespondence":false,"officialAddress":{"addressLine1":"7","addressLine2":"Morrison street","nonUKCountry":"IN","nonUKAddress":true}}},"partner":[{"responsiblePerson":{"action":"1","relation":"2"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Einstien","firstName":"Albert","middleName":"G","title":"0001"},"dateOfBirth":"2000-12-11","position":"23","nino":"QQ123456C"},"addressDetails":{"currentAddress":{"postcode":"G27JD","addressLine1":"2","addressLine2":"Dubai Main Road","addressLine3":"line3","addressLine4":"line4","nonUKAddress":false}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}},{"responsiblePerson":{"action":"1","relation":"2"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Beckham","firstName":"David","title":"0001"},"dateOfBirth":"2000-12-11","position":"05","nino":"QQ123456A"},"addressDetails":{"currentAddress":{"addressLine1":"3","addressLine2":"Morrison Street","addressLine3":"Bill Tower","nonUKCountry":"IT","nonUKAddress":true}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}},{"responsiblePerson":{"action":"1","relation":"1"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Einstien","firstName":"Albert","middleName":"G","title":"0001"},"dateOfBirth":"2000-12-11","position":"02","nino":"QQ123456C"},"addressDetails":{"currentAddress":{"postcode":"G27JD","addressLine1":"2","addressLine2":"Dubai Main Road","addressLine3":"line3","addressLine4":"line4","nonUKAddress":false}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}},{"responsiblePerson":{"action":"1","relation":"1"},"individualDetails":{"mobilePhone":"07700 900 981","dayPhoneNumber":"07700 900 982","name":{"lastName":"Beckham","firstName":"David","title":"0001"},"dateOfBirth":"2000-12-11","position":"05","nino":"QQ123456A"},"addressDetails":{"currentAddress":{"addressLine1":"3","addressLine2":"Morrison Street","addressLine3":"Bill Tower","nonUKCountry":"IT","nonUKAddress":true}},"type":"1","addPartner":{"effectiveDateOfChange":"2023-02-21"}}],"charity":{"operationAndFunds":{"incomeReceivedToDate":121,"northernIreland":false,"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":false,"operationAndFundsCommon":{"financialAccounts":true,"accountPeriodEnd":"0101"},"englandAndWales":true,"estimatedGrossIncome":123,"scotland":false,"overseas":false},"charityOrganisation":{"nonRegReason":"1","registeredRegulator":false},"orgPurpose":{"charitablePurposes":{"other":false,"education":false,"animalWelfare":true,"humanRights":false,"healthOrSavingOfLives":false,"religion":false,"artsCultureOrScience":false,"amateurSport":true,"citizenshipOrCommunityDevelopment":false,"environmentalProtection":false,"reliefOfPoverty":false,"armedForcesOfTheCrown":false,"reliefOfYouthAge":false},"charitableObjectives":{"objectivesA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"},"publicBenefit":{"publicBenefitA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"}},"aboutOrganisation":{"governingApprovedDoc":false,"documentEnclosed":"2","aboutOrgCommon":{"otherDocument":"Other Documents for Charity","effectiveDate":"2014-07-01"},"governingApprovedWords":true}}}}
       |""".stripMargin
  )
}
