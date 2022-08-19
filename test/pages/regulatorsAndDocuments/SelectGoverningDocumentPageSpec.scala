/*
 * Copyright 2022 HM Revenue & Customs
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
import models.regulators.SelectGoverningDocument.{MemorandumArticlesAssociation, Other}
import org.scalacheck.{Arbitrary, Gen}
import pages.behaviours.PageBehaviours
import play.api.libs.json.Json

class SelectGoverningDocumentPageSpec extends PageBehaviours {

  implicit lazy val arbitraryCharityContactDetails: Arbitrary[SelectGoverningDocument] = Arbitrary {
    Gen.oneOf(SelectGoverningDocument.values)
  }

  "SelectGoverningDocument" must {

    beRetrievable[SelectGoverningDocument](SelectGoverningDocumentPage)

    beSettable[SelectGoverningDocument](SelectGoverningDocumentPage)

    beRemovable[SelectGoverningDocument](SelectGoverningDocumentPage)

    "cleanup" when {

      val userAnswer = UserAnswers("id", Json.obj())
        .set(SelectGoverningDocumentPage, SelectGoverningDocument.values.head)
        .flatMap(_.set(GoverningDocumentNamePage, "will"))
        .success
        .value

      "setting SelectGoverningDocumentPage to other" must {

        val result = userAnswer.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation).success.value

        "remove GoverningDocumentNamePage" in {

          result.get(GoverningDocumentNamePage) mustNot be(defined)
        }
      }

      "setting SelectGoverningDocumentPage to other" must {

        val result = userAnswer.set(SelectGoverningDocumentPage, Other).success.value

        "not remove GoverningDocumentNamePage" in {

          result.get(GoverningDocumentNamePage) must be(defined)
        }
      }
    }
  }

}
