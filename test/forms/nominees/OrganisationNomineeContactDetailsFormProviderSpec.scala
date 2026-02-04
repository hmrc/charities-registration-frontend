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

package forms.nominees

import forms.behaviours.StringFieldBehaviours
import models.nominees.OrganisationNomineeContactDetails
import play.api.data.{Form, FormError}

class OrganisationNomineeContactDetailsFormProviderSpec extends StringFieldBehaviours {

  private val formProvider: OrganisationNomineeContactDetailsFormProvider =
    inject[OrganisationNomineeContactDetailsFormProvider]
  private val form: Form[OrganisationNomineeContactDetails]               = formProvider()

  ".phoneNumber" must {

    val requiredKey = "organisationContactDetails.phoneNumber.error.required"
    val invalidKey  = "organisationContactDetails.phoneNumber.error.format"

    val fieldName = "phoneNumber"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "invalidPhone",
      FormError(fieldName, invalidKey, Seq(formProvider.validateTelephoneNumber))
    )
  }

  ".email" must {

    val fieldName  = "email"
    val invalidKey = "organisationContactDetails.email.error.format"
    val tooLongKey = "organisationContactDetails.email.error.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "invalidEmail",
      FormError(fieldName, invalidKey, Seq(formProvider.validateEmailAddress))
    )

    val maxNumberOfChars = 160

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxNumberOfChars,
      FormError(fieldName, tooLongKey, Seq(maxNumberOfChars))
    )
  }

  "OrganisationNomineeContactDetailsFormProvider" must {

    "apply OrganisationNomineeContactDetailsSpec correctly" in {

      val details = form
        .bind(
          Map(
            "phoneNumber" -> daytimePhone,
            "email"       -> organisationEmail
          )
        )
        .get

      details.phoneNumber mustBe daytimePhone
      details.email mustBe organisationEmail
    }

    "unapply OrganisationNomineeContactDetailsSpec correctly" in {
      val filled = form.fill(nomineeOrganisationContactDetails)
      filled("phoneNumber").value.value mustBe daytimePhone
      filled("email").value.value mustBe organisationEmail
    }
  }

  "validateTelephoneNumber" must {

    "be valid for 01632 960 001" in {

      "01632 960 001" must fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be invalid for short numbers like 01632 960" in {

      "01632 960" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be invalid for special chars like (0)1632 960 001" in {

      "(0)1632 960 001" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be invalid for hyphens like 1-632-960-001" in {

      "1-632-960-001" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be invalid for dots like 1.632.960.001" in {

      "1.632.960.001" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be valid for international numbers like +44 777 777 7777" in {

      "+44 777 777 7777" must fullyMatch regex formProvider.validateTelephoneNumber
    }
  }

  "validateEmailAddress" must {

    s"be valid for $organisationEmail" in {

      organisationEmail must fullyMatch regex formProvider.validateEmailAddress
    }

    "be invalid for testmail" in {

      "testmail" mustNot fullyMatch regex formProvider.validateEmailAddress
    }

    "be invalid for testmail@email" in {

      "testmail@email" mustNot fullyMatch regex formProvider.validateEmailAddress
    }

    "be invalid for email.com" in {

      "email.com" mustNot fullyMatch regex formProvider.validateEmailAddress
    }

    "be invalid for aWeirdEmail!%^&$*%&$*(*)@email.com" in {

      "aWeirdEmail!%^&$*%&$*(*)@email.com" mustNot fullyMatch regex formProvider.validateEmailAddress
    }
  }
}
