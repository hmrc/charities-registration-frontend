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

import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.{Failure, Success, Try}


private[mappings] class LocalDateFormatter(
                                            invalidKey: String,
                                            allRequiredKey: String,
                                            twoRequiredKey: String,
                                            requiredKey: String,
                                            nonNumericKey: String,
                                            args: Seq[String] = Seq.empty
                                          ) extends Formatter[LocalDate] with GenericDateFormatter {

  val fieldKeys: List[String] = List("day", "month", "year")

  private def toDate(key: String, day: Int, month: Int, year: Int): Either[Seq[FormError], LocalDate] =
    Try(LocalDate.of(year, month, day)) match {
      case Success(date) =>
        Right(date)
      case Failure(_) =>
        Left(Seq(FormError(keyWithError(key, "day"), invalidKey, args)))
    }

  private def formatDate(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {

    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = invalidKey,
      args
    )

    for {
      day   <- int.bind(s"$key.day", data).right
      month <- int.bind(s"$key.month", data).right
      year  <- int.bind(s"$key.year", data).right
      date  <- toDate(key, day, month, year).right
    } yield date
  }

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {

    val dataWithoutSpaces: Map[String, String] = data.map(entry => (entry._1, entry._2.replace(" ", "")))

    fields(key, dataWithoutSpaces).count(_._2.isDefined) match {
      case 3 if illegalFields(key, dataWithoutSpaces).nonEmpty | illegalZero(key, dataWithoutSpaces).nonEmpty =>
        Left(List() ++ illegalErrors(key, dataWithoutSpaces, nonNumericKey, args, illegalFields)
          ++ illegalErrors(key, dataWithoutSpaces, invalidKey, args, illegalZero))
      case 3 =>
        formatDate(key, dataWithoutSpaces)
      case 2 =>
       leftErrors(key, dataWithoutSpaces, requiredKey, invalidKey, args)
      case 1 =>
        leftErrors(key, dataWithoutSpaces, twoRequiredKey, invalidKey, args)
      case _ =>
        leftErrors(key, dataWithoutSpaces, allRequiredKey, invalidKey, args)
    }

  }

  override def unbind(key: String, value: LocalDate): Map[String, String] =
    Map(
      s"$key.day" -> value.getDayOfMonth.toString,
      s"$key.month" -> value.getMonthValue.toString,
      s"$key.year" -> value.getYear.toString
    )
}
