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

package forms.authorisedOfficials

import forms.behaviours.OptionFieldBehaviours
import models.AuthOfficials.AuthorisedOfficialsPosition
import play.api.data.{Form, FormError}

class AuthorisedOfficialsPositionFormProviderSpec extends OptionFieldBehaviours {

  private val form: Form[AuthorisedOfficialsPosition] = inject[AuthorisedOfficialsPositionFormProvider].apply()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "authorisedOfficialsPosition.error.required"

    behave like optionsField[AuthorisedOfficialsPosition](
      form,
      fieldName,
      validValues  = AuthorisedOfficialsPosition.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
