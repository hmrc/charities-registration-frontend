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

import models.addressLookup.{AddressModel, CountryModel}
import models.authOfficials.OfficialsPosition
import models.operations.CharitablePurposes.{AmateurSport, AnimalWelfare}
import models.operations.{CharitablePurposes, FundRaisingOptions, OperatingLocationOptions}
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Other, Scottish}
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import models.{BankDetails, CharityName, CharityOtherRegulatorDetails, MongoDateTimeFormats, Name, PhoneNumber}
import org.joda.time.{MonthDay, LocalDate => JLocalDate}
import pages.addressLookup._
import pages.authorisedOfficials._
import pages.charityInformation.{CanWeSendToThisAddressPage, CharityNamePage}
import pages.operationsAndFunds._
import pages.otherOfficials._
import pages.regulatorsAndDocuments._

class CharitySubmissionTransformerSpec extends CharityTransformerTodoPages {

  val jsonTransformer = new CharitySubmissionTransformer(new CharityTransformer, new CharityPartnerTransformer, new CharityCommonTransformer)

  "CharitySubmissionTransformer" must {

    "convert to CharitySubmission" in {

      val localUserAnswers = baseAnswers
        .flatMap(
        _.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.UKAgent))
        .flatMap(
          _.set(AuthorisedOfficialsNamePage(1), Name("David", None, "Beckham"))).flatMap(
        _.set(AuthorisedOfficialsPositionPage(1), OfficialsPosition.Director)).flatMap(
        _.set(AuthorisedOfficialsDOBPage(1), LocalDate.of(year, month, day))).flatMap(
        _.set(AuthorisedOfficialsPhoneNumberPage(1), PhoneNumber("07700 900 982", "07700 900 981"))).flatMap(
        _.set(AuthorisedOfficialsNinoPage(1), "QQ 12 34 56 A")).flatMap(
        _.set(AuthorisedOfficialAddressLookupPage(1),
          AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, CountryModel("IT", "Italy")))).flatMap(
        _.set(OtherOfficialsNamePage(1), Name("David", None, "Beckham"))).flatMap(
        _.set(OtherOfficialsPositionPage(1), OfficialsPosition.Director)).flatMap(
        _.set(OtherOfficialsDOBPage(1), LocalDate.of(year, month, day))).flatMap(
        _.set(OtherOfficialsPhoneNumberPage(1), PhoneNumber("07700 900 982", "07700 900 981"))).flatMap(
        _.set(OtherOfficialsNinoPage(1), "QQ 12 34 56 A")).flatMap(
        _.set(OtherOfficialAddressLookupPage(1),
          AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, CountryModel("IT", "Italy"))))
        .flatMap(
        _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)).flatMap(
        _.set(GoverningDocumentNamePage, "Other Documents for Charity")).flatMap(
        _.set(IsApprovedGoverningDocumentPage, false)).flatMap(
        _.set(HasCharityChangedPartsOfGoverningDocumentPage, false)).flatMap(
        _.set(AccountingPeriodEndDatePage,
          MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
          _.set(HasFinancialAccountsPage, true)).flatMap(
          _.set(FundRaisingPage, FundRaisingOptions.values.toSet)).flatMap(
          _.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.EnglandAndWales))).flatMap(
          _.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare))).flatMap(
          _.set(CharitableObjectivesPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
          ))
      ).success.value


      localUserAnswers.data.transform(jsonTransformer.userAnswersToSubmission(fakeDataRequest)).asOpt.value mustBe jsonGeneral
    }

    "convert right with all fields filled" in {

      val userAnswers = baseAnswers
        .flatMap(_.set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName"))))
        .flatMap(_.set(CharityOfficialAddressLookupPage,
          AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("GB", "United Kingdom"))))
        .flatMap(_.set(CanWeSendToThisAddressPage, false))
        .flatMap(_.set(CharityPostalAddressLookupPage,
          AddressModel(Seq("1", "Morrison street"), Some("ZZ11ZZ"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CharityNamePage, CharityName("ABC", Some("OpName"))))
        .flatMap(
        _.set(AuthorisedOfficialsNamePage(1), Name("David", None, "Beckham"))).flatMap(
        _.set(AuthorisedOfficialsPositionPage(1), OfficialsPosition.Director)).flatMap(
        _.set(AuthorisedOfficialsDOBPage(1), LocalDate.of(year, month, day))).flatMap(
        _.set(AuthorisedOfficialsPhoneNumberPage(1), PhoneNumber("07700 900 982", "07700 900 981"))).flatMap(
        _.set(AuthorisedOfficialsNinoPage(1), "QQ 12 34 56 A")).flatMap(
        _.set(AuthorisedOfficialAddressLookupPage(1),
          AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, CountryModel("IT", "Italy")))).flatMap(
        _.set(OtherOfficialsNamePage(1), Name("David", None, "Beckham"))).flatMap(
        _.set(OtherOfficialsPositionPage(1), OfficialsPosition.Director)).flatMap(
        _.set(OtherOfficialsDOBPage(1), LocalDate.of(year, month, day))).flatMap(
        _.set(OtherOfficialsPhoneNumberPage(1), PhoneNumber("07700 900 982", "07700 900 981"))).flatMap(
        _.set(OtherOfficialsNinoPage(1), "QQ 12 34 56 A")).flatMap(
        _.set(OtherOfficialAddressLookupPage(1),
          AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, CountryModel("IT", "Italy"))))

        .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other))).flatMap(
        _.set(CharityCommissionRegistrationNumberPage, "123456")).flatMap(
        _.set(ScottishRegulatorRegNumberPage, "SC123456")).flatMap(
        _.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890")).flatMap(
        _.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890"))).flatMap(
        _.set(IsCharityRegulatorPage, true))
        .flatMap(_.set(PublicBenefitsPage,
        "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"
      )).flatMap(_.set(CharitablePurposesPage, CharitablePurposes.values.toSet)).flatMap(
        _.set(CharitableObjectivesPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"
        )).flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation).flatMap(
          _.set(GoverningDocumentNamePage, "Other Documents for Charity%")).flatMap(
          _.set(IsApprovedGoverningDocumentPage, true)).flatMap(
         _.set(HasCharityChangedPartsOfGoverningDocumentPage, false)).flatMap(
          _.set(GoverningDocumentChangePage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"))
      ).flatMap(_.set(AccountingPeriodEndDatePage,
        MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
        _.set(HasFinancialAccountsPage, true)).flatMap(
        _.set(NoBankStatementPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900")
      ).flatMap(
        _.set(EstimatedIncomePage, 2000.00)).flatMap(
        _.set(GrossIncomePage, 19999.99)).flatMap(
        _.set(FundRaisingPage, FundRaisingOptions.values.toSet)).flatMap(
        _.set(OperatingLocationPage, OperatingLocationOptions.values.toSet)).flatMap(
        _.set(OverseasCountryPage(0), "Country 1")).flatMap(
        _.set(OverseasCountryPage(1), "Country 2")).flatMap(
        _.set(OverseasCountryPage(2), "Country 3")).flatMap(
        _.set(OverseasCountryPage(3), "Country 4")).flatMap(
        _.set(OverseasCountryPage(4), "Country 5")
      )).success.value

      userAnswers.data.transform(jsonTransformer.userAnswersToSubmission(fakeDataRequest)).asOpt.value mustBe jsonAllFields
    }

    "convert with minimum fields" in {

      localUserAnswers.data.transform(jsonTransformer.userAnswersToSubmission(fakeDataRequest)).asOpt.value mustBe jsonMinFields

    }

  }
}