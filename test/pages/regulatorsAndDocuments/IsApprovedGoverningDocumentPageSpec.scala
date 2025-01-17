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

package pages.regulatorsAndDocuments

import models.UserAnswers
import pages.behaviours.PageBehaviours
import play.api.libs.json.Json

class IsApprovedGoverningDocumentPageSpec extends PageBehaviours {

  "IsApprovedGoverningDocumentPage" must {

    beRetrievable[Boolean](IsApprovedGoverningDocumentPage)

    beSettable[Boolean](IsApprovedGoverningDocumentPage)

    beRemovable[Boolean](IsApprovedGoverningDocumentPage)

    "cleanup" when {

      val userAnswer = UserAnswers("id", Json.obj())
        .set(IsApprovedGoverningDocumentPage, true)
        .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
        .flatMap(_.set(SectionsChangedGoverningDocumentPage, "change"))
        .success
        .value

      "setting IsApprovedGoverningDocumentPage to false" must {

        val result = userAnswer.set(IsApprovedGoverningDocumentPage, false).success.value

        "remove HasCharityChangedPartsOfGoverningDocumentPage" in {

          result.get(HasCharityChangedPartsOfGoverningDocumentPage) mustNot be(defined)
        }

        "remove SectionsChangedGoverningDocumentPage" in {

          result.get(SectionsChangedGoverningDocumentPage) mustNot be(defined)
        }
      }

      "setting IsApprovedGoverningDocumentPage to true" must {

        val result = userAnswer.set(IsApprovedGoverningDocumentPage, true).success.value

        "not remove HasCharityChangedPartsOfGoverningDocumentPage" in {

          result.get(HasCharityChangedPartsOfGoverningDocumentPage) must be(defined)
        }

        "not remove SectionsChangedGoverningDocumentPage" in {

          result.get(SectionsChangedGoverningDocumentPage) must be(defined)
        }
      }
    }
  }
}
