/*
 * Copyright 2020 HM Revenue & Customs
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

package forms.charityInformation

import forms.behaviours.StringFieldBehaviours
import models.CharityContactDetails
import play.api.data.{Form, FormError}

class CharityContactDetailsFormProviderSpec extends StringFieldBehaviours {

  private val maxLength = 160
  private val formProvider: CharityContactDetailsFormProvider = inject[CharityContactDetailsFormProvider]
  private val form: Form[CharityContactDetails] = formProvider()

  ".mainPhoneNumber" must {

    val fieldName = "mainPhoneNumber"

    val requiredKey = "charityContactDetails.mainPhoneNumber.error.required"
    val invalidKey = "charityContactDetails.mainPhoneNumber.error.format"

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

  ".alternativePhoneNumber" must {

    val fieldName = "alternativePhoneNumber"

    val requiredKey = "charityContactDetails.alternativePhoneNumber.error.required"
    val invalidKey = "charityContactDetails.alternativePhoneNumber.error.format"

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

  ".emailAddress" must {

    val fieldName = "emailAddress"

    val lengthKey = "charityContactDetails.emailAddress.error.length"
    val requiredKey = "charityContactDetails.emailAddress.error.required"
    val invalidKey = "charityContactDetails.emailAddress.error.format"

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

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "invalidEmail",
      FormError(fieldName, invalidKey, Seq(formProvider.emailAddressPattern))
    )
  }

  "CharityContactDetailsFormProvider" must {

    val charityContactDetails = CharityContactDetails("01632 960 001", "01632 960 001", "abc@gmail.com")

    "apply CharityContactDetails correctly" in {

      val details = form.bind(
        Map(
          "mainPhoneNumber" -> charityContactDetails.daytimePhone,
          "alternativePhoneNumber" -> charityContactDetails.mobilePhone,
          "emailAddress"-> charityContactDetails.emailAddress
        )
      ).get

      details.daytimePhone mustBe charityContactDetails.daytimePhone
      details.mobilePhone mustBe charityContactDetails.mobilePhone
      details.emailAddress mustBe charityContactDetails.emailAddress
    }

    "unapply CharityContactDetails correctly" in {
      val filled = form.fill(charityContactDetails)
      filled("mainPhoneNumber").value.value mustBe charityContactDetails.daytimePhone
      filled("alternativePhoneNumber").value.value mustBe charityContactDetails.mobilePhone
      filled("emailAddress").value.value mustBe charityContactDetails.emailAddress
    }
  }

  "validateTelephoneNumber" must {

    "valid for 01632 960 001" in {

      "01632 960 001" must fullyMatch regex formProvider.validateTelephoneNumber
    }

    "valid for 01632 960" in {

      "01632 960" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }
  }

  "emailAddress" must {

    "valid for abc@gmail.com" in {

      "abc@gmail.com" must fullyMatch regex formProvider.emailAddressPattern
    }

    "valid for abc@gmail" in {

      "abc@gmail" mustNot fullyMatch regex formProvider.emailAddressPattern
    }
  }

}
