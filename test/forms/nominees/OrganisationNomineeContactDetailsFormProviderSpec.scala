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

    behave like fieldWithMaxLength(
      form,
      fieldName,
      160,
      FormError(fieldName, tooLongKey, Seq(160))
    )
  }

  "OrganisationNomineeContactDetailsFormProvider" must {

    val organisationContactDetails = OrganisationNomineeContactDetails("01632 960 001", "company@org.com")

    "apply OrganisationNomineeContactDetailsSpec correctly" in {

      val details = form
        .bind(
          Map(
            "phoneNumber" -> organisationContactDetails.phoneNumber,
            "email"       -> organisationContactDetails.email
          )
        )
        .get

      details.phoneNumber mustBe organisationContactDetails.phoneNumber
      details.email mustBe organisationContactDetails.email
    }

    "unapply OrganisationNomineeContactDetailsSpec correctly" in {
      val filled = form.fill(organisationContactDetails)
      filled("phoneNumber").value.value mustBe organisationContactDetails.phoneNumber
      filled("email").value.value mustBe organisationContactDetails.email
    }
  }

  "validateTelephoneNumber" must {

    "be valid for 01632 960 001" in {

      "01632 960 001" must fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be valid for 01632 960" in {

      "01632 960" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }
  }

  "validateEmailAddress" must {

    "be valid for testmail@email.com" in {

      "testmail@email.com" must fullyMatch regex formProvider.validateEmailAddress
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
