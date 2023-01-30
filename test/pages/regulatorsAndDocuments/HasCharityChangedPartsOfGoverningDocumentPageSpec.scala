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

package pages.regulatorsAndDocuments

import models.UserAnswers
import pages.behaviours.PageBehaviours
import play.api.libs.json.Json

class HasCharityChangedPartsOfGoverningDocumentPageSpec extends PageBehaviours {

  "HasCharityChangedPartsOfGoverningDocumentPage" must {

    beRetrievable[Boolean](HasCharityChangedPartsOfGoverningDocumentPage)

    beSettable[Boolean](HasCharityChangedPartsOfGoverningDocumentPage)

    beRemovable[Boolean](HasCharityChangedPartsOfGoverningDocumentPage)

    "cleanup" when {

      val userAnswer = UserAnswers("id", Json.obj())
        .set(HasCharityChangedPartsOfGoverningDocumentPage, true)
        .flatMap(_.set(SectionsChangedGoverningDocumentPage, "Section One"))
        .success
        .value

      "setting HasCharityChangedPartsOfGoverningDocumentPage to SectionsChangedGoverningDocumentPage" must {

        "remove SectionsChangedGoverningDocumentPage" in {

          val result = userAnswer.set(HasCharityChangedPartsOfGoverningDocumentPage, false).success.value

          result.get(SectionsChangedGoverningDocumentPage) mustNot be(defined)
        }

        "not remove SectionsChangedGoverningDocumentPage" in {

          val result = userAnswer.set(HasCharityChangedPartsOfGoverningDocumentPage, true).success.value

          result.get(SectionsChangedGoverningDocumentPage) must be(defined)
        }

      }

    }
  }
}
