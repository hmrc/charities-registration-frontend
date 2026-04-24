/*
 * Copyright 2026 HM Revenue & Customs
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

package forms.regulatorsAndDocuments

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class CharityCommissionRegistrationNumberFormProvider @Inject() extends Mappings {

  private[regulatorsAndDocuments] val validateRegistrationNumber = """^[0-9]{6,7}$"""

  private def registrationNumberCharactersConstraint(fieldName: String): Constraint[String] = Constraint { input =>
    if (input.forall(char => char.isDigit || Character.isWhitespace(char))) Valid
    else Invalid(s"$fieldName.error.format")
  }

  private def digitsOnly(input: String): String =
    input.filter(_.isDigit)

  def apply(): Form[String] =
    Form(
      "registrationNumber" -> text("charityCommissionRegistrationNumber.error.required")
        .verifying(registrationNumberCharactersConstraint("charityCommissionRegistrationNumber"))
        .transform[String](digitsOnly, identity)
        .verifying(regexp(validateRegistrationNumber, "charityCommissionRegistrationNumber.error.format"))
    )
}
