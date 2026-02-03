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

import models.operations.CharitablePurposes.{AmateurSport, AnimalWelfare}
import models.operations.{CharitablePurposes, FundRaisingOptions, OperatingLocationOptions}
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Other, Scottish}
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import models.{CharityOtherRegulatorDetails, MongoDateTimeFormats, UserAnswers}
import pages.operationsAndFunds.*
import pages.regulatorsAndDocuments.*
import play.api.libs.json.Json

import java.time.{LocalDate, MonthDay}
import scala.collection.immutable.SortedSet

class CharityTransformerSpec extends CharityTransformerConstants {

  private val reason: String =
    "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre" +
      "664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxa" +
      "sxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"

  private val jsonTransformer: CharityTransformer = new CharityTransformer

  "CharityTransformer" when {

    "userAnswersToRegulator" must {

      "convert the correct Regulator object for all regulator" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, charityCommissionRegistrationNumber))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, scottishRegulatorRegistrationNumber))
          .flatMap(_.set(NIRegulatorRegNumberPage, niRegulatorRegistrationNumber))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              charityRegulatorDetails
            )
          )
          .success
          .value

        val expectedJson =
          s"""{
            |      "regulator": {
            |        "ccew": true,
            |        "ccewRegistrationNumber": "$charityCommissionRegistrationNumber",
            |        "oscr": true,
            |        "oscrRegistrationNumber": "$scottishRegulatorRegistrationNumber",
            |        "ccni": true,
            |        "ccniRegistrationNumber": "$niRegulatorRegistrationNumber",
            |        "otherRegulator": true,
            |        "otherRegulatorName": "$charityRegulatorName",
            |        "otherRegulatorRegistrationNumber": "$chartyRegulatorRegistrationNumber"
            |      }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToRegulator).asOpt.value mustBe
          Json.parse(expectedJson)
      }

      "convert the correct Regulator object without other regulator" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, charityCommissionRegistrationNumber))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, scottishRegulatorRegistrationNumber))
          .flatMap(
            _.set(NIRegulatorRegNumberPage, niRegulatorRegistrationNumber)
          )
          .success
          .value

        val expectedJson =
          s"""{
            |      "regulator": {
            |        "ccew": true,
            |        "ccewRegistrationNumber": "$charityCommissionRegistrationNumber",
            |        "oscr": true,
            |        "oscrRegistrationNumber": "$scottishRegulatorRegistrationNumber",
            |        "ccni": true,
            |        "ccniRegistrationNumber": "$niRegulatorRegistrationNumber"
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
              charityRegulatorDetails
            )
          )
          .success
          .value

        val expectedJson =
          s"""{
            |      "regulator": {
            |        "otherRegulator": true,
            |        "otherRegulatorName": "$charityRegulatorName",
            |        "otherRegulatorRegistrationNumber": "$chartyRegulatorRegistrationNumber"
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
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, charityCommissionRegistrationNumber))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, scottishRegulatorRegistrationNumber))
          .flatMap(_.set(NIRegulatorRegNumberPage, niRegulatorRegistrationNumber))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              charityRegulatorDetails
            ).flatMap(
              _.set(IsCharityRegulatorPage, true)
            )
          )
          .success
          .value

        val expectedJson =
          s"""{
            |"charityOrganisation": {
            |      "registeredRegulator": true,
            |      "regulator": {
            |        "ccew": true,
            |        "ccewRegistrationNumber": "$charityCommissionRegistrationNumber",
            |        "oscr": true,
            |        "oscrRegistrationNumber": "$scottishRegulatorRegistrationNumber",
            |        "ccni": true,
            |        "ccniRegistrationNumber": "$niRegulatorRegistrationNumber",
            |        "otherRegulator": true,
            |        "otherRegulatorName": "$charityRegulatorName",
            |        "otherRegulatorRegistrationNumber": "$chartyRegulatorRegistrationNumber"
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

      "convert the correct Regulator object other and reason" when {
        val reasonWithNoInvalidCharactersBelow100: String     = "notRegisteredReason"
        val reasonWithNoInvalidCharactersAt100: String        =
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut elementum accumsan quam, et turpis duis."
        val reasonWithInvalidCharactersBelow100: String       = "a\r\nb"
        val updatedReasonForInvalidCharactersBelow100: String = "a b"
        val reasonWithInvalidCharactersAt100: String          =
          "Lorem ipsum dolor sit amet, consectetur adipiscing.\r\nUt elementum accumsan quam, et turpis lectus."
        val updatedReasonForInvalidCharactersAt100: String    =
          "Lorem ipsum dolor sit amet, consectetur adipiscing. Ut elementum accumsan quam, et turpis lectus."

        Seq(
          (reasonWithNoInvalidCharactersBelow100, "less than 100"),
          (reasonWithNoInvalidCharactersAt100, "equal to 100")
        ).foreach { case (reason, scenario) =>
          s"no invalid characters are present in a string with character length $scenario" in {
            val localUserAnswers: UserAnswers = emptyUserAnswers
              .set(IsCharityRegulatorPage, false)
              .flatMap(
                _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other)
                  .flatMap(_.set(WhyNotRegisteredWithCharityPage, reason))
              )
              .success
              .value

            val expectedJson: String =
              s"""
                 |{
                 |    "charityOrganisation": {
                 |        "registeredRegulator": false,
                 |        "nonRegReason": "7",
                 |        "otherReason": "$reason"
                 |    }
                 |}
            """.stripMargin

            localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json
              .parse(
                expectedJson
              )
          }
        }

        Seq(
          (reasonWithInvalidCharactersBelow100, updatedReasonForInvalidCharactersBelow100, "less than 100"),
          (reasonWithInvalidCharactersAt100, updatedReasonForInvalidCharactersAt100, "equal to 100")
        ).foreach { case (reason, updatedReason, scenario) =>
          s"the invalid characters in a string with character length $scenario are replaced with spaces" in {
            val localUserAnswers: UserAnswers = emptyUserAnswers
              .set(IsCharityRegulatorPage, false)
              .flatMap(
                _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other)
                  .flatMap(_.set(WhyNotRegisteredWithCharityPage, reason))
              )
              .success
              .value

            val expectedJson: String =
              s"""
                 |{
                 |    "charityOrganisation": {
                 |        "registeredRegulator": false,
                 |        "nonRegReason": "7",
                 |        "otherReason": "$updatedReason"
                 |    }
                 |}
              """.stripMargin

            localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json
              .parse(
                expectedJson
              )
          }
        }
      }

      "convert the correct Regulator object other and remove other reason" when {
        val reasonWithNoInvalidCharactersAbove100: String =
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla bibendum sodales varius. " +
            "Sed nulla nibh, rutrum vitae erat ut, dapibus tristique tortor. In id mattis libero, vitae gravida felis. " +
            "Sed sit amet dictum sem. Pellentesque non blandit eros. Proin cursus nisi sed interdum dictum."
        val reasonWithInvalidCharactersAbove100: String   =
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla bibendum sodales varius.\r\n" +
            "Sed nulla nibh, rutrum vitae erat ut, dapibus tristique tortor. In id mattis libero, vitae gravida felis.\r\n" +
            "Sed sit amet dictum sem. Pellentesque non blandit eros. Proin cursus nisi sed interdum dictum."

        Seq(
          (reasonWithNoInvalidCharactersAbove100, "no invalid characters"),
          (reasonWithInvalidCharactersAbove100, "invalid characters")
        ).foreach { case (reason, scenario) =>
          s"$scenario are present in a string with character length greater than 100" in {
            val localUserAnswers: UserAnswers = emptyUserAnswers
              .set(IsCharityRegulatorPage, false)
              .flatMap(
                _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other)
                  .flatMap(_.set(WhyNotRegisteredWithCharityPage, reason))
              )
              .success
              .value

            val expectedJson: String =
              """
                |{
                |    "charityOrganisation": {
                |        "registeredRegulator": false,
                |        "nonRegReason": "7"
                |    }
                |}
              """.stripMargin

            localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json
              .parse(
                expectedJson
              )
          }
        }
      }
    }

    "userAnswersToAboutOrganisationCommon" must {

      "convert the correct AboutOrganisationCommon object" in {

        val localUserAnswers = emptyUserAnswers
          .set(GoverningDocumentNamePage, "Other Documents for Charity")
          .flatMap(
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01"))
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
          emptyUserAnswers
            .set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01"))
            .success
            .value

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
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01"))
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

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisation).asOpt.value mustBe
          Json.parse(expectedJson)
      }

      "convert the correct AboutOrganisation object and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(SelectGoverningDocumentPage, MemorandumArticlesAssociation)
          .flatMap(
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01"))
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
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01"))
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
            _.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01"))
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

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-12-25")))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(
            _.set(IsFinancialAccountsPage, true).flatMap(
              _.set(
                WhyNoBankStatementPage,
                reason
              )
            )
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

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFundsCommon).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct OperationAndFundsCommon object and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-11-01")))(
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
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-11-01")))(
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
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-01-15")))(
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
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-01-01")))(
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
          .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
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
          """{
            |    "operationAndFunds": {
            |      "operationAndFundsCommon": {
            |       "accountPeriodEnd": "0101",
            |       "financialAccounts": true,
            |        "noBankStatements": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |        "noBankStatementsB": "11223344556677889900"
            |      },
            |      "estimatedGrossIncome": 2000.00,
            |      "incomeReceivedToDate": 19999.99,
            |      "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
            |      "otherAreaOperation": true,
            |      "englandAndWales": true,
            |      "scotland": true,
            |      "northernIreland": true,
            |      "ukWide": true,
            |      "overseas": true,
            |      "otherCountriesOfOperation": {
            |        "overseas1": "Country 1",
            |        "overseas2": "Country 2",
            |        "overseas3": "Country 3",
            |        "overseas4": "Country 4",
            |        "overseas5": "Country 5"
            |        }
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with all required values" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-01-01")))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
          .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
          .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
          .flatMap(
            _.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England))
          )
          .success
          .value

        val expectedJson =
          """{
            |    "operationAndFunds": {
            |      "operationAndFundsCommon": {
            |       "accountPeriodEnd": "0101",
            |       "financialAccounts": true
            |      },
            |     "estimatedGrossIncome": 123.00,
            |     "incomeReceivedToDate": 121.00,
            |      "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
            |      "otherAreaOperation": true,
            |      "englandAndWales": true,
            |      "scotland": false,
            |      "northernIreland": false,
            |      "ukWide": false,
            |      "overseas": false
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with Wales, Scotland and Northern Ireland" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-01-01")))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
          .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
          .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
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
          """{
            |    "operationAndFunds": {
            |      "operationAndFundsCommon": {
            |       "accountPeriodEnd": "0101",
            |       "financialAccounts": true
            |      },
            |     "estimatedGrossIncome": 123.00,
            |     "incomeReceivedToDate": 121.00,
            |      "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
            |      "otherAreaOperation": true,
            |      "englandAndWales": true,
            |      "scotland": true,
            |      "northernIreland": true,
            |      "ukWide": true,
            |      "overseas": false
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with England, Scotland and Northern Ireland" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            AccountingPeriodEndDatePage,
            MonthDay.from(LocalDate.parse("2020-01-01"))
          )(MongoDateTimeFormats.localDayMonthWrite)
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
          .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
          .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
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
          """{
            |    "operationAndFunds": {
            |      "operationAndFundsCommon": {
            |       "accountPeriodEnd": "0101",
            |       "financialAccounts": true
            |      },
            |     "estimatedGrossIncome": 123.00,
            |     "incomeReceivedToDate": 121.00,
            |      "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
            |      "otherAreaOperation": true,
            |      "englandAndWales": true,
            |      "scotland": true,
            |      "northernIreland": true,
            |      "ukWide": true,
            |      "overseas": false
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with England and Northern Ireland" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-01-01")))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
          .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
          .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
          .flatMap(
            _.set(
              OperatingLocationPage,
              Set[OperatingLocationOptions](OperatingLocationOptions.England, OperatingLocationOptions.NorthernIreland)
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |    "operationAndFunds": {
            |      "operationAndFundsCommon": {
            |       "accountPeriodEnd": "0101",
            |       "financialAccounts": true
            |      },
            |     "estimatedGrossIncome": 123.00,
            |     "incomeReceivedToDate": 121.00,
            |      "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
            |      "otherAreaOperation": true,
            |      "englandAndWales": true,
            |      "scotland": false,
            |      "northernIreland": true,
            |      "ukWide": false,
            |      "overseas": false
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OperationAndFunds object with England and Wales" in {

        val localUserAnswers = emptyUserAnswers
          .set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-01-01")))(
            MongoDateTimeFormats.localDayMonthWrite
          )
          .flatMap(_.set(IsFinancialAccountsPage, true))
          .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
          .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
          .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
          .flatMap(
            _.set(
              OperatingLocationPage,
              Set[OperatingLocationOptions](OperatingLocationOptions.England, OperatingLocationOptions.Wales)
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |    "operationAndFunds": {
            |      "operationAndFundsCommon": {
            |       "accountPeriodEnd": "0101",
            |       "financialAccounts": true
            |      },
            |     "estimatedGrossIncome": 123.00,
            |     "incomeReceivedToDate": 121.00,
            |      "futureFunds":"donations, fundraising, grants, membershipSubscriptions, tradingIncome, tradingSubsidiaries, investmentIncome, other",
            |      "otherAreaOperation": true,
            |      "englandAndWales": true,
            |      "scotland": false,
            |      "northernIreland": false,
            |      "ukWide": false,
            |      "overseas": false
            |    }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToCharitableObjectives" must {

      "convert the correct CharitableObjectives" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            CharitableObjectivesPage,
            reason
          )
          .success
          .value

        val expectedJson =
          """{
            |   "charitableObjectives": {
            |      "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |      "objectivesB": "11223344556677889900"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitableObjectives).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct CharitableObjectives with few selections" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            CharitableObjectivesPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
          )
          .success
          .value

        val expectedJson =
          """{
            |   "charitableObjectives": {
            |      "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitableObjectives).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct CharitableObjectives and replace tabs and new line characters with spaces and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            CharitableObjectivesPage,
            "Hello I am writing this story to test replace tab \r\nand new line \r\ncharacters. " +
              "If total length of the story is greater than 255 characters after replacing the tab and new line \r\ncharacters " +
              "then split first 255 characters in part 1 \r\nand remaining in part 2"
          )
          .success
          .value

        val expectedJson =
          """{
            |   "charitableObjectives": {
            |      "objectivesA": "Hello I am writing this story to test replace tab  and new line  characters. If total length of the story is greater than 255 characters after replacing the tab and new line  characters then split first 255 characters in part 1  and remaining in part 2"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitableObjectives).asOpt.value mustBe Json
          .parse(expectedJson)
      }
    }

    "userAnswersToCharitablePurposes" must {

      "convert the correct CharitablePurposes object" in {

        val localUserAnswers =
          emptyUserAnswers.set(CharitablePurposesPage, CharitablePurposes.values.toSet).success.value

        val expectedJson =
          """{
            |   "charitablePurposes": {
            |      "reliefOfPoverty": true,
            |      "education": true,
            |      "religion": true,
            |      "healthOrSavingOfLives": true,
            |      "citizenshipOrCommunityDevelopment": true,
            |      "artsCultureOrScience": true,
            |      "amateurSport": true,
            |      "humanRights": true,
            |      "environmentalProtection": true,
            |      "reliefOfYouthAge": true,
            |      "animalWelfare": true,
            |      "armedForcesOfTheCrown": true,
            |      "other": true
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitablePurposes).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct CharitablePurposes object with few selections" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare))
          .success
          .value

        val expectedJson =
          """{
            |   "charitablePurposes": {
            |      "reliefOfPoverty": false,
            |      "education": false,
            |      "religion": false,
            |      "healthOrSavingOfLives": false,
            |      "citizenshipOrCommunityDevelopment": false,
            |      "artsCultureOrScience": false,
            |      "amateurSport": true,
            |      "humanRights": false,
            |      "environmentalProtection": false,
            |      "reliefOfYouthAge": false,
            |      "animalWelfare": true,
            |      "armedForcesOfTheCrown": false,
            |      "other": false
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitablePurposes).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToPublicBenefit" must {

      "convert the correct PublicBenefit object" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            PublicBenefitsPage,
            reason
          )
          .success
          .value

        val expectedJson =
          """{
            |   "publicBenefit": {
            |      "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |      "publicBenefitB": "11223344556677889900"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToPublicBenefit).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct PublicBenefit object with few selections" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            PublicBenefitsPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
          )
          .success
          .value

        val expectedJson =
          """{
            |   "publicBenefit": {
            |      "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToPublicBenefit).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct PublicBenefit object and replace tabs and new line characters with spaces and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            PublicBenefitsPage,
            "Hello I am writing this story to test replace tab \r\nand new line \r\ncharacters. If total length of the story is greater than 255 characters after replacing the tab and new line \r\ncharacters then split first 255 characters in part 1 \r\nand remaining in part 2"
          )
          .success
          .value

        val expectedJson =
          """{
            |   "publicBenefit": {
            |      "publicBenefitA": "Hello I am writing this story to test replace tab  and new line  characters. If total length of the story is greater than 255 characters after replacing the tab and new line  characters then split first 255 characters in part 1  and remaining in part 2"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToPublicBenefit).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToOrgPurpose" must {

      "convert the correct OrgPurpose object all objects" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            PublicBenefitsPage,
            reason
          )
          .flatMap(_.set(CharitablePurposesPage, CharitablePurposes.values.toSet))
          .flatMap(
            _.set(
              CharitableObjectivesPage,
              reason
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |"orgPurpose": {
            |   "charitableObjectives": {
            |      "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |      "objectivesB": "11223344556677889900"
            |   },
            |   "charitablePurposes": {
            |      "reliefOfPoverty": true,
            |      "education": true,
            |      "religion": true,
            |      "healthOrSavingOfLives": true,
            |      "citizenshipOrCommunityDevelopment": true,
            |      "artsCultureOrScience": true,
            |      "amateurSport": true,
            |      "humanRights": true,
            |      "environmentalProtection": true,
            |      "reliefOfYouthAge": true,
            |      "animalWelfare": true,
            |      "armedForcesOfTheCrown": true,
            |      "other": true
            |   },
            |   "publicBenefit": {
            |        "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |        "publicBenefitB": "11223344556677889900"
            |   }
            |  }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOrgPurpose).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct OrgPurpose object with minimum objects" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            PublicBenefitsPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
          )
          .flatMap(_.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare)))
          .flatMap(
            _.set(
              CharitableObjectivesPage,
              "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            )
          )
          .success
          .value

        val expectedJson =
          """{
            |"orgPurpose": {
            |   "charitableObjectives": {
            |      "objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            |   },
            |   "charitablePurposes": {
            |      "reliefOfPoverty": false,
            |      "education": false,
            |      "religion": false,
            |      "healthOrSavingOfLives": false,
            |      "citizenshipOrCommunityDevelopment": false,
            |      "artsCultureOrScience": false,
            |      "amateurSport": true,
            |      "humanRights": false,
            |      "environmentalProtection": false,
            |      "reliefOfYouthAge": false,
            |      "animalWelfare": true,
            |      "armedForcesOfTheCrown": false,
            |      "other": false
            |   },
            |   "publicBenefit": {
            |        "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            |   }
            |  }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOrgPurpose).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToCharity" must {

      "convert the correct Charity object all objects" in {

        val localUserAnswers = emptyUserAnswers
          .set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other))
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
              _.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01"))
                .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
                .flatMap(_.set(IsApprovedGoverningDocumentPage, true))
                .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
                .flatMap(_.set(SectionsChangedGoverningDocumentPage, reason))
            )
          )
          .flatMap(
            _.set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-01-01")))(
              MongoDateTimeFormats.localDayMonthWrite
            ).flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(WhyNoBankStatementPage, reason))
              .flatMap(_.set(EstimatedIncomePage, BigDecimal.valueOf(2000.00)))
              .flatMap(_.set(ActualIncomePage, BigDecimal.valueOf(19999.99)))
              .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
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
            |    }
            |  }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharity).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct Charity object with minimum objects" in {

        val localUserAnswers = emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold))
          .flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
          .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01")))
          .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
          .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
          .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
          .flatMap(
            _.set(AccountingPeriodEndDatePage, MonthDay.from(LocalDate.parse("2020-01-01")))(
              MongoDateTimeFormats.localDayMonthWrite
            ).flatMap(_.set(IsFinancialAccountsPage, true))
              .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
              .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
              .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
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
          """{
            |  "charityRegistration": {
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
            |    }
            |  }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharity).asOpt.value mustBe
          Json.parse(expectedJson)
      }
    }
  }
}
