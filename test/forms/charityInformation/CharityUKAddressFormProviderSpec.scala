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
import models.CharityUKAddress
import play.api.data.FormError

class CharityUKAddressFormProviderSpec extends StringFieldBehaviours {

  val maxLength = 35
  val formProvider = new CharityUKAddressFormProvider()
  val form = formProvider()

  ".addressLine1" must {

    val requiredKey = "charityUKAddress.addressLine1.required"
    val lengthKey = "charityUKAddress.addressLine1.error.length"

    val fieldName = "addressLine1"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
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
  }

  ".addressLine2" must {

    val lengthKey = "charityUKAddress.addressLine2.error.length"

    val fieldName = "addressLine2"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }

  ".addressLine3" must {

    val lengthKey = "charityUKAddress.addressLine3.error.length"

    val fieldName = "addressLine3"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }

  ".townCity" must {

    val requiredKey = "charityUKAddress.townCity.required"
    val lengthKey = "charityUKAddress.townCity.error.length"

    val fieldName = "townCity"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
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
  }

  ".postcode" must {

    val requiredKey = "charityUKAddress.postcode.required"

    val fieldName = "postcode"

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

  "CharityUKAddressFormProvider" must {

    val charityUKAddress = CharityUKAddress("123 Morrison st",Some("2/3"), Some("a-b"), "Edinburgh", "EH12 5WU")

    "apply CharityUKAddress correctly" in {

      val details = form.bind(
        Map(
          "addressLine1" -> charityUKAddress.addressLine1,
          "addressLine2" -> charityUKAddress.addressLine2.getOrElse(""),
          "addressLine3" -> charityUKAddress.addressLine3.getOrElse(""),
          "townCity" -> charityUKAddress.townCity,
          "postcode"-> charityUKAddress.postcode
        )
      ).get

      details.addressLine1 mustBe charityUKAddress.addressLine1
      details.addressLine2 mustBe charityUKAddress.addressLine2
      details.addressLine3 mustBe charityUKAddress.addressLine3
      details.townCity mustBe charityUKAddress.townCity
      details.postcode mustBe charityUKAddress.postcode
    }

    "unapply CharityUKAddress correctly" in {
      val filled = form.fill(charityUKAddress)
      filled("addressLine1").value.value mustBe charityUKAddress.addressLine1
      filled("addressLine2").value.value mustBe charityUKAddress.addressLine2.get
      filled("addressLine3").value.value mustBe charityUKAddress.addressLine3.get
      filled("townCity").value.value mustBe charityUKAddress.townCity
      filled("postcode").value.value mustBe charityUKAddress.postcode
    }
  }

  "validateAddressLine1" must {

    "valid for 123, Morrison st" in {

      "123, Morrison st" must fullyMatch regex formProvider.validateFields
    }

    "valid for 123: Morrison st" in {

      "123@ Morrison st" mustNot fullyMatch regex formProvider.validateFields
    }
  }

  "validateAddressLine2" must {

    "valid for 4/11" in {

      "4/11" must fullyMatch regex formProvider.validateFields
    }

    "valid for 4&11" in {

      "4&11" mustNot fullyMatch regex formProvider.validateFields
    }
  }

  "validateAddressLine3" must {

    "valid for a-b" in {

      "a-b" must fullyMatch regex formProvider.validateFields
    }

    "valid for a:b" in {

      "a:b" mustNot fullyMatch regex formProvider.validateFields
    }
  }

  "validateTownCity" must {

    "valid for Edinburgh" in {

      "Edinburgh" must fullyMatch regex formProvider.validateFields
    }

    "valid for Edinburgh)" in {

      "Edinburgh)" mustNot fullyMatch regex formProvider.validateFields
    }
  }

  "validatePostcode" must {

    "valid for EH12 5WU" in {

      "EH12 5WU" must fullyMatch regex formProvider.validateFields
    }

    "valid for EH125WU)" in {

      "EH125WU)" mustNot fullyMatch regex formProvider.validatePostcode
    }
  }

}
