/*
 * Copyright 2021 HM Revenue & Customs
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

package forms.contactDetails

import forms.mappings.Mappings
import javax.inject.Inject
import models.CharityName
import play.api.data.Form
import play.api.data.Forms._

class CharityNameFormProvider @Inject() extends Mappings {

  private[contactDetails] val maxLength = 160
  private[contactDetails] val fullNameMaxLength = 60

  def apply(): Form[CharityName] =
    Form(
      mapping(
      "fullName" -> text("charityName.fullName.error.required")
        .verifying(maxLength(fullNameMaxLength, "charityName.fullName.error.length"))
        .verifying(regexp(validateFieldWithFullStop,"charityName.fullName.error.format")),
      "operatingName" -> optional(text()
        .verifying(maxLength(maxLength, "charityName.operatingName.error.length")).
        verifying(regexp(validateFieldWithFullStop,"charityName.operatingName.error.format")))
      )(CharityName.apply)(CharityName.unapply)
    )
}


