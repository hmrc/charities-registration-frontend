/*
 * Copyright 2022 HM Revenue & Customs
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

package viewModels.charityInformation

import assets.constants.ConfirmedAddressConstants
import assets.messages.BaseMessages
import base.SpecBase
import controllers.contactDetails.{routes => charityInfoRoutes}
import models.{CharityContactDetails, CharityName, CheckMode, UserAnswers}
import pages.addressLookup.{CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import play.api.i18n.Messages
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.SummaryListRowHelper
import viewmodels.charityInformation.CharityInformationSummaryHelper

class CharityInformationSummaryHelperSpec extends SpecBase with SummaryListRowHelper {

  private val officialAddress: UserAnswers = emptyUserAnswers
    .set(CharityNamePage, CharityName(fullName = "Believe",
                                      operatingName = Some("Original Charity"))).success.value
    .set(CharityContactDetailsPage, CharityContactDetails(daytimePhone = "07700 900 982",
                                                          mobilePhone = Some("07700 900 982"),
                                                          emailAddress = "japan@china.com")).success.value
    .set(CharityOfficialAddressLookupPage, ConfirmedAddressConstants.address).success.value
    .set(CanWeSendToThisAddressPage, true).success.value

  private val postalAnswers: UserAnswers = emptyUserAnswers
    .set(CharityNamePage, CharityName(fullName = "Believe",
      operatingName = Some("Original Charity"))).success.value
    .set(CharityContactDetailsPage, CharityContactDetails(daytimePhone = "07700 900 982",
                                                          mobilePhone = Some("07700 900 982"),
                                                          emailAddress = "japan@china.com")).success.value
    .set(CharityOfficialAddressLookupPage, ConfirmedAddressConstants.address).success.value
    .set(CanWeSendToThisAddressPage, false).success.value
    .set(CharityPostalAddressLookupPage, ConfirmedAddressConstants.address).success.value

  private val welshRequest: FakeRequest[_] = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "cy"))
  private lazy val welshMessages: Messages = messagesApi.preferred(welshRequest)

  def helperWelsh(userAnswers: UserAnswers = officialAddress) = new CharityInformationSummaryHelper(userAnswers)(welshMessages)


  def helper(userAnswers: UserAnswers = officialAddress) =   new CharityInformationSummaryHelper(userAnswers)


  "Check Your Answers Helper" must {

    "For the Charity names answers" must {

      "have a correctly formatted summary list rows" in {

        helper().charityNameRows mustBe Seq(summaryListRow(
          messages("charityName.fullName.checkYourAnswersLabel"),
          HtmlContent("Believe"),
          Some(messages("charityName.fullName.checkYourAnswersLabel")),
          charityInfoRoutes.CharityNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ),
        summaryListRow(
          messages("charityName.operatingName.checkYourAnswersLabel"),
          HtmlContent("Original Charity"),
          Some(messages("charityName.operatingName.checkYourAnswersLabel")),
          charityInfoRoutes.CharityNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        )
        )
      }
    }

    "For the Charity Contact Details answers" must {

      "have a correctly formatted summary list rows" in {

        helper().charityContactDetailsRows mustBe Seq(
          summaryListRow(
          messages("charityContactDetails.mainPhoneNumber.checkYourAnswersLabel"),
            HtmlContent("07700 900 982"),
          Some(messages("charityContactDetails.mainPhoneNumber.checkYourAnswersLabel")),
          charityInfoRoutes.CharityContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          ),
          summaryListRow(
            messages("charityContactDetails.alternativePhoneNumber.checkYourAnswersLabel"),
            HtmlContent("07700 900 982"),
            Some(messages("charityContactDetails.alternativePhoneNumber.checkYourAnswersLabel")),
            charityInfoRoutes.CharityContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          ),
          summaryListRow(
            messages("charityContactDetails.emailAddress.checkYourAnswersLabel"),
            HtmlContent("japan@china.com"),
            Some(messages("charityContactDetails.emailAddress.checkYourAnswersLabel")),
            charityInfoRoutes.CharityContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Charity official address answer" must {

      "have a correctly formatted summary list row" in {

        helper().officialAddressRow mustBe Seq(
          summaryListRow(
            messages("charityOfficialAddress.addressLookup.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, United Kingdom"),
            Some(messages("charityOfficialAddress.addressLookup.checkYourAnswersLabel")),
            controllers.addressLookup.routes.CharityOfficialAddressLookupController.initializeJourney() -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Can we send letters to this address answer if value is Yes" must {

      "have a correctly formatted summary list row" in {

        helper().canWeSendToThisAddressRow mustBe Seq(
          summaryListRow(
           messages("canWeSendLettersToThisAddress.checkYourAnswersLabel"),
            HtmlContent(s"<div>${messages("site.yes")}</div>${"Test 1, Test 2, AA00 0AA, United Kingdom"}"),
           Some(messages("canWeSendLettersToThisAddress.checkYourAnswersLabel")),
           charityInfoRoutes.CanWeSendToThisAddressController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Can we send letters to this address answer if value is No" must {

      "have a correctly formatted summary list row" in {

        helper(postalAnswers).canWeSendToThisAddressRow mustBe Seq(
          summaryListRow(
            messages("canWeSendLettersToThisAddress.checkYourAnswersLabel"),
            HtmlContent(s"${messages("site.no")}<div>${messages("canWeSendLettersToThisAddress.no.hint")}</div>"),
            Some(messages("canWeSendLettersToThisAddress.checkYourAnswersLabel")),
            charityInfoRoutes.CanWeSendToThisAddressController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Charity postal address answer" must {

      "have a correctly formatted summary list row" in {

        helper(postalAnswers).postalAddressRow mustBe Seq(
          summaryListRow(
            messages("charityPostalAddress.addressLookup.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, United Kingdom"),
            Some(messages("charityPostalAddress.addressLookup.checkYourAnswersLabel")),
            controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney() -> BaseMessages.changeLink
          )
        )
      }

      "have a correctly formatted postalAddressRow row in welsh" in {

        helperWelsh(postalAnswers).postalAddressRow mustBe Seq(
          summaryListRow(
            welshMessages("charityPostalAddress.addressLookup.checkYourAnswersLabel"),
            Text("Test 1, Test 2, AA00 0AA, Y Deyrnas Unedig"),
            Some(welshMessages("charityPostalAddress.addressLookup.checkYourAnswersLabel")),
            controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney() -> BaseMessages.changeLinkWelsh
          )
        )

      }

      "have a correctly formatted canWeSendToThisAddressRow row in welsh" in {
        helperWelsh(postalAnswers.set(CanWeSendToThisAddressPage, true).success.value).canWeSendToThisAddressRow mustBe Seq(
          summaryListRow(
            welshMessages("canWeSendLettersToThisAddress.checkYourAnswersLabel"),
            HtmlContent(s"<div>${welshMessages("site.yes")}</div>Test 1, Test 2, AA00 0AA, Y Deyrnas Unedig"),
            Some(welshMessages("canWeSendLettersToThisAddress.checkYourAnswersLabel")),
            controllers.contactDetails.routes.CanWeSendToThisAddressController.onPageLoad(CheckMode) -> BaseMessages.changeLinkWelsh
          )
        )
      }
    }
  }
}
