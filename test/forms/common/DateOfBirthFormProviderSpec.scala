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

package forms.common

import java.time.LocalDate

import forms.behaviours.DateBehaviours
import play.api.data.{Form, FormError}

class DateOfBirthFormProviderSpec extends DateBehaviours {

  private val messagePrefix: String = "authorisedOfficialsDOB"
  private val form: Form[LocalDate] = inject[DateOfBirthFormProvider].apply(messagePrefix)

  ".value" should {
    val minYear = 16
    val validDate = LocalDate.now().minusYears(minYear)

    behave like dateField(form, "date", validDate)

    behave like mandatoryDateField(form, "date", s"$messagePrefix.error.required.all")

    behave like dateFieldWithMax(form, "date",
      max = validDate,
      FormError("date", s"$messagePrefix.error.minimum", List("day", "month", "year"))
    )
  }
}
