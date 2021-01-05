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

package forms.common

import forms.mappings.Mappings
import javax.inject.Inject
import models.{Name, SelectTitle}
import play.api.data.Form
import play.api.data.Forms._

class NameFormProvider @Inject() extends Mappings {
  private[common] val maxLength = 100

  def apply(messagePrefix: String): Form[Name] =
    Form(
      mapping(

        "value" -> enumerable[SelectTitle](s"$messagePrefix.title.error.required"),
        "firstName" -> text(s"$messagePrefix.firstName.error.required")
              .verifying(maxLength(maxLength, s"$messagePrefix.firstName.error.length"))
              .verifying(regexp(validateFieldWithFullStop,s"$messagePrefix.firstName.error.format")),
        "middleName" -> optional(text()
              .verifying(maxLength(maxLength, s"$messagePrefix.middleName.error.length"))
              .verifying(regexp(validateFieldWithFullStop,s"$messagePrefix.middleName.error.format"))),
        "lastName" -> text(s"$messagePrefix.lastName.error.required")
              .verifying(maxLength(maxLength, s"$messagePrefix.lastName.error.length"))
              .verifying(regexp(validateFieldWithFullStop,s"$messagePrefix.lastName.error.format"))
      )(Name.apply)(Name.unapply)
    )
}


