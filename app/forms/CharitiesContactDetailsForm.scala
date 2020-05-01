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

package forms

import common.CharitiesValidator
import common.Transformers._
import common.Validation._
import models.ContactDetailsModel
import play.api.data.Form
import play.api.data.Forms._

object CharitiesContactDetailsForm{

  val contactDetailsForm = Form(
    mapping(
      "daytimePhone" -> text.verifying("error.phMobNum_invalid", _.nonEmpty)
        .verifying("error.phMobNum_invalid", field => field.isEmpty || field.matches(CharitiesValidator.validateTelephoneNumber)),
      "mobilePhone" -> optional(text.verifying("error.phMobNum_invalid", field => field.isEmpty || field.matches(CharitiesValidator.validateTelephoneNumber))),
      "emailAddress" -> text.verifying("error.emailAddress_invalid", field => field.isEmpty || field.matches(CharitiesValidator.emailAddressPattern)).verifying("charities_err.error.emailTooLong", field => field.length<160))
    (ContactDetailsModel.apply)(ContactDetailsModel.unapply)
  )
}
