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

package forms.behaviours

import play.api.data.{Form, FormError}

class DecimalFieldBehaviours extends FieldBehaviours {

  def decimalField(form: Form[BigDecimal], fieldName: String, invalidCurrencyError: FormError): Unit = {

    "not bind non-numeric numbers" in {

      forAll(nonNumerics -> "nonNumeric") { nonNumeric =>
        val result = form.bind(Map(fieldName -> nonNumeric)).apply(fieldName)
        result.errors mustEqual Seq(invalidCurrencyError)
      }
    }

    "not bind invalid decimals (over 2dp)" in {
      val result = form.bind(Map(fieldName -> "12.123")).apply(fieldName)
      result.errors mustEqual Seq(invalidCurrencyError)
    }

    "not bind numbers with £ on either side" in {
      val result = form.bind(Map(fieldName -> "£123.12£")).apply(fieldName)
      result.errors mustEqual Seq(invalidCurrencyError)
    }
  }

  def decimalFieldWithMinimum(
    form: Form[BigDecimal],
    fieldName: String,
    minimum: BigDecimal,
    expectedError: FormError
  ): Unit =
    "value is less than the minimum" in {

      val result = form.bind(Map(fieldName -> (minimum - 0.01).toString)).apply(fieldName)
      result.errors mustEqual Seq(expectedError)
    }

  def decimalFieldWithMaximum(
    form: Form[BigDecimal],
    fieldName: String,
    maximum: BigDecimal,
    expectedError: FormError
  ): Unit =
    "value is greater than the maximum" in {

      val result = form.bind(Map(fieldName -> (maximum + 0.01).toString)).apply(fieldName)
      result.errors mustEqual Seq(expectedError)
    }

  def decimalFieldWithRange(
    form: Form[BigDecimal],
    fieldName: String,
    minimum: BigDecimal,
    maximum: BigDecimal,
    expectedError: FormError
  ): Unit =
    s"not bind outside the range $minimum to $maximum" when {

      "value is greater than the maximum" in {

        val result = form.bind(Map(fieldName -> (maximum + 0.01).toString)).apply(fieldName)
        result.errors mustEqual Seq(expectedError)
      }

      "value is less than the minimum" in {

        val result = form.bind(Map(fieldName -> (minimum - 0.01).toString)).apply(fieldName)
        result.errors mustEqual Seq(expectedError)
      }
    }
}
