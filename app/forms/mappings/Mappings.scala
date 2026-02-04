/*
 * Copyright 2026 HM Revenue & Customs
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

import java.time.{LocalDate, MonthDay}

import models.Enumerable
import play.api.data.FieldMapping
import play.api.data.Forms.of

trait Mappings extends Formatters with Constraints {

  val validateField             = "^[a-zA-Z0-9-, '’]+$"
  val validateFieldWithFullStop = "^[a-zA-Z0-9-, '’.]+$"
  val validateFieldWithNewLine  = "^[a-zA-Z0-9-, '’.\n\r\t]+$"
  val validateTelephoneNumber   = """^\+?(?:\s*\d){10,20}$"""
  val validateEmailExtraTld     = """^.*(@([0-9]+.)+)$"""
  val validateEmailAddress      =
    """^(?i)(?:[A-Za-z0-9!#$%&'*+/=?^_`~-]+(?:\.[A-Za-z0-9!#$%&'*+/=?^_`-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[A-Za-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+))"""

  protected def text(errorKey: String = "error.required"): FieldMapping[String] =
    of(stringFormatter(errorKey))

  protected def textWithOneSpace(errorKey: String = "error.required"): FieldMapping[String] =
    of(stringFormatter(errorKey, replaceSpaces))

  protected def int(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric"
  ): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey))

  protected def currency(
    requiredKey: String = "error.required",
    invalidCurrency: String = "error.invalidNumeric"
  ): FieldMapping[BigDecimal] =
    of(currencyFormatter(requiredKey, invalidCurrency))

  protected def boolean(
    requiredKey: String = "error.required",
    invalidKey: String = "error.boolean"
  ): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey))

  protected def enumerable[A](requiredKey: String = "error.required", invalidKey: String = "error.invalid")(implicit
    ev: Enumerable[A]
  ): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def localDate(
    invalidKey: String,
    allRequiredKey: String,
    twoRequiredKey: String,
    requiredKey: String,
    nonNumericKey: String,
    args: Seq[String] = Seq.empty
  ): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, nonNumericKey, args))

  protected def localDateDayMonth(
    invalidKey: String,
    allRequiredKey: String,
    requiredKey: String,
    nonNumericKey: String,
    leapYearKey: String,
    args: Seq[String] = Seq.empty
  ): FieldMapping[MonthDay] =
    of(new LocalDateFormatterDayMonth(invalidKey, allRequiredKey, requiredKey, nonNumericKey, leapYearKey, args))
}
