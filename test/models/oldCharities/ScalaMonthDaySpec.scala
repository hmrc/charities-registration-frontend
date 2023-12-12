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

package models.oldCharities

import java.time.{LocalDate, MonthDay}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsString, JsValue, Json}

class ScalaMonthDaySpec extends AnyWordSpecLike with Matchers {

  private val json: JsValue = Json.parse(
    """
      |{
      |    "monthInYear": 2,
      |    "dayInMonth": 1
      |}
    """.stripMargin
  )

  private val monthDay: MonthDay = MonthDay.from(
    LocalDate.parse("2026-02-01")
  )

  private val model: ScalaMonthDay = ScalaMonthDay(monthDay = monthDay)

  "ScalaMonthDay" when {
    "read from valid JSON" should {
      "produce the expected ScalaMonthDay model" in {
        json.as[ScalaMonthDay] shouldBe model
      }
    }

    "written to JSON" should {
      "produce the expected JsString" in {
        Json.toJson(model) shouldBe JsString("--2-1")
      }
    }
  }
}
