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

package forms.operationsAndFunds

import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}

class CharitableObjectivesFormProviderSpec extends StringFieldBehaviours {

  private val formProvider: CharitableObjectivesFormProvider = inject[CharitableObjectivesFormProvider]
  private val form: Form[String] = formProvider()

  ".value" must {

    val requiredKey = "charitableObjectives.error.required"
    val lengthKey = "charitableObjectives.error.length"
    val invalidKey = "charitableObjectives.error.format"
    val maxLength = 500
    val fieldName = "value"

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
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithNewLine))
    )
  }

  "validatereason" must {

    "valid for abcd" in {

      "ab\n\r\tcd" must fullyMatch regex formProvider.validateFieldWithNewLine
    }

    "valid for abc@" in {

      "abc@" mustNot fullyMatch regex formProvider.validateFieldWithNewLine
    }
  }
}
