/*
 * Copyright 2022 HM Revenue & Customs
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

import forms.behaviours.StringFieldBehaviours
import models.addressLookup.AmendAddressModel
import play.api.data.{Form, FormError}

class AmendAddressFormProviderSpec extends StringFieldBehaviours {

  private val messagePrefix: String                  = "amendAddress"
  private val formProvider: AmendAddressFormProvider = inject[AmendAddressFormProvider]
  private val form: Form[AmendAddressModel]          = formProvider(messagePrefix)
  private val maxLength                              = 35

  ".line1" must {

    val fieldName   = "line1"
    val requiredKey = s"$messagePrefix.addressLine1.error.required"
    val lengthKey   = s"$messagePrefix.addressLine1.error.length"
    val invalidKey  = s"$messagePrefix.addressLine1.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "()invalidAddressLine1",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  ".line2" must {

    val fieldName  = "line2"
    val lengthKey  = s"$messagePrefix.addressLine2.error.length"
    val invalidKey = s"$messagePrefix.addressLine2.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
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
      "()invalidAddressLine2",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  ".line3" must {

    val fieldName  = "line3"
    val lengthKey  = s"$messagePrefix.addressLine3.error.length"
    val invalidKey = s"$messagePrefix.addressLine3.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
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
      "()invalidAddressLine3",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  ".town" must {

    val fieldName   = "town"
    val requiredKey = s"$messagePrefix.townOrCity.error.required"
    val lengthKey   = s"$messagePrefix.townOrCity.error.length"
    val invalidKey  = s"$messagePrefix.townOrCity.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "()town",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  ".country" must {

    val fieldName   = "country"
    val requiredKey = s"$messagePrefix.country.error.required"

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

  "AmendCharitiesOfficialsAddressFormProvider" must {

    val amendCharitiesOfficialsAddress =
      AmendAddressModel("23", Some("Morrison street"), Some(""), "Glasgow", "G58 AN", "GB")

    "unapply AmendCharitiesOfficialsAddress correctly" in {

      val filled = form.fill(amendCharitiesOfficialsAddress)

      filled("line1").value.value mustBe amendCharitiesOfficialsAddress.line1
      filled("line2").value.value mustBe amendCharitiesOfficialsAddress.line2.get
      filled("line3").value.value mustBe amendCharitiesOfficialsAddress.line3.get
      filled("town").value.value mustBe amendCharitiesOfficialsAddress.town
      filled("postcode").value.value mustBe amendCharitiesOfficialsAddress.postcode
      filled("country").value.value mustBe amendCharitiesOfficialsAddress.country
    }

    "validate postcodes" when {

      Seq(
        ("case 1", "MN 99555"),
        ("case 2", "G58 AU"),
        ("case 3", "G 5 8 A U"),
        ("case 4", "999999999999"),
        ("case 5", "ABC123XYZ123"),
        ("case 6", "SW778 2BH")
      ).foreach { case (caseNum, postcode) =>
        s"not accept a UK address with an invalid postcode ($caseNum)" in {
          formProvider
            .validatePostCode(form.fill(AmendAddressModel("23", Some(""), Some(""), "", postcode, "GB")))
            .hasErrors mustBe true
        }
      }

      Seq(("case 1", "G5 8AU"), ("case 2", "G5 8AU"), ("case 3", ""), ("case 4", "EH7 5RG"), ("case 5", "EH7 5RG"))
        .foreach { case (caseNum, postcode) =>
          s"accept UK address with valid or empty postcode ($caseNum)" in {
            formProvider
              .validatePostCode(form.fill(AmendAddressModel("23", Some(""), Some(""), "", postcode, "GB")))
              .hasErrors mustBe false
          }
        }

      Seq(("case 1", "MN *&%99555"), ("case 2", "G58 AU98JNG54FVB09MJSZA234HNBPR693GBJU78")).foreach {
        case (caseNum, postcode) =>
          s"not accept non UK address with an invalid postcode ($caseNum)" in {
            formProvider
              .validatePostCode(form.fill(AmendAddressModel("23", Some(""), Some(""), "", postcode, "FR")))
              .hasErrors mustBe true
          }
      }

      Seq(("case 1", "MN 99555"), ("case 2", "600073")).foreach { case (caseNum, postcode) =>
        s"accept non UK address with valid postcode ($caseNum)" in {
          formProvider
            .validatePostCode(form.fill(AmendAddressModel("23", Some(""), Some(""), "", postcode, "FR")))
            .hasErrors mustBe false
        }
      }
    }
  }

}
