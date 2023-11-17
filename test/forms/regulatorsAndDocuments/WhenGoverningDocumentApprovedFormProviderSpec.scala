/*
 * Copyright 2023 HM Revenue & Customs
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

package forms.regulatorsAndDocuments

import forms.behaviours.DateBehaviours
import play.api.data.{Form, FormError}
import utils.TimeMachine

import java.time.LocalDate

class WhenGoverningDocumentApprovedFormProviderSpec extends DateBehaviours {

  private val timeMachine: TimeMachine = inject[TimeMachine]
  private val form: Form[LocalDate]    = inject[WhenGoverningDocumentApprovedFormProvider].apply()
  private val year: Int                = 1800
  private val month: Int               = 1
  private val dayOfMonth: Int          = 1
  private val fakeNow: LocalDate       = timeMachine.now()

  ".value" should {

    val validData = datesBetween(
      min = LocalDate.of(year, month, dayOfMonth),
      max = fakeNow
    )

    behave like dateField(form, "date", validData)

    behave like mandatoryDateField(
      form,
      "date",
      "whenGoverningDocumentApproved.error.required.all",
      Seq("day", "month", "year")
    )

    behave like dateFieldWithMax(
      form,
      "date",
      max = fakeNow,
      FormError("date", "whenGoverningDocumentApproved.error.future", List("day", "month", "year"))
    )

    behave like dateFieldWithMin(
      form,
      "date",
      min = LocalDate.of(year, month, dayOfMonth),
      FormError("date", s"whenGoverningDocumentApproved.error.dateBetween", List(fakeNow.getYear.toString))
    )
  }
}
