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

import forms.mappings.Mappings
import javax.inject.Inject
import models.AuthorisedOfficialsName
import play.api.data.Form
import play.api.data.Forms._

class AuthorisedOfficialsNameFormProvider @Inject() extends Mappings {

  private[authorisedOfficials] val validateFields = "^[^@&:)(]+$"
  private[authorisedOfficials] val maxLength = 100

  def apply(): Form[AuthorisedOfficialsName] =
    Form(
      mapping(
      "firstName" -> text("authorisedOfficialsName.firstName.error.required")
        .verifying(maxLength(maxLength, "authorisedOfficialsName.firstName.error.length"))
        .verifying(regexp(validateFields,"authorisedOfficialsName.firstName.error.format")),
      "middleName" -> optional(text()
        .verifying(maxLength(maxLength, "authorisedOfficialsName.middleName.error.length")).
        verifying(regexp(validateFields,"authorisedOfficialsName.middleName.error.format"))),
        "lastName" -> text("authorisedOfficialsName.lastName.error.required")
          .verifying(maxLength(maxLength, "authorisedOfficialsName.lastName.error.length"))
          .verifying(regexp(validateFields,"authorisedOfficialsName.lastName.error.format"))
      )(AuthorisedOfficialsName.apply)(AuthorisedOfficialsName.unapply)
    )
}


