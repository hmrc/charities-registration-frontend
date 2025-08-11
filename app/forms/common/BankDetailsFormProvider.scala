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

package forms.common

import forms.mappings.Mappings
import models.BankDetails
import play.api.data.Form
import play.api.data.Forms.{default, mapping, optional}
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class BankDetailsFormProvider @Inject() extends Mappings {

  private[common] val rollNumberPattern: String = "^[a-zA-Z0-9- \\/.]*$".r.anchored.toString

  private[common] val maxLengthAccountName = 60
  private[common] val maxLengthRollNumber  = 18

  private def digitsOnlyConstraint(fieldName: String): Constraint[String] = Constraint { input =>
    if (input.forall(_.isDigit)) Valid
    else Invalid(s"$fieldName.error.format")
  }

  private def sortCodeLengthConstraint(fieldName: String): Constraint[String] = Constraint { input =>
    if (input.length == 6) Valid
    else Invalid(s"$fieldName.error.length")
  }

  private def accountNumberLengthConstraint(fieldName: String): Constraint[String] = Constraint { input =>
    if (input.length >= 6 && input.length <= 8) Valid
    else Invalid(s"$fieldName.error.length")
  }

  def apply(messagePrefix: String): Form[BankDetails] =
    Form(
      mapping(
        "accountName"   -> text(s"$messagePrefix.accountName.error.required")
          .verifying(maxLength(maxLengthAccountName, s"$messagePrefix.accountName.error.length"))
          .verifying(regexp(validateFieldWithFullStop, s"$messagePrefix.accountName.error.format")),
        "sortCode"      -> text(s"$messagePrefix.sortCode.error.required")
          .verifying(digitsOnlyConstraint(s"$messagePrefix.sortCode"))
          .transform[String](_.filter(_.isDigit), identity)
          .verifying(sortCodeLengthConstraint(s"$messagePrefix.sortCode")),
        "accountNumber" -> text(s"$messagePrefix.accountNumber.error.required")
          .verifying(digitsOnlyConstraint(s"$messagePrefix.accountNumber"))
          .transform[String](_.filter(_.isDigit), identity)
          .verifying(accountNumberLengthConstraint(s"$messagePrefix.accountNumber")),
        "rollNumber"    -> optional(
          textWithOneSpace()
            .verifying(maxLength(maxLengthRollNumber, s"$messagePrefix.rollNumber.error.length"))
            .verifying(regexp(rollNumberPattern, s"$messagePrefix.rollNumber.error.format"))
        )
      )(BankDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
    )

  def apply(messagePrefix: String, charityName: String): Form[BankDetails] =
    Form(
      mapping(
        "accountName"   -> default(text(), charityName),
        "sortCode"      -> text(s"$messagePrefix.sortCode.error.required")
          .verifying(digitsOnlyConstraint(s"$messagePrefix.sortCode"))
          .transform[String](_.filter(_.isDigit), identity)
          .verifying(sortCodeLengthConstraint(s"$messagePrefix.sortCode")),
        "accountNumber" -> text(s"$messagePrefix.accountNumber.error.required")
          .verifying(digitsOnlyConstraint(s"$messagePrefix.accountNumber"))
          .transform[String](_.filter(_.isDigit), identity)
          .verifying(accountNumberLengthConstraint(s"$messagePrefix.accountNumber")),
        "rollNumber"    -> optional(
          textWithOneSpace()
            .verifying(maxLength(maxLengthRollNumber, s"$messagePrefix.rollNumber.error.length"))
            .verifying(regexp(rollNumberPattern, s"$messagePrefix.rollNumber.error.format"))
        )
      )(BankDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
    )
}
