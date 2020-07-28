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

import java.time.LocalDate

import base.SpecBase
import controllers.addressLookup.{routes => addressLookupRoutes}
import controllers.otherOfficials.{routes => otherOfficialRoutes}
import controllers.routes
import models.AuthOfficials.OfficialsPosition
import models.addressLookup.{AddressModel, CountryModel}
import models.{CheckMode, Index, Name, NormalMode, PhoneNumber}
import pages.IndexPage
import pages.addressLookup.OtherOfficialAddressLookupPage
import pages.otherOfficials._

class OtherOfficialsNavigatorSpec extends SpecBase {

  private val navigator: OtherOfficialsNavigator = inject[OtherOfficialsNavigator]
  private val otherOfficialsName: Name = Name("Jim", Some("John"), "Jones")
  private val otherOfficialsPhoneNumber: PhoneNumber = PhoneNumber("07700 900 982", Some("07700 900 982"))
  private val address: AddressModel = AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))
  private val minYear = 16

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the OtherOfficialsNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsNamePage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]'s date of birth? when save and continue button clicked" in {
          navigator.nextPage(OtherOfficialsNamePage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsNamePage(0), otherOfficialsName).getOrElse(emptyUserAnswers)) mustBe
            otherOfficialRoutes.OtherOfficialsDOBController.onPageLoad(NormalMode, Index(0))
        }
      }

      "from the OtherOfficialsDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsDOBPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [full name]'s phone number? when save and continue button clicked" in {
          navigator.nextPage(OtherOfficialsDOBPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsDOBPage(0), LocalDate.now().minusYears(minYear)).getOrElse(emptyUserAnswers)) mustBe
            otherOfficialRoutes.OtherOfficialsPhoneNumberController.onPageLoad(NormalMode, Index(0))
        }
      }

      "from the OtherOfficialsPhoneNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsPhoneNumberPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [full name]'s position in charity? page when clicked continue button" in {
          navigator.nextPage(OtherOfficialsPhoneNumberPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsPhoneNumberPage(0), otherOfficialsPhoneNumber).getOrElse(emptyUserAnswers)) mustBe
            otherOfficialRoutes.OtherOfficialsPositionController.onPageLoad(NormalMode, Index(0))
        }
      }

      "from the OtherOfficialsPositionPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsPositionPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to Does [Full name] have a National Insurance number? when clicked continue button" in {
          navigator.nextPage(OtherOfficialsPositionPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsPositionPage(0), OfficialsPosition.BoardMember).getOrElse(emptyUserAnswers)) mustBe
            otherOfficialRoutes.IsOtherOfficialNinoController.onPageLoad(NormalMode, Index(0))
        }
      }

      "from the IsOtherOfficialNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsOtherOfficialNinoPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [full name]’s National Insurance number? when yes is selected" in {
          navigator.nextPage(IsOtherOfficialNinoPage(0), NormalMode,
            emptyUserAnswers.set(IsOtherOfficialNinoPage(0),true).success.value) mustBe
            otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(NormalMode, Index(0))
        }

        "go to the DeadEnd page when no is selected" in {
          navigator.nextPage(IsOtherOfficialNinoPage(0), NormalMode,
            emptyUserAnswers.set(IsOtherOfficialNinoPage(0),false).success.value) mustBe
            routes.DeadEndController.onPageLoad()
        }
      }

      "from the OtherOfficialsNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsNinoPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]’s home address? when clicked continue button" in {
          navigator.nextPage(OtherOfficialsNinoPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C").getOrElse(emptyUserAnswers)) mustBe
            addressLookupRoutes.OtherOfficialsAddressLookupController.initializeJourney(Index(0))
        }
      }

      "from the OtherOfficialAddressLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialAddressLookupPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Has [Full name]’s home address changed in the last 12 months? page when clicked continue button" in {
          navigator.nextPage(OtherOfficialAddressLookupPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialAddressLookupPage(0), address).getOrElse(emptyUserAnswers)) mustBe
            otherOfficialRoutes.OtherOfficialsPreviousAddressController.onPageLoad(NormalMode, Index(0)) // TODO when address lookup page is ready
        }
      }

      "from the OtherOfficialsPreviousAddress Page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsPreviousAddressPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to you have added one authorised official when clicked continue button" in {
          navigator.nextPage(OtherOfficialsPreviousAddressPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsPreviousAddressPage(0), true).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO When next page is created
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

      "from the OtherOfficialsNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsNamePage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the dead-end when save and continue button is clicked" in {
          navigator.nextPage(OtherOfficialsNamePage(0), CheckMode,
            emptyUserAnswers.set(OtherOfficialsNamePage(0), otherOfficialsName).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when summary page is ready
        }
      }

      "from the OtherOfficialsDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsDOBPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the dead-end when save and continue button is clicked" in {
          navigator.nextPage(OtherOfficialsDOBPage(0), CheckMode,
            emptyUserAnswers.set(OtherOfficialsDOBPage(0), LocalDate.now().minusYears(minYear)).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when summary page is ready
        }
      }

      "from the OtherOfficialsPhoneNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsPhoneNumberPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the dead-end when save and continue button is clicked" in {
          navigator.nextPage(OtherOfficialsPhoneNumberPage(0), CheckMode,
            emptyUserAnswers.set(OtherOfficialsPhoneNumberPage(0), otherOfficialsPhoneNumber).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when summary page is ready
        }
      }

      "from the OtherOfficialsPositionPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsPositionPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the dead-end when save and continue button is clicked" in {
          navigator.nextPage(OtherOfficialsPositionPage(0), CheckMode,
            emptyUserAnswers.set(OtherOfficialsPositionPage(0), OfficialsPosition.BoardMember).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when summary page is ready
        }
      }

      "from the IsOtherOfficialNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsOtherOfficialNinoPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the DeadEnd page when no is selected" in {
          navigator.nextPage(IsOtherOfficialNinoPage(0), CheckMode,
            emptyUserAnswers.set(IsOtherOfficialNinoPage(0),false).success.value) mustBe
            routes.DeadEndController.onPageLoad()
        }
      }

      "from the OtherOfficialsNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsNinoPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]’s home address? when clicked continue button" in {
          navigator.nextPage(OtherOfficialsNinoPage(0), CheckMode,
            emptyUserAnswers.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C").getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when summary page is ready
        }
      }

      "from the OtherOfficialAddressLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialAddressLookupPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(OtherOfficialAddressLookupPage(0), CheckMode,
            emptyUserAnswers.set(OtherOfficialAddressLookupPage(0), address).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when summary page is ready
        }
      }

      "from the OtherOfficialsPreviousAddress Page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsPreviousAddressPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when clicked continue button" in {
          navigator.nextPage(OtherOfficialsPreviousAddressPage(0), CheckMode,
            emptyUserAnswers.set(OtherOfficialsPreviousAddressPage(0), true).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when summary page is created
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }
    }
  }

}
