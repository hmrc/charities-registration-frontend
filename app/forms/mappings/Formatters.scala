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

import models.Enumerable
import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.control.Exception.nonFatalCatch

trait Formatters {

  def replaceSpaces (input: String): String = trimString(input).replaceAll(" +", " ")

  def trimString (input: String): String = input.trim

  private[mappings] def stringFormatter(errorKey: String, updateString: String => String = trimString): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None => Left(Seq(FormError(key, errorKey)))
        case Some(str) if updateString(str).length == 0 => Left(Seq(FormError(key, errorKey)))
        case Some(str) => Right(updateString(str))
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value.trim)
  }

  private[mappings] def booleanFormatter(requiredKey: String, invalidKey: String): Formatter[Boolean] =
    new Formatter[Boolean] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Boolean] =
        baseFormatter
          .bind(key, data)
          .right.flatMap {
          case "true" => Right(true)
          case "false" => Right(false)
          case _ => Left(Seq(FormError(key, invalidKey)))
        }

      def unbind(key: String, value: Boolean) = Map(key -> value.toString)
    }

  private[mappings] def intFormatter(requiredKey: String, wholeNumberKey: String, nonNumericKey: String, args: Seq[String] = Seq.empty): Formatter[Int] =
    new Formatter[Int] {

      val decimalRegexp = """^-?(\d*\.\d*)$"""

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Int] =
        baseFormatter
          .bind(key, data)
          .right.map(_.replace(",", "").replace(" ", ""))
          .right.flatMap {
          case s if s.matches(decimalRegexp) =>
            Left(Seq(FormError(key, wholeNumberKey, args)))
          case s =>
            nonFatalCatch
              .either(s.toInt)
              .left.map(_ => Seq(FormError(key, nonNumericKey, args)))
        }

      override def unbind(key: String, value: Int): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }

  private[mappings] def currencyFormatter(requiredKey: String,
                                          invalidCurrency: String,
                                          args: Seq[String] = Seq.empty): Formatter[BigDecimal] =
    new Formatter[BigDecimal] {

      val upTo2DecimalPlaces = """(^\d*$)|(^\d*\.\d{1,2}$)"""
      private val baseFormatter = stringFormatter(requiredKey)
      def onlyOnePound: String => Boolean = input =>
        !List(-1, 0, input.length - 1).contains(input.indexOf("£")) | input.count(_ == '£') > 1

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BigDecimal] =
        baseFormatter
          .bind(key, data)
          .right.map(_.replace(",", "").replace(" ", ""))
          .right.flatMap {
          case s if onlyOnePound(s) =>
            Left(Seq(FormError(key, invalidCurrency, args)))
          case s if !s.replace("£", "").matches(upTo2DecimalPlaces) =>
            Left(Seq(FormError(key, invalidCurrency, args)))
          case s =>
            nonFatalCatch
              .either(BigDecimal(s.replace("£", "")))
              .left.map(_ => Seq(FormError(key, invalidCurrency, args)))
        }

      override def unbind(key: String, value: BigDecimal): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }


  private[mappings] def enumerableFormatter[A](requiredKey: String, invalidKey: String)(implicit ev: Enumerable[A]): Formatter[A] =
    new Formatter[A] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
        baseFormatter.bind(key, data).right.flatMap {
          str =>
            ev.withName(str).map(Right.apply).getOrElse(Left(Seq(FormError(key, invalidKey))))
        }

      override def unbind(key: String, value: A): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }
}
