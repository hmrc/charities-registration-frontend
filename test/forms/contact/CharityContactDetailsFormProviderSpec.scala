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

package forms.contact

import forms.behaviours.StringFieldBehaviours
import models.{CharityContactDetails, CharityName}
import play.api.data.FormError

class CharityContactDetailsFormProviderSpec extends StringFieldBehaviours {

  val maxLength = 160
  val formProvider = new CharityContactDetailsFormProvider()
  val form = formProvider()

  ".mainPhoneNumber" must {

    val requiredKey = "charityContactDetails.mainPhoneNumber.error.required"


    val fieldName = "mainPhoneNumber"

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
  }

  ".alternativePhoneNumber" must {

    val fieldName = "alternativePhoneNumber"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

  }
  ".emailAddress" must {

    val fieldName = "emailAddress"

    val lengthKey = "charityContactDetails.emailAddress.error.length"
    val requiredKey = "charityContactDetails.emailAddress.error.required"

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
  }

  "CharityContactDetailsFormProvider" must {

    val charityContactDetails = CharityContactDetails("01632 960 001",Some("01632 960 001"),"abc@gmail.com")

    "apply CharityContactDetails correctly" in {

      val details = form.bind(
        Map(
          "mainPhoneNumber" -> charityContactDetails.daytimePhone,
          "alternativePhoneNumber" -> charityContactDetails.mobilePhone.getOrElse(""),
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
      filled("alternativePhoneNumber").value.value mustBe charityContactDetails.mobilePhone.get
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

  "emailAddressPattern" must {

    "valid for abc@gmail.com" in {

      "abc@gmail.com" must fullyMatch regex formProvider.emailAddressPattern
    }

    "valid for abc@gmail" in {

      "abc@gmail" mustNot fullyMatch regex formProvider.emailAddressPattern
    }
  }

}
