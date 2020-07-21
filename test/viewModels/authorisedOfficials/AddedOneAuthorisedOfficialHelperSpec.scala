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


import assets.constants.ConfirmedAddressConstants
import controllers.authorisedOfficials.{routes => authOfficials}
import models.{AuthorisedOfficialsName, CheckMode, Index, UserAnswers}
import models.AuthOfficials.AuthorisedOfficialsPosition
import assets.messages.BaseMessages
import base.SpecBase
import java.time.LocalDate
import pages.authorisedOfficials._
import pages.addressLookup.AuthorisedOfficialAddressLookupPage
import viewmodels.SummaryListRowHelper
import viewmodels.authorisedOfficials.AddedOneAuthorisedOfficialHelper

class AddedOneAuthorisedOfficialHelperSpec extends SpecBase with SummaryListRowHelper {

  private val year = 2000
  private val month = 1
  private val dayOfMonth = 2

  private val authorisedOfficialDetails: UserAnswers = emptyUserAnswers
    .set(AuthorisedOfficialsNamePage(0), AuthorisedOfficialsName(firstName = "John", None, lastName = "Jones")).success.value
    .set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, dayOfMonth)).success.value
    .set(AuthorisedOfficialsPositionPage(0), AuthorisedOfficialsPosition.values.head).success.value
    .set(IsAuthorisedOfficialPositionPage(0), true).success.value
    .set(AuthorisedOfficialsNINOPage(0), "AA123456A").success.value
    .set(AuthorisedOfficialAddressLookupPage(0), ConfirmedAddressConstants.address).success.value
    .set(AuthorisedOfficialPreviousAddressPage(0), false).success.value


  def helper(userAnswers: UserAnswers = authorisedOfficialDetails, index: Index) =   new AddedOneAuthorisedOfficialHelper(index)(userAnswers)


  "Check Your Answers Helper" must {

    "For the Authorised Official names answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialNamesRows mustBe Seq(summaryListRow(
          messages("authorisedOfficialsName.checkYourAnswersLabel"),
          "John Jones",
          Some(messages("authorisedOfficialsName.checkYourAnswersLabel")),
          authOfficials.AuthorisedOfficialsNameController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
        )
        )
      }
    }

    "For the Authorised Official Date of Birth answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialDobRows mustBe Seq(
          summaryListRow(
            messages("authorisedOfficialsDOB.checkYourAnswersLabel"),
            "2 January 2000",
            Some(messages("authorisedOfficialsDOB.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsDOBController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official Position answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialPositionRows mustBe Seq(
          summaryListRow(
            messages("authorisedOfficialsPosition.checkYourAnswersLabel"),
            "Board member",
            Some(messages("authorisedOfficialsPosition.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPositionController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the does Authorised Official have NINO answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialHasNINORow mustBe Seq(
          summaryListRow(
            messages("isAuthorisedOfficialPosition.checkYourAnswersLabel"),
            s"${messages("site.yes")}",
            Some(messages("isAuthorisedOfficialPosition.checkYourAnswersLabel")),
            authOfficials.IsAuthorisedOfficialPositionController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official NINO answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialNINoRows mustBe Seq(
          summaryListRow(
            messages("authorisedOfficialsNINO.checkYourAnswersLabel"),
            "AA123456A",
            Some(messages("authorisedOfficialsNINO.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsNINOController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official address answer" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails, 0).authOfficialAddressRow mustBe Seq(
          summaryListRow(
            messages("authorisedOfficialAddress.checkYourAnswersLabel"),
            "Test 1, Test 2, AA00 0AA, United Kingdom",
            Some(messages("authorisedOfficialAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.AuthorisedOfficialsAddressLookupController.initializeJourney(0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Previous Address query answer if the answer is no" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails, 0).authOfficialHadPreviousAddressRow mustBe Seq(
          summaryListRow(
            messages("authorisedOfficialPreviousAddress.checkYourAnswersLabel"),
            s"${messages("site.no")}",
            Some(messages("authorisedOfficialPreviousAddress.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialPreviousAddressController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
