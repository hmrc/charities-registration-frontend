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

package forms.regulatorsAndDocuments

import forms.behaviours.StringFieldBehaviours
import models.CharityOtherRegulatorDetails
import play.api.data.{Form, FormError}

class CharityOtherRegulatorDetailsFormProviderSpec extends StringFieldBehaviours {

  private val maxLengthRegulatorName                                 = 100
  private val maxLengthRegistrationNumber                            = 20
  private val formProvider: CharityOtherRegulatorDetailsFormProvider = inject[CharityOtherRegulatorDetailsFormProvider]
  private val form: Form[CharityOtherRegulatorDetails]               = formProvider()

  ".regulatorName" must {

    val fieldName   = "regulatorName"
    val requiredKey = "charityOtherRegulatorDetails.regulatorName.error.required"
    val lengthKey   = "charityOtherRegulatorDetails.regulatorName.error.length"
    val invalidKey  = "charityOtherRegulatorDetails.regulatorName.error.format"

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
      maxLength = maxLengthRegulatorName,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthRegulatorName))
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "123456&",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  ".registrationNumber" must {

    val fieldName   = "registrationNumber"
    val requiredKey = "charityOtherRegulatorDetails.registrationNumber.error.required"
    val lengthKey   = "charityOtherRegulatorDetails.registrationNumber.error.length"
    val invalidKey  = "charityOtherRegulatorDetails.registrationNumber.error.format"

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
      maxLength = maxLengthRegistrationNumber,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthRegistrationNumber))
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "123456&",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  "CharityOtherRegulatorDetailsFormProvider" must {

    "apply CharityOtherRegulatorDetails correctly" in {

      val details = form
        .bind(
          Map(
            "regulatorName"      -> charityRegulatorDetails.regulatorName,
            "registrationNumber" -> charityRegulatorDetails.registrationNumber
          )
        )
        .get

      details.regulatorName mustBe charityRegulatorDetails.regulatorName
      details.registrationNumber mustBe charityRegulatorDetails.registrationNumber
    }

    "unapply charityOtherRegulatorDetails correctly" in {
      val filled = form.fill(charityRegulatorDetails)
      filled("regulatorName").value.value mustBe charityRegulatorDetails.regulatorName
      filled("registrationNumber").value.value mustBe charityRegulatorDetails.registrationNumber
    }
  }

  "regulatorName" must {
    s"be valid for $charityRegulatorDetails.regulatorName" in {
      charityRegulatorDetails.regulatorName must fullyMatch regex formProvider.validateFieldWithFullStop
    }

    "be invalid if contains &" in {
      s"${charityRegulatorDetails.regulatorName}&" mustNot fullyMatch regex formProvider.validateFieldWithFullStop
    }
  }

  "validateRegistrationNumber" must {
    s"be valid for ${charityRegulatorDetails.registrationNumber}" in {
      charityRegulatorDetails.registrationNumber must fullyMatch regex formProvider.validateFieldWithFullStop
    }

    "be invalid if contains a &" in {
      s"${charityRegulatorDetails.registrationNumber}&"
    }
  }

}
