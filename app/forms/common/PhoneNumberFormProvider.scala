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
import models.PhoneNumber
import play.api.data.Form
import play.api.data.Forms._

class PhoneNumberFormProvider @Inject() extends Mappings {

  def apply(messagePrefix: String): Form[PhoneNumber] =
    Form(
      mapping(
        "mainPhoneNumber" -> text(s"$messagePrefix.mainPhoneNumber.error.required")
          .verifying(regexp(validateTelephoneNumber,s"$messagePrefix.mainPhoneNumber.error.format")),
        "alternativePhoneNumber" -> optional(text()
          .verifying(regexp(validateTelephoneNumber,s"$messagePrefix.alternativePhoneNumber.error.format"))))
    (PhoneNumber.apply)(PhoneNumber.unapply)
  )
}
