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

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import utils.TimeMachine

class DateOfBirthFormProvider @Inject()(timeMachine: TimeMachine) extends Mappings {

  private val minYears = 16
  private val startYear = 1900
  private val dayMonth = 1

  def apply(messagePrefix: String): Form[LocalDate] =
    Form(
      "date" -> localDate(
        invalidKey     = s"$messagePrefix.error.invalid",
        allRequiredKey = s"$messagePrefix.error.required.all",
        twoRequiredKey = s"$messagePrefix.error.required.two",
        requiredKey    = s"$messagePrefix.error.required.one",
        nonNumericKey  = s"$messagePrefix.error.nonNumeric"
      ).verifying(maxDate(timeMachine.now().minusYears(minYears), s"$messagePrefix.error.minimum", "day", "month", "year")
      ).verifying(minDate(LocalDate.of(startYear, dayMonth, dayMonth), s"$messagePrefix.error.dateBetween", "day", "month", "year"))
    )
}


