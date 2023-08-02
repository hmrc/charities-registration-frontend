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

package models

import base.data.constants.DateConstants.feb1st2018
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import play.api.libs.json._
import java.time.LocalDateTime

import org.scalatest.wordspec.AnyWordSpec

class MongoDateTimeFormatsSpec extends AnyWordSpec with Matchers with OptionValues with MongoDateTimeFormats {

  private val date: LocalDateTime = feb1st2018.atStartOfDay
  private val dateMillis: Long    = 1517443200000L
  private val json: JsObject      = Json.obj(
    f"$$date" -> dateMillis
  )

  "MongoDateTimeFormats" when {
    ".localDateTimeFormat" must {
      "serialise to json" in {
        val result: JsValue = Json.toJson(date)

        result mustEqual json
      }

      "deserialise from json" in {
        val result: LocalDateTime = json.as[LocalDateTime]

        result mustEqual date
      }

      "serialise/deserialise to the same value" in {
        val result: LocalDateTime = Json.toJson(date).as[LocalDateTime]

        result mustEqual date
      }

      def test(json: JsObject): Unit =
        s"produce a JsError when converting from $json" in {
          val result: JsResult[LocalDateTime] = json.validate[LocalDateTime]

          result mustBe JsError("Unexpected LocalDateTime Format")
        }

      Seq(
        Json.obj(s"$$date" -> JsBoolean(true)),
        Json.obj(s"$$date" -> Json.obj()),
        Json.obj()
      ).foreach(test)
    }
  }
}
