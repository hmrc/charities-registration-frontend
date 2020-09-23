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
import controllers.otherOfficials.{routes => otherOfficialRoutes}
import models.{CheckMode, Index, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.authorisedOfficials._
import pages.otherOfficials._
import service.CountryService
import viewmodels.SummaryListRowHelper
import viewmodels.authorisedOfficials.AddedOfficialsSummaryHelper

class AddedOfficialsSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val authorisedOfficialDetails: UserAnswers = emptyUserAnswers
  .set(IsAddAnotherAuthorisedOfficialPage, true).success.value

  private val otherOfficialDetails: UserAnswers = emptyUserAnswers
  .set(AddAnotherOtherOfficialPage, false).success.value

  lazy val mockCountryService: CountryService = MockitoSugar.mock[CountryService]
  when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

  def helper(userAnswers: UserAnswers, index: Index) = new AddedOfficialsSummaryHelper(index, countryService = mockCountryService)(userAnswers)

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

    "For the addedAnotherOtherOfficial answer" must {

      "have a correctly formatted summary list row" in {
        helper(otherOfficialDetails, 1).addedAnotherOtherOfficialRow mustBe Some(summaryListRow(
          messages("addAnotherOtherOfficial.checkYourAnswersLabel"),
          BaseMessages.no,
          Some(messages("addAnotherOtherOfficial.checkYourAnswersLabel")),
          otherOfficialRoutes.AddAnotherOtherOfficialController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ))
      }
    }

  }
}
