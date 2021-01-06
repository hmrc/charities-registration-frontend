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
import models.CharityContactDetails
import play.api.data.Form
import play.api.data.Forms._

class CharityContactDetailsFormProvider @Inject() extends Mappings {

  private[contactDetails] val maxLength = 160

  def apply(): Form[CharityContactDetails] =
    Form(
      mapping(
        "mainPhoneNumber" -> text("charityContactDetails.mainPhoneNumber.error.required")
          .verifying(regexp(validateTelephoneNumber,"charityContactDetails.mainPhoneNumber.error.format")),
        "alternativePhoneNumber" -> optional(text()
          .verifying(regexp(validateTelephoneNumber,"charityContactDetails.alternativePhoneNumber.error.format"))),
        "emailAddress" -> text("charityContactDetails.emailAddress.error.required")
          .verifying(maxLength(maxLength, "charityContactDetails.emailAddress.error.length"))
          .verifying(regexp(validateEmailAddress, "charityContactDetails.emailAddress.error.format")))
      (CharityContactDetails.apply)(CharityContactDetails.unapply)
    )

}

