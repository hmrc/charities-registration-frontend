/*
 * Copyright 2021 HM Revenue & Customs
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

package forms.common

import forms.mappings.Mappings
import javax.inject.Inject
import models.BankDetails
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, default}

class BankDetailsFormProvider @Inject() extends Mappings {

  private[common] val sortCodePattern: String = "^[ -]*(?:\\d[ -]*){6,6}$".r.anchored.toString
  private[common] val accountNumberPattern: String = "^[ -]*(?:\\d[ -]*){6,8}$".r.anchored.toString
  private[common] val rollNumberPattern: String = "^[a-zA-Z0-9- \\/.]*$".r.anchored.toString

  private[common] val maxLengthAccountName = 60
  private[common] val maxLengthRollNumber = 18


  def apply(messagePrefix: String): Form[BankDetails] =
    Form(
      mapping(
        "accountName" -> text(s"$messagePrefix.accountName.error.required")
          .verifying(maxLength(maxLengthAccountName, s"$messagePrefix.accountName.error.length"))
          .verifying(regexp(validateFieldWithFullStop,s"$messagePrefix.accountName.error.format")),
        "sortCode" -> text(s"$messagePrefix.sortCode.error.required")
          .verifying(regexp(sortCodePattern,s"$messagePrefix.sortCode.error.format")),
        "accountNumber" -> text(s"$messagePrefix.accountNumber.error.required")
          .verifying(regexp(accountNumberPattern,s"$messagePrefix.accountNumber.error.format")),
        "rollNumber" -> optional(textWithOneSpace()
          .verifying(maxLength(maxLengthRollNumber, s"$messagePrefix.rollNumber.error.length"))
          .verifying(regexp(rollNumberPattern,s"$messagePrefix.rollNumber.error.format")))
      )(BankDetails.apply)(BankDetails.unapply)
    )

  def apply(messagePrefix: String, charityName: String): Form[BankDetails] =
    Form(
      mapping(
        "accountName" -> default(text(), charityName),
        "sortCode" -> text(s"$messagePrefix.sortCode.error.required")
          .verifying(regexp(sortCodePattern,s"$messagePrefix.sortCode.error.format")),
        "accountNumber" -> text(s"$messagePrefix.accountNumber.error.required")
          .verifying(regexp(accountNumberPattern,s"$messagePrefix.accountNumber.error.format")),
        "rollNumber" -> optional(textWithOneSpace()
          .verifying(maxLength(maxLengthRollNumber, s"$messagePrefix.rollNumber.error.length"))
          .verifying(regexp(rollNumberPattern,s"$messagePrefix.rollNumber.error.format")))
      )(BankDetails.apply)(BankDetails.unapply)
    )
}
