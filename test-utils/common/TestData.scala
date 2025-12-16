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

import models.{BankDetails, CharityName, CharityContactDetails, Passport, PhoneNumber}
import models.nominees.OrganisationNomineeContactDetails

import java.time.LocalDate

trait TestData extends ModelGenerators {
  val sortCode: String            = sortCodeGen.sample.get
  val sortCodeWithSpaces: String  = s"${sortCode.slice(0, 2)} ${sortCode.slice(2, 4)} ${sortCode.slice(4, 6)}"
  val sortCodeWithHyphens: String = s"${sortCode.slice(0, 2)}-${sortCode.slice(2, 4)}-${sortCode.slice(4, 6)}"

  val accountNumber: String           = accountNumberGen.sample.get
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

  val nino: String = ninoGen.sample.get
  val ninoWithSpaces: String =
    s"${nino.slice(0, 2)} ${nino.slice(2, 4)} ${nino.slice(4, 6)} ${nino.slice(6, 8)} ${nino.slice(8, 9)}"
  val nino2: String = ninoGen.sample.get
  val nino2WithSpaces: String =
    s"${nino2.slice(0, 2)} ${nino2.slice(2, 4)} ${nino2.slice(4, 6)} ${nino2.slice(6, 8)} ${nino2.slice(8, 9)}"

  val nino3: String = ninoGen.sample.get
  val nino3WithSpaces: String =
    s"${nino3.slice(0, 2)} ${nino3.slice(2, 4)} ${nino3.slice(4, 6)} ${nino3.slice(6, 8)} ${nino3.slice(8, 9)}"

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

  val passportNumber: String = passportGen.sample.get
  val passport: Passport     = Passport(passportNumber, "GB", LocalDate.now)

  val charityFullName = "A Charity"
  val charityOperatingName = "Charity Operating Name"

  val charityName: CharityName = CharityName(charityFullName, Some(charityOperatingName))
  val charityNameNoOperatingName: CharityName = charityName.copy(operatingName = None)

  val charityEmail = "charity@example.com"
  val organisationEmail = "company@example.com"


  val daytimePhone: String = exampleFixedLineGen.sample.get
  val mobileNumber: String = exampleMobileGen.sample.get

  val phoneNumbers: PhoneNumber = PhoneNumber(daytimePhone, Some(mobileNumber))

  val daytimePhoneWithIntCode: String = exampleFixedLineIntGen.sample.get
  val mobileNumberWithIntCode: String = exampleMobileIntGen.sample.get

  val phoneNumbersWithIntCode: PhoneNumber = PhoneNumber(daytimePhoneWithIntCode, Some(mobileNumberWithIntCode))

  val charityContactDetails: CharityContactDetails = CharityContactDetails(
    daytimePhone = daytimePhone,
    mobilePhone = Some(mobileNumber),
    emailAddress = charityEmail
  )

  val nomineeOrganisationContactDetails: OrganisationNomineeContactDetails = 
    OrganisationNomineeContactDetails(daytimePhone, organisationEmail)
  
  def replacePlaceholders(inString: String): String =
    inString
      .replaceAll("__ACCOUNTNAME__", accountName)
      .replaceAll("__SORTCODE__", sortCode)
      .replaceAll("__ACCOUNTNUMBER__", accountNumber)
      .replaceAll("__ROLLNUMBER__", rollNumber)
      .replaceAll("__NINO1__",nino)
      .replaceAll("__NINOWITHSPACE1__",ninoWithSpaces)
      .replaceAll("__NINOWITHSPACE2__",nino2WithSpaces)
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
}
