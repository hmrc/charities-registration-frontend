/*
 * Copyright 2026 HM Revenue & Customs
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

package viewModels.otherOfficials

import base.SpecBase
import base.data.constants.ConfirmedAddressConstants
import base.data.messages.BaseMessages
import controllers.otherOfficials.routes as otherOfficials
import models.authOfficials.OfficialsPosition
import models.{CheckMode, Index, Name, PhoneNumber, UserAnswers}
import pages.addressLookup.{OtherOfficialAddressLookupPage, OtherOfficialPreviousAddressLookupPage}
import pages.otherOfficials.*
import play.api.i18n.Messages
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.SummaryListRowHelper
import viewmodels.otherOfficials.AddedOtherOfficialHelper

import java.time.LocalDate

class AddedOtherOfficialHelperSpec extends SpecBase with SummaryListRowHelper {

  private val year       = 2000
  private val month      = 1
  private val dayOfMonth = 2

  private val otherOfficialDetails: UserAnswers = emptyUserAnswers
    .set(OtherOfficialsNamePage(0), personNameWithoutMiddle)
    .success
    .value
    .set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, dayOfMonth))
    .success
    .value
    .set(
      OtherOfficialsPhoneNumberPage(0),
      phoneNumbers
    )
    .success
    .value
    .set(OtherOfficialsPositionPage(0), OfficialsPosition.values.head)
    .success
    .value
    .set(IsOtherOfficialNinoPage(0), true)
    .success
    .value
    .set(OtherOfficialsNinoPage(0), nino)
    .success
    .value
    .set(OtherOfficialAddressLookupPage(0), ConfirmedAddressConstants.address)
    .success
    .value
    .set(IsOtherOfficialsPreviousAddressPage(0), false)
    .success
    .value

  private val welshRequest: FakeRequest[?] = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "cy"))
  private lazy val welshMessages: Messages = messagesApi.preferred(welshRequest)

  def helperWelsh(userAnswers: UserAnswers = otherOfficialDetails, index: Index): AddedOtherOfficialHelper =
    new AddedOtherOfficialHelper(index, CheckMode, countryService = CountryService)(userAnswers)(welshMessages)

  def helper(userAnswers: UserAnswers = otherOfficialDetails, index: Index): AddedOtherOfficialHelper =
    new AddedOtherOfficialHelper(index, CheckMode, countryService = CountryService)(userAnswers)

  "Check Your Answers Helper" must {

    "For the Other Official names answers" must {

      "have a correctly formatted summary list rows" in {

        helper(otherOfficialDetails, 0).otherOfficialNamesRow mustBe Some(
          summaryListRow(
            messages("otherOfficialsName.checkYourAnswersLabel"),
            HtmlContent("Mr Firstname Lastname"),
            Some(messages("otherOfficialsName.checkYourAnswersLabel")),
            otherOfficials.OtherOfficialsNameController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Official Date of Birth answers" must {

      "have a correctly formatted summary list rows" in {

        helper(otherOfficialDetails, 0).otherOfficialDobRow mustBe Some(
          summaryListRow(
            messages("otherOfficialsDOB.checkYourAnswersLabel"),
            HtmlContent("2 January 2000"),
            Some(messages("otherOfficialsDOB.checkYourAnswersLabel")),
            otherOfficials.OtherOfficialsDOBController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Official Main Phone Number answer" must {

      "have a correctly formatted summary list row" in {

        helper(otherOfficialDetails, 0).otherOfficialMainPhoneNoRow mustBe Some(
          summaryListRow(
            messages("otherOfficialsPhoneNumber.mainPhoneNumber.checkYourAnswersLabel"),
            HtmlContent(daytimePhone),
            Some(messages("otherOfficialsPhoneNumber.mainPhoneNumber.checkYourAnswersLabel")),
            otherOfficials.OtherOfficialsPhoneNumberController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Official Alternative Phone Number answer" must {

      "have a correctly formatted summary list row" in {

        helper(otherOfficialDetails, 0).otherOfficialAlternativePhoneNoRow mustBe Some(
          summaryListRow(
            messages("otherOfficialsPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel"),
            HtmlContent(mobileNumber),
            Some(messages("otherOfficialsPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel")),
            otherOfficials.OtherOfficialsPhoneNumberController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Official Position answers" must {

      "have a correctly formatted summary list rows" in {

        helper(otherOfficialDetails, 0).otherOfficialPositionRow mustBe Some(
          summaryListRow(
            messages("otherOfficialsPosition.checkYourAnswersLabel"),
            HtmlContent("Board member"),
            Some(messages("otherOfficialsPosition.checkYourAnswersLabel")),
            otherOfficials.OtherOfficialsPositionController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the does Other Official have NINO answers" must {

      "have a correctly formatted summary list rows" in {

        helper(otherOfficialDetails, 0).otherOfficialHasNinoRow mustBe Some(
          summaryListRow(
            messages("isOtherOfficialNino.checkYourAnswersLabel"),
            HtmlContent(s"${messages("site.yes")}"),
            Some(messages("isOtherOfficialNino.checkYourAnswersLabel")),
            otherOfficials.IsOtherOfficialNinoController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Official NINO answers" must {

      "have a correctly formatted summary list rows" in {

        helper(otherOfficialDetails, 0).otherOfficialNinoRow mustBe Some(
          summaryListRow(
            messages("otherOfficialsNino.checkYourAnswersLabel"),
            HtmlContent(nino),
            Some(messages("otherOfficialsNino.checkYourAnswersLabel")),
            otherOfficials.OtherOfficialsNinoController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Official address answer" must {

      "have a correctly formatted summary list row" in {

        helper(otherOfficialDetails, 0).otherOfficialAddressRow mustBe Some(
          summaryListRow(
            messages("otherOfficialAddress.checkYourAnswersLabel"),
            Text(s"$line1, $line2, $ukPostcode, $gbCountryName"),
            Some(messages("otherOfficialAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.OtherOfficialsAddressLookupController
              .initializeJourney(0, CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Previous Address query answer if the answer is no" must {

      "have a correctly formatted summary list row" in {

        helper(otherOfficialDetails, 0).otherOfficialHadPreviousAddressRow mustBe Some(
          summaryListRow(
            messages("isOtherOfficialsPreviousAddress.checkYourAnswersLabel"),
            HtmlContent(s"${messages("site.no")}"),
            Some(messages("isOtherOfficialsPreviousAddress.checkYourAnswersLabel")),
            otherOfficials.IsOtherOfficialsPreviousAddressController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Official previous address answer" must {

      "have a correctly formatted summary list row" in {

        helper(
          otherOfficialDetails
            .set(OtherOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address)
            .success
            .value,
          0
        ).otherOfficialPreviousAddressRow mustBe Some(
          summaryListRow(
            messages("otherOfficialPreviousAddress.checkYourAnswersLabel"),
            Text(s"$line1, $line2, $ukPostcode, $gbCountryName"),
            Some(messages("otherOfficialPreviousAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.OtherOfficialsPreviousAddressLookupController
              .initializeJourney(0, CheckMode) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list row in welsh" in {

        helperWelsh(
          otherOfficialDetails
            .set(OtherOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address)
            .success
            .value,
          0
        ).otherOfficialPreviousAddressRow mustBe Some(
          summaryListRow(
            welshMessages("otherOfficialPreviousAddress.checkYourAnswersLabel"),
            Text(s"$line1, $line2, $ukPostcode, Y Deyrnas Unedig"),
            Some(welshMessages("otherOfficialPreviousAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.OtherOfficialsPreviousAddressLookupController
              .initializeJourney(0, CheckMode) -> BaseMessages.changeLinkWelsh
          )
        )
      }
    }
  }
}
