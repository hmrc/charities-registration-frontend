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

package pages.regulatorsAndDocuments

import models.UserAnswers
import models.regulators.CharityRegulator
import pages.QuestionPage
import play.api.libs.json.JsPath

import scala.util.Try

case object CharityRegulatorPage extends QuestionPage[Set[CharityRegulator]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "charityRegulator"

  override def cleanup(value: Option[Set[CharityRegulator]], userAnswers: UserAnswers): Try[UserAnswers] = value match {
    case Some(value) => {
      userAnswers.remove(CharityRegulator.pageMap.filterNot(p => value.contains(p._1)).values.toSeq)
    }
    case _ => super.cleanup(value, userAnswers)
  }
}
