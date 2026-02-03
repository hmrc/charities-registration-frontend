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

package navigation

import base.SpecBase
import controllers.contactDetails.{routes => charityInfoRoutes}
import controllers.routes
import models._
import models.addressLookup.AddressModel
import pages.IndexPage
import pages.addressLookup.{CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityInformationSummaryPage, CharityNamePage}

class CharityInformationNavigatorSpec extends SpecBase {

  private val navigator: CharityInformationNavigator = inject[CharityInformationNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the CharityNamePage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityContactDetailsController page when clicked continue button" in {
          navigator.nextPage(
            CharityNamePage,
            NormalMode,
            emptyUserAnswers.set(CharityNamePage, charityName).success.value
          ) mustBe
            charityInfoRoutes.CharityContactDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the CharityContactDetailsPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityContactDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the CharityInformationAddressLookupController page when clicked continue button" in {
          navigator.nextPage(
            CharityContactDetailsPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityContactDetailsPage,
                charityContactDetails
              )
              .success
              .value
          ) mustBe
            controllers.addressLookup.routes.CharityOfficialAddressLookupController.initializeJourney
        }

        "go to the ConfirmCharityOfficialAddressController page when CharityOfficialAddressLookupPage is present and clicked continue button" in {
          navigator.nextPage(
            CharityContactDetailsPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityContactDetailsPage,
                charityContactDetails
              )
              .flatMap(
                _.set(
                  CharityOfficialAddressLookupPage,
                  address
                )
              )
              .success
              .value
          ) mustBe
            controllers.contactDetails.routes.ConfirmCharityOfficialAddressController.onPageLoad()
        }
      }

      "from the CharityInformationAddressLookupPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityOfficialAddressLookupPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Send letters page when clicked Confirm and continue button" in {
          navigator.nextPage(
            CharityOfficialAddressLookupPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityOfficialAddressLookupPage,
                address
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.CanWeSendToThisAddressController.onPageLoad(NormalMode)
        }

        "go to the Amend address page if user entered invalid characters for postcode when clicked Confirm and continue button" in {
          navigator.nextPage(
            CharityOfficialAddressLookupPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityOfficialAddressLookupPage,
                address.copy(postcode = Some(ukPostcode + ")("))
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.AmendCharityOfficialAddressController.onPageLoad()
        }

        "go to the Amend address page if address lines < 2  when clicked Confirm and continue button" in {
          navigator.nextPage(
            CharityOfficialAddressLookupPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityOfficialAddressLookupPage,
                addressModelMin
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.AmendCharityOfficialAddressController.onPageLoad()
        }

      }

      "from the CanWeSendToThisAddressPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CanWeSendToThisAddressPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when yes is selected" in {
          navigator.nextPage(
            CanWeSendToThisAddressPage,
            NormalMode,
            emptyUserAnswers.set(CanWeSendToThisAddressPage, true).success.value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }

        "go to the Postal Address Lookup flow when no is selected" in {
          navigator.nextPage(
            CanWeSendToThisAddressPage,
            NormalMode,
            emptyUserAnswers.set(CanWeSendToThisAddressPage, false).success.value
          ) mustBe
            controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney
        }

        "go to the ConfirmCharityPostalAddressController page when CharityOfficialAddressLookupPage is present and clicked continue button" in {
          navigator.nextPage(
            CanWeSendToThisAddressPage,
            NormalMode,
            emptyUserAnswers
              .set(CanWeSendToThisAddressPage, false)
              .flatMap(
                _.set(
                  CharityPostalAddressLookupPage,
                  address
                )
              )
              .success
              .value
          ) mustBe
            controllers.contactDetails.routes.ConfirmCharityPostalAddressController.onPageLoad()
        }
      }

      "from the CharityInformationPostalAddressLookupPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityPostalAddressLookupPage, NormalMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when clicked Confirm and continue button" in {
          navigator.nextPage(
            CharityPostalAddressLookupPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityPostalAddressLookupPage,
                address
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }

        "go to the Amend address page if user entered invalid characters in address line when clicked Confirm and continue button" in {
          navigator.nextPage(
            CharityPostalAddressLookupPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityPostalAddressLookupPage,
                AddressModel(Seq(line1, line2 + "£$"), Some(ukPostcode), gbCountryModel)
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.AmendCharityPostalAddressController.onPageLoad()
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(CharityInformationSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }

      "from any Unknown page" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Check mode" when {

      "from any Unknown page" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }

      "from the CharityNamePage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityNamePage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {

          navigator.nextPage(
            CharityNamePage,
            CheckMode,
            emptyUserAnswers.set(CharityNamePage, charityName).success.value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the CharityContactDetailsPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityContactDetailsPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {

          navigator.nextPage(
            CharityContactDetailsPage,
            CheckMode,
            emptyUserAnswers
              .set(
                CharityContactDetailsPage,
                charityContactDetails
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the CharityInformationAddressLookupPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityOfficialAddressLookupPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Send letters page when clicked continue button" in {
          navigator.nextPage(
            CharityOfficialAddressLookupPage,
            CheckMode,
            emptyUserAnswers
              .set(
                CharityOfficialAddressLookupPage,
                address
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the CanWeSendToThisAddressPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CanWeSendToThisAddressPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when yes is selected" in {
          navigator.nextPage(
            CanWeSendToThisAddressPage,
            CheckMode,
            emptyUserAnswers.set(CanWeSendToThisAddressPage, true).success.value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }

        "go to the Postal Address Lookup flow when no is selected and postal address in not defined" in {
          navigator.nextPage(
            CanWeSendToThisAddressPage,
            CheckMode,
            emptyUserAnswers.set(CanWeSendToThisAddressPage, false).success.value
          ) mustBe
            controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney
        }

        "go to the Postal Address Lookup flow when no is selected and address is defined" in {
          navigator.nextPage(
            CanWeSendToThisAddressPage,
            CheckMode,
            emptyUserAnswers
              .set(CanWeSendToThisAddressPage, false)
              .flatMap(
                _.set(
                  CharityPostalAddressLookupPage,
                  address
                )
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the CharityInformationPostalAddressLookupPage" must {

        "go to the PageNotFoundController page when user answer is empty" in {
          navigator.nextPage(CharityPostalAddressLookupPage, CheckMode, emptyUserAnswers) mustBe
            routes.PageNotFoundController.onPageLoad()
        }

        "go to the Charity Details Summary page when clicked Confirm and continue button" in {
          navigator.nextPage(
            CharityPostalAddressLookupPage,
            CheckMode,
            emptyUserAnswers
              .set(
                CharityPostalAddressLookupPage,
                address
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

    }

    "in Playback mode" when {

      "attempting to go to any site" must {

        "go to the PageNotFoundController page" in {
          navigator.nextPage(CharityPostalAddressLookupPage, PlaybackMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad(None)
        }
      }

      "go to the BankDetailsController page" in {
        navigator.nextPage(
          CharityNamePage,
          PlaybackMode,
          emptyUserAnswers
            .set(CharityNamePage, charityName)
            .success
            .value
        ) mustBe
          controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(NormalMode)
      }

      "go to the PageNotFoundController page" in {
        navigator.nextPage(CharityNamePage, PlaybackMode, emptyUserAnswers) mustBe
          routes.PageNotFoundController.onPageLoad()
      }
    }
  }
}
