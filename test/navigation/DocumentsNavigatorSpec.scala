/*
 * Copyright 2020 HM Revenue & Customs
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

package navigation

import base.SpecBase
import controllers.routes
import models._
import models.regulators.SelectGoverningDocument
import pages.regulatorsAndDocuments._
import pages.{IndexPage, QuestionPage}

class DocumentsNavigatorSpec extends SpecBase {

  val navigator = new DocumentsNavigator()

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the SelectGoverningDocument" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(SelectGoverningDocumentPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Governing Document name page when Other is selected" in {

          navigator.nextPage(SelectGoverningDocumentPage, NormalMode,
            emptyUserAnswers.set(SelectGoverningDocumentPage, SelectGoverningDocument.Other).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO modify once Governing Document name page is created
        }

        "go to the Governing Document approved page when other than Other is selected" in {
          navigator.nextPage(SelectGoverningDocumentPage, NormalMode,
            emptyUserAnswers.set(SelectGoverningDocumentPage, SelectGoverningDocument.Will).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO modify once Governing Document approved page is created
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

    "in Check mode" when {

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

    def userAnswers(page: QuestionPage[Boolean], value: Boolean): UserAnswers = {
      emptyUserAnswers.set(page, value).getOrElse(emptyUserAnswers)
    }
  }

}
