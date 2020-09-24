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

class CharityTransformerSpec extends SpecBase with CharityTransformerTodoPages {
  //scalastyle:off magic.number
  val jsonTransformer: CharityTransformer = new CharityTransformer

  "CharityTransformer" when {

    "userAnswersToRegulator" must {

      "convert the correct Regulator object for all regulator" in {

        val localUserAnswers = emptyUserAnswers.set(CharityRegulatorPage,Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other)).flatMap(
          _.set(CharityCommissionRegistrationNumberPage, "123456")).flatMap(
          _.set(ScottishRegulatorRegNumberPage, "SC123456")).flatMap(
          _.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890")).flatMap(
          _.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890"))
        ).success.value

        val expectedJson =
          """{
            |			"regulator": {
            |				"ccew": true,
            |				"ccewRegistrationNumber": "123456",
            |				"oscr": true,
            |				"oscrRegistrationNumber": "SC123456",
            |				"ccni": true,
            |				"ccniRegistrationNumber": "ABCDEFGHIJ1234567890",
            |				"otherRegulator": true,
            |				"otherRegulatorName": "Other Regulator Name",
            |				"otherRegulatorRegistrationNumber": "12345678901234567890"
            |			}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToRegulator).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct Regulator object without other regulator" in {

        val localUserAnswers = emptyUserAnswers.set(CharityRegulatorPage,Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland)).flatMap(
          _.set(CharityCommissionRegistrationNumberPage, "123456")).flatMap(
          _.set(ScottishRegulatorRegNumberPage, "SC123456")).flatMap(
          _.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890")
        ).success.value

        val expectedJson =
          """{
            |			"regulator": {
            |				"ccew": true,
            |				"ccewRegistrationNumber": "123456",
            |				"oscr": true,
            |				"oscrRegistrationNumber": "SC123456",
            |				"ccni": true,
            |				"ccniRegistrationNumber": "ABCDEFGHIJ1234567890"
            |			}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToRegulator).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct Regulator object only other" in {

        val localUserAnswers = emptyUserAnswers.set(CharityRegulatorPage,Set[CharityRegulator]( Other)).flatMap(
          _.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890"))
        ).success.value

        val expectedJson =
          """{
            |			"regulator": {
            |				"otherRegulator": true,
            |				"otherRegulatorName": "Other Regulator Name",
            |				"otherRegulatorRegistrationNumber": "12345678901234567890"
            |			}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToRegulator).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToCharityOrganisation" must {

      "convert the correct CharityOrganisation when regulators are defined" in {

        val localUserAnswers = emptyUserAnswers.set(CharityRegulatorPage,Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other)).flatMap(
          _.set(CharityCommissionRegistrationNumberPage, "123456")).flatMap(
          _.set(ScottishRegulatorRegNumberPage, "SC123456")).flatMap(
          _.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890")).flatMap(
          _.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890")).flatMap(
            _.set(IsCharityRegulatorPage, true)
          )
        ).success.value

        val expectedJson =
          """{
            |"charityOrganisation": {
            |			"registeredRegulator": true,
            |			"regulator": {
            |				"ccew": true,
            |				"ccewRegistrationNumber": "123456",
            |				"oscr": true,
            |				"oscrRegistrationNumber": "SC123456",
            |				"ccni": true,
            |				"ccniRegistrationNumber": "ABCDEFGHIJ1234567890",
            |				"otherRegulator": true,
            |				"otherRegulatorName": "Other Regulator Name",
            |				"otherRegulatorRegistrationNumber": "12345678901234567890"
            |			}
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct Regulator object without regulators" in {

        val localUserAnswers = emptyUserAnswers.set(IsCharityRegulatorPage, false).flatMap(
          _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)
        ).success.value

        val expectedJson =
          """{
            |"charityOrganisation": {
            |			"registeredRegulator": false,
            |			"nonRegReason": "1"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct Regulator object other and reason" in {

        val localUserAnswers = emptyUserAnswers.set(IsCharityRegulatorPage, false).flatMap(
          _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other).flatMap(
            _.set(WhyNotRegisteredWithCharityPage, "notRegisteredReason"))
        ).success.value

        val expectedJson =
          """{
            |"charityOrganisation": {
            |			"registeredRegulator": false,
            |			"nonRegReason": "7",
            |			"otherReason": "notRegisteredReason"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharityOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToAboutOrganisationCommon" must {

      "convert the correct AboutOrganisationCommon object" in {

        val localUserAnswers = emptyUserAnswers.set(GoverningDocumentNamePage, "Other Documents for Charity").flatMap(
          _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1))
        ).success.value

        val expectedJson =
          """{
            |	  "aboutOrgCommon": {
            |				"otherDocument": "Other Documents for Charity",
            |				"effectiveDate": "2014-07-01"
            |	 }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisationCommon).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct AboutOrganisationCommon object when other is not defined" in {

        val localUserAnswers = emptyUserAnswers.set(WhenGoverningDocumentApprovedPage,
          LocalDate.of(2014, 7, 1)).success.value

        val expectedJson =
          """{
            |	  "aboutOrgCommon": {
            |				"effectiveDate": "2014-07-01"
            |	 }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisationCommon).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToAboutOrganisation" must {

      "convert the correct AboutOrganisation object and changes are >255 characters long" in {

        val localUserAnswers = emptyUserAnswers.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation).flatMap(
          _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)).flatMap(
            _.set(GoverningDocumentNamePage, "Other Documents for Charity")).flatMap(
            _.set(IsApprovedGoverningDocumentPage, true)).flatMap(
            _.set(HasCharityChangedPartsOfGoverningDocumentPage, false)).flatMap(
            _.set(SectionsChangedGoverningDocumentPage,
              "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"))
        ).success.value

        val expectedJson =
          """{
            |		"aboutOrganisation": {
            |			"aboutOrgCommon": {
            |				"otherDocument": "Other Documents for Charity",
            |				"effectiveDate": "2014-07-01"
            |			},
            |			"documentEnclosed": "1",
            |			"governingApprovedDoc": true,
            |			"governingApprovedWords": false,
            |			"governingApprovedChanges": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |			"governingApprovedChangesB": "11223344556677889900"
            |		}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct AboutOrganisation object and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation).flatMap(
          _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)).flatMap(
            _.set(GoverningDocumentNamePage, "Other Documents for Charity")).flatMap(
            _.set(IsApprovedGoverningDocumentPage, true)).flatMap(
            _.set(HasCharityChangedPartsOfGoverningDocumentPage, false)).flatMap(
            _.set(SectionsChangedGoverningDocumentPage,
              "changes are shorter than 255 characters"))
        ).success.value

        val expectedJson =
          """{
            |		"aboutOrganisation": {
            |			"aboutOrgCommon": {
            |				"otherDocument": "Other Documents for Charity",
            |				"effectiveDate": "2014-07-01"
            |			},
            |			"documentEnclosed": "1",
            |			"governingApprovedDoc": true,
            |			"governingApprovedWords": false,
            |			"governingApprovedChanges": "changes are shorter than 255 characters"
            |		}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct AboutOrganisation object when document is not approved and changes are not defined" in {

        val localUserAnswers = emptyUserAnswers.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation).flatMap(
          _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)).flatMap(
            _.set(GoverningDocumentNamePage, "Other Documents for Charity")).flatMap(
            _.set(IsApprovedGoverningDocumentPage, false)).flatMap(
            _.set(HasCharityChangedPartsOfGoverningDocumentPage, false))).success.value

        val expectedJson =
          """{
            |		"aboutOrganisation": {
            |			"aboutOrgCommon": {
            |				"otherDocument": "Other Documents for Charity",
            |				"effectiveDate": "2014-07-01"
            |			},
            |			"documentEnclosed": "1",
            |			"governingApprovedDoc": false,
            |			"governingApprovedWords": false
            |		}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToAboutOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToOperationAndFundsCommon" must {

      "convert the correct OperationAndFundsCommon object and changes are >255 characters long" in {

        val localUserAnswers = emptyUserAnswers.set(AccountingPeriodEndDatePage,
          MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
          _.set(IsFinancialAccountsPage, true).flatMap(
            _.set(NoBankStatementPage,
              "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"))
        ).success.value

        val expectedJson =
          """{
            |		"operationAndFundsCommon": {
            |     "accountPeriodEnd": "0101",
            |     "financialAccounts": true,
            |			"noBankStatements": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |			"noBankStatementsB": "11223344556677889900"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFundsCommon).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct OperationAndFundsCommon object and changes are <255 characters long" in {

        val localUserAnswers = emptyUserAnswers.set(AccountingPeriodEndDatePage,
          MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
          _.set(IsFinancialAccountsPage, true).flatMap(
            _.set(NoBankStatementPage,
              "the changes are less than 255 characters long"))
        ).success.value

        val expectedJson =
          """{
            |		"operationAndFundsCommon": {
            |     "accountPeriodEnd": "0101",
            |     "financialAccounts": true,
            |			"noBankStatements": "the changes are less than 255 characters long"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFundsCommon).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct OperationAndFundsCommon object with no bank statements" in {

        val localUserAnswers = emptyUserAnswers.set(AccountingPeriodEndDatePage,
          MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
          _.set(IsFinancialAccountsPage, false)).success.value

        val expectedJson =
          """{
            |		"operationAndFundsCommon": {
            |     "accountPeriodEnd": "0101",
            |     "financialAccounts": false
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFundsCommon).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToOtherCountriesOfOperation" must {

      "convert the correct OtherCountriesOfOperation object for all possible" in {

        val localUserAnswers = emptyUserAnswers.set(OverseasCountryPage(0), "Country 1").flatMap(
          _.set(OverseasCountryPage(1), "Country 2")).flatMap(
          _.set(OverseasCountryPage(2), "Country 3")).flatMap(
          _.set(OverseasCountryPage(3), "Country 4")).flatMap(
          _.set(OverseasCountryPage(4), "Country 5")
        ).success.value

        val expectedJson =
          """{
            |		"otherCountriesOfOperation": {
            |				"overseas1": "Country 1",
            |				"overseas2": "Country 2",
            |				"overseas3": "Country 3",
            |				"overseas4": "Country 4",
            |				"overseas5": "Country 5"
            |		}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOtherCountriesOfOperation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct OtherCountriesOfOperation object for one value" in {

        val localUserAnswers = emptyUserAnswers.set(OverseasCountryPage(0), "Country 1").success.value

        val expectedJson =
          """{
            |		"otherCountriesOfOperation": {
            |				"overseas1": "Country 1"
            |		}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOtherCountriesOfOperation).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToOperationAndFunds" must {

      "convert the correct OperationAndFunds object for all possible" in {

        val localUserAnswers =  emptyUserAnswers.set(AccountingPeriodEndDatePage,
          MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
          _.set(IsFinancialAccountsPage, true)).flatMap(
          _.set(NoBankStatementPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900")
        ).flatMap(
          _.set(EstimatedIncomePage, BigDecimal.valueOf(2000.00))).flatMap(
          _.set(ActualIncomePage, BigDecimal.valueOf(19999.99))).flatMap(
          _.set(FundRaisingPage, FundRaisingOptions.values.toSet)).flatMap(
          _.set(OperatingLocationPage, OperatingLocationOptions.values.toSet)).flatMap(
          _.set(OverseasCountryPage(0), "Country 1")).flatMap(
          _.set(OverseasCountryPage(1), "Country 2")).flatMap(
          _.set(OverseasCountryPage(2), "Country 3")).flatMap(
          _.set(OverseasCountryPage(3), "Country 4")).flatMap(
          _.set(OverseasCountryPage(4), "Country 5")
        ).success.value

        val expectedJson =
          """{
            |		"operationAndFunds": {
            |			"operationAndFundsCommon": {
            |       "accountPeriodEnd": "0101",
            |       "financialAccounts": true,
            |			  "noBankStatements": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |			  "noBankStatementsB": "11223344556677889900"
            |			},
            |			"estimatedGrossIncome": 2000.00,
            |			"incomeReceivedToDate": 19999.99,
            |			"futureFunds": "other, donations, tradingSubsidiaries, tradingIncome, fundraising, grants, membershipSubscriptions, investmentIncome",
            |			"otherAreaOperation": true,
            |			"englandAndWales": true,
            |			"scotland": true,
            |			"northernIreland": true,
            |			"ukWide": true,
            |			"overseas": true,
            |			"otherCountriesOfOperation": {
            |				"overseas1": "Country 1",
            |				"overseas2": "Country 2",
            |				"overseas3": "Country 3",
            |				"overseas4": "Country 4",
            |				"overseas5": "Country 5"
            |				}
            |		}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct OperationAndFunds object with all required values" in {

        val localUserAnswers =  emptyUserAnswers.set(AccountingPeriodEndDatePage,
          MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
          _.set(IsFinancialAccountsPage, true)).flatMap(
          _.set(FundRaisingPage, FundRaisingOptions.values.toSet)).flatMap(
          _.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.EnglandAndWales))
        ).success.value

        val expectedJson =
          """{
            |		"operationAndFunds": {
            |			"operationAndFundsCommon": {
            |       "accountPeriodEnd": "0101",
            |       "financialAccounts": true
            |			},
            |			"futureFunds": "other, donations, tradingSubsidiaries, tradingIncome, fundraising, grants, membershipSubscriptions, investmentIncome",
            |			"otherAreaOperation": true,
            |			"englandAndWales": true,
            |			"scotland": false,
            |			"northernIreland": false,
            |			"ukWide": false,
            |			"overseas": false
            |		}
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOperationAndFunds).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToCharitableObjectives" must {

      "convert the correct CharitableObjectives" in {

        val localUserAnswers = emptyUserAnswers.set(CharitableObjectivesPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"
        ).success.value

        val expectedJson =
          """{
            |   "charitableObjectives": {
            |			"objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |			"objectivesB": "11223344556677889900"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitableObjectives).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct CharitableObjectives with few selections" in {

        val localUserAnswers = emptyUserAnswers.set(CharitableObjectivesPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
        ).success.value

        val expectedJson =
          """{
            |   "charitableObjectives": {
            |			"objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitableObjectives).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToCharitablePurposes" must {

      "convert the correct CharitablePurposes object" in {

        val localUserAnswers = emptyUserAnswers.set(CharitablePurposesPage, CharitablePurposes.values.toSet).success.value

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

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitablePurposes).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct CharitablePurposes object with few selections" in {

        val localUserAnswers = emptyUserAnswers.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare)).success.value

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

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharitablePurposes).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToPublicBenefit" must {

      "convert the correct PublicBenefit object" in {

        val localUserAnswers = emptyUserAnswers.set(PublicBenefitsPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"
        ).success.value

        val expectedJson =
          """{
            |   "publicBenefit": {
            |			"publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |			"publicBenefitB": "11223344556677889900"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToPublicBenefit).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct PublicBenefit object with few selections" in {

        val localUserAnswers = emptyUserAnswers.set(PublicBenefitsPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
        ).success.value

        val expectedJson =
          """{
            |   "publicBenefit": {
            |			"publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            |   }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToPublicBenefit).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToOrgPurpose" must {

      "convert the correct OrgPurpose object all objects" in {

        val localUserAnswers = emptyUserAnswers.set(PublicBenefitsPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"
        ).flatMap(_.set(CharitablePurposesPage, CharitablePurposes.values.toSet))
          .flatMap(_.set(CharitableObjectivesPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"
          )).success.value

        val expectedJson =
          """{
            |"orgPurpose": {
            |   "charitableObjectives": {
            |			"objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |			"objectivesB": "11223344556677889900"
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
            |			  "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre6643",
            |			  "publicBenefitB": "11223344556677889900"
            |   }
            |  }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOrgPurpose).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct OrgPurpose object with minimum objects" in {

        val localUserAnswers = emptyUserAnswers.set(PublicBenefitsPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
        ).flatMap(_.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare)))
          .flatMap(_.set(CharitableObjectivesPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
          )).success.value

        val expectedJson =
          """{
            |"orgPurpose": {
            |   "charitableObjectives": {
            |			"objectivesA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
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
            |			  "publicBenefitA": "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            |   }
            |  }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToOrgPurpose).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToCharity" must {

      "convert the correct Charity object all objects" in {

        val localUserAnswers = emptyUserAnswers.set(CharityRegulatorPage,Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other)).flatMap(
          _.set(CharityCommissionRegistrationNumberPage, "123456")).flatMap(
          _.set(ScottishRegulatorRegNumberPage, "SC123456")).flatMap(
          _.set(NIRegulatorRegNumberPage, "ABCDEFGHIJ1234567890")).flatMap(
          _.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("Other Regulator Name", "12345678901234567890"))).flatMap(
          _.set(IsCharityRegulatorPage, true)).flatMap(_.set(PublicBenefitsPage,
          "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"
        )).flatMap(_.set(CharitablePurposesPage, CharitablePurposes.values.toSet)).flatMap(
          _.set(CharitableObjectivesPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"
          )).flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation).flatMap(
          _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1)).flatMap(
            _.set(GoverningDocumentNamePage, "Other Documents for Charity")).flatMap(
            _.set(IsApprovedGoverningDocumentPage, true)).flatMap(
            _.set(HasCharityChangedPartsOfGoverningDocumentPage, false)).flatMap(
            _.set(SectionsChangedGoverningDocumentPage,
              "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900"))
        )).flatMap(_.set(AccountingPeriodEndDatePage,
          MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
          _.set(IsFinancialAccountsPage, true)).flatMap(
          _.set(NoBankStatementPage,
            "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre664311223344556677889900")
        ).flatMap(
          _.set(EstimatedIncomePage, BigDecimal.valueOf(2000.00))).flatMap(
          _.set(ActualIncomePage, BigDecimal.valueOf(19999.99))).flatMap(
          _.set(FundRaisingPage, FundRaisingOptions.values.toSet)).flatMap(
          _.set(OperatingLocationPage, OperatingLocationOptions.values.toSet)).flatMap(
          _.set(OverseasCountryPage(0), "Country 1")).flatMap(
          _.set(OverseasCountryPage(1), "Country 2")).flatMap(
          _.set(OverseasCountryPage(2), "Country 3")).flatMap(
          _.set(OverseasCountryPage(3), "Country 4")).flatMap(
          _.set(OverseasCountryPage(4), "Country 5")
        )).success.value

        val expectedJson =
          """{
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
            |        "documentEnclosed": "1",
            |        "governingApprovedDoc": true,
            |        "governingApprovedWords": false,
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
            |    }
            |  }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharity).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct Charity object with minimum objects" in {

        val localUserAnswers = emptyUserAnswers.set(IsCharityRegulatorPage, false).flatMap(
          _.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold)).flatMap(
          _.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation)).flatMap(
          _.set(WhenGoverningDocumentApprovedPage, LocalDate.of(2014, 7, 1))).flatMap(
          _.set(GoverningDocumentNamePage, "Other Documents for Charity")).flatMap(
          _.set(IsApprovedGoverningDocumentPage, false)).flatMap(
          _.set(HasCharityChangedPartsOfGoverningDocumentPage, false)).flatMap(
          _.set(AccountingPeriodEndDatePage,
            MonthDay.fromDateFields(new JLocalDate(2020, 1, 1).toDate))(MongoDateTimeFormats.localDayMonthWrite).flatMap(
            _.set(IsFinancialAccountsPage, true)).flatMap(
            _.set(FundRaisingPage, FundRaisingOptions.values.toSet)).flatMap(
            _.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.EnglandAndWales))).flatMap(
            _.set(PublicBenefitsPage,
              "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66")).flatMap(
            _.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare))).flatMap(
            _.set(CharitableObjectivesPage,
              "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
            ))
        ).success.value

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
            |        "documentEnclosed": "1",
            |        "governingApprovedDoc": false,
            |        "governingApprovedWords": false
            |      },
            |      "operationAndFunds": {
            |        "operationAndFundsCommon": {
            |          "accountPeriodEnd": "0101",
            |          "financialAccounts": true
            |        },
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
            |    }
            |  }
            |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToCharity).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

  }

}
