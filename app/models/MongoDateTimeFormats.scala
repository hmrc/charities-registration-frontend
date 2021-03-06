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

package models

import org.joda.time.MonthDay
import play.api.libs.json._

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}

trait MongoDateTimeFormats {

  implicit val localDayMonthRead: Reads[MonthDay] =
    __.read[String].map {
      dayMonth=>
        MonthDay.parse(dayMonth)
    }

  implicit val localDayMonthWrite: Writes[MonthDay] = (dayMonth: MonthDay) => JsString(dayMonth.toString)

  implicit val localDateTimeRead: Reads[LocalDateTime] =
    (__ \ "$date").read[Long].map {
      millis =>
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC)
    }

  implicit val localDateTimeWrite: Writes[LocalDateTime] = (dateTime: LocalDateTime) => Json.obj(
    "$date" -> dateTime.atZone(ZoneOffset.UTC).toInstant.toEpochMilli
  )

  implicit val localDateReads: Reads[LocalDate] =
    __.read[String].map {
      date =>
        LocalDate.parse(date)
    }

  implicit val localDateWrites: Writes[LocalDate] = (date: LocalDate) => JsString(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

}

object MongoDateTimeFormats extends MongoDateTimeFormats
