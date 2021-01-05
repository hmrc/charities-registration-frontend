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

package forms.regulatorsAndDocuments

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import utils.TimeMachine

class WhenGoverningDocumentApprovedFormProvider @Inject()(timeMachine: TimeMachine) extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "date" -> localDate(
        invalidKey     = "whenGoverningDocumentApproved.error.invalid",
        allRequiredKey = "whenGoverningDocumentApproved.error.required.all",
        twoRequiredKey = "whenGoverningDocumentApproved.error.required.two",
        requiredKey    = "whenGoverningDocumentApproved.error.required",
        nonNumericKey  = "whenGoverningDocumentApproved.error.nonNumeric"
      ).verifying(maxDate(timeMachine.now(), "whenGoverningDocumentApproved.error.future", "day", "month", "year"))
    )
}


