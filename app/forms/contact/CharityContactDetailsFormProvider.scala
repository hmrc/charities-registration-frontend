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

package forms.contact

import forms.mappings.Mappings
import javax.inject.Inject
import models.{CharityContactDetails}
import utils.CharitiesValidator
import play.api.data.Form
import play.api.data.Forms._

class CharityContactDetailsFormProvider @Inject() extends Mappings {

  def apply(): Form[CharityContactDetails] =
    Form(
      mapping(
        "mainPhoneNumber" -> text("charityContactDetails.mainPhoneNumber.error.format")
          .verifying("charityContactDetails.mainPhoneNumber.error.required", field => field.isEmpty
            || field.matches(CharitiesValidator.validateTelephoneNumber)),
        "alternativePhoneNumber" -> optional(text().verifying("charityContactDetails.alternativePhoneNumber.error.format",
          field => field.isEmpty || field.matches(CharitiesValidator.validateTelephoneNumber))),
        "emailAddress" -> text("charityContactDetails.emailAddress.error.required").verifying("charityContactDetails.emailAddress.error.required",
          _.matches(CharitiesValidator.emailAddressPattern))
          .verifying("charityContactDetails.emailAddress.error.length", model => model.length<160))
      (CharityContactDetails.apply)(CharityContactDetails.unapply)
    )
}
