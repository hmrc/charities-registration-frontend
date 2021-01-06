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
import models.regulators.SelectGoverningDocument
import models.regulators.SelectGoverningDocument.Other
import pages.QuestionPage
import play.api.libs.json.JsPath

import scala.util.Try

case object SelectGoverningDocumentPage extends QuestionPage[SelectGoverningDocument] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "selectGoverningDocument"

  override def cleanup(value: Option[SelectGoverningDocument], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(item) if item != Other =>
        userAnswers.remove(Seq(GoverningDocumentNamePage))
      case _ =>
        super.cleanup(value, userAnswers)
    }
}
