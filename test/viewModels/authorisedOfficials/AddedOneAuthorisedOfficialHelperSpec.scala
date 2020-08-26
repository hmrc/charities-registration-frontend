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


import java.time.LocalDate

import assets.constants.ConfirmedAddressConstants
import assets.messages.BaseMessages
import base.SpecBase
import controllers.authorisedOfficials.{routes => authOfficials}
import models.authOfficials.OfficialsPosition
import models.{CheckMode, Index, Name, PhoneNumber, UserAnswers}
import pages.addressLookup.AuthorisedOfficialAddressLookupPage
import pages.authorisedOfficials._
import viewmodels.SummaryListRowHelper
import viewmodels.authorisedOfficials.AddedOneAuthorisedOfficialHelper

class AddedOneAuthorisedOfficialHelperSpec extends SpecBase with SummaryListRowHelper {

  private val year = 2000
  private val month = 1
  private val dayOfMonth = 2

  private val authorisedOfficialDetails: UserAnswers = emptyUserAnswers
    .set(AuthorisedOfficialsNamePage(0), Name(firstName = "John", None, lastName = "Jones")).success.value
    .set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, dayOfMonth)).success.value
    .set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber(daytimePhone = "07700 900 982",
                                             mobilePhone = Some("07700 900 982"))).success.value
    .set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.values.head).success.value
    .set(IsAuthorisedOfficialNinoPage(0), true).success.value
    .set(AuthorisedOfficialsNinoPage(0), "AA123456A").success.value
    .set(AuthorisedOfficialAddressLookupPage(0), ConfirmedAddressConstants.address).success.value
    .set(AuthorisedOfficialPreviousAddressPage(0), false).success.value


  def helper(userAnswers: UserAnswers = authorisedOfficialDetails, index: Index) =   new AddedOneAuthorisedOfficialHelper(index)(userAnswers)


  "Check Your Answers Helper" must {

    "For the Authorised Official names answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialNamesRow mustBe Some(summaryListRow(
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

        helper(authorisedOfficialDetails, 0).authOfficialDobRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsDOB.checkYourAnswersLabel"),
            "2 January 2000",
            Some(messages("authorisedOfficialsDOB.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsDOBController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official Main Phone Number answer" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails, 0).authOfficialMainPhoneNoRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsPhoneNumber.mainPhoneNumber.checkYourAnswersLabel"),
            "07700 900 982",
            Some(messages("authorisedOfficialsPhoneNumber.mainPhoneNumber.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPhoneNumberController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          ))
      }
    }


    "For the Authorised Official Alternative Phone Number answer" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails, 0).authOfficialAlternativePhoneNoRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel"),
            "07700 900 982",
            Some(messages("authorisedOfficialsPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPhoneNumberController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          ))
      }
    }

    "For the Authorised Official Position answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialPositionRow mustBe Some(
          summaryListRow(
            messages("officialsPosition.checkYourAnswersLabel"),
            "Board member",
            Some(messages("officialsPosition.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPositionController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the does Authorised Official have NINO answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialHasNinoRow mustBe Some(
          summaryListRow(
            messages("isAuthorisedOfficialNino.checkYourAnswersLabel"),
            s"${messages("site.yes")}",
            Some(messages("isAuthorisedOfficialNino.checkYourAnswersLabel")),
            authOfficials.IsAuthorisedOfficialNinoController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official NINO answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails, 0).authOfficialNinoRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsNino.checkYourAnswersLabel"),
            "AA123456A",
            Some(messages("authorisedOfficialsNino.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsNinoController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official address answer" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails, 0).authOfficialAddressRow mustBe Some(
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

        helper(authorisedOfficialDetails, 0).authOfficialHadPreviousAddressRow mustBe Some(
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
