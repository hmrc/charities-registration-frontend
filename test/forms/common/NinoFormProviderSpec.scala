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

package forms.common

import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}

class NinoFormProviderSpec extends StringFieldBehaviours {

  private val messagePrefix: String          = "authorisedOfficialsNino"
  private val formProvider: NinoFormProvider = inject[NinoFormProvider]
  private val form: Form[String]             = formProvider(messagePrefix)

  ".nationalInsuranceNumber" must {

    val requiredKey = s"$messagePrefix.error.required"
    val invalidKey  = s"$messagePrefix.error.format"

    val fieldName = "nino"

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
      "invalidNINO",
      FormError(fieldName, invalidKey, Seq(formProvider.ninoPattern))
    )
  }

  "AuthorisedOfficialsNINOFormProvider" must {

    val nino = "QQ 12 345 6C"

    "apply nationlInsuranceNumber correctly" in {

      val details = form
        .bind(
          Map(
            "nino" -> nino
          )
        )
        .get

      details mustBe nino
    }

    "unapply nationlInsuranceNumber correctly" in {
      val filled = form.fill(nino)
      filled("nino").value.value mustBe nino
    }
  }

  "validateNINO" must {

    "valid for QQ12 345 6C" in {

      "QQ12 345 6C" must fullyMatch regex formProvider.ninoPattern
    }

    "valid for 1Q12 345 6C" in {

      "1Q12 345 6C" mustNot fullyMatch regex formProvider.ninoPattern
    }
  }

}
