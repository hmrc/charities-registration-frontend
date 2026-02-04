/*
 * Copyright 2026 HM Revenue & Customs
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

import forms.behaviours.CheckboxFieldBehaviours
import models.operations.CharitablePurposes
import play.api.data.{Form, FormError}

class CharitablePurposesFormProviderSpec extends CheckboxFieldBehaviours {

  private val form: Form[Set[CharitablePurposes]] = inject[CharitablePurposesFormProvider].apply()

  ".value" must {

    val fieldName   = "value"
    val requiredKey = "charitablePurposes.error.required"

    behave like checkboxField[CharitablePurposes](
      form,
      fieldName,
      validValues = CharitablePurposes.values.toIndexedSeq,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }

}
