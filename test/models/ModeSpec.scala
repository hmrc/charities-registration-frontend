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

package models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsString, Json}

class ModeSpec extends AnyWordSpec with Matchers {

  "Mode" when {
    ".jsLiteral" must {
      "return normal mode string" in {
        Mode.jsLiteral.to(NormalMode) mustBe "\"NormalMode\""
      }

      "return check mode string" in {
        Mode.jsLiteral.to(CheckMode) mustBe "\"CheckMode\""
      }

      "return playback mode string" in {
        Mode.jsLiteral.to(PlaybackMode) mustBe "\"PlaybackMode\""
      }
    }

    ".writes" must {
      "return the expected JsString of NormalMode" in {
        Json.toJson[Mode](NormalMode) mustBe JsString("NormalMode")
      }

      "return the expected JsString of CheckMode" in {
        Json.toJson[Mode](CheckMode) mustBe JsString("CheckMode")
      }

      "return the expected JsString of PlaybackMode" in {
        Json.toJson[Mode](PlaybackMode) mustBe JsString("PlaybackMode")
      }
    }
  }
}
