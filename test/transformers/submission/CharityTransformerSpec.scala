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
import models.operations.CharitablePurposes.{AmateurSport, AnimalWelfare}
import models.operations.{CharitablePurposes, FundRaisingOptions, OperatingLocationOptions}
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Other, Scottish}
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import models.{CharityOtherRegulatorDetails, MongoDateTimeFormats}
import org.joda.time.{MonthDay, LocalDate => JLocalDate}
import pages.operationsAndFunds._
import pages.regulatorsAndDocuments._
import play.api.libs.json.Json

import java.time.LocalDate

class CharityTransformerSpec extends SpecBase with CharityTransformerConstants {

  val jsonTransformer: CharityTransformer = new CharityTransformer

  val reason =
    "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre" +
      "664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvf" +
      "xasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"

  "CharityTransformer" when {

    "userAnswersToRegulator" must {

      "convert the correct Regulator object for all regulator" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, "123456"))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "SC123456"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890"))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890")
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |      "regulator": {
            |        "ccew": true,
            |        "ccewRegistrationNumber": "123456",
            |        "oscr": true,
            |        "oscrRegistrationNumber": "SC123456",
            |        "ccni": true,
            |        "ccniRegistrationNumber": "ABCDEFGHIJ1234567890",
            |        "otherRegulator": true,
            |        "otherRegulatorName": "Other Regulator Name",
            |        "otherRegulatorRegistrationNumber": "12345678901234567890"
            |      }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToRegulator).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct Regulator object without other regulator" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, "123456"))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "SC123456"))
          .flatMap(
            _.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890")
          )
          .success
          .value

        val expectedJson =
          """{
            |      "regulator": {
            |        "ccew": true,
            |        "ccewRegistrationNumber": "123456",
            |        "oscr": true,
            |        "oscrRegistrationNumber": "SC123456",
            |        "ccni": true,
            |        "ccniRegistrationNumber": "ABCDEFGHIJ1234567890"
            |      }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToRegulator).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct Regulator object only other" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharityRegulatorPage, Set[CharityRegulator](Other))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890")
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |      "regulator": {
            |        "otherRegulator": true,
            |        "otherRegulatorName": "Other Regulator Name",
            |        "otherRegulatorRegistrationNumber": "12345678901234567890"
            |      }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToRegulator).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToCharityOrganisation" must {

      "convert the correct CharityOrganisation when regulators are defined" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, "123456"))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "SC123456"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890"))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890")
            ).flatMap(
              _.set(IsCharityRegulatorPage, true)
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |"charityOrganisation": {
            |      "registeredRegulator": true,
            |      "regulator": {
            |        "ccew": true,
            |        "ccewRegistrationNumber": "123456",
            |        "oscr": true,
            |        "oscrRegistrationNumber": "SC123456",
            |        "ccni": true,
            |        "ccniRegistrationNumber": "ABCDEFGHIJ1234567890",
            |        "otherRegulator": true,
            |        "otherRegulatorName": "Other Regulator Name",
            |        "otherRegulatorRegistrationNumber": "12345678901234567890"
            |      }
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct Regulator object without regulators" in {

        val localUserAnswers = emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(
            _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)
          )
          .success
          .value

        val expectedJson =
          """{
            |"charityOrganisation": {
            |      "registeredRegulator": false,
            |      "nonRegReason": "1"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct Regulator object other and reason" in {

        val localUserAnswers = emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(
            _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other)
              .flatMap(_.set(WhyNotRegisteredWithCharityPage, "notRegisteredReason"))
          )
          .success
          .value

        val expectedJson =
          """{
            |"charityOrganisation": {
            |      "registeredRegulator": false,
            |      "nonRegReason": "7",
            |      "otherReason": "notRegisteredReason"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToAboutOrganisationCommon" must {

      "convert the correct AboutOrganisationCommon object" in {

        val localUserAnswers = emptyUserAnswers
          .set(GoverningDocumentNamePage, "Other Documents for Charity")
          .flatMap(
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1))
          )
          .success
          .value

        val expectedJson =
          """{
            |    "aboutOrgCommon": {
            |        "otherDocument": "Other Documents for Charity",
            |        "effectiveDate": "2014-07-01"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisationCommon).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct AboutOrganisationCommon object when other is not defined" in {

        val localUserAnswers =
          emptyUserAnswers.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)).success.value

        val expectedJson =
          """{
            |    "aboutOrgCommon": {
            |        "effectiveDate": "2014-07-01"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisationCommon).asOpt.value mustBe Json
          .parse(expectedJson)
      }
    }

    "userAnswersToAboutOrganisation" must {

      "convert the correct AboutOrganisation object and changes are >255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(SelectGoverningDocumentPage, MemorandumArticlesAssociation)
          .flatMap(
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1))
              .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
              .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
              .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
              .flatMap(
                _.set(
                  SectionsChangedGoverningDocumentPage,
                  reason
                )
              )
          )
          .success
          .value

        val expectedJson =
          """{
            |    "aboutOrganisation": {
            |      "aboutOrgCommon": {
            |        "otherDocument": "Other Documents for Charity",
            |        "effectiveDate": "2014-07-01"
            |      },
            |      "documentEnclosed": "2",
            |      "governingApprovedDoc": true,
            |      "governingApprovedWords": true,
            |      "governingApprovedChanges": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |      "governingApprovedChangesB": "11223344556677889900"
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct AboutOrganisation object and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(SelectGoverningDocumentPage, MemorandumArticlesAssociation)
          .flatMap(
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1))
              .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
              .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
              .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
              .flatMap(_.set(SectionsChangedGoverningDocumentPage, "changes are shorter than 255 characters"))
          )
          .success
          .value

        val expectedJson =
          """{
            |    "aboutOrganisation": {
            |      "aboutOrgCommon": {
            |        "otherDocument": "Other Documents for Charity",
            |        "effectiveDate": "2014-07-01"
            |      },
            |      "documentEnclosed": "2",
            |      "governingApprovedDoc": true,
            |      "governingApprovedWords": true,
            |      "governingApprovedChanges": "changes are shorter than 255 characters"
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct AboutOrganisation object and replace tabs and new line characters with spaces and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(SelectGoverningDocumentPage, MemorandumArticlesAssociation)
          .flatMap(
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1))
              .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
              .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
              .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
              .flatMap(
                _.set(
                  SectionsChangedGoverningDocumentPage,
                  "Hello I am writing this story to test replace tab \r\nand new line \r\ncharacters. If total length of the story is greater than 255 characters after replacing the tab and new line \r\ncharacters then split first 255 characters in part 1 \r\nand remaining in part 2"
                )
              )
          )
          .success
          .value

        val expectedJson =
          """{
            |    "aboutOrganisation": {
            |      "aboutOrgCommon": {
            |        "otherDocument": "Other Documents for Charity",
            |        "effectiveDate": "2014-07-01"
            |      },
            |      "documentEnclosed": "2",
            |      "governingApprovedDoc": true,
            |      "governingApprovedWords": true,
            |      "governingApprovedChanges": "Hello I am writing this story to test replace tab  and new line  characters. If total length of the story is greater than 255 characters after replacing the tab and new line  characters then split first 255 characters in part 1  and remaining in part 2"
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct AboutOrganisation object when document is not approved and changes are not defined" in {

        val localUserAnswers = emptyUserAnswers
          .set(SelectGoverningDocumentPage, MemorandumArticlesAssociation)
          .flatMap(
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1))
              .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
              .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
              .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
          )
          .success
          .value

        val expectedJson =
          """{
            |    "aboutOrganisation": {
            |      "aboutOrgCommon": {
            |        "otherDocument": "Other Documents for Charity",
            |        "effectiveDate": "2014-07-01"
            |      },
            |      "documentEnclosed": "2",
            |      "governingApprovedDoc": false,
            |      "governingApprovedWords": true
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToOperationAndFundsCommon" must {

      "convert the correct OperationAndFundsCommon object and changes are >255 characters long" in {

        val localUserAnswers =
          emptyUserAnswers
            .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 12, 25).toDate))(
              MongoDateTimeFormats.localDayMonthWrite
            )
            .flatMap(_.set(IsFinancialAccountsPage, true)
              .flatMap(_.set(WhyNoBankStatementPage, reason))
            )
            .success
            .value

        val expectedJson =
          """{
            |    "operationAndFundsCommon": {
            |     "accountPeriodEnd": "2512",
            |     "financialAccounts": true,
            |      "noBankStatements": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |      "noBankStatementsB": "11223344556677889900"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFundsCommon).asOpt.value mustBe
          Json.parse(expectedJson)
      }

      "convert the correct OperationAndFundsCommon object and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 11, 1).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(
            _.set(IsFinancialAccountsPage, true)
              .flatMap(_.set(WhyNoBankStatementPage, "the changes are less than 255 characters long"))
          )
          .success
          .value

        val expectedJson =
          """{
            |    "operationAndFundsCommon": {
            |     "accountPeriodEnd": "0111",
            |     "financialAccounts": true,
            |      "noBankStatements": "the changes are less than 255 characters long"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFundsCommon).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct OperationAndFundsCommon object and replace tabs and new line characters with spaces and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 11, 1).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(
            _.set(IsFinancialAccountsPage, true).flatMap(
              _.set(
                WhyNoBankStatementPage,
                "Hello I am writing this story to test replace tab \r\nand new line \r\ncharacters. If total length of the story is greater than 255 characters after replacing the tab and new line \r\ncharacters then split first 255 characters in part 1 \r\nand remaining in part 2"
              )
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |    "operationAndFundsCommon": {
            |     "accountPeriodEnd": "0111",
            |     "financialAccounts": true,
            |      "noBankStatements": "Hello I am writing this story to test replace tab  and new line  characters. If total length of the story is greater than 255 characters after replacing the tab and new line  characters then split first 255 characters in part 1  and remaining in part 2"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFundsCommon).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct OperationAndFundsCommon object with no bank statements" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 15).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, false))
          .success
          .value

        val expectedJson =
          """{
            |    "operationAndFundsCommon": {
            |     "accountPeriodEnd": "1501",
            |     "financialAccounts": false
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFundsCommon).asOpt.value mustBe Json
          .parse(expectedJson)
      }
    }

    "userAnswersToOtherCountriesOfOperation" must {

      "convert the correct OtherCountriesOfOperation object for all possible" in {

        val localUserAnswers = emptyUserAnswers
          .set(WhatCountryDoesTheCharityOperateInPage(0), "Country 1")
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "Country 2"))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), "Country 3"))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(3), "Country 4"))
          .flatMap(
            _.set(WhatCountryDoesTheCharityOperateInPage(4), "Country 5")
          )
          .success
          .value

        val expectedJson =
          """{
            |    "otherCountriesOfOperation": {
            |        "overseas1": "Country 1",
            |        "overseas2": "Country 2",
            |        "overseas3": "Country 3",
            |        "overseas4": "Country 4",
            |        "overseas5": "Country 5"
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOtherCountriesOfOperation).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct OtherCountriesOfOperation object for one value" in {

        val localUserAnswers =
          emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0), "Country 1").success.value

        val expectedJson =
          """{
            |    "otherCountriesOfOperation": {
            |        "overseas1": "Country 1"
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOtherCountriesOfOperation).asOpt.value mustBe Json
          .parse(expectedJson)
      }
    }

    "userAnswersToOperationAndFunds" must {

      "convert the correct OperationAndFunds object for all possible" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(
            _.set(
              WhyNoBankStatementPage,
              reason
            )
          )
          .flatMap(_.set(EstimatedIncomePage, BigDecimal.valueOf(2000.00)))
          .flatMap(_.set(ActualIncomePage, BigDecimal.valueOf(19999.99)))
          .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
          .flatMap(_.set(OperatingLocationPage, OperatingLocationOptions.values.toSet))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "Country 1"))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), "Country 2"))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), "Country 3"))
          .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(3), "Country 4"))
          .flatMap(
            _.set(WhatCountryDoesTheCharityOperateInPage(4), "Country 5")
          )
          .success
          .value

        val expectedJson =
          """
            |{"operationAndFunds":{"incomeReceivedToDate":19999.99,"northernIreland":true,"otherCountriesOfOperation":{"overseas1":"Country 1","overseas2":"Country 2","overseas3":"Country 3","overseas4":"Country 4","overseas5":"Country 5"},"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":true,"operationAndFundsCommon":{"noBankStatementsB":"11223344556677889900","financialAccounts":true,"accountPeriodEnd":"0101","noBankStatements":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643"},"englandAndWales":true,"estimatedGrossIncome":2000,"scotland":true,"overseas":true}}
            |""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe
          Json.parse(expectedJson)
      }

      "convert the correct OperationAndFunds object with all required values" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal(123)))
          .flatMap(_.set(ActualIncomePage, BigDecimal(121)))
          .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
          .flatMap(
            _.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England))
          )
          .success
          .value

        val expectedJson =
          """
            |{"operationAndFunds":{"incomeReceivedToDate":121,"northernIreland":false,"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":false,"operationAndFundsCommon":{"financialAccounts":true,"accountPeriodEnd":"0101"},"englandAndWales":true,"estimatedGrossIncome":123,"scotland":false,"overseas":false}}
            |""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with Wales, Scotland and Northern Ireland" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal(123)))
          .flatMap(_.set(ActualIncomePage, BigDecimal(121)))
          .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
          .flatMap(
            _.set(
              OperatingLocationPage,
              Set[OperatingLocationOptions](
                OperatingLocationOptions.Wales,
                OperatingLocationOptions.Scotland,
                OperatingLocationOptions.NorthernIreland
              )
            )
          )
          .success
          .value

        val expectedJson =
          """
            |{"operationAndFunds":{"incomeReceivedToDate":121,"northernIreland":true,"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":true,"operationAndFundsCommon":{"financialAccounts":true,"accountPeriodEnd":"0101"},"englandAndWales":true,"estimatedGrossIncome":123,"scotland":true,"overseas":false}}
            |""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with England, Scotland and Northern Ireland" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal(123)))
          .flatMap(_.set(ActualIncomePage, BigDecimal(121)))
          .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
          .flatMap(
            _.set(
              OperatingLocationPage,
              Set[OperatingLocationOptions](
                OperatingLocationOptions.England,
                OperatingLocationOptions.Scotland,
                OperatingLocationOptions.NorthernIreland
              )
            )
          )
          .success
          .value

        val expectedJson =
          """
            |{"operationAndFunds":{"incomeReceivedToDate":121,"northernIreland":true,"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":true,"operationAndFundsCommon":{"financialAccounts":true,"accountPeriodEnd":"0101"},"englandAndWales":true,"estimatedGrossIncome":123,"scotland":true,"overseas":false}}
            |""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with England and Northern Ireland" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal(123)))
          .flatMap(_.set(ActualIncomePage, BigDecimal(121)))
          .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
          .flatMap(
            _.set(
              OperatingLocationPage,
              Set[OperatingLocationOptions](OperatingLocationOptions.England, OperatingLocationOptions.NorthernIreland)
            )
          )
          .success
          .value

        val expectedJson =
          """
            |{"operationAndFunds":{"incomeReceivedToDate":121,"northernIreland":true,"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":false,"operationAndFundsCommon":{"financialAccounts":true,"accountPeriodEnd":"0101"},"englandAndWales":true,"estimatedGrossIncome":123,"scotland":false,"overseas":false}}
            |""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with England and Wales" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal(123)))
          .flatMap(_.set(ActualIncomePage, BigDecimal(121)))
          .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
          .flatMap(
            _.set(
              OperatingLocationPage,
              Set[OperatingLocationOptions](OperatingLocationOptions.England, OperatingLocationOptions.Wales)
            )
          )
          .success
          .value

        val expectedJson =
          """
            |{"operationAndFunds":{"incomeReceivedToDate":121,"northernIreland":false,"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":false,"operationAndFundsCommon":{"financialAccounts":true,"accountPeriodEnd":"0101"},"englandAndWales":true,"estimatedGrossIncome":123,"scotland":false,"overseas":false}}
            |""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe
          Json.parse(expectedJson)
      }
    }

    "userAnswersToCharity" must {

      "convert the correct Charity object all objects" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, "123456"))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "SC123456"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890"))
          .flatMap(_.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890")))
          .flatMap(_.set(IsCharityRegulatorPage, true))
          .flatMap(
            _.set(
              PublicBenefitsPage,
              reason
            )
          )
          .flatMap(_.set(CharitablePurposesPage, CharitablePurposes.values.toSet))
          .flatMap(
            _.set(
              CharitableObjectivesPage,
              reason
            )
          )
          .flatMap(
            _.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation).flatMap(
              _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1))
                .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
                .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
                .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
                .flatMap(
                  _.set(
                    SectionsChangedGoverningDocumentPage,
                    reason
                  )
                )
            )
          )
          .flatMap(
            _.set(AccountingPeriodEndDatePage, MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(
              MongoDateTimeFormats.localDayMonthWrite
            ).flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(
                _.set(
                  WhyNoBankStatementPage,
                  reason
                )
              )
              .flatMap(_.set(EstimatedIncomePage, BigDecimal.valueOf(2000.00)))
              .flatMap(_.set(ActualIncomePage, BigDecimal.valueOf(19999.99)))
              .flatMap(_.set(FundRaisingPage, FundRaisingOptions.values.toSet))
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

        val expectedJson =
          """
            |{"charityRegistration":{"charity":{"operationAndFunds":{"incomeReceivedToDate":19999.99,"northernIreland":true,"otherCountriesOfOperation":{"overseas1":"Country 1","overseas2":"Country 2","overseas3":"Country 3","overseas4":"Country 4","overseas5":"Country 5"},"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":true,"operationAndFundsCommon":{"noBankStatementsB":"11223344556677889900","financialAccounts":true,"accountPeriodEnd":"0101","noBankStatements":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643"},"englandAndWales":true,"estimatedGrossIncome":2000,"scotland":true,"overseas":true},"charityOrganisation":{"registeredRegulator":true,"regulator":{"otherRegulatorName":"Other Regulator Name","ccni":true,"oscr":true,"ccewRegistrationNumber":"123456","oscrRegistrationNumber":"SC123456","ccew":true,"otherRegulator":true,"ccniRegistrationNumber":"ABCDEFGHIJ1234567890","otherRegulatorRegistrationNumber":"12345678901234567890"}},"orgPurpose":{"charitablePurposes":{"other":true,"education":true,"animalWelfare":true,"humanRights":true,"healthOrSavingOfLives":true,"religion":true,"artsCultureOrScience":true,"amateurSport":true,"citizenshipOrCommunityDevelopment":true,"environmentalProtection":true,"reliefOfPoverty":true,"armedForcesOfTheCrown":true,"reliefOfYouthAge":true},"charitableObjectives":{"objectivesB":"11223344556677889900","objectivesA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643"},"publicBenefit":{"publicBenefitB":"11223344556677889900","publicBenefitA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643"}},"aboutOrganisation":{"governingApprovedDoc":true,"governingApprovedChangesB":"11223344556677889900","documentEnclosed":"2","aboutOrgCommon":{"otherDocument":"Other Documents for Charity","effectiveDate":"2014-07-01"},"governingApprovedChanges":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643","governingApprovedWords":true}}}}
            |""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharity).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct Charity object with minimum objects" in {

        val localUserAnswers = emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold))
          .flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
          .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)))
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
              .flatMap(_.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England)))
              .flatMap(
                _.set(
                  PublicBenefitsPage,
                  "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
                )
              )
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

        val expectedJson =
          """
            | {"charityRegistration":{"charity":{"operationAndFunds":{"incomeReceivedToDate":121,"northernIreland":false,"otherAreaOperation":true,"futureFunds":"other, donations, tradingSubsidiaries, tradingIncome, fundraising, investmentIncome, grants, membershipSubscriptions","ukWide":false,"operationAndFundsCommon":{"financialAccounts":true,"accountPeriodEnd":"0101"},"englandAndWales":true,"estimatedGrossIncome":123,"scotland":false,"overseas":false},"charityOrganisation":{"nonRegReason":"1","registeredRegulator":false},"orgPurpose":{"charitablePurposes":{"other":false,"education":false,"animalWelfare":true,"humanRights":false,"healthOrSavingOfLives":false,"religion":false,"artsCultureOrScience":false,"amateurSport":true,"citizenshipOrCommunityDevelopment":false,"environmentalProtection":false,"reliefOfPoverty":false,"armedForcesOfTheCrown":false,"reliefOfYouthAge":false},"charitableObjectives":{"objectivesA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"},"publicBenefit":{"publicBenefitA":"qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"}},"aboutOrganisation":{"governingApprovedDoc":false,"documentEnclosed":"2","aboutOrgCommon":{"otherDocument":"Other Documents for Charity","effectiveDate":"2014-07-01"},"governingApprovedWords":true}}}}
            |""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharity).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }
  }
}
