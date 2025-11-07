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
import models.CharityName
import play.api.data.{Form, FormError}

class CharityNameFormProviderSpec extends StringFieldBehaviours {

  private val maxLength                             = 160
  private val fullNameMaxLength                     = 60
  private val formProvider: CharityNameFormProvider = inject[CharityNameFormProvider]
  private val form: Form[CharityName]               = formProvider()

  ".fullName" must {

    val fieldName   = "fullName"
    val requiredKey = "charityName.fullName.error.required"
    val lengthKey   = "charityName.fullName.error.length"
    val invalidKey  = "charityName.fullName.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = fullNameMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(fullNameMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "()invalidName",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  ".operatingName" must {

    val fieldName  = "operatingName"
    val lengthKey  = "charityName.operatingName.error.length"
    val invalidKey = "charityName.operatingName.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
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
      "CName&",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  "CharityNameFormProvider" must {

    "apply CharityName correctly" in {

      val details = form
        .bind(
          Map(
            "fullName"      -> charityName.fullName,
            "operatingName" -> charityName.operatingName.getOrElse("")
          )
        )
        .get

      details.fullName mustBe charityName.fullName
      details.operatingName mustBe charityName.operatingName
    }

    "unapply CharityName correctly" in {
      val filled = form.fill(charityName)
      filled("fullName").value.value mustBe charityName.fullName
      filled("operatingName").value.value mustBe charityName.operatingName.get
    }
  }

  "fullName" must {
    s"valid for $charityFullName" in {
      charityFullName must fullyMatch regex formProvider.validateFieldWithFullStop
    }

    s"valid for $charityFullName&" in {

      s"$charityFullName&" mustNot fullyMatch regex formProvider.validateFieldWithFullStop
    }
  }

  "operatingName" must {
    s"valid for $charityOperatingName" in {
      charityOperatingName must fullyMatch regex formProvider.validateFieldWithFullStop
    }

    s"not valid be valid if contains&" in {
      s"$charityOperatingName&" mustNot fullyMatch regex formProvider.validateFieldWithFullStop
    }
  }
}
