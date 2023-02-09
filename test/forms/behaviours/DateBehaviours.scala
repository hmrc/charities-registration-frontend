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

package forms.behaviours

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.joda.time.{MonthDay, LocalDate => JodaLocalDate}
import org.scalacheck.Gen
import play.api.data.{Form, FormError}

class DateBehaviours extends FieldBehaviours {

  def dateField(form: Form[_], key: String, validData: Gen[LocalDate]): Unit =
    forAll(validData -> "valid date") { date =>
      s"bind valid data for $date" in {
        val data = Map(
          s"$key.day"   -> date.getDayOfMonth.toString,
          s"$key.month" -> date.getMonthValue.toString,
          s"$key.year"  -> date.getYear.toString
        )

        val result = form.bind(data)
        result.value.value mustEqual date
      }
    }

  def dateFieldIgnoreSpaces(form: Form[_], key: String, validData: Gen[LocalDate]): Unit =
    forAll(validData -> "valid date") { date =>
      s"bind valid data while ignoring spaces for $date" in {
        val data = Map(
          s"$key.day"   -> s" ${date.getDayOfMonth.toString}",
          s"$key.month" -> s"${date.getMonthValue.toString} ",
          s"$key.year"  -> s" ${date.getYear.toString} "
        )

        val result = form.bind(data)
        result.value.value mustEqual date
      }
    }

  def dayMonthField(form: Form[_], key: String, validData: Gen[JodaLocalDate]): Unit =
    forAll(validData -> "valid date") { date =>
      s"bind valid data for $date" in {
        val data = Map(
          s"$key.day"   -> date.getDayOfMonth.toString,
          s"$key.month" -> date.getMonthOfYear.toString
        )

        val result = form.bind(data)
        result.value.value mustEqual MonthDay.fromDateFields(date.toDate)
      }
    }

  def dayMonthFieldFailOn29Feb(form: Form[_], key: String, formError: FormError): Unit =
    s"not bind valid data for 29/02/2000" in {
      val data = Map(
        s"$key.day"   -> "29",
        s"$key.month" -> "02"
      )

      val result = form.bind(data)
      result.errors must contain only formError
    }

  def dateFieldWithMax(form: Form[_], key: String, max: LocalDate, formError: FormError): Unit = {
    val generator = datesBetween(max.plusDays(1), max.plusYears(10))
    forAll(generator -> "invalid dates") { date =>
      s"fail to bind a date greater than ${max.format(DateTimeFormatter.ISO_LOCAL_DATE)} with $date" in {

        val data = Map(
          s"$key.day"   -> date.getDayOfMonth.toString,
          s"$key.month" -> date.getMonthValue.toString,
          s"$key.year"  -> date.getYear.toString
        )

        val result = form.bind(data)

        result.errors must contain only formError
      }
    }
  }

  def dateFieldWithMin(form: Form[_], key: String, min: LocalDate, formError: FormError): Unit = {

    val generator = datesBetween(min.minusYears(10), min.minusDays(1))
    forAll(generator -> "invalid dates") { date =>
      s"fail to bind a date earlier than ${min.format(DateTimeFormatter.ISO_LOCAL_DATE)} with data" in {
        val data = Map(
          s"$key.day"   -> date.getDayOfMonth.toString,
          s"$key.month" -> date.getMonthValue.toString,
          s"$key.year"  -> date.getYear.toString
        )

        val result = form.bind(data)

        result.errors must contain only formError
      }
    }
  }

  def mandatoryDateField(
    form: Form[_],
    key: String,
    requiredAllKey: String,
    errorArgs: Seq[String] = Seq("day", "month", "year")
  ): Unit =
    "fail to bind an empty date" in {

      val result = form.bind(Map.empty[String, String])

      result.errors must contain only FormError(s"$key.day", requiredAllKey, errorArgs)
    }
}
