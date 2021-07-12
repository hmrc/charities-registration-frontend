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

package forms.mappings

import java.time.LocalDate
import org.scalacheck.Gen
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.validation.{Invalid, Valid}
import utils.Generators

class ConstraintsSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with Generators with Constraints {


  "firstError" must {

    lazy val first = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))

    "return Valid when all constraints pass" in {
      first("foo") mustEqual Valid
    }

    "return Invalid when the first constraint fails" in {
      first("a" * 11) mustEqual Invalid("error.length", 10)
    }

    "return Invalid when the second constraint fails" in {
      first("") mustEqual Invalid("error.regexp", """^\w+$""")
    }
  }

  "minimumValue" must {

    lazy val min = minimumValue(1, "error.min")

    "return Valid for a number greater than the threshold" in {
      min(2) mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      min(1) mustEqual Valid
    }

    "return Invalid for a number below the threshold" in {
      min(0) mustEqual Invalid("error.min", 1)
    }
  }

  "inRange" must {

    lazy val range = inRange(1, 3, "error")

    "return Valid for a number in the range" in {
      range(2) mustEqual Valid
    }

    "return Valid for a number equal to the minimum" in {
      range(1) mustEqual Valid
    }

    "return Valid for a number equal to the maximum" in {
      range(3) mustEqual Valid
    }

    "return Invalid for a number below the minimum" in {
      range(0) mustEqual Invalid("error", 1, 3)
    }

    "return Invalid for a number below the maximum" in {
      range(4) mustEqual Invalid("error", 1, 3)
    }
  }

  "maximumValue" must {

    lazy val maxmimum = maximumValue(1, "error.max")

    "return Valid for a number less than the threshold" in {
      maxmimum(0) mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      maxmimum(1) mustEqual Valid
    }

    "return Invalid for a number above the threshold" in {
      maxmimum(2) mustEqual Invalid("error.max", 1)
    }
  }

  "regexp" must {

    "return Valid for an input that matches the expression" in {
      val result = regexp("""^\w+$""", "error.invalid")("foo")
      result mustEqual Valid
    }

    "return Invalid for an input that does not match the expression" in {
      val result = regexp("""^\d+$""", "error.invalid")("foo")
      result mustEqual Invalid("error.invalid", """^\d+$""")
    }
  }

  "minLength" must {

    lazy val min = minLength(10, "error.length")

    "return error for a string shorter than the allowed length" in {
      min("a" * 9) mustEqual Invalid("error.length", 10)
    }

    "return error for an empty string" in {
      min("") mustEqual Invalid("error.length", 10)
    }

    "return Valid for a string equal to the allowed length" in {
      min("a" * 10) mustEqual Valid
    }

    "return valid for a string longer than the allowed length" in {
      min("a" * 11) mustEqual Valid
    }
  }

  "maxLength" must {

    lazy val max = maxLength(10, "error.length")

    "return Valid for a string shorter than the allowed length" in {
      max("a" * 9) mustEqual Valid
    }

    "return Valid for an empty string" in {
      max("") mustEqual Valid
    }

    "return Valid for a string equal to the allowed length" in {
      max("a" * 10) mustEqual Valid
    }

    "return Invalid for a string longer than the allowed length" in {
      max("a" * 11) mustEqual Invalid("error.length", 10)
    }
  }

  "maxLengthTextArea" must {

    lazy val max = maxLengthTextArea(10, "error.length")

    "return Valid for a string shorter than the allowed length" in {
      max(("a" * 8) + "\r\n") mustEqual Valid
    }

    "return Valid for an empty string" in {
      max("") mustEqual Valid
    }

    "return Valid for a string equal to the allowed length" in {
      max(("a" * 8) + "\r\n\t") mustEqual Valid
    }

    "return Invalid for a string longer than the allowed length" in {
      max("a" * 11) mustEqual Invalid("error.length", 10)
    }
  }

  "lengthBetween" must {

    lazy val lenBet = lengthBetween(10, 20, "error.length")

    "return Valid for a string defined between the allowed length" in {
      lenBet("a" * 14) mustEqual Valid
    }

    "return Valid for a string equal to lower bound" in {
      lenBet("a" * 10) mustEqual Valid
    }

    "return Valid for a string equal to upper bound" in {
      lenBet("a" * 20) mustEqual Valid
    }

    "return error for an empty string" in {
      lenBet("") mustEqual Invalid("error.length", 10, 20)
    }

    "return error for a string less than min length" in {
      lenBet("a" * 9) mustEqual Invalid("error.length", 10, 20)
    }

    "return error for a string more than max length" in {
      lenBet("a" * 21) mustEqual Invalid("error.length", 10, 20)
    }
  }

  "maxDate" must {

    "return Valid for a date before or equal to the maximum" in {

      val gen: Gen[(LocalDate, LocalDate)] = for {
        max <- datesBetween(LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1))
        date <- datesBetween(LocalDate.of(2000, 1, 1), max)
      } yield (max, date)

      forAll(gen) {
        case (max, date) =>

          val result = maxDate(max, "error.future")(date)
          result mustEqual Valid
      }
    }

    "return Invalid for a date after the maximum" in {

      val gen: Gen[(LocalDate, LocalDate)] = for {
        max <- datesBetween(LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1))
        date <- datesBetween(max.plusDays(1), LocalDate.of(3000, 1, 2))
      } yield (max, date)

      forAll(gen) {
        case (max, date) =>

          val result = maxDate(max, "error.future", "foo")(date)
          result mustEqual Invalid("error.future", "foo")
      }
    }
  }

  "minDate" must {

    "return Valid for a date after or equal to the minimum" in {

      val gen: Gen[(LocalDate, LocalDate)] = for {
        min <- datesBetween(LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1))
        date <- datesBetween(min, LocalDate.of(3000, 1, 1))
      } yield (min, date)

      forAll(gen) {
        case (min, date) =>

          val result = minDate(min, "error.past", "foo")(date)
          result mustEqual Valid
      }
    }

    "return Invalid for a date before the minimum" in {

      val gen: Gen[(LocalDate, LocalDate)] = for {
        min <- datesBetween(LocalDate.of(2000, 1, 2), LocalDate.of(3000, 1, 1))
        date <- datesBetween(LocalDate.of(2000, 1, 1), min.minusDays(1))
      } yield (min, date)

      forAll(gen) {
        case (min, date) =>

          val result = minDate(min, "error.past", "foo")(date)
          result mustEqual Invalid("error.past", "foo")
      }
    }
  }

  "uniqueEntry" must {

    val values = Seq("a", "b", "c", "d", "e")

    for(idx <- 1 to values.length) {
      if(idx == 3) {
        s"return valid for a value thats in the list but at the current idx $idx" in {
          uniqueEntry(values, 3, "error")("c") mustBe Valid
        }
      } else {
        s"return invalid for a value thats in the list at idx $idx" in {
          uniqueEntry(values, 1, "error")("c") mustBe Invalid("error", values)
        }
      }
    }

    for(idx <- 1 to values.length) {

      s"return valid for with idx $idx for a value not in the values sequence" in {
        uniqueEntry(values, idx, "error")("f") mustBe Valid
      }
    }
  }

  "nonEmptySet" must {

    lazy val nonEmpty = nonEmptySet("error")

    "return Valid when supplied with a Set of values" in {
      nonEmpty(Set(1,2)) mustEqual Valid
    }

    "return Invalid when an empty set is supplied" in {
      nonEmpty(Set()) mustEqual Invalid("error")
    }
  }
}
