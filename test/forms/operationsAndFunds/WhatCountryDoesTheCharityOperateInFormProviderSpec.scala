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

package forms.operationsAndFunds

import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}

class WhatCountryDoesTheCharityOperateInFormProviderSpec extends StringFieldBehaviours {

  private val form: Form[String] = inject[WhatCountryDoesTheCharityOperateInFormProvider].apply()
  private val formProvider: WhatCountryDoesTheCharityOperateInFormProvider = inject[WhatCountryDoesTheCharityOperateInFormProvider]


  ".country" must {

    val fieldName = "country"
    val requiredKey = "whatCountryDoesTheCharityOperateIn.error.required"
    val lengthKey = "whatCountryDoesTheCharityOperateIn.error.length"
    val maxLengthCountry = 50
    val invalidKey = "whatCountryDoesTheCharityOperateIn.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthCountry,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthCountry))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "abc@&",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFields))
    )

  }


}
