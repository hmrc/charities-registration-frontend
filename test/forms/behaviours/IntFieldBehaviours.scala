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

trait IntFieldBehaviours extends FieldBehaviours {

  def intField(form: Form[?], fieldName: String, nonNumericError: FormError, wholeNumberError: FormError): Unit = {
    forAll(nonNumerics -> "nonNumeric") { nonNumeric =>
      s"not bind non-numeric numbers value $nonNumeric" in {
        val result = form.bind(Map(fieldName -> nonNumeric)).apply(fieldName)
        result.errors mustEqual Seq(nonNumericError)
      }
    }

    forAll(decimals -> "decimal") { decimal =>
      s"not bind decimals value $decimal" in {
        val result = form.bind(Map(fieldName -> decimal)).apply(fieldName)
        result.errors mustEqual Seq(wholeNumberError)
      }
    }

    forAll(intsLargerThanMaxValue -> "massiveInt") { (num: BigInt) =>
      s"not bind integers larger than Int.MaxValue value $num" in {
        val result = form.bind(Map(fieldName -> num.toString)).apply(fieldName)
        result.errors mustEqual Seq(nonNumericError)
      }
    }

    forAll(intsSmallerThanMinValue -> "massivelySmallInt") { (num: BigInt) =>
      s"not bind integers smaller than Int.MinValue with $num" in {
        val result = form.bind(Map(fieldName -> num.toString)).apply(fieldName)
        result.errors mustEqual Seq(nonNumericError)
      }
    }
  }

  def intFieldWithMinimum(form: Form[?], fieldName: String, minimum: Int, expectedError: FormError): Unit =
    forAll(intsBelowValue(minimum) -> "intBelowMin") { (number: Int) =>
      s"not bind integers below $minimum value $number" in {
        val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
        result.errors mustEqual Seq(expectedError)
      }
    }

  def intFieldWithMaximum(form: Form[?], fieldName: String, maximum: Int, expectedError: FormError): Unit =
    forAll(intsAboveValue(maximum) -> "intAboveMax") { (number: Int) =>
      s"not bind integers above $maximum value $number" in {
        val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
        result.errors mustEqual Seq(expectedError)
      }
    }

  def intFieldWithRange(form: Form[?], fieldName: String, minimum: Int, maximum: Int, expectedError: FormError): Unit =
    forAll(intsOutsideRange(minimum, maximum) -> "intOutsideRange") { number =>
      s"not bind integers outside the range $minimum to $maximum value $number" in {
        val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
        result.errors mustEqual Seq(expectedError)
      }
    }
}
