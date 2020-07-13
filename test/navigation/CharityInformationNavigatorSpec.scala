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

package navigation

import base.SpecBase
import controllers.charityInformation.{routes => charityInfoRoutes}
import controllers.routes
import models._
import models.addressLookup.{AddressModel, CountryModel}
import pages.IndexPage
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.charityInformation.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityInformationSummaryPage, CharityNamePage}

class CharityInformationNavigatorSpec extends SpecBase {

  private val navigator: CharityInformationNavigator = inject[CharityInformationNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the CharityNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the CharityContactDetailsController page when clicked continue button" in {
          navigator.nextPage(CharityNamePage, NormalMode,
            emptyUserAnswers.set(CharityNamePage, CharityName("CName", Some("OpName"))).getOrElse(emptyUserAnswers)) mustBe
            charityInfoRoutes.CharityContactDetailsController.onPageLoad(NormalMode)
        }
      }

      "from the CharityContactDetailsPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityContactDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the CharityInformationAddressLookupController page when clicked continue button" in {
          navigator.nextPage(CharityContactDetailsPage, NormalMode,
            emptyUserAnswers.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", None, "abc@gmail.com")).getOrElse(emptyUserAnswers)) mustBe
            controllers.addressLookup.routes.CharityOfficialAddressLookupController.initializeJourney()
        }
      }

      "from the CharityInformationAddressLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityOfficialAddressLookupPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Send letters page when clicked continue button" in {
          navigator.nextPage(CharityOfficialAddressLookupPage, NormalMode,
            emptyUserAnswers.set(CharityOfficialAddressLookupPage,
              AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))).getOrElse(emptyUserAnswers)) mustBe
            charityInfoRoutes.CanWeSendToThisAddressController.onPageLoad(NormalMode)
        }
      }

      "from the CanWeSendToThisAddressPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CanWeSendToThisAddressPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Charity Details Summary page when yes is selected" in {
          navigator.nextPage(CanWeSendToThisAddressPage, NormalMode,
            emptyUserAnswers.set(CanWeSendToThisAddressPage, true).success.value) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }

        "go to the Postal Address Lookup flow when no is selected" in {
          navigator.nextPage(CanWeSendToThisAddressPage, NormalMode,
            emptyUserAnswers.set(CanWeSendToThisAddressPage, false).success.value) mustBe
            routes.DeadEndController.onPageLoad() //TODO modify once Postal Address Lookup flow is created
        }
      }

      "from the Summary page" must {

        "go to the Task List page when click continue button" in {
          navigator.nextPage(CharityInformationSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }

    "in Check mode" when {

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "from the CharityNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityNamePage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {

          navigator.nextPage(CharityNamePage, CheckMode,
            emptyUserAnswers.set(CharityNamePage, CharityName("CName", Some("OpName"))).getOrElse(emptyUserAnswers)) mustBe
              charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the CharityContactDetailsPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityContactDetailsPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Charity Details Summary page when an answer is given" in {

          navigator.nextPage(CharityContactDetailsPage, CheckMode,
            emptyUserAnswers.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", None, "abc@gmail.com")).getOrElse(emptyUserAnswers)) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the CharityInformationAddressLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityOfficialAddressLookupPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Send letters page when clicked continue button" in {
          navigator.nextPage(CharityOfficialAddressLookupPage, CheckMode,
            emptyUserAnswers.set(CharityOfficialAddressLookupPage,
              AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))).getOrElse(emptyUserAnswers)) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the CanWeSendToThisAddressPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CanWeSendToThisAddressPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Charity Details Summary page when yes is selected" in {
          navigator.nextPage(CanWeSendToThisAddressPage, CheckMode,
            emptyUserAnswers.set(CanWeSendToThisAddressPage,true).success.value) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }

        "go to the Postal Address Lookup flow when no is selected" in {
          navigator.nextPage(CanWeSendToThisAddressPage, CheckMode,
            emptyUserAnswers.set(CanWeSendToThisAddressPage,false).success.value) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
          //TODO if no is selected once the postal address page is created, it should redirect to that flow
        }
      }

    }
  }
}
