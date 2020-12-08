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

package viewModels.otherOfficials

import java.time.LocalDate

import assets.constants.ConfirmedAddressConstants
import assets.messages.BaseMessages
import base.SpecBase
import controllers.otherOfficials.{routes => otherOfficials}
import models.authOfficials.OfficialsPosition
import models.{Index, Name, PhoneNumber, CheckMode, SelectTitle, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.addressLookup.{OtherOfficialAddressLookupPage, OtherOfficialPreviousAddressLookupPage}
import pages.otherOfficials._
import service.CountryService
import viewmodels.SummaryListRowHelper
import viewmodels.otherOfficials.AddedOtherOfficialHelper

class AddedOtherOfficialHelperSpec extends SpecBase with SummaryListRowHelper {

  private val year = 2000
  private val month = 1
  private val dayOfMonth = 2

  private val otherOfficialDetails: UserAnswers = emptyUserAnswers
    .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones")).success.value
    .set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, dayOfMonth)).success.value
    .set(OtherOfficialsPhoneNumberPage(0), PhoneNumber(daytimePhone = "07700 900 982",
                                             mobilePhone = Some("07700 900 982"))).success.value
    .set(OtherOfficialsPositionPage(0), OfficialsPosition.values.head).success.value
    .set(IsOtherOfficialNinoPage(0), true).success.value
    .set(OtherOfficialsNinoPage(0), "AA123456A").success.value
    .set(OtherOfficialAddressLookupPage(0), ConfirmedAddressConstants.address).success.value
    .set(IsOtherOfficialsPreviousAddressPage(0), false).success.value

  lazy val mockCountryService: CountryService = MockitoSugar.mock[CountryService]
  when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))


  def helper(userAnswers: UserAnswers = otherOfficialDetails, index: Index) =
    new AddedOtherOfficialHelper(index, CheckMode, countryService = mockCountryService)(userAnswers)


  "Check Your Answers Helper" must {

    "For the Other Official names answers" must {

      "have a correctly formatted summary list rows" in {

        helper(otherOfficialDetails, 0).otherOfficialNamesRow mustBe Some(summaryListRow(
          messages("otherOfficialsName.checkYourAnswersLabel"),
          "Mr John Jones",
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
            "2 January 2000",
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
            "07700 900 982",
            Some(messages("otherOfficialsPhoneNumber.mainPhoneNumber.checkYourAnswersLabel")),
            otherOfficials.OtherOfficialsPhoneNumberController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          ))
      }
    }


    "For the Other Official Alternative Phone Number answer" must {

      "have a correctly formatted summary list row" in {

        helper(otherOfficialDetails, 0).otherOfficialAlternativePhoneNoRow mustBe Some(
          summaryListRow(
            messages("otherOfficialsPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel"),
            "07700 900 982",
            Some(messages("otherOfficialsPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel")),
            otherOfficials.OtherOfficialsPhoneNumberController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          ))
      }
    }

    "For the Other Official Position answers" must {

      "have a correctly formatted summary list rows" in {

        helper(otherOfficialDetails, 0).otherOfficialPositionRow mustBe Some(
          summaryListRow(
            messages("otherOfficialsPosition.checkYourAnswersLabel"),
            "Board member",
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
            s"${messages("site.yes")}",
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
            "AA123456A",
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
            "Test 1, Test 2, AA00 0AA, United Kingdom",
            Some(messages("otherOfficialAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.OtherOfficialsAddressLookupController.initializeJourney(0, CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Previous Address query answer if the answer is no" must {

      "have a correctly formatted summary list row" in {

        helper(otherOfficialDetails, 0).otherOfficialHadPreviousAddressRow mustBe Some(
          summaryListRow(
            messages("isOtherOfficialsPreviousAddress.checkYourAnswersLabel"),
            s"${messages("site.no")}",
            Some(messages("isOtherOfficialsPreviousAddress.checkYourAnswersLabel")),
            otherOfficials.IsOtherOfficialsPreviousAddressController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Other Official previous address answer" must {

      "have a correctly formatted summary list row" in {

        helper(otherOfficialDetails.set(OtherOfficialPreviousAddressLookupPage(0),
          ConfirmedAddressConstants.address).success.value, 0).otherOfficialPreviousAddressRow mustBe Some(
          summaryListRow(
            messages("otherOfficialPreviousAddress.checkYourAnswersLabel"),
            "Test 1, Test 2, AA00 0AA, United Kingdom",
            Some(messages("otherOfficialPreviousAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.OtherOfficialsPreviousAddressLookupController.initializeJourney(0, CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
