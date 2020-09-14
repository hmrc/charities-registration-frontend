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
import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import controllers.routes
import models.authOfficials.OfficialsPosition
import models.addressLookup.{AddressModel, CountryModel}
import models.{CheckMode, Index, Name, NormalMode, PhoneNumber}
import pages.IndexPage
import pages.addressLookup.AuthorisedOfficialAddressLookupPage
import pages.authorisedOfficials._

class AuthorisedOfficialsNavigatorSpec extends SpecBase {

  private val navigator: AuthorisedOfficialsNavigator = inject[AuthorisedOfficialsNavigator]

  private val authorisedOfficialsName: Name = Name("Jim", Some("John"), "Jones")
  private val authorisedOfficialsPhoneNumber: PhoneNumber = PhoneNumber("07700 900 982", "07700 900 982")
  private val address: AddressModel = AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))
  private val minYear = 16


  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {
        List(0,1).foreach(index => {

        s"from the AuthorisedOfficialsNamePage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsNamePage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the What is [Full name]'s date of birth? when clicked continue button" in {
            navigator.nextPage(AuthorisedOfficialsNamePage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName)
                .flatMap(_.set(AuthorisedOfficialsNamePage(index), authorisedOfficialsName))
                .getOrElse({
                emptyUserAnswers})
            ) mustBe
              authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(NormalMode, index)
          }
        }

       s"from the AuthorisedOfficialsDOBPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsDOBPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the What is [full name]'s phone number? page when clicked continue button" in {
            navigator.nextPage(AuthorisedOfficialsDOBPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialsDOBPage(0), LocalDate.now().minusYears(minYear))
                .flatMap(_.set(AuthorisedOfficialsDOBPage(index), LocalDate.now().minusYears(minYear)))
                .getOrElse(emptyUserAnswers)) mustBe
              authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(NormalMode, index)
          }
        }

        s"from the AuthorisedOfficialsPhoneNumberPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the What is [full name]'s position in charity? page when clicked continue button" in {
            navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialsPhoneNumberPage(0), authorisedOfficialsPhoneNumber)
              .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(index), authorisedOfficialsPhoneNumber))
                .getOrElse(emptyUserAnswers)) mustBe
              authOfficialRoutes.AuthorisedOfficialsPositionController.onPageLoad(NormalMode, index)
          }
        }

        s"from the AuthorisedOfficialsPositionPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsPositionPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to Does [Full name] have a National Insurance number? when clicked continue button" in {
            navigator.nextPage(AuthorisedOfficialsPositionPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.BoardMember)
                .flatMap(_.set(AuthorisedOfficialsPositionPage(index), OfficialsPosition.BoardMember))
                .getOrElse(emptyUserAnswers)) mustBe
              authOfficialRoutes.IsAuthorisedOfficialNinoController.onPageLoad(NormalMode, Index(index))
          }
        }

        s"from the IsAuthorisedOfficialNinoPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(IsAuthorisedOfficialNinoPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the What is [full name]’s National Insurance number? when yes is selected" in {
            navigator.nextPage(IsAuthorisedOfficialNinoPage(index), NormalMode,
              emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), true)
                .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), true))
                .success.value) mustBe
              authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(NormalMode, index)
          }

          "go to the DeadEnd page when no is selected" in {
            navigator.nextPage(IsAuthorisedOfficialNinoPage(index), NormalMode,
              emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), false)
                .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), false))
                .success.value) mustBe
              routes.DeadEndController.onPageLoad()
          }
        }

        s"from the AuthorisedOfficialsNINOPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsNinoPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the What is [Full name]’s home address? when clicked continue button" in {
            navigator.nextPage(AuthorisedOfficialsNinoPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C")
                .flatMap(_.set(AuthorisedOfficialsNinoPage(index), "QQ 12 34 56 C"))
                .getOrElse(emptyUserAnswers)) mustBe
              addressLookupRoutes.AuthorisedOfficialsAddressLookupController.initializeJourney(Index(index))
          }
        }

        s"from the AuthorisedOfficialAddressLookupPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialAddressLookupPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the Has [Full name]’s home address changed in the last 12 months? page when clicked continue button" in {
            navigator.nextPage(AuthorisedOfficialAddressLookupPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialAddressLookupPage(0), address)
              .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), address))
                .getOrElse(emptyUserAnswers)) mustBe
              authOfficialRoutes.AuthorisedOfficialPreviousAddressController.onPageLoad(NormalMode, index)
          }
        }

        s"from the AuthorisedOfficialPreviousAddressPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialPreviousAddressPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the Previous Address Lookup flow when yes is selected" in {
            navigator.nextPage(AuthorisedOfficialPreviousAddressPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialPreviousAddressPage(0), true)
              .flatMap(_.set(AuthorisedOfficialPreviousAddressPage(index), true))
                .success.value) mustBe
              routes.DeadEndController.onPageLoad() // TODO when next page is ready
          }

          "go to the You have added one authorised official page when no is selected" in {
            navigator.nextPage(AuthorisedOfficialPreviousAddressPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialPreviousAddressPage(0), false)
                .flatMap(_.set(AuthorisedOfficialPreviousAddressPage(index), false))
                .success.value) mustBe {index match {
              case 0 => authOfficialRoutes.AddedOneAuthorisedOfficialController.onPageLoad ()
              case 1 => routes.DeadEndController.onPageLoad() // TODO must point to summary for 2nd official when ready
            }}
          }
        }
        })
      "from the AddedOneAuthorisedOfficialPage" must {

          "go to the DoYouWantToAddAnotherAuthorisedOfficial page when user answer is empty for 1st loop" in {
            navigator.nextPage(AddedOneAuthorisedOfficialPage, NormalMode, emptyUserAnswers) mustBe
              authOfficialRoutes.IsAddAnotherAuthorisedOfficialController.onPageLoad(NormalMode)
          }
      }

      "from the IsAddAnotherAuthorisedOfficialPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAddAnotherAuthorisedOfficialPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the AuthorisedOfficialsNamePage page when yes is selected" in {
          navigator.nextPage(IsAddAnotherAuthorisedOfficialPage, NormalMode,
            emptyUserAnswers.set(IsAddAnotherAuthorisedOfficialPage, true).success.value) mustBe
            authOfficialRoutes.AuthorisedOfficialsNameController.onPageLoad(NormalMode, 1)
        }

        "go to the summary page when no is selected" in {
          navigator.nextPage(IsAddAnotherAuthorisedOfficialPage, NormalMode,
            emptyUserAnswers.set(IsAddAnotherAuthorisedOfficialPage, false).success.value) mustBe
            authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad()
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

      "from the AuthorisedOfficialsNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsNamePage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(AuthorisedOfficialsNamePage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the AuthorisedOfficialsDOBPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsDOBPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(AuthorisedOfficialsDOBPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsDOBPage(0), LocalDate.now().minusYears(minYear)).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the AuthorisedOfficialsPhoneNumberPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsPhoneNumberPage(0), authorisedOfficialsPhoneNumber).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the AuthorisedOfficialsPositionPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsPositionPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(AuthorisedOfficialsPositionPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.BoardMember).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IsAuthorisedOfficialNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAuthorisedOfficialNinoPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(IsAuthorisedOfficialNinoPage(0), CheckMode,
            emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), true).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the AuthorisedOfficialsNINOPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsNinoPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]’s home address? when clicked continue button" in {
          navigator.nextPage(AuthorisedOfficialsNinoPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C").getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the AuthorisedOfficialAddressLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialAddressLookupPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(AuthorisedOfficialAddressLookupPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialAddressLookupPage(0), address).getOrElse(emptyUserAnswers)) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the AuthorisedOfficialPreviousAddressPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialPreviousAddressPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Previous Address Lookup flow when yes is selected" in {
          navigator.nextPage(AuthorisedOfficialPreviousAddressPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialPreviousAddressPage(0),true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }

        "go to the You have added one authorised official page when no is selected" in {
          navigator.nextPage(AuthorisedOfficialPreviousAddressPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialPreviousAddressPage(0),false).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }

        //TODO Further cases depending on existing Address data/cleanup

      }

      "from the IsAddAnotherAuthorisedOfficialPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAddAnotherAuthorisedOfficialPage, CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the DeadEnd page when clicked continue button" in {
          navigator.nextPage(IsAddAnotherAuthorisedOfficialPage, CheckMode,
            emptyUserAnswers.set(IsAddAnotherAuthorisedOfficialPage, true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
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
