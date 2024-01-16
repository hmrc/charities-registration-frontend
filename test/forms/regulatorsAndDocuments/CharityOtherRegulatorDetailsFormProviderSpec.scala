/*
 * Copyright 2024 HM Revenue & Customs
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

    val charityOtherRegulatorDetails = CharityOtherRegulatorDetails("ORegulatorName", "1234567")

    "apply CharityOtherRegulatorDetails correctly" in {

      val details = form
        .bind(
          Map(
            "regulatorName"      -> charityOtherRegulatorDetails.regulatorName,
            "registrationNumber" -> charityOtherRegulatorDetails.registrationNumber
          )
        )
        .get

      details.regulatorName mustBe charityOtherRegulatorDetails.regulatorName
      details.registrationNumber mustBe charityOtherRegulatorDetails.registrationNumber
    }

    "unapply charityOtherRegulatorDetails correctly" in {
      val filled = form.fill(charityOtherRegulatorDetails)
      filled("regulatorName").value.value mustBe charityOtherRegulatorDetails.regulatorName
      filled("registrationNumber").value.value mustBe charityOtherRegulatorDetails.registrationNumber
    }
  }

  "regulatorName" must {

    "valid for ORegulatorName" in {

      "ORegulatorName" must fullyMatch regex formProvider.validateFieldWithFullStop
    }

    "ORegulatorName" in {

      "ORegulatorName&" mustNot fullyMatch regex formProvider.validateFieldWithFullStop
    }
  }

  "validateRegistrationNumber" must {

    "valid for 123456" in {

      "123456" must fullyMatch regex formProvider.validateFieldWithFullStop
    }

    "valid for 01632 960" in {

      "123456&" mustNot fullyMatch regex formProvider.validateFieldWithFullStop
    }
  }

}
