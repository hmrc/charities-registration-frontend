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

package viewModels.nominees

import assets.messages.BaseMessages
import base.SpecBase
import controllers.nominees.{routes => nomineesRoutes}
import models.{CheckMode, UserAnswers}
import pages.nominees.{ChooseNomineePage, IsAuthoriseNomineePage}
import viewmodels.SummaryListRowHelper
import viewmodels.nominees.{NomineeDetailsSummaryHelper, NomineeTypeSummaryHelper}

class NomineeTypeSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val helper = new NomineeTypeSummaryHelper(UserAnswers("id")
    .set(ChooseNomineePage, false).success.value)


  "Nominee Type Check Your Answers Helper" must {

    "For the is authorise nominee answer" must {

      "have a correctly formatted summary list row" in {
        helper.nomineeTypeRow mustBe Some(summaryListRow(
          messages("chooseNominee.checkYourAnswersLabel"),
          messages("chooseNominee.false"),
          Some(messages("chooseNominee.checkYourAnswersLabel")),
          nomineesRoutes.ChooseNomineeController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }
  }
}
