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

package models.responses

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue}

enum BarsAssessmentType(val value: String) {
  case Yes extends BarsAssessmentType("yes")
  case No extends BarsAssessmentType("no")

  def convertToUrlParam: String = value

}

object BarsAssessmentType {
  implicit val format: Format[BarsAssessmentType] = new Format[BarsAssessmentType] {
    def reads(json: JsValue): JsResult[BarsAssessmentType] = json.validate[String].flatMap {
      case "yes" => JsSuccess(BarsAssessmentType.Yes)
      case "no"  => JsSuccess(BarsAssessmentType.No)
      case other => JsError(s"Unknown BarsAssessmentType: $other")
    }

    def writes(barsAssessmentType: BarsAssessmentType): JsValue =
      JsString(barsAssessmentType.value)
  }

  def fromString(value: String): Option[BarsAssessmentType] = value match
    case "yes" => Some(BarsAssessmentType.Yes)
    case "no"  => Some(BarsAssessmentType.No)
    case _     => None
}
