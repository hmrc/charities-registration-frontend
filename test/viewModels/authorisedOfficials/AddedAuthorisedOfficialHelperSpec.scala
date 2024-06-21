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

package viewModels.authorisedOfficials

import base.SpecBase
import base.data.constants.ConfirmedAddressConstants
import base.data.messages.BaseMessages
import controllers.authorisedOfficials.{routes => authOfficials}
import models.authOfficials.OfficialsPosition
import models.{CheckMode, Country, Index, Name, Passport, PhoneNumber, SelectTitle, UserAnswers}
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{mock, when}
import pages.addressLookup.{AuthorisedOfficialAddressLookupPage, AuthorisedOfficialPreviousAddressLookupPage}
import pages.authorisedOfficials._
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.SummaryListRowHelper
import viewmodels.authorisedOfficials.AddedAuthorisedOfficialHelper

import java.time.LocalDate

class AddedAuthorisedOfficialHelperSpec extends SpecBase with SummaryListRowHelper {

  private val year       = 2000
  private val month      = 1
  private val dayOfMonth = 2

  private def authorisedOfficialDetails(title: SelectTitle = SelectTitle.Mr): UserAnswers = emptyUserAnswers
    .set(AuthorisedOfficialsNamePage(0), Name(title, firstName = "John", None, lastName = "Jones"))
    .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, dayOfMonth)))
    .flatMap(
      _.set(
        AuthorisedOfficialsPhoneNumberPage(0),
        PhoneNumber(daytimePhone = "07700 900 982", mobilePhone = Some("07700 900 982"))
      )
    )
    .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.values.head))
    .flatMap(_.set(IsAuthorisedOfficialNinoPage(0), true))
    .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
    .flatMap(
      _.set(AuthorisedOfficialsPassportPage(0), Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth)))
    )
    .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0), ConfirmedAddressConstants.address))
    .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
    .success
    .value

  lazy val mockCountryService: CountryService = mock(classOf[CountryService])
  when(mockCountryService.find(meq("GB"))(any())).thenReturn(Some(Country("GB", "United Kingdom")))
  when(mockCountryService.find(meq("Unknown"))(any())).thenReturn(None)

  def helper(userAnswers: UserAnswers = authorisedOfficialDetails(), index: Index): AddedAuthorisedOfficialHelper =
    new AddedAuthorisedOfficialHelper(index, CheckMode, mockCountryService)(userAnswers)

  "Check Your Answers Helper" must {

    "For the Authorised Official names answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails(), 0).authOfficialNamesRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsName.checkYourAnswersLabel"),
            HtmlContent("Mr John Jones"),
            Some(messages("authorisedOfficialsName.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsNameController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list rows if title is unsupported" in {

        helper(authorisedOfficialDetails(SelectTitle.UnsupportedTitle), 0).authOfficialNamesRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsName.checkYourAnswersLabel"),
            HtmlContent("John Jones"),
            Some(messages("authorisedOfficialsName.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsNameController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official Date of Birth answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails(), 0).authOfficialDobRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsDOB.checkYourAnswersLabel"),
            HtmlContent("2 January 2000"),
            Some(messages("authorisedOfficialsDOB.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsDOBController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official Main Phone Number answer" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails(), 0).authOfficialMainPhoneNoRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsPhoneNumber.mainPhoneNumber.checkYourAnswersLabel"),
            HtmlContent("07700 900 982"),
            Some(messages("authorisedOfficialsPhoneNumber.mainPhoneNumber.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPhoneNumberController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official Alternative Phone Number answer" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails(), 0).authOfficialAlternativePhoneNoRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel"),
            HtmlContent("07700 900 982"),
            Some(messages("authorisedOfficialsPhoneNumber.alternativePhoneNumber.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPhoneNumberController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official Position answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails(), 0).authOfficialPositionRow mustBe Some(
          summaryListRow(
            messages("officialsPosition.checkYourAnswersLabel"),
            HtmlContent("Board member"),
            Some(messages("officialsPosition.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPositionController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the does Authorised Official have NINO answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails(), 0).authOfficialHasNinoRow mustBe Some(
          summaryListRow(
            messages("isAuthorisedOfficialNino.checkYourAnswersLabel"),
            HtmlContent(s"${messages("site.yes")}"),
            Some(messages("isAuthorisedOfficialNino.checkYourAnswersLabel")),
            authOfficials.IsAuthorisedOfficialNinoController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official NINO answers" must {

      "have a correctly formatted summary list rows" in {

        helper(authorisedOfficialDetails(), 0).authOfficialNinoRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsNino.checkYourAnswersLabel"),
            HtmlContent("AA123456A"),
            Some(messages("authorisedOfficialsNino.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsNinoController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official Passport answers" must {

      "have a correctly formatted summary list rows for passport number" in {

        helper(authorisedOfficialDetails(), 0).authOfficialPassportNumberRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsPassport.passportNumber.checkYourAnswersLabel"),
            HtmlContent("GB12345"),
            Some(messages("authorisedOfficialsPassport.passportNumber.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPassportController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list rows for country of issue" in {

        helper(authorisedOfficialDetails(), 0).authOfficialCountryOfIssueRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsPassport.country.checkYourAnswersLabel"),
            HtmlContent("United Kingdom"),
            Some(messages("authorisedOfficialsPassport.country.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPassportController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list rows for country of issue if code is Unknown" in {

        helper(
          authorisedOfficialDetails()
            .set(
              AuthorisedOfficialsPassportPage(0),
              Passport("GB12345", "Unknown", LocalDate.of(year, month, dayOfMonth))
            )
            .success
            .value,
          0
        ).authOfficialCountryOfIssueRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsPassport.country.checkYourAnswersLabel"),
            HtmlContent("Unknown"),
            Some(messages("authorisedOfficialsPassport.country.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPassportController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted summary list rows for expiry date" in {

        helper(authorisedOfficialDetails(), 0).authOfficialExpiryDateRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialsPassport.expiryDate.checkYourAnswersLabel"),
            HtmlContent("2 January 2000"),
            Some(messages("authorisedOfficialsPassport.expiryDate.checkYourAnswersLabel")),
            authOfficials.AuthorisedOfficialsPassportController.onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official address answer" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails(), 0).authOfficialAddressRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialAddress.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, United Kingdom"),
            Some(messages("authorisedOfficialAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.AuthorisedOfficialsAddressLookupController
              .initializeJourney(0, CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Previous Address query answer if the answer is no" must {

      "have a correctly formatted summary list row" in {

        helper(authorisedOfficialDetails(), 0).authOfficialHadPreviousAddressRow mustBe Some(
          summaryListRow(
            messages("isAuthorisedOfficialPreviousAddress.checkYourAnswersLabel"),
            HtmlContent(s"${messages("site.no")}"),
            Some(messages("isAuthorisedOfficialPreviousAddress.checkYourAnswersLabel")),
            authOfficials.IsAuthorisedOfficialPreviousAddressController
              .onPageLoad(CheckMode, 0) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Authorised Official previous address answer" must {

      "have a correctly formatted summary list row" in {

        helper(
          authorisedOfficialDetails()
            .set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address)
            .success
            .value,
          0
        ).authOfficialPreviousAddressRow mustBe Some(
          summaryListRow(
            messages("authorisedOfficialPreviousAddress.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, United Kingdom"),
            Some(messages("authorisedOfficialPreviousAddress.checkYourAnswersLabel")),
            controllers.addressLookup.routes.AuthorisedOfficialsPreviousAddressLookupController
              .initializeJourney(0, CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
