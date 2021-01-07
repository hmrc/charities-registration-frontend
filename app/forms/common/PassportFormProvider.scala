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
import models.Passport
import play.api.data.Form
import play.api.data.Forms._
import utils.TimeMachine

class PassportFormProvider @Inject()(timeMachine: TimeMachine) extends Mappings {

  private[common] val maxLengthCountry = 50
  private[common] val maxLengthPassport = 30

  def apply(messagePrefix: String): Form[Passport] =
    Form(
      mapping(

        "passportNumber" -> textWithOneSpace(s"$messagePrefix.passportNumber.error.required")
              .verifying(maxLength(maxLengthPassport, s"$messagePrefix.passportNumber.error.length"))
              .verifying(regexp(validateFieldWithFullStop,s"$messagePrefix.passportNumber.error.format")),
        "country" -> text(s"$messagePrefix.country.error.required")
          .verifying(maxLength(maxLengthCountry, s"$messagePrefix.country.error.length"))
          .verifying(regexp(validateField,s"$messagePrefix.country.error.format")),
        "expiryDate" -> localDate(
          invalidKey     = s"$messagePrefix.error.invalid",
          allRequiredKey = s"$messagePrefix.error.required.all",
          twoRequiredKey = s"$messagePrefix.error.required.two",
          requiredKey    = s"$messagePrefix.error.required.one",
          nonNumericKey  = s"$messagePrefix.error.nonNumeric"
        ).verifying(minDate(timeMachine.now().plusDays(1), s"$messagePrefix.error.minimum", "day", "month", "year"))
      )(Passport.apply)(Passport.unapply)
    )
}


