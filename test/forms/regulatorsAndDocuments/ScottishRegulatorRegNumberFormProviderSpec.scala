/*
 * Copyright 2022 HM Revenue & Customs
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

class ScottishRegulatorRegNumberFormProviderSpec extends StringFieldBehaviours {

  private val formProvider: ScottishRegulatorRegNumberFormProvider = inject[ScottishRegulatorRegNumberFormProvider]
  private val form: Form[String] = formProvider()

  ".registrationNumber" must {

    val fieldName = "registrationNumber"
    val requiredKey = "scottishRegulatorRegNumber.error.required"
    val invalidKey = "scottishRegulatorRegNumber.error.format"

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
      "123456&",
      FormError(fieldName, invalidKey, Seq(formProvider.validateRegistrationNumber))
    )
  }

  "ScottishRegulatorRegNumberFormProvider" must {

    val scottishRegulatorRegNumber = "SC045673"

    "apply ScottishRegulatorRegNumber correctly" in {

      val details = form.bind(
        Map(
          "registrationNumber" -> scottishRegulatorRegNumber,
        )
      ).get

      details mustBe scottishRegulatorRegNumber
    }

    "unapply ScottishRegulatorRegNumber correctly" in {
   val filled = form.fill(scottishRegulatorRegNumber)
      filled("registrationNumber").value.value mustBe scottishRegulatorRegNumber
    }
  }

  "validateRegistrationNumber" must {

    "valid for SC045673" in {

      "SC045673" must fullyMatch regex formProvider.validateRegistrationNumber
    }

    "valid for 01632" in {

      "01632" mustNot fullyMatch regex formProvider.validateRegistrationNumber
    }
  }
}
