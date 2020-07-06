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

package forms.operationsAndFunds

import forms.behaviours.DateBehaviours
import org.joda.time.{LocalDate, MonthDay}
import play.api.data.Form

class AccountingPeriodEndDateFormProviderSpec extends DateBehaviours {

  private val form: Form[MonthDay] = inject[AccountingPeriodEndDateFormProvider].apply()

  ".value" should {

    val validData = daysBetween(
      min = new LocalDate(2000,1,1),
      max = new LocalDate(2000,12,31)
    )

    ".value" should {

      behave like dayMonthField(form, "date", validData)

      behave like mandatoryDateField(form, "date", "accountingPeriodEndDate.error.required.all")

    }
  }
}