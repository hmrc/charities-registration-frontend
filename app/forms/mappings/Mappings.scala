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

import models.Enumerable
import org.joda.time.MonthDay
import play.api.data.FieldMapping
import play.api.data.Forms.of

trait Mappings extends Formatters with Constraints {

  val validateField = "^[a-zA-Z0-9-, ']+$"
  val validateFieldWithFullStop = "^[a-zA-Z0-9-, '.]+$"
  val validateTelephoneNumber = """^\+?(?:\s*\d){10,13}$"""
  val validateEmailAddress = """^(?i)[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$"""

  protected def text(errorKey: String = "error.required"): FieldMapping[String] =
    of(stringFormatter(errorKey))

  protected def textWithOneSpace(errorKey: String = "error.required"): FieldMapping[String] =
    of(stringFormatter(errorKey, replaceSpaces))

  protected def int(requiredKey: String = "error.required",
                    wholeNumberKey: String = "error.wholeNumber",
                    nonNumericKey: String = "error.nonNumeric"): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey))

  protected def currency(requiredKey: String = "error.required",
                        invalidCurrency: String = "error.invalidNumeric"): FieldMapping[BigDecimal] =
    of(currencyFormatter(requiredKey, invalidCurrency))

  protected def boolean(requiredKey: String = "error.required",
                        invalidKey: String = "error.boolean"): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey))


  protected def enumerable[A](requiredKey: String = "error.required",
                              invalidKey: String = "error.invalid")(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def localDate(
                           invalidKey: String,
                           allRequiredKey: String,
                           twoRequiredKey: String,
                           requiredKey: String,
                           nonNumericKey: String,
                           args: Seq[String] = Seq.empty): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, nonNumericKey, args))

  protected def localDateDayMonth(
                           invalidKey: String,
                           allRequiredKey: String,
                           requiredKey: String,
                           nonNumericKey: String,
                           leapYearKey: String,
                           args: Seq[String] = Seq.empty): FieldMapping[MonthDay] =
    of(new LocalDateFormatterDayMonth(invalidKey, allRequiredKey, requiredKey, nonNumericKey, leapYearKey, args))
}
