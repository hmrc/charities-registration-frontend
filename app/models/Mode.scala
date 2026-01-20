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

import play.api.libs.json.{JsString, Writes}
import play.api.mvc.JavascriptLiteral


enum Mode {
  case NormalMode
  case CheckMode
  case PlaybackMode
}

object Mode {

  given jsLiteral: JavascriptLiteral[Mode] with 
    override def to(mode: Mode): String = mode match {
      case Mode.NormalMode => "\"NormalMode\""
      case Mode.CheckMode => "\"CheckMode\""
      case Mode.PlaybackMode => "\"PlaybackMode\""
    }

  given Writes[Mode] with
    def writes(mode: Mode): JsString = mode match {
      case Mode.NormalMode => JsString("NormalMode")
      case Mode.CheckMode => JsString("CheckMode")
      case Mode.PlaybackMode => JsString("PlaybackMode")
    }
}
