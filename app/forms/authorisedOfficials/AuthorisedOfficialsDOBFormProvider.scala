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

package forms.authorisedOfficials

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import utils.TimeMachine

class AuthorisedOfficialsDOBFormProvider @Inject()(timeMachine: TimeMachine) extends Mappings {

  private val minYears = 16

  def apply(): Form[LocalDate] =
    Form(
      "date" -> localDate(
        invalidKey     = "authorisedOfficialsDOB.error.invalid",
        allRequiredKey = "authorisedOfficialsDOB.error.required.all",
        twoRequiredKey = "authorisedOfficialsDOB.error.required.two",
        requiredKey    = "authorisedOfficialsDOB.error.required.one"
      ).verifying(maxDate(timeMachine.now().minusYears(minYears), "authorisedOfficialsDOB.error.minimum", "day", "month", "year"))
    )
}


