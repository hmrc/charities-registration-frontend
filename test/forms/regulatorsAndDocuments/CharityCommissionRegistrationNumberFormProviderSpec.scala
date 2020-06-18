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
import play.api.data.{Form, FormError}

class CharityCommissionRegistrationNumberFormProviderSpec extends StringFieldBehaviours {


  val formProvider = new CharityCommissionRegistrationNumberFormProvider()
  val form: Form[String] = formProvider()

  ".registrationNumber" must {

    val requiredKey = "charityCommissionRegistrationNumber.error.required"
    val invalidKey = "charityCommissionRegistrationNumber.error.format"

    val fieldName = "registrationNumber"

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
      "invalidRegistrationNumber",
      FormError(fieldName, invalidKey, Seq(formProvider.validateRegistrationNumber))
    )
  }

  "CharityCommissionRegistrationNumberFormProvider" must {

    val charityCommissionRegistrationNumber = "1234567"

    "apply CharityCommissionRegistrationNumber correctly" in {

      val details = form.bind(
        Map(
          "registrationNumber" -> charityCommissionRegistrationNumber,
        )
      ).get

      details mustBe charityCommissionRegistrationNumber
    }

    "unapply CharityCommissionRegistrationNumber correctly" in {
      val filled = form.fill(charityCommissionRegistrationNumber)
      filled("registrationNumber").value.value mustBe charityCommissionRegistrationNumber
    }
  }

  "validateRegistrationNumber" must {

    "valid for 123456" in {

      "123456" must fullyMatch regex formProvider.validateRegistrationNumber
    }

    "valid for 01632 960" in {

      "01632 960" mustNot fullyMatch regex formProvider.validateRegistrationNumber
    }
  }

}
