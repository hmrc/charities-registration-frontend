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

package pages.operationsAndFunds

import models.UserAnswers
import models.operations.FundRaisingOptions
import models.operations.FundRaisingOptions.Other
import pages.QuestionPage
import play.api.libs.json.JsPath

import scala.util.Try

case object FundRaisingPage extends QuestionPage[Set[FundRaisingOptions]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "selectFundRaising"

  override def cleanup(value: Option[Set[FundRaisingOptions]], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(notOther) if !notOther.contains(Other) => userAnswers.remove(OtherFundRaisingPage)
      case _ => super.cleanup(value, userAnswers)
    }

}
