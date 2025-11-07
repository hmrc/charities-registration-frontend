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
import play.api.data.{Form, FormError}

class GoverningDocumentNameFormProviderSpec extends StringFieldBehaviours {

  private val formProvider: GoverningDocumentNameFormProvider = inject[GoverningDocumentNameFormProvider]
  private val form: Form[String]                              = formProvider()

  ".name" must {

    val requiredKey = "governingDocumentName.error.required"
    val invalidKey  = "governingDocumentName.error.format"
    val lengthKey   = "governingDocumentName.error.length"
    val maxLength   = 50
    val fieldName   = "name"

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
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "abc@&",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  "GoverningDocumentNameFormProvider" must {

    val governingDocumentName = "will"

    "apply governingDocumentName correctly" in {

      val details = form
        .bind(
          Map(
            "name" -> governingDocumentName
          )
        )
        .get

      details mustBe governingDocumentName
    }

    "unapply governingDocumentName correctly" in {
      val filled = form.fill(governingDocumentName)
      filled("name").value.value mustBe governingDocumentName
    }
  }

  "validateGoverningDocumentName" must {
    s"valid for $governingDocument" in {
      governingDocument must fullyMatch regex formProvider.validateFieldWithFullStop
    }

    "be invalid if contains @" in {
      s"$governingDocument@" mustNot fullyMatch regex formProvider.validateFieldWithFullStop
    }
  }

}
