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

package forms.mappings

import org.joda.time.{LocalDate, MonthDay}
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.{Form, FormError}
import utils.Generators

class DateMappingsDayMonthSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with Generators with OptionValues
  with Mappings {

  lazy val form: Form[MonthDay] = Form(
    "value" -> localDateDayMonth(
      invalidKey = "error.invalid",
      allRequiredKey = "error.required.all",
      requiredKey = "error.required",
      nonNumericKey = "error.nonNumeric",
      leapYearKey = "error.leapYear"
    )
  )

  val validData: Gen[LocalDate] = daysBetween(
    min = new LocalDate(2000,1,31),
    max = new LocalDate(2000,12,31)
  )

  val invalidField: Gen[String] = Gen.alphaStr.suchThat(_.nonEmpty)

  val missingField: Gen[Option[String]] = Gen.option(Gen.const(""))

    forAll(validData -> "valid date") {
      date =>
      s"bind valid data $date" in {

        val data = Map(
          "value.day" -> date.getDayOfMonth.toString,
          "value.month" -> date.getMonthOfYear.toString,
        )

        val result = form.bind(data)

        result.value.value mustEqual MonthDay.fromDateFields(date.toDate)
    }
  }

  "fail to bind an empty date" in {

    val result = form.bind(Map.empty[String, String])

    result.errors must contain only FormError("value.day", "error.required.all", List("day", "month"))
  }

  "fail to bind a date with a missing day" in {

    forAll(validData -> "valid date", missingField -> "missing field") {
      (date, field) =>

        val initialData = Map(
          "value.month" -> date.getMonthOfYear.toString,
        )

        val data = field.fold(initialData) {
          value =>
            initialData + ("value.day" -> value)
        }

        val result = form.bind(data)

        result.errors must contain only FormError("value.day", "error.required", List("day"))
    }
  }

  "fail to bind a date with an invalid day" in {

    forAll(validData -> "valid date", invalidField -> "invalid field") {
      (date, field) =>

        val data = Map(
          "value.day" -> field,
          "value.month" -> date.getMonthOfYear.toString,
        )

        val result = form.bind(data)

        result.errors must contain(
          FormError("value.day", "error.nonNumeric", List("day"))
        )
    }
  }

  "fail to bind a date with a missing month" in {

    forAll(validData -> "valid date", missingField -> "missing field") {
      (date, field) =>

        val initialData = Map(
          "value.day" -> date.getDayOfMonth.toString,
        )

        val data = field.fold(initialData) {
          value =>
            initialData + ("value.month" -> value)
        }

        val result = form.bind(data)

        result.errors must contain only FormError("value.month", "error.required", List("month"))
    }
  }

  "fail to bind a date with an invalid month" in {

    forAll(validData -> "valid data", invalidField -> "invalid field") {
      (date, field) =>

        val data = Map(
          "value.day" -> date.getDayOfMonth.toString,
          "value.month" -> field,
        )

        val result = form.bind(data)

        result.errors must contain(
          FormError("value.month", "error.nonNumeric", List("month"))
        )
    }
  }

  "fail to bind an invalid day and month" in {

    forAll(validData -> "valid date", invalidField -> "invalid day", invalidField -> "invalid month") {
      (date, day, month) =>

        val data = Map(
          "value.day" -> day,
          "value.month" -> month,
          "value.year" -> date.getYear.toString
        )

        val result = form.bind(data)

        result.errors must contain only FormError("value.day", "error.nonNumeric", List("day", "month"))
    }
  }

  "fail to bind an invalid day, month" in {

    forAll(invalidField -> "valid day", invalidField -> "invalid month") {
      (day, month) =>

        val data = Map(
          "value.day" -> day,
          "value.month" -> month,
        )

        val result = form.bind(data)

        result.errors must contain only FormError("value.day", "error.nonNumeric", List("day", "month"))
    }
  }

  "fail to bind leap year date" in {

    val data = Map(
      "value.day" -> "29",
      "value.month" -> "2",
    )

    val result = form.bind(data)

    result.errors must contain(
      FormError("value.day", "error.invalid", List.empty)
    )
  }

  "fail to bind an invalid date" in {

    val data = Map(
      "value.day" -> "30",
      "value.month" -> "2",
    )

    val result = form.bind(data)

    result.errors must contain(
      FormError("value.day", "error.invalid", List.empty)
    )
  }

  "unbind a date" in {

    forAll(validData -> "valid date") {
      date =>

        val filledForm = form.fill(MonthDay.fromDateFields(date.toDate))

        filledForm("value.day").value.value mustEqual date.getDayOfMonth.toString
        filledForm("value.month").value.value mustEqual date.getMonthOfYear.toString
    }
  }
}
