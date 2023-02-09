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

package utils

import models.oldCharities.{Acknowledgement, _}
import org.joda.time.{LocalDate, MonthDay}

trait TestData {
  // scalastyle:off magic.number
  val contactDetails: CharityContactDetails                                     = CharityContactDetails("Test123", None, "1234567890", None, None, None)
  val charityAddress: CharityAddress                                            = CharityAddress("Test123", "line2", "", "", "postcode", "")
  val correspondenceAddress: OptionalCharityAddress                             =
    OptionalCharityAddress(Some("true"), CharityAddress("Test123", "line2", "", "", "postcode", ""))
  val charityRegulator: CharityRegulator                                        = CharityRegulator(
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = true, "ccewTestRegulator"),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = true, "ccewTestRegulator"),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorOtherInfoDetails(
      isCharityOtherRegulatorSelected = true,
      "otherRegulatorName",
      "otherRegulatorRegistrationNumber"
    ),
    CharityReasonForNotRegistering(None, None)
  )
  val noCharityRegulator5: CharityRegulator                                     = CharityRegulator(
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorOtherInfoDetails(isCharityOtherRegulatorSelected = false, "", ""),
    CharityReasonForNotRegistering(Some("5"), Some("Reason"))
  )
  val noCharityRegulator6: CharityRegulator                                     = CharityRegulator(
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorOtherInfoDetails(isCharityOtherRegulatorSelected = false, "", ""),
    CharityReasonForNotRegistering(Some("6"), Some("Reason"))
  )
  val noCharityRegulator7: CharityRegulator                                     = CharityRegulator(
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorOtherInfoDetails(isCharityOtherRegulatorSelected = false, "", ""),
    CharityReasonForNotRegistering(Some("7"), Some("Reason"))
  )
  val noCharityRegulator8: CharityRegulator                                     = CharityRegulator(
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorOtherInfoDetails(isCharityOtherRegulatorSelected = false, "", ""),
    CharityReasonForNotRegistering(Some("8"), Some("Reason"))
  )
  val noCharityRegulator9: CharityRegulator                                     = CharityRegulator(
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorOtherInfoDetails(isCharityOtherRegulatorSelected = false, "", ""),
    CharityReasonForNotRegistering(Some("9"), Some("Reason"))
  )
  val noCharityRegulator10: CharityRegulator                                    = CharityRegulator(
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorOtherInfoDetails(isCharityOtherRegulatorSelected = false, "", ""),
    CharityReasonForNotRegistering(Some("10"), Some("Reason"))
  )
  val charityGoverningDocument1: CharityGoverningDocument                       =
    CharityGoverningDocument("1", "", "test", Some(LocalDate.parse("1990-11-11")), Some(true))
  val charityGoverningDocument2: CharityGoverningDocument                       =
    CharityGoverningDocument("2", "", "test", Some(LocalDate.parse("1990-11-11")), Some(true))
  val charityGoverningDocument3: CharityGoverningDocument                       =
    CharityGoverningDocument("3", "", "test", Some(LocalDate.parse("1990-11-11")), Some(true))
  val charityGoverningDocument4: CharityGoverningDocument                       =
    CharityGoverningDocument("4", "", "test", Some(LocalDate.parse("1990-11-11")), Some(true))
  val charityGoverningDocument6: CharityGoverningDocument                       =
    CharityGoverningDocument("6", "", "test", Some(LocalDate.parse("1990-11-11")), Some(true))
  val charityGoverningDocument7: CharityGoverningDocument                       =
    CharityGoverningDocument("7", "", "test", Some(LocalDate.parse("1990-11-11")), Some(true))
  val whatYourCharityDoes: WhatYourCharityDoes                                  = WhatYourCharityDoes(
    "objectives",
    reliefOfPoverty = true,
    education = false,
    animalWelfare = false,
    healthOrSavingOfLives = false,
    citizenshipOrCommunityDevelopment = false,
    reliefOfThoseInNeed = false,
    religion = false,
    amateurSport = false,
    humanRights = false,
    artsCultureHeritageOrScience = false,
    environmentalProtectionOrImprovement = false,
    promotionOfEfficiencyInArmedForcesPoliceFireAndRescueService = false,
    whatYourCharityDoesOther = false,
    "otherReason",
    "benefitThePublic"
  )
  val operationAndFunds: OperationAndFunds                                      = OperationAndFunds(
    OperationAndFundsCommon(
      ScalaMonthDay(MonthDay.fromDateFields(new LocalDate(2020, 1, 1).toDate)),
      Some(true),
      Some(true),
      Some("noBankStatements")
    ),
    FutureFunds(
      donations = true,
      fundraising = false,
      grants = false,
      membershipSubscriptions = false,
      tradingIncome = false,
      tradingSubsidiaries = false,
      investmentIncome = false,
      other = false
    ),
    Some("fundsOther"),
    Some(100),
    Some(100),
    Some(false),
    WhereWillCharityOperate(
      englandAndWales = true,
      scotland = false,
      northernIreland = false,
      ukWide = false,
      overseas = false
    ),
    OtherCountriesOfOperation(Some("EN"), Some("EN"), None, None, None)
  )
  val operationAndFundsUKWide: OperationAndFunds                                = OperationAndFunds(
    OperationAndFundsCommon(
      ScalaMonthDay(MonthDay.fromDateFields(new LocalDate(2020, 1, 1).toDate)),
      Some(true),
      Some(true),
      Some("noBankStatements")
    ),
    FutureFunds(
      donations = true,
      fundraising = false,
      grants = false,
      membershipSubscriptions = false,
      tradingIncome = false,
      tradingSubsidiaries = false,
      investmentIncome = false,
      other = false
    ),
    Some("fundsOther"),
    Some(100),
    Some(100),
    Some(false),
    WhereWillCharityOperate(
      englandAndWales = false,
      scotland = false,
      northernIreland = false,
      ukWide = true,
      overseas = false
    ),
    OtherCountriesOfOperation(None, None, None, None, None)
  )
  val operationAndFundsFiveCountries: OperationAndFunds                         = OperationAndFunds(
    OperationAndFundsCommon(
      ScalaMonthDay(MonthDay.fromDateFields(new LocalDate(2020, 1, 1).toDate)),
      Some(true),
      Some(true),
      Some("noBankStatements")
    ),
    FutureFunds(
      donations = true,
      fundraising = false,
      grants = false,
      membershipSubscriptions = false,
      tradingIncome = false,
      tradingSubsidiaries = false,
      investmentIncome = false,
      other = false
    ),
    Some("fundsOther"),
    Some(100),
    Some(100),
    Some(false),
    WhereWillCharityOperate(
      englandAndWales = false,
      scotland = false,
      northernIreland = false,
      ukWide = true,
      overseas = true
    ),
    OtherCountriesOfOperation(Some("AA"), Some("BB"), Some("CC"), Some("DD"), Some("EE"))
  )
  val operationAndFundsFiveCountriesNoUK: OperationAndFunds                     = OperationAndFunds(
    OperationAndFundsCommon(
      ScalaMonthDay(MonthDay.fromDateFields(new LocalDate(2020, 1, 1).toDate)),
      Some(true),
      Some(true),
      Some("noBankStatements")
    ),
    FutureFunds(
      donations = true,
      fundraising = false,
      grants = false,
      membershipSubscriptions = false,
      tradingIncome = false,
      tradingSubsidiaries = false,
      investmentIncome = false,
      other = false
    ),
    Some("fundsOther"),
    Some(100),
    Some(100),
    Some(false),
    WhereWillCharityOperate(
      englandAndWales = false,
      scotland = false,
      northernIreland = false,
      ukWide = false,
      overseas = true
    ),
    OtherCountriesOfOperation(Some("AA"), Some("BB"), Some("CC"), Some("DD"), Some("EE"))
  )
  val charityBankAccountDetails: CharityBankAccountDetails                      =
    CharityBankAccountDetails("Tesco", "123456", "12345678", Some("rollNumber"))
  val charityHowManyAuthOfficials: CharityHowManyAuthOfficials                  = CharityHowManyAuthOfficials(Some(22))
  val identity: OfficialIndividualIdentity                                      =
    OfficialIndividualIdentity(Some("true"), "AB111111A", OfficialIndividualNationalIdentityCardDetails("", "", None))
  val currentAddress: CharityAddress                                            = CharityAddress("current", "address", "", "", "AA1 1AA", "")
  val identityPassport: OfficialIndividualIdentity                              = OfficialIndividualIdentity(
    Some("false"),
    "",
    OfficialIndividualNationalIdentityCardDetails("PaspNum", "Country", Some(LocalDate.parse("2100-01-01")))
  )
  val previousAddress: OptionalCharityAddress                                   =
    OptionalCharityAddress(Some("true"), CharityAddress("previous", "address", "", "", "AA2 2AA", ""))
  val charityAuthorisedOfficialIndividual1: CharityAuthorisedOfficialIndividual = CharityAuthorisedOfficialIndividual(
    "0001",
    "First",
    "Middle",
    "Last",
    LocalDate.parse("1990-01-01"),
    "01",
    "0123123123",
    Some("0123123124"),
    None,
    currentAddress,
    previousAddress,
    identity
  )
  val charityAuthorisedOfficialIndividual2: CharityAuthorisedOfficialIndividual = CharityAuthorisedOfficialIndividual(
    "0008",
    "First",
    "Middle",
    "Last",
    LocalDate.parse("1990-01-01"),
    "01",
    "0123123123",
    Some("0123123124"),
    None,
    currentAddress,
    previousAddress,
    identityPassport
  )
  val charityHowManyOtherOfficials: CharityHowManyOtherOfficials                = CharityHowManyOtherOfficials(Some(33))
  val charityOtherOfficialIndividual1: CharityAuthorisedOfficialIndividual      = CharityAuthorisedOfficialIndividual(
    "0001",
    "First",
    "Middle",
    "Last",
    LocalDate.parse("1990-01-01"),
    "01",
    "0123123123",
    Some("0123123124"),
    None,
    currentAddress,
    previousAddress,
    identity
  )
  val charityOtherOfficialIndividual2: CharityAuthorisedOfficialIndividual      = CharityAuthorisedOfficialIndividual(
    "0008",
    "First",
    "Middle",
    "Last",
    LocalDate.parse("1990-01-01"),
    "01",
    "0123123123",
    Some("0123123124"),
    None,
    currentAddress,
    previousAddress,
    identityPassport
  )
  val charityOtherOfficialIndividual3: CharityAuthorisedOfficialIndividual      = CharityAuthorisedOfficialIndividual(
    "0008",
    "First",
    "Middle",
    "Last",
    LocalDate.parse("1990-01-01"),
    "01",
    "0123123123",
    Some("0123123124"),
    None,
    currentAddress,
    previousAddress,
    identity
  )
  val charityNoNominee: CharityAddNominee                                       = CharityAddNominee(Some(false))
  val charityAddNominee: CharityAddNominee                                      = CharityAddNominee(Some(true))
  val charityNomineeStatusInd: CharityNomineeStatus                             = CharityNomineeStatus(Some("individual"))
  val charityNomineeIndividual: CharityNomineeIndividual                        = CharityNomineeIndividual(
    "Mr",
    "firstName",
    Some("middleName"),
    "lastName",
    new LocalDate(2000, 10, 10),
    "1234567890",
    Some(""),
    Some(""),
    CharityAddress("Line1", "Line2", "Line3", "Line5", "", "UK"),
    OptionalCharityAddress(Some("false"), CharityAddress("", "", "", "", "", "")),
    OfficialIndividualIdentity(Some("true"), "CS700100A", OfficialIndividualNationalIdentityCardDetails("", "", None)),
    NomineeBankDetails(
      Some("false"),
      NomineePaymentDetails("AA", CharityBankAccountDetails("AABB", "12345678", "123456", Some("BB")))
    )
  )
  val charityNomineeStatusOrg: CharityNomineeStatus                             = CharityNomineeStatus(Some("organisation"))
  val charityNomineeOrganisation: CharityNomineeOrganisation                    = CharityNomineeOrganisation(
    "Tesco",
    "1234567890",
    charityAddress,
    correspondenceAddress,
    NomineeOrgPersonalDetails("Mr", "firstName", Some("middleName"), "lastName", new LocalDate(2000, 10, 10)),
    OfficialIndividualIdentity(
      Some("false"),
      "",
      OfficialIndividualNationalIdentityCardDetails("AK123456K", "UK", Option(new LocalDate(2000, 10, 10)))
    ),
    NomineeBankDetails(
      Some("true"),
      NomineePaymentDetails("", CharityBankAccountDetails("AABB", "12345678", "123456", Some("BB")))
    )
  )
  val acknowledgement: Acknowledgement                                          = Acknowledgement("080582080582", "9:56am, Tuesday 1 December 2020")
  // scalastyle:on magic.number
}
