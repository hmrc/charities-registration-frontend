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

import play.api.libs.json.Json

import java.time.{LocalDate, MonthDay}

import models.addressLookup.AddressModel
import models.authOfficials.OfficialsPosition
import models.operations.CharitablePurposes.{AmateurSport, AnimalWelfare}
import models.operations.{CharitablePurposes, CharityEstablishedOptions, FundRaisingOptions, OperatingLocationOptions}
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Other, Scottish}
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import models.{BankDetails, CharityName, CharityOtherRegulatorDetails, MongoDateTimeFormats, Name, PhoneNumber, SelectTitle}
import pages.addressLookup._
import pages.authorisedOfficials._
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityNamePage}
import pages.operationsAndFunds._
import pages.otherOfficials._
import pages.regulatorsAndDocuments._

class CharitySubmissionTransformerSpec extends CharityTransformerConstants {

  private val jsonTransformer = new CharitySubmissionTransformer(
    new CharityTransformer,
    new CharityPartnerTransformer,
    new CharityCommonTransformer
  )

  private val reason: String =
    "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6" +
      "64354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxa" +
      "sxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"

  "CharitySubmissionTransformer" must {

    "convert to CharitySubmission" in {

      val localUserAnswers = baseAnswers
        .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.UKAgent))
        .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
        .flatMap(_.set(AuthorisedOfficialsNamePage(1), Name(SelectTitle.Mr, "David", None, "Beckham")))
        .flatMap(_.set(AuthorisedOfficialsPositionPage(1), OfficialsPosition.Director))
        .flatMap(_.set(AuthorisedOfficialsDOBPage(1), LocalDate.parse(dec11th2000.toString)))
        .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(1), phoneNumbers))
        .flatMap(_.set(AuthorisedOfficialsNinoPage(1), nino2WithSpaces))
        .flatMap(
          _.set(
            AuthorisedOfficialAddressLookupPage(1),
            AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, itCountryModel)
          )
        )
        .flatMap(_.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mr, "David", None, "Beckham")))
        .flatMap(_.set(OtherOfficialsPositionPage(1), OfficialsPosition.Director))
        .flatMap(_.set(OtherOfficialsDOBPage(1), LocalDate.parse(dec11th2000.toString)))
        .flatMap(_.set(OtherOfficialsPhoneNumberPage(1), phoneNumbers))
        .flatMap(_.set(OtherOfficialsNinoPage(1), nino2WithSpaces))
        .flatMap(
          _.set(
            OtherOfficialAddressLookupPage(1),
            AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, itCountryModel)
          )
        )
        .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold))
        .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
        .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
        .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
        .flatMap(
          _.set(AccountingPeriodEndDatePage, MonthDay.from(jan1st2020))(
            MongoDateTimeFormats.localDayMonthWrite
          ).flatMap(_.set(IsFinancialAccountsPage, true))
            .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
            .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
            .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
            .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales))
            .flatMap(_.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England)))
            .flatMap(_.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare)))
            .flatMap(
              _.set(
                CharitableObjectivesPage,
                "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
              )
            )
        )
        .success
        .value

      localUserAnswers.data.transform(jsonTransformer.userAnswersToSubmission(fakeDataRequest)).asOpt.value mustBe Json
        .parse(jsonGeneral)
    }

    "convert right with all fields filled" in {

      val userAnswers = baseAnswers
        .flatMap(_.set(BankDetailsPage, bankDetails))
        .flatMap(
          _.set(
            CharityOfficialAddressLookupPage,
            AddressModel(
              Seq("7", "Morrison street", "line3", "line4"),
              Some("G58AN"),
              gbCountryModel
            )
          )
        )
        .flatMap(_.set(CanWeSendToThisAddressPage, false))
        .flatMap(
          _.set(
            CharityPostalAddressLookupPage,
            AddressModel(Seq("1", "Morrison street"), Some("ZZ11ZZ"), gbCountryModel)
          )
        )
        .flatMap(_.set(CharityNamePage, charityName))
        .flatMap(_.set(AuthorisedOfficialsNamePage(1), Name(SelectTitle.Mr, "David", None, "Beckham")))
        .flatMap(_.set(AuthorisedOfficialsPositionPage(1), OfficialsPosition.Director))
        .flatMap(_.set(AuthorisedOfficialsDOBPage(1), LocalDate.parse(dec11th2000.toString)))
        .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(1), phoneNumbers))
        .flatMap(_.set(AuthorisedOfficialsNinoPage(1), nino2WithSpaces))
        .flatMap(
          _.set(
            AuthorisedOfficialAddressLookupPage(1),
            AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, itCountryModel)
          )
        )
        .flatMap(_.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mr, "David", None, "Beckham")))
        .flatMap(_.set(OtherOfficialsPositionPage(1), OfficialsPosition.Director))
        .flatMap(_.set(OtherOfficialsDOBPage(1), LocalDate.parse(dec11th2000.toString)))
        .flatMap(_.set(OtherOfficialsPhoneNumberPage(1), phoneNumbers))
        .flatMap(_.set(OtherOfficialsNinoPage(1), nino2WithSpaces))
        .flatMap(
          _.set(
            OtherOfficialAddressLookupPage(1),
            AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, itCountryModel)
          )
        )
        .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other)))
        .flatMap(_.set(CharityCommissionRegistrationNumberPage, charityCommissionRegistrationNumber))
        .flatMap(_.set(ScottishRegulatorRegNumberPage, scottishRegulatorRegistrationNumber))
        .flatMap(_.set(NIRegulatorRegNumberPage, niRegulatorRegistrationNumber))
        .flatMap(
          _.set(
            CharityOtherRegulatorDetailsPage,
            charityRegulatorDetails
          )
        )
        .flatMap(_.set(IsCharityRegulatorPage, true))
        .flatMap(
          _.set(PublicBenefitsPage, reason)
        )
        .flatMap(_.set(CharitablePurposesPage, CharitablePurposes.values.toSet))
        .flatMap(
          _.set(CharitableObjectivesPage, reason)
        )
        .flatMap(
          _.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation)
            .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
            .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
            .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
            .flatMap(
              _.set(SectionsChangedGoverningDocumentPage, reason)
            )
        )
        .flatMap(
          _.set(AccountingPeriodEndDatePage, MonthDay.from(jan1st2020))(
            MongoDateTimeFormats.localDayMonthWrite
          ).flatMap(_.set(IsFinancialAccountsPage, true))
            .flatMap(
              _.set(WhyNoBankStatementPage, reason)
            )
            .flatMap(_.set(EstimatedIncomePage, BigDecimal(2000.00)))
            .flatMap(_.set(ActualIncomePage, BigDecimal(19999.99)))
            .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
            .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales))
            .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "Country 1"))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "Country 2"))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), "Country 3"))
            .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(3), "Country 4"))
            .flatMap(
              _.set(WhatCountryDoesTheCharityOperateInPage(4), "Country 5")
            )
        )
        .success
        .value

      userAnswers.data.transform(jsonTransformer.userAnswersToSubmission(fakeDataRequest)).asOpt.value mustBe Json
        .parse(jsonAllFields)
    }

    "convert with minimum fields" in {

      val userAnswers = localUserAnswers
        .set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien"))
        .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
        .success
        .value

      userAnswers.data.transform(jsonTransformer.userAnswersToSubmission(fakeDataRequest)).asOpt.value mustBe Json
        .parse(jsonMinFields)
    }
  }
}
