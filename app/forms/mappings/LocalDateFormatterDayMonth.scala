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

package forms.mappings

import play.api.data.FormError
import play.api.data.format.Formatter

import java.time.{LocalDate, MonthDay}
import scala.util.{Failure, Success, Try}

private[mappings] class LocalDateFormatterDayMonth(
  invalidKey: String,
  allRequiredKey: String,
  requiredKey: String,
  nonNumericKey: String,
  leapYearKey: String,
  args: Seq[String]
) extends Formatter[MonthDay]
    with GenericDateFormatter {

  val fieldKeys: List[String] = List("day", "month")

  private def toDate(key: String, day: Int, month: Int): Either[Seq[FormError], MonthDay] = {
    val (leapYearDay, leapYearMonth): (Int, Int) = (29, 2)

    if (day == leapYearDay && month == leapYearMonth) {
      Left(Seq(FormError(keyWithError(key, "day"), leapYearKey, args)))
    } else {
      Try(MonthDay.from(LocalDate.of(LocalDate.now().getYear, month, day))) match {
        case Success(date) => Right(date)
        case Failure(_)    => Left(Seq(FormError(keyWithError(key, "day"), invalidKey, args)))
      }
    }
  }

  private def formatDate(key: String, data: Map[String, String]): Either[Seq[FormError], MonthDay] = {

    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = nonNumericKey,
      args
    )

    for {
      day   <- int.bind(s"$key.day", data)
      month <- int.bind(s"$key.month", data)
      date  <- toDate(key, day, month)
    } yield date
  }

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], MonthDay] = {

    val dataWithoutSpaces: Map[String, String] = data.map(entry => (entry._1, entry._2.replace(" ", "")))

    fields(key, dataWithoutSpaces).count(_._2.isDefined) match {
      case 2 if illegalFields(key, dataWithoutSpaces).nonEmpty | illegalZero(key, dataWithoutSpaces).nonEmpty =>
        Left(
          List() ++ illegalErrors(key, dataWithoutSpaces, nonNumericKey, args, illegalFields) ++
            illegalErrors(key, dataWithoutSpaces, invalidKey, args, illegalZero)
        )
      case 2                                                                                                  =>
        formatDate(key, dataWithoutSpaces)
      case 1                                                                                                  =>
        leftErrors(key, dataWithoutSpaces, requiredKey, invalidKey, args)
      case _                                                                                                  =>
        leftErrors(key, dataWithoutSpaces, allRequiredKey, invalidKey, args)
    }
  }

  override def unbind(key: String, value: MonthDay): Map[String, String] =
    Map(
      s"$key.day"   -> value.getDayOfMonth.toString,
      s"$key.month" -> value.getMonthValue.toString
    )
}
