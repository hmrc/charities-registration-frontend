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

package forms.regulatorsAndDocuments

import java.time.LocalDate

import forms.behaviours.DateBehaviours
import play.api.data.FormError
import utils.TimeMachine

class WhenGoverningDocumentApprovedFormProviderSpec extends DateBehaviours {

  val timeMachine: TimeMachine = new TimeMachine {
  }

  val form = new WhenGoverningDocumentApprovedFormProvider(timeMachine = new TimeMachine)()

  val year: Int = 1000
  val month: Int = 1
  val dayOfMonth: Int = 1
  val fakeNow: LocalDate = timeMachine.now()

  ".value" should {

    val validData = datesBetween(
      min = LocalDate.of(year, month, dayOfMonth),
      max = fakeNow
    )

    behave like dateField(form, "date", validData)

    behave like mandatoryDateField(form, "date", "whenGoverningDocumentApproved.error.required.all")

    behave like dateFieldWithMax(form, "date",
      max = fakeNow,
      FormError("date", "whenGoverningDocumentApproved.error.future", List("day", "month", "year"))
    )
  }
}