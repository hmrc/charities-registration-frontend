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

package forms.nominees

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class WhatIsTheNameOfOrganisationFormProvider @Inject() extends Mappings {

  private[nominees] val validateReason = "^[^@&:)(]+$"
  private[nominees] val maxLength = 160

  def apply(): Form[String] =
    Form(
      "name" -> text("nameOfOrganisation.error.required")
        .verifying(maxLength(maxLength, "nameOfOrganisation.error.length"))
        .verifying(regexp(validateReason,"nameOfOrganisation.error.format"))
    )
}