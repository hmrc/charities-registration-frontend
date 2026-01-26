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

package common

import models.{BankDetails, CharityContactDetails, CharityName, CharityOtherRegulatorDetails, Country, FcoCountry, Name, Passport, PhoneNumber, SelectTitle, withNameToString}
import models.nominees.OrganisationNomineeContactDetails
import models.addressLookup.{AddressModel, AmendAddressModel, CountryModel}

import java.time.LocalDate

trait TestData extends ModelGenerators {

  val sortCode: String            = sortCodeGen.sample.get
  val sortCodeWithSpaces: String  = s"${sortCode.slice(0, 2)} ${sortCode.slice(2, 4)} ${sortCode.slice(4, 6)}"
  val sortCodeWithHyphens: String = s"${sortCode.slice(0, 2)}-${sortCode.slice(2, 4)}-${sortCode.slice(4, 6)}"

  val accountNumber: String           = accountNumberGen.sample.get
  val accountNumberWithSpaces: String =
    s"${accountNumber.slice(0, 2)} ${accountNumber.slice(2, 4)} ${accountNumber.slice(4, 6)} ${accountNumber.slice(6, 8)}"
  val accountName: String             = accountNameGen.sample.get

  val rollNumber: String = rollNumberGen.sample.get

  val bankDetails: BankDetails = BankDetails(
    accountName = accountName,
    sortCode = sortCode,
    accountNumber = accountNumber,
    rollNumber = Some(rollNumber)
  )

  val bankDetailsWithoutRollNumber: BankDetails = BankDetails(
    accountName = accountName,
    sortCode = sortCode,
    accountNumber = accountNumber,
    rollNumber = None
  )

  val bankDetailsWithHyphensAndSpaces: BankDetails = BankDetails(
    accountName = accountName,
    sortCode = sortCodeWithHyphens,
    accountNumber = accountNumberWithSpaces,
    rollNumber = Some(rollNumber)
  )

  val nino: String           = ninoGen.sample.get
  val ninoWithSpaces: String =
    s"${nino.slice(0, 2)} ${nino.slice(2, 4)} ${nino.slice(4, 6)} ${nino.slice(6, 8)} ${nino.slice(8, 9)}"

  val nino2: String           = ninoGen.sample.get
  val nino2WithSpaces: String =
    s"${nino2.slice(0, 2)} ${nino2.slice(2, 4)} ${nino2.slice(4, 6)} ${nino2.slice(6, 8)} ${nino2.slice(8, 9)}"

  val nino3: String = ninoGen.sample.get

  val nino3WithSpaces: String =
    s"${nino3.slice(0, 2)} ${nino3.slice(2, 4)} ${nino3.slice(4, 6)} ${nino3.slice(6, 8)} ${nino3.slice(8, 9)}"

  val passportNumber: String = passportGen.sample.get

  val passport: Passport = Passport(passportNumber, "GB", LocalDate.now)

  val daytimePhone: String      = exampleFixedLineGen.sample.get
  val mobileNumber: String      = exampleMobileGen.sample.get
  val phoneNumbers: PhoneNumber = PhoneNumber(daytimePhone, Some(mobileNumber))

  val daytimePhoneWithIntCode: String      = exampleFixedLineIntGen.sample.get
  val mobileNumberWithIntCode: String      = exampleMobileIntGen.sample.get
  val phoneNumbersWithIntCode: PhoneNumber = PhoneNumber(daytimePhoneWithIntCode, Some(mobileNumberWithIntCode))

  val charityFullName      = "A Charity"
  val charityOperatingName = "Charity Operating Name"

  val charityName: CharityName                = CharityName(charityFullName, Some(charityOperatingName))
  val charityNameNoOperatingName: CharityName = charityName.copy(operatingName = None)

  val charityRegistrationNumber: String = charityRegistrationGen.sample.get

  val charityCommissionRegistrationNumber: String = charityRegulatorRegistrationGen.sample.get
  val scottishRegulatorRegistrationNumber: String = "SC" + charityRegulatorRegistrationGen.sample.get
  val niRegulatorRegistrationNumber: String       = charityRegulatorRegistrationGen.sample.get

  val charityRegulatorName: String              = "Regulator name"
  val chartyRegulatorRegistrationNumber: String = charityRegulatorRegistrationGen.sample.get

  val charityRegulatorDetails: CharityOtherRegulatorDetails =
    CharityOtherRegulatorDetails(charityRegulatorName, chartyRegulatorRegistrationNumber)

  val charityEmail      = "charity@example.com"
  val organisationEmail = "company@example.com"

  val charityContactDetails: CharityContactDetails = CharityContactDetails(
    daytimePhone = daytimePhone,
    mobilePhone = Some(mobileNumber),
    emailAddress = charityEmail
  )

  val charityObjective: String        = "Make the World better"
  val acknowledgementRef: String      = acknowledgementRefGen.sample.get
  val publicBenefit: String           = "FreeEducation"
  val whyNoBankStatement: String      = "Reason why no bank statement"
  val otherFundRaising: String        = "Other fund raising"
  val governingDocument: String       = "will"
  val governingDocumentOther: String  = "other"
  val whyNoRegulator: String          = "reason"
  val whyNotRegistered: String        = "reason"
  val governingDocumentChange: String = "Governing document change and reason"

  val nomineeOrganisationName: String                                      = "Nominee Organisation"
  val nomineeOrganisationContactDetails: OrganisationNomineeContactDetails =
    OrganisationNomineeContactDetails(daytimePhone, organisationEmail)

  val gbCountryModel: CountryModel     = CountryModel("GB", "United Kingdom")
  val gbCountry: Country               = Country("GB", "United Kingdom")
  val gbCountryTuple: (String, String) = (gbCountry.code, gbCountry.name)
  val gbFcoCountry: FcoCountry         = FcoCountry("GB", "United Kingdom")
  val (gbCountryCode, gbCountryName)   = gbCountryTuple

  val thCountryModel: CountryModel     = CountryModel("TH", "Thailand")
  val thCountry: Country               = Country("TH", "Thailand")
  val thCountryTuple: (String, String) = (thCountry.code, thCountry.name)
  val thFcoCountry: FcoCountry         = FcoCountry("TH", "Thailand")
  val (thCountryCode, thCountryName)   = thCountryTuple

  val frCountryModel: CountryModel     = CountryModel("FR", "France")
  val frCountry: Country               = Country("FR", "France")
  val frCountryTuple: (String, String) = (frCountry.code, frCountry.name)
  val (frCountryCode, frCountryName)   = frCountryTuple

  val inCountryModel: CountryModel     = CountryModel("IN", "India")
  val inCountry: Country               = Country("IN", "India")
  val inCountryTuple: (String, String) = (inCountry.code, inCountry.name)
  val (inCountryCode, inCountryName)   = inCountryTuple

  val itCountryModel: CountryModel     = CountryModel("IT", "Italy")
  val itCountry: Country               = Country("IT", "Italy")
  val itCountryTuple: (String, String) = (itCountry.code, itCountry.name)
  val (itCountryCode, itCountryName)   = itCountryTuple

  val usCountry: Country               = Country("US", "United States")
  val usCountryTuple: (String, String) = (usCountry.code, usCountry.name)
  val (usCountryCode, usCountryName)   = usCountryTuple

  val chCountry: Country               = Country("CH", "Switzerland")
  val chCountryTuple: (String, String) = (chCountry.code, chCountry.name)
  val (chCountryCode, chCountryName)   = chCountryTuple

  val today: LocalDate                = LocalDate.now()
  val futureDate: LocalDate           = today.plusYears(1)
  val farInThePastDate: LocalDate     = LocalDate.of(1111, 1, 1)
  val officialsDOB: LocalDate         = LocalDate.of(2000, 12, 11)
  val govDocApprovedDate: LocalDate   = LocalDate.of(2014, 7, 1)
  val singleDigitMonthDate: LocalDate = LocalDate.of(2015, 6, 10)
  val singleDigitDayDate: LocalDate   = LocalDate.of(2017, 4, 1)
  val correctFormatDate: LocalDate    = LocalDate.of(2020, 9, 23)
  val moreThan20Date: LocalDate       = LocalDate.of(2020, 9, 24)
  val lessThan20Date: LocalDate       = LocalDate.of(2020, 9, 14)
  val datesMin: LocalDate             = LocalDate.of(2003, 1, 1)
  val datesMax: LocalDate             = LocalDate.of(2004, 1, 1)

  val addressId: String     = "Address Id"
  val line1: String         = "Test 1"
  val line2: String         = "Test 2"
  val line3: String         = "Test Area"
  val maxLine: String       = "Testttt Street near TestTest Gardens"
  val ukPostcode: String    = "ZY11 1AA"
  val nonUkPostcode: String = "NonUKCode"
  val town: Option[String]  = Some("TestTown")

  val address: AddressModel =
    AddressModel(
      Seq(line1, line2),
      Some(ukPostcode),
      gbCountryModel
    )

  val addressAllLines: AddressModel =
    AddressModel(
      Seq(line1, line2, line3, town.get),
      Some(ukPostcode),
      gbCountryModel
    )

  val addressModelMax: AddressModel =
    AddressModel(
      Seq(line1, maxLine),
      Some(ukPostcode),
      gbCountryModel
    )

  val addressModelMin: AddressModel =
    AddressModel(Seq(maxLine), Some(ukPostcode), gbCountryModel)

  val addressModelMinWithTown: AddressModel =
    addressModelMin.copy(lines = addressModelMin.lines :+ town.get)

  val addressWithTown: AddressModel =
    address.copy(lines = address.lines :+ town.get)

  val addressWithTownAnd3Lines: AddressModel =
    address.copy(lines = address.lines :+ line3 :+ town.get)

  val addressModelMaxWithTown: AddressModel =
    addressModelMax.copy(lines = addressModelMax.lines :+ town.get)

  def toAmendAddressModel(
    addressModel: AddressModel,
    maybeTown: Option[String] = None
  ): AmendAddressModel =
    AmendAddressModel(
      addressModel.lines.head,
      addressModel.lines.slice(1, 2).headOption,
      addressModel.lines.slice(2, 3).headOption,
      addressModel.lines.slice(3, 4).headOption.orElse(maybeTown).getOrElse(""),
      addressModel.postcode.get,
      addressModel.country.code
    )

  val personNameWithoutMiddle: Name  = Name(SelectTitle.Mr, "Firstname", None, "Lastname")
  val personNameWithMiddle: Name     = Name(SelectTitle.Mr, "Firstname", Some("Middle"), "Lastname")
  val personName2WithoutMiddle: Name = Name(SelectTitle.Ms, "Firstname2", None, "Lastname2")
  val personName2WithMiddle: Name    = Name(SelectTitle.Ms, "Firstname2", Some("Middle2"), "Lastname2")
  val personName3WithoutMiddle: Name = Name(SelectTitle.Mrs, "Firstname3", None, "Lastname3")
  val personName3WithMiddle: Name    = Name(SelectTitle.Ms, "Firstname3", Some("Middle3"), "Lastname3")
  val personName4WithoutMiddle: Name = Name(SelectTitle.Ms, "Firstname4", None, "Lastname4")
  val personName4WithMiddle: Name    = Name(SelectTitle.Ms, "Firstname4", Some("Middle4"), "Lastname4")

  def replacePlaceholders(inString: String): String =
    inString
      .replaceAll("__ACCOUNTNAME__", accountName)
      .replaceAll("__SORTCODE__", sortCode)
      .replaceAll("__ACCOUNTNUMBER__", accountNumber)
      .replaceAll("__ROLLNUMBER__", rollNumber)
      .replaceAll("__NINO1__", nino)
      .replaceAll("__NINOWITHSPACE1__", ninoWithSpaces)
      .replaceAll("__NINOWITHSPACE2__", nino2WithSpaces)
      .replaceAll("__PASSPORTNUMBER__", passportNumber)
      .replaceAll("__PASSPORTCOUNTRY__", passport.country)
      .replaceAll("__EXPIRYDATE__", passport.expiryDate.toString)
      .replaceAll("__CHARITYOPERATINGNAME__", charityOperatingName)
      .replaceAll("__CHARITYFULLNAME__", charityFullName)
      .replaceAll("__CHARITYEMAIL__", charityEmail)
      .replaceAll("__ORGANISATIONEMAIL__", organisationEmail)
      .replaceAll("__DAYTIMEPHONE__", daytimePhone)
      .replaceAll("__DAYTIMEPHONEINT__", daytimePhoneWithIntCode)
      .replaceAll("__MOBILEPHONE__", mobileNumber)
      .replaceAll("__MOBILEPHONEINT__", mobileNumberWithIntCode)
      .replaceAll("__THCOUNTRYCODE__", thCountry.code)
      .replaceAll("__THCOUNTRYNAME__", thCountry.name)
      .replaceAll("__GBCOUNTRYCODE__", gbCountry.code)
      .replaceAll("__GBCOUNTRYNAME__", gbCountry.name)
      .replaceAll("__ITCOUNTRYCODE__", itCountry.code)
      .replaceAll("__ITCOUNTRYNAME__", itCountry.name)
      .replaceAll("__INCOUNTRYCODE__", inCountry.code)
      .replaceAll("__INCOUNTRYNAME__", inCountry.name)
      .replaceAll("__FRCOUNTRYCODE__", frCountry.code)
      .replaceAll("__FRCOUNTRYNAME__", frCountry.name)
      .replaceAll("__NOMINEEORGANISATIONNAME__", nomineeOrganisationName)
      .replaceAll("__ACKNOWLEDGEMENTREF__", acknowledgementRef)
      .replaceAll("__CHARITYOBJECTIVE__", charityObjective)
      .replaceAll("__PUBLICBENEFIT__", publicBenefit)
      .replaceAll("__WHYNOBANKSTETEMENT__", whyNoBankStatement)
      .replaceAll("__OTHERFUNDRAISING__", otherFundRaising)
      .replaceAll("__GOVERNINGDOCUMENT__", governingDocument)
      .replaceAll("__GOVERNINGDOCUMENTOTHER__", governingDocumentOther)
      .replaceAll("__WHYNOREGULATOR__", whyNoRegulator)
      .replaceAll("__WHYNOTREGISTERED__", whyNotRegistered)
      .replaceAll("__GOVERNINGDOCUMENTCHANGE__", governingDocumentChange)
      .replaceAll("__PERSONFIRSTNAME__", personNameWithMiddle.firstName)
      .replaceAll("__PERSONMIDDLENAME__", personNameWithMiddle.middleName)
      .replaceAll("__PERSONLASTNAME__", personNameWithMiddle.lastName)
      .replaceAll("__PERSON2FIRSTNAME__", personName2WithMiddle.firstName)
      .replaceAll("__PERSON2MIDDLENAME__", personName2WithMiddle.middleName)
      .replaceAll("__PERSON2LASTNAME__", personName2WithMiddle.lastName)
      .replaceAll("__CCREGISTRATIONNUMBER__", charityCommissionRegistrationNumber)
      .replaceAll("__SCREGULATORNUMBER__", scottishRegulatorRegistrationNumber)
      .replaceAll("__NIREGULATORNUMBER__", niRegulatorRegistrationNumber)
      .replaceAll("__CREGULATORNAME__", charityRegulatorName)
      .replaceAll("__CREGULATORNUMBER__", chartyRegulatorRegistrationNumber)
      .replaceAll("__GOVDOCAPPROVEDDATE__", govDocApprovedDate.toString)
      .replaceAll("__OFFICIALSDOB__", officialsDOB.toString)
      .replaceAll("__ADDRESSLINE1__", line1)
      .replaceAll("__ADDRESSLINE2__", line2)
      .replaceAll("__ADDRESSLINE3__", line3)
      .replaceAll("__ADDRESSMAXLINE__", maxLine)
      .replaceAll("__ADDRESSTOWN__", town.get)
      .replaceAll("__ADDRESSUKPOSTCODE__", ukPostcode)
}
