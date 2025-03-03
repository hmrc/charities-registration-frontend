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

package forms.nominees

import forms.mappings.Mappings
import models.nominees.OrganisationNomineeContactDetails
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.Inject

class OrganisationNomineeContactDetailsFormProvider @Inject() extends Mappings {

  private val maxEmailLength: Int = 160

  def apply(): Form[OrganisationNomineeContactDetails] =
    Form(
      mapping(
        "phoneNumber" -> text("organisationContactDetails.phoneNumber.error.required")
          .verifying(regexp(validateTelephoneNumber, "organisationContactDetails.phoneNumber.error.format")),
        "email"       -> text("organisationContactDetails.email.error.required")
          .verifying(maxLength(maxEmailLength, "organisationContactDetails.email.error.length"))
          .verifying(regexp(validateEmailAddress, "organisationContactDetails.email.error.format"))
          .verifying(
            "charityContactDetails.emailAddress.error.format",
            email => !email.contains("\"") && !email.matches(validateEmailExtraTld)
          )
      )(OrganisationNomineeContactDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
    )
}
