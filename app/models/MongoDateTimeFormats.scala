/*
 * Copyright 2024 HM Revenue & Customs
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

import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime.ofInstant
import java.time.format.DateTimeFormatter
import java.time._

import play.api.libs.json._

trait MongoDateTimeFormats {

  implicit val localDayMonthRead: Reads[MonthDay] =
    __.read[String].map { dayMonth =>
      MonthDay.parse(dayMonth)
    }

  implicit val localDayMonthWrite: Writes[MonthDay] = (dayMonth: MonthDay) => JsString(dayMonth.toString)

  implicit val localDateTimeFormat: OFormat[LocalDateTime] = new OFormat[LocalDateTime] {
    override def writes(localDateTime: LocalDateTime): JsObject =
      Json.obj("$date" -> localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli)

    override def reads(json: JsValue): JsResult[LocalDateTime]  =
      json match {
        case JsObject(map) if map.contains("$date") =>
          map("$date") match {
            case JsNumber(v)            => JsSuccess(ofInstant(ofEpochMilli(v.toLong), ZoneOffset.UTC))
            case JsObject(stringObject) =>
              if (stringObject.contains("$numberLong")) {
                JsSuccess(
                  ofInstant(
                    ofEpochMilli(BigDecimal(stringObject("$numberLong").as[JsString].value).toLong),
                    ZoneOffset.UTC
                  )
                )
              } else {
                JsError("Unexpected LocalDateTime Format")
              }
            case _                      => JsError("Unexpected LocalDateTime Format")
          }
        case _                                      => JsError("Unexpected LocalDateTime Format")
      }
  }

  implicit val localDateReads: Reads[LocalDate] =
    __.read[String].map { date =>
      LocalDate.parse(date)
    }

  implicit val localDateWrites: Writes[LocalDate] = (date: LocalDate) =>
    JsString(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

}

object MongoDateTimeFormats extends MongoDateTimeFormats
