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

import models.BankDetails

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

  def replacePlaceholders(inString: String): String =
    inString
      .replaceAll("__ACCOUNTNAME__", accountName)
      .replaceAll("__SORTCODE__", sortCode)
      .replaceAll("__ACCOUNTNUMBER__", accountNumber)
      .replaceAll("__ROLLNUMBER__", rollNumber)
}
