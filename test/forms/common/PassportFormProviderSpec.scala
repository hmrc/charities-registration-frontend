/*
 * Copyright 2025 HM Revenue & Customs
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
import models.Passport
import play.api.data.{Form, FormError}

import java.time.LocalDate

class PassportFormProviderSpec extends StringFieldBehaviours {

  private val messagePrefix: String              = "authorisedOfficialsPassport"
  private val formProvider: PassportFormProvider = inject[PassportFormProvider]
  private val form: Form[Passport]               = formProvider(messagePrefix)
  private val maxLengthCountry                   = 50
  private val maxLengthPassport                  = 30

  ".passportNumber" must {

    val fieldName   = "passportNumber"
    val requiredKey = s"$messagePrefix.passportNumber.error.required"
    val lengthKey   = s"$messagePrefix.passportNumber.error.length"
    val invalidKey  = s"$messagePrefix.passportNumber.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthPassport,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthPassport))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "()invalidFirstName",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  ".country" must {

    val fieldName   = "country"
    val requiredKey = s"$messagePrefix.country.error.required"
    val lengthKey   = s"$messagePrefix.country.error.length"
    val invalidKey  = s"$messagePrefix.country.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthCountry,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthCountry))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "()invalidFirstName",
      FormError(fieldName, invalidKey, Seq(formProvider.validateField))
    )
  }

  ".expiryDate" should {

    val fieldName = "expiryDate"

    behave like mandatoryField(
      form,
      s"$fieldName.day",
      requiredError = FormError(s"$fieldName.day", s"$messagePrefix.error.required.all", Seq("day", "month", "year"))
    )

    "fail to bind an empty date" in {

      val result = form.bind(Map("passportNumber" -> passportNumber, "country" -> passport.country))

      result.errors must contain only FormError(
        s"$fieldName.day",
        s"$messagePrefix.error.required.all",
        Seq("day", "month", "year")
      )
    }

    s"fail to bind a today's date" in {

      val data = Map(
        "passportNumber"    -> passportNumber,
        "country"           -> passport.country,
        s"$fieldName.day"   -> passport.expiryDate.getDayOfMonth.toString,
        s"$fieldName.month" -> passport.expiryDate.getMonthValue.toString,
        s"$fieldName.year"  -> passport.expiryDate.getYear.toString
      )

      val result = form.bind(data)

      result.errors must contain only FormError(fieldName, s"$messagePrefix.error.minimum", Seq("day", "month", "year"))
    }

    s"fail to bind date in past" in {

      val data = Map(
        "passportNumber"    -> passportNumber,
        "country"           -> passport.country,
        s"$fieldName.day"   -> passport.expiryDate.minusDays(1).getDayOfMonth.toString,
        s"$fieldName.month" -> passport.expiryDate.getMonthValue.toString,
        s"$fieldName.year"  -> passport.expiryDate.getYear.toString
      )

      val result = form.bind(data)

      result.errors must contain only FormError(fieldName, s"$messagePrefix.error.minimum", Seq("day", "month", "year"))
    }

    s"fail to bind date with five digits" in {

      val data = Map(
        "passportNumber"    -> passportNumber,
        "country"           -> passport.country,
        s"$fieldName.day"   -> passport.expiryDate.getDayOfMonth.toString,
        s"$fieldName.month" -> passport.expiryDate.getMonthValue.toString,
        s"$fieldName.year"  -> passport.expiryDate.plusYears(9999).getYear.toString
      )

      val result = form.bind(data)

      result.errors must contain only FormError(fieldName, s"$messagePrefix.error.format")
    }

    s"bind valid data if date is in future" in {
      val data = Map(
        "passportNumber"    -> passportNumber,
        "country"           -> passport.country,
        s"$fieldName.day"   -> passport.expiryDate.getDayOfMonth.toString,
        s"$fieldName.month" -> passport.expiryDate.getMonthValue.toString,
        s"$fieldName.year"  -> passport.expiryDate.plusYears(1).getYear.toString
      )

      val result = form.bind(data)
      result.value.get.expiryDate mustEqual futureDate
    }

  }

  "AuthorisedOfficialsPassportFormProvider" must {

    val authorisedOfficialsPassport = passport.copy(expiryDate = LocalDate.now.plusYears(1))

    "apply AuthorisedOfficialsPassport correctly" in {

      val details = form
        .bind(
          Map(
            "passportNumber"   -> authorisedOfficialsPassport.passportNumber,
            "country"          -> authorisedOfficialsPassport.country,
            "expiryDate.year"  -> futureDate.getYear.toString,
            "expiryDate.month" -> futureDate.getMonthValue.toString,
            "expiryDate.day"   -> futureDate.getDayOfMonth.toString
          )
        )
        .get

      details.passportNumber mustBe authorisedOfficialsPassport.passportNumber
      details.country mustBe authorisedOfficialsPassport.country
      details.expiryDate mustBe authorisedOfficialsPassport.expiryDate
    }

    "unapply AuthorisedOfficialsName correctly" in {

      val filled = form.fill(authorisedOfficialsPassport)
      filled("passportNumber").value.value mustBe authorisedOfficialsPassport.passportNumber
      filled("country").value.value mustBe authorisedOfficialsPassport.country
      filled("expiryDate.year").value.value mustBe authorisedOfficialsPassport.expiryDate.getYear.toString
      filled("expiryDate.month").value.value mustBe authorisedOfficialsPassport.expiryDate.getMonthValue.toString
      filled("expiryDate.day").value.value mustBe authorisedOfficialsPassport.expiryDate.getDayOfMonth.toString
    }
  }

  "passportNumber" must {

    "valid for passportNumber" in {

      "passportNumber" must fullyMatch regex formProvider.validateFieldWithFullStop
    }

    "valid for passportNumber&" in {

      "passportNumber&" mustNot fullyMatch regex formProvider.validateFieldWithFullStop
    }
  }

  "country" must {

    "valid for country" in {

      "country" must fullyMatch regex formProvider.validateField
    }

    "valid for country&" in {

      "country&" mustNot fullyMatch regex formProvider.validateField
    }
  }
}
