/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
import base.data.messages.BaseMessages
import controllers.nominees.{routes => nomineesRoutes}
import models.{CheckMode, UserAnswers}
import pages.nominees.{ChooseNomineePage, IsAuthoriseNomineePage}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.SummaryListRowHelper
import viewmodels.nominees.NomineeDetailsSummaryHelper

class NomineeDetailsSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val helperWithOrganisation = new NomineeDetailsSummaryHelper(
    UserAnswers("id")
      .set(IsAuthoriseNomineePage, true)
      .flatMap(_.set(ChooseNomineePage, false))
      .success
      .value
  )

  private val helperNoNominee = new NomineeDetailsSummaryHelper(
    UserAnswers("id")
      .set(IsAuthoriseNomineePage, false)
      .success
      .value
  )

  "Nominee Details Check Your Answers Helper" must {

    "For the is authorise nominee answer" must {

      "have a correctly formatted summary list row" in {
        helperNoNominee.authoriseNomineeRow mustBe Some(
          summaryListRow(
            messages("isAuthoriseNominee.checkYourAnswersLabel"),
            HtmlContent(messages("site.no")),
            Some(messages("isAuthoriseNominee.checkYourAnswersLabel")),
            nomineesRoutes.IsAuthoriseNomineeController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the nominee type answer" must {

      "have a correctly formatted summary list row" in {
        helperWithOrganisation.nomineeTypeRow mustBe Some(
          summaryListRow(
            messages("chooseNominee.checkYourAnswersLabel"),
            HtmlContent(messages("chooseNominee.false")),
            Some(messages("chooseNominee.checkYourAnswersLabel")),
            nomineesRoutes.ChooseNomineeController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
