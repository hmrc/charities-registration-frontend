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

import forms.behaviours.StringFieldBehaviours
import models.ScottishRegulatorRegNumber
import play.api.data.FormError

class ScottishRegulatorRegNumberFormProviderSpec extends StringFieldBehaviours {


  val formProvider = new ScottishRegulatorRegNumberFormProvider()
  val form = formProvider()

  ".registrationNumber" must {

    val requiredKey = "scottishRegulatorRegNumber.error.required"


    val fieldName = "registrationNumber"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "ScottishRegulatorRegNumberFormProvider" must {

    val scottishRegulatorRegNumber = ScottishRegulatorRegNumber("SC045673")

    "apply ScottishRegulatorRegNumber correctly" in {

      val details = form.bind(
        Map(
          "registrationNumber" -> scottishRegulatorRegNumber.registrationNumber,
        )
      ).get

      details.registrationNumber mustBe scottishRegulatorRegNumber.registrationNumber
    }

    "unapply ScottishRegulatorRegNumber correctly" in {
   val filled = form.fill(scottishRegulatorRegNumber)
      filled("registrationNumber").value.value mustBe scottishRegulatorRegNumber.registrationNumber
    }
  }

  "validateRegistrationNumber" must {

    "valid for SC045673" in {

      "SC045673" must fullyMatch regex formProvider.validateRegistrationNumber
    }

    "valid for 01632" in {

      "01632" mustNot fullyMatch regex formProvider.validateRegistrationNumber
    }
  }
}
