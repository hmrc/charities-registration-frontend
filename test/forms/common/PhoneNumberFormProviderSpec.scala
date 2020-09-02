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

package forms.common

import forms.behaviours.StringFieldBehaviours
import models.PhoneNumber
import play.api.data.{Form, FormError}

class PhoneNumberFormProviderSpec extends StringFieldBehaviours {

  private val formProvider: PhoneNumberFormProvider = inject[PhoneNumberFormProvider]
  private val form: Form[PhoneNumber] = formProvider("authorisedOfficialsPhoneNumber")

  ".mainPhoneNumber" must {

    val requiredKey = "authorisedOfficialsPhoneNumber.mainPhoneNumber.error.required"
    val invalidKey = "authorisedOfficialsPhoneNumber.mainPhoneNumber.error.format"

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

    behave like fieldWithRegex(
      form,
      fieldName,
      "invalidPhone",
      FormError(fieldName, invalidKey, Seq(formProvider.validateTelephoneNumber))
    )
  }

  ".alternativePhoneNumber" must {

    val fieldName = "alternativePhoneNumber"
    val invalidKey = "authorisedOfficialsPhoneNumber.alternativePhoneNumber.error.format"

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

  "AuthorisedOfficialsPhoneNumberFormProvider" must {

    val authorisedOfficialsPhoneNumber = PhoneNumber("01632 960 001", "01632 960 001")

    "apply AuthorisedOfficialsPhoneNumber correctly" in {

      val details = form.bind(
        Map(
          "mainPhoneNumber" -> authorisedOfficialsPhoneNumber.daytimePhone,
          "alternativePhoneNumber" -> authorisedOfficialsPhoneNumber.mobilePhone
        )
      ).get

      details.daytimePhone mustBe authorisedOfficialsPhoneNumber.daytimePhone
      details.mobilePhone mustBe authorisedOfficialsPhoneNumber.mobilePhone
    }

    "unapply AuthorisedOfficialsPhoneNumber correctly" in {
      val filled = form.fill(authorisedOfficialsPhoneNumber)
      filled("mainPhoneNumber").value.value mustBe authorisedOfficialsPhoneNumber.daytimePhone
      filled("alternativePhoneNumber").value.value mustBe authorisedOfficialsPhoneNumber.mobilePhone
    }
  }

  "validateTelephoneNumber" must {

    "be valid for 01632 960 001" in {

      "01632 960 001" must fullyMatch regex formProvider.validateTelephoneNumber
    }

    "be valid for 01632 960" in {

      "01632 960" mustNot fullyMatch regex formProvider.validateTelephoneNumber
    }
  }
}
