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

package viewModels.authorisedOfficials

import assets.messages.BaseMessages
import base.SpecBase
import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import models.{CheckMode, Index, UserAnswers}
import pages.authorisedOfficials._
import viewmodels.SummaryListRowHelper
import viewmodels.authorisedOfficials.AuthorisedOfficialsSummaryHelper

class AuthorisedOfficialsSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val authorisedOfficialDetails: UserAnswers = emptyUserAnswers
  .set(IsAddAnotherAuthorisedOfficialPage, true).success.value

  def helper(userAnswers: UserAnswers = authorisedOfficialDetails, index: Index) =   new AuthorisedOfficialsSummaryHelper(index)(userAnswers)

  "Check Your Answers Helper" must {

    "For the isAddAnotherAuthorisedOfficial answer" must {

      "have a correctly formatted summary list row" in {
        helper(authorisedOfficialDetails, 0).isAddAnotherAuthorisedOfficialRow mustBe Some(summaryListRow(
          messages("isAddAnotherAuthorisedOfficial.checkYourAnswersLabel"),
          BaseMessages.yes,
          Some(messages("isAddAnotherAuthorisedOfficial.checkYourAnswersLabel")),
          authOfficialRoutes.IsAddAnotherAuthorisedOfficialController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

  }
}
