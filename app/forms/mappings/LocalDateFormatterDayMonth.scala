/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.{Failure, Success, Try}

private[mappings] class LocalDateFormatterDayMonth(
                                            invalidKey: String,
                                            allRequiredKey: String,
                                            requiredKey: String,
                                            args: Seq[String] = Seq.empty
                                          ) extends Formatter[MonthDay] with GenericDateFormatter {

  val fieldKeys: List[String] = List("day", "month")

  private def toDate(key: String, day: Int, month: Int): Either[Seq[FormError], MonthDay] =
   Try(MonthDay.fromDateFields(new LocalDate(LocalDate.now().getYear, month, day).toDate)) match {
      case Success(date) =>
        Right(date)
      case Failure(_) =>
        Left(Seq(FormError(keyWithError(key, "day"), invalidKey, args)))
    }

  private def formatDate(key: String, data: Map[String, String]): Either[Seq[FormError], MonthDay] = {

    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = invalidKey,
      args
    )

    for {
      day   <- int.bind(s"$key.day", data).right
      month <- int.bind(s"$key.month", data).right
      date  <- toDate(key, day, month).right
    } yield date
  }

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], MonthDay] = {

    fields(key, data).count(_._2.isDefined) match {
      case 2 if illegalFields(key, data).nonEmpty | illegalZero(key, data).nonEmpty =>
        Left(List() ++ illegalErrors(key, data, invalidKey, args, illegalFields) ++ illegalErrors(key, data, invalidKey, args, illegalZero))
      case 2 =>
        formatDate(key, data)
      case 1 =>
        leftErrors(key, data, requiredKey, invalidKey, args)
      case _ =>
        leftErrors(key, data, allRequiredKey, invalidKey, args)
    }
  }

  override def unbind(key: String, value: MonthDay): Map[String, String] =
    Map(
      s"$key.day" -> value.getDayOfMonth.toString,
      s"$key.month" -> value.getMonthOfYear.toString
    )
}
