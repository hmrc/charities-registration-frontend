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

package base

import models.{BankDetails, CharityContactDetails, CharityName, CharityOtherRegulatorDetails, Country, FcoCountry, Name, Passport, PhoneNumber, SelectTitle}
import models.addressLookup.{AddressModel, AmendAddressModel, CountryModel}
import models.nominees.OrganisationNomineeContactDetails
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

trait TestData extends ModelGenerators {
  val july1st2014: LocalDate = LocalDate.of(2014, 7, 1)

  val january1st2002: LocalDate = LocalDate.of(2002, 1, 1)

  val feb1st2018: LocalDate = LocalDate.of(2018, 2, 1)

  val futureDate: LocalDate = LocalDate.now().plusDays(1)

  val addressId: String = "Address Id"

  val town: String = "TestTown"

  val gbCountry: Country = Country("GB", "United Kingdom")
  val gbCountryModel: CountryModel = CountryModel("GB", "United Kingdom")
  val gbCountryTuple: (String, String) = (gbCountryModel.code, gbCountryModel.name)
  val gbFcoCountry: FcoCountry = FcoCountry("GB", "United Kingdom")

  val thCountryModel: CountryModel = CountryModel("TH", "Thailand")
  val thCountry: Country = Country("TH", "Thailand")
  val thCountryTuple: (String, String) = (thCountry.code, thCountry.name)

  val address: AddressModel =
    AddressModel(
      Some("Test Organisation"),
      Seq("7", "Test Street"),
      Some("ZY11 1AA"),
      gbCountryModel
    )
    
  val addressAllLines: AddressModel =
    AddressModel(
      Some("Test Organisation"),
      Seq("7", "Test Street", "Area", "City"),
      Some("ZY11 1AA"),
      gbCountryModel
    )

  val addressWithTown: AddressModel = address.copy(lines = address.lines :+ town)

  val addressWithTownAnd3Lines: AddressModel = address.copy(lines = address.lines :+ "Test Area" :+ town)

  val addressModelMax: AddressModel =
    AddressModel(
      Some("Test Organisation"),
      Seq("7", "Testttt Street near TestTest Gardens"),
      Some("ZY11 1AA"),
      gbCountryModel
    )

  val addressModelMaxWithTown: AddressModel = addressModelMax.copy(lines = addressModelMax.lines :+ town)

  val addressModelMin: AddressModel =
    AddressModel(None, Seq("7 Test Street"), Some("ZY11 1AA"), CountryModel("UK", "United Kingdom"))

  def toAmendAddressModel(addressModel: AddressModel, maybeTown: Option[String] = None): AmendAddressModel =
    AmendAddressModel(
      addressModel.organisation,
      addressModel.lines.head,
      addressModel.lines.slice(1, 2).headOption,
      addressModel.lines.slice(2, 3).headOption,
      addressModel.lines.slice(3, 4).headOption.orElse(maybeTown).getOrElse(""),
      addressModel.postcode.get,
      addressModel.country.code
    )

  val organisationName: String      = "Test Organisation"
  val addressLines: Seq[String] = Seq("Test 1", "Test 2")
  val postcode: String          = "AA00 0AA"

  val confirmedAddress: AddressModel = AddressModel(
    Some(organisationName),
    addressLines,
    postcode = Some(postcode),
    gbCountryModel
  )

  val addressLookupResponse: JsObject = Json.obj(
    "auditRef" -> "a1fe6969-e3fd-421b-a5fb-c9458c9cfd22",
    "id"       -> "GB690091234501",
    "address"  -> Json.toJson(confirmedAddress)
  )

  val personNameWithoutMiddle: Name = Name(SelectTitle.Mr, "Firstname", None, "Lastname")
  val personNameWithMiddle: Name = Name(SelectTitle.Mr, "Firstname", Some("Middle"), "Lastname")
  val personName2WithoutMiddle: Name = Name(SelectTitle.Ms, "Firstname2", None, "Lastname2")
  val personName2WithMiddle: Name = Name(SelectTitle.Ms, "Firstname2", Some("Middle2"), "Lastname2")
  val personName3WithoutMiddle: Name = Name(SelectTitle.Mrs, "Firstname3", None, "Lastname3")
  val personName3WithMiddle: Name = Name(SelectTitle.Ms, "Firstname3", Some("Middle3"), "Lastname3")
  val personName4WithoutMiddle: Name = Name(SelectTitle.Ms, "Firstname4", None, "Lastname4")
  val personName4WithMiddle: Name = Name(SelectTitle.Ms, "Firstname4", Some("Middle4"), "Lastname4")

  val sortCode: String            = sortCodeGen.sample.get
  val sortCodeWithSpaces: String  = s"${sortCode.slice(0, 2)} ${sortCode.slice(2, 4)} ${sortCode.slice(4, 6)}"
  val sortCodeWithHyphens: String = s"${sortCode.slice(0, 2)}-${sortCode.slice(2, 4)}-${sortCode.slice(4, 6)}"

  val accountNumber: String           = sortCodeGen.sample.get
  val accountNumberWithSpaces: String =
    s"${accountNumber.slice(0, 2)} ${accountNumber.slice(2, 4)} ${accountNumber.slice(4, 6)} ${accountNumber.slice(6, 8)}"
  val rollNumber: String              = rollNumberGen.sample.get
  val accountName: String             = accountNameGen.sample.get

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
  val nino2: String          = ninoGen.sample.get
  val nino2WithSpaces: String =
    s"${nino2.slice(0, 2)} ${nino2.slice(2, 4)} ${nino2.slice(4, 6)} ${nino2.slice(6, 8)} ${nino2.slice(8, 9)}"

  val passportNumber: String = passportGen.sample.get
  val passport: Passport     = Passport(passportNumber, "GB", LocalDate.now)

  val charityEmail      = "charity@example.com"
  val organisationEmail = "company@example.com"

  val daytimePhone: String = "01632 960 001"
  val mobileNumber: String = "07700 900 982"

  val phoneNumbers: PhoneNumber = PhoneNumber(daytimePhone, Some(mobileNumber))
  
  val daytimePhoneWithIntCode: String = "+44 1632 960 001"
  val mobileNumberWithIntCode: String = "+44 7700 900 982"

  val phoneNumbersWithIntCode: PhoneNumber = PhoneNumber(daytimePhoneWithIntCode, Some(mobileNumberWithIntCode))

  val charityContactDetails: CharityContactDetails = CharityContactDetails(
    daytimePhone = daytimePhone,
    mobilePhone = Some(mobileNumber),
    emailAddress = charityEmail
  )

  val charityRegistrationNumber: String = charityRegistrationGen.sample.get
  
  val charityFullName      = "A Charity"
  val charityOperatingName = "Charity Operating Name"

  val charityName: CharityName                = CharityName(charityFullName, Some(charityOperatingName))
  val charityNameNoOperatingName: CharityName = charityName.copy(operatingName = None)

  val charityCommissionRegistrationNumber: String = charityRegulatorRegistrationGen.sample.get
  val scottishRegulatorRegistrationNumber: String = "SC" + charityRegulatorRegistrationGen.sample.get
  val niRegulatorRegistrationNumber: String = charityRegulatorRegistrationGen.sample.get

  val charityRegulatorName: String = "Regulator name"
  val chartyRegulatorRegistrationNumber: String = charityRegulatorRegistrationGen.sample.get

  val charityRegulatorDetails: CharityOtherRegulatorDetails = CharityOtherRegulatorDetails(charityRegulatorName, chartyRegulatorRegistrationNumber)

  val nomineeOrganisationName: String = "Nominee Organisation"
  val nomineeOrganisationContactDetails: OrganisationNomineeContactDetails = OrganisationNomineeContactDetails(daytimePhone, organisationEmail)

  val acknowledgementRef: String = acknowledgementRefGen.sample.get
  
  val charityObjective: String = "Make the World better" 
  val publicBenefit: String = "FreeEducation"
  val whyNoBankStatement: String = "Reason why no bank statement"
  val otherFundRaising: String = "Other fund raising"
  val governingDocument: String = "will"
  val governingDocumentOther: String = "other"
  val whyNoRegulator: String = "reason"
  val whyNotRegistered: String = "reason"
  val governingDocumentChange: String = "Governing document change and reason"
}
