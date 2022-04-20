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

class OtherFundRaisingFormProviderSpec extends StringFieldBehaviours {
  private val form: Form[String] = inject[OtherFundRaisingFormProvider].apply()
  private val formProvider: OtherFundRaisingFormProvider = inject[OtherFundRaisingFormProvider]


  val requiredKey = "otherFundRaising.error.required"
  val invalidKey = "otherFundRaising.error.format"
  val lengthKey = "otherFundRaising.error.length"
  val maxLength = 95

  ".value" must {
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
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }
  "OtherFundRaisingFormProvider" must {

    val otherFundRaisingMethod = "selling cookies"

    "apply otherFundRaisingMethod correctly" in {

      val details = form.bind(
        Map(
          "value" -> otherFundRaisingMethod,
        )
      ).get

      details mustBe otherFundRaisingMethod
    }

    "unapply otherFundRaisingMethod correctly" in {
      val filled = form.fill(otherFundRaisingMethod)
      filled("value").value.value mustBe otherFundRaisingMethod
    }
  }


}
