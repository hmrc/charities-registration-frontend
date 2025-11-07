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

import scala.util.Random

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
            "mainPhoneNumber"        -> daytimePhone,
            "alternativePhoneNumber" -> mobileNumber,
            "emailAddress"           -> charityEmail
          )
        )
        .get

      details.daytimePhone mustBe daytimePhone
      details.mobilePhone mustBe Some(mobileNumber)
      details.emailAddress mustBe charityEmail
    }

    "unapply CharityContactDetails correctly" in {
      val filled = form.fill(charityContactDetails)
      filled("mainPhoneNumber").value.value mustBe daytimePhone
      filled("alternativePhoneNumber").value.value mustBe mobileNumber
      filled("emailAddress").value.value mustBe charityEmail
    }
  }

  "validateTelephoneNumber" must {
    s"be valid for $daytimePhone" in {
      daytimePhone must fullyMatch regex formProvider.validateTelephoneNumber
    }

    s"be invalid for short numbers like ${daytimePhone.dropRight(4)}" in {
      daytimePhone.dropRight(4) mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be invalid for special chars such as numbers starting (0)" in {
      s"(0)${daytimePhone.drop(1)}" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be invalid if containing hyphens" in {
      s"1${daytimePhone.drop(2).replace(' ', '-')}" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be invalid if containing dots" in {
      s"1{$daytimePhone.drop(2).replace(' ', '.'))" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be valid for international numbers like those starting +44" in {
      s"+44 ${mobileNumber.drop(1)}" must fullyMatch regex formProvider.validateTelephoneNumber
    }
  }

  "emailAddress" must {
    "return valid" when {
      s"email is $charityEmail" in {
        charityEmail must fullyMatch regex formProvider.validateEmailAddress
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
      "for emails with an invalid domain" in {
        "abc@example" mustNot fullyMatch regex formProvider.validateEmailAddress
      }

      "for email with with two consecutive dots" in {
        "two-dots..in-local@example.com" mustNot fullyMatch regex formProvider.validateEmailAddress
      }

      "for emails containing a pipe character" in {
        "pipe-in-domain@example.com|example.org" mustNot fullyMatch regex formProvider.validateEmailAddress
      }

      "for emails containing parenthesis" in {
        "brackets(in)local@example.com" mustNot fullyMatch regex formProvider.validateEmailAddress
      }

      "for emails containing commas" in {
        "comma-in-domain@example,com" mustNot fullyMatch regex formProvider.validateEmailAddress
      }
    }
  }

  "validateEmailExtraTld" must {
    "return invalid" when {
      "email is abc@example.com" in {
        "abc@example.com" mustNot fullyMatch regex formProvider.validateEmailExtraTld
      }

      "email domain has a number and ends in .com" in {
        s"abc@${Math.abs(Random().nextInt()) % 256}.com" mustNot fullyMatch regex formProvider.validateEmailExtraTld
      }
    }

    "return valid" when {
      "when the domain is a dotted ip" in {
        val a = Math.abs(Random().nextInt()) % 256
        val b = Math.abs(Random().nextInt()) % 256
        val c = Math.abs(Random().nextInt()) % 256
        val d = Math.abs(Random().nextInt()) % 256
        
        s"email@$a.$b.$c.$d" must fullyMatch regex formProvider.validateEmailExtraTld
      }
    }
  }

}
