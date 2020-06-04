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
import play.api.data.FormError

class NIRegulatorRegNumberFormProviderSpec extends StringFieldBehaviours {

  val formProvider = new NIRegulatorRegNumberFormProvider()
  val form = formProvider()

  ".nIRegistrationNumber" must {

    val requiredKey = "NIRegulatorRegNumber.error.required"


    val fieldName = "nIRegistrationNumber"

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

  "NIRegulatorRegNumberFormProvider" must {

    val nIRegulatorRegNumber = "123456"

    "apply NIRegulatorRegNumber correctly" in {

      val details = form.bind(
        Map(
          "nIRegistrationNumber" -> nIRegulatorRegNumber,
        )
      ).get

      details mustBe nIRegulatorRegNumber
    }

    "unapply NIRegulatorRegNumber correctly" in {
      val filled = form.fill(nIRegulatorRegNumber)
      filled("nIRegistrationNumber").value.value mustBe nIRegulatorRegNumber
    }
  }

  "validateRegistrationNumberNI" must {

    "valid for 123456" in {

      "123456" must fullyMatch regex formProvider.validateRegistrationNumberNI
    }

    "valid for 01632 960" in {

      "01632 960" mustNot fullyMatch regex formProvider.validateRegistrationNumberNI
    }
  }



}
