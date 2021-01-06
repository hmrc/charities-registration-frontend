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

package forms.operationsAndFunds
import forms.mappings.Mappings
import javax.inject.Inject
import org.joda.time.MonthDay
import play.api.data.Form

class AccountingPeriodEndDateFormProvider @Inject()extends Mappings {

  def apply(): Form[MonthDay] =
    Form(
      "date" -> localDateDayMonth(
        invalidKey = "accountingPeriodEndDate.error.invalid",
        allRequiredKey = "accountingPeriodEndDate.error.required.all",
        requiredKey = "accountingPeriodEndDate.error.required",
        nonNumericKey = "accountingPeriodEndDate.error.nonNumeric",
        leapYearKey = "accountingPeriodEndDate.error.leapYear"
      )
    )
}
