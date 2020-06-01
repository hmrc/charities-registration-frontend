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

package forms.regulatorsAndDocuments

import forms.behaviours.StringFieldBehaviours
import models.CharityOtherRegulatorDetails
import play.api.data.FormError

class CharityOtherRegulatorDetailsFormProviderSpec extends StringFieldBehaviours {

  val maxLengthRegulatorName = 100
  val maxLengthRegistrationNumber = 20
  val formProvider = new CharityOtherRegulatorDetailsFormProvider()
  val form = formProvider()

  ".regulatorName" must {

    val fieldName = "regulatorName"
    val requiredKey = "charityOtherRegulatorDetails.regulatorName.error.required"
    val lengthKey = "charityOtherRegulatorDetails.regulatorName.error.length"

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
  }

  ".registrationNumber" must {

    val fieldName = "registrationNumber"
    val requiredKey = "charityOtherRegulatorDetails.registrationNumber.error.required"
    val lengthKey = "charityOtherRegulatorDetails.registrationNumber.error.length"

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
  }


  "CharityOtherRegulatorDetailsFormProvider" must {

    val charityOtherRegulatorDetails = CharityOtherRegulatorDetails("ORegulatorName", "1234567")

    "apply CharityOtherRegulatorDetails correctly" in {

      val details = form.bind(
        Map(
          "regulatorName" -> charityOtherRegulatorDetails.regulatorName,
          "registrationNumber" -> charityOtherRegulatorDetails.registrationNumber,
        )
      ).get

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

      "ORegulatorName" must fullyMatch regex formProvider.validateFields
    }

    "ORegulatorName" in {

      "ORegulatorName&" mustNot fullyMatch regex formProvider.validateFields
    }
  }

  "validateRegistrationNumber" must {

    "valid for 123456" in {

      "123456" must fullyMatch regex formProvider.validateFields
    }

    "valid for 01632 960" in {

      "123456&" mustNot fullyMatch regex formProvider.validateFields
    }
  }

}