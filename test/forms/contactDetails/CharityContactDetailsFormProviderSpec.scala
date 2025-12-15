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

package forms.contactDetails

import forms.behaviours.StringFieldBehaviours
import models.CharityContactDetails
import play.api.data.{Form, FormError}

class CharityContactDetailsFormProviderSpec extends StringFieldBehaviours {

  private val maxLength                                       = 160
  private val formProvider: CharityContactDetailsFormProvider = inject[CharityContactDetailsFormProvider]
  private val form: Form[CharityContactDetails]               = formProvider()

  ".mainPhoneNumber" must {

    val fieldName = "mainPhoneNumber"

    val requiredKey = "charityContactDetails.mainPhoneNumber.error.required"
    val invalidKey  = "charityContactDetails.mainPhoneNumber.error.format"

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

    val invalidKey = "charityContactDetails.alternativePhoneNumber.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
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

    val lengthKey   = "charityContactDetails.emailAddress.error.length"
    val requiredKey = "charityContactDetails.emailAddress.error.required"
    val invalidKey  = "charityContactDetails.emailAddress.error.format"

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
      FormError(fieldName, invalidKey, Seq(formProvider.validateEmailAddress))
    )
  }

  "CharityContactDetailsFormProvider" must {

    "apply CharityContactDetails correctly" in {

      val details = form
        .bind(
          Map(
            "mainPhoneNumber"        -> charityContactDetails.daytimePhone,
            "alternativePhoneNumber" -> charityContactDetails.mobilePhone.get,
            "emailAddress"           -> charityContactDetails.emailAddress
          )
        )
        .get

      details.daytimePhone mustBe charityContactDetails.daytimePhone
      details.mobilePhone mustBe charityContactDetails.mobilePhone
      details.emailAddress mustBe charityContactDetails.emailAddress
    }

    "unapply CharityContactDetails correctly" in {
      val filled = form.fill(charityContactDetails)
      filled("mainPhoneNumber").value.value mustBe charityContactDetails.daytimePhone
      filled("alternativePhoneNumber").value.value mustBe charityContactDetails.mobilePhone.get
      filled("emailAddress").value.value mustBe charityContactDetails.emailAddress
    }
  }

  "validateTelephoneNumber" must {

    "valid for 01632 960 001" in {

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

  "emailAddress" must {
    "return valid" when {
      "email is abc@gmail.com" in {
        "abc@gmail.com" must fullyMatch regex formProvider.validateEmailAddress
      }

      "email is firstname.o\'lastname@domain.com" in {
        "firstname.o\'lastname@domain.com" must fullyMatch regex formProvider.validateEmailAddress
      }

      "valid for firstname+lastname@domain.com" in {
        "firstname+lastname@domain.com" must fullyMatch regex formProvider.validateEmailAddress
      }

      "valid for email@subdomain.domain.com" in {
        "email@subdomain.domain.com" must fullyMatch regex formProvider.validateEmailAddress
      }
    }

    "return invalid" when {
      "for email abc@gmail" in {
        "abc@gmail" mustNot fullyMatch regex formProvider.validateEmailAddress
      }

      "for email two-dots..in-local@domain.com" in {
        "two-dots..in-local@domain.com" mustNot fullyMatch regex formProvider.validateEmailAddress
      }

      "for email pipe-in-domain@example.com|gov.uk" in {
        "pipe-in-domain@example.com|gov.uk" mustNot fullyMatch regex formProvider.validateEmailAddress
      }

      "for email brackets(in)local@domain.com" in {
        "brackets(in)local@domain.com" mustNot fullyMatch regex formProvider.validateEmailAddress
      }

      "for email comma-in-domain@domain,gov.uk" in {
        "comma-in-domain@domain,gov.uk" mustNot fullyMatch regex formProvider.validateEmailAddress
      }
    }
  }

  "validateEmailExtraTld" must {
    "return invalid" when {
      "email is abc@gmail.com" in {
        "abc@gmail.com" mustNot fullyMatch regex formProvider.validateEmailExtraTld
      }

      "email is abc@123.com" in {
        "abc@123.com" mustNot fullyMatch regex formProvider.validateEmailExtraTld
      }
    }

    "return valid" when {
      "for email email@123.123.123.123" in {
        "email@123.123.123.123" must fullyMatch regex formProvider.validateEmailExtraTld
      }
    }
  }

}
