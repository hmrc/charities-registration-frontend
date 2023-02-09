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

package forms.nominees

import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}

class OrganisationNomineeNameFormProviderSpec extends StringFieldBehaviours {

  private val formProvider: OrganisationNomineeNameFormProvider = inject[OrganisationNomineeNameFormProvider]
  private val form: Form[String]                                = formProvider()

  ".name" must {

    val requiredKey = "nameOfOrganisation.error.required"
    val invalidKey  = "nameOfOrganisation.error.format"
    val lengthKey   = "nameOfOrganisation.error.length"
    val maxLength   = 160
    val fieldName   = "name"

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

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "abc@&",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  "OrganisationNomineeNameFormProvider" must {

    val organisationName = "abc"

    "apply organisationName correctly" in {

      val details = form
        .bind(
          Map(
            "name" -> organisationName
          )
        )
        .get

      details mustBe organisationName
    }

    "unapply organisationName correctly" in {
      val filled = form.fill(organisationName)
      filled("name").value.value mustBe organisationName
    }
  }

  "validateOrganisationName" must {

    "valid for abc" in {

      "abc" must fullyMatch regex formProvider.validateField
    }

    "valid for abc@" in {

      "abc@" mustNot fullyMatch regex formProvider.validateField
    }
  }

}
