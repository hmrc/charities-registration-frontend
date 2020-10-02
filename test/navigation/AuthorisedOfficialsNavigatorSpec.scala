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
import models.{CheckMode, Index, Name, NormalMode, Passport, PhoneNumber, PlaybackMode, SelectTitle}
import pages.IndexPage
import pages.addressLookup.{AuthorisedOfficialAddressLookupPage, AuthorisedOfficialPreviousAddressLookupPage}
import pages.authorisedOfficials._
import play.api.mvc.Call

class AuthorisedOfficialsNavigatorSpec extends SpecBase {

  private val navigator: AuthorisedOfficialsNavigator = inject[AuthorisedOfficialsNavigator]

  private val authorisedOfficialsName: Name = Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")
  private val authorisedOfficialsPhoneNumber: PhoneNumber = PhoneNumber("07700 900 982", "07700 900 982")
  private val address: AddressModel = AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))
  private val minYear = 16


  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {
      List(0, 1).foreach(index => {

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
                  emptyUserAnswers
                })
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
                .success.value) mustBe
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
                .success.value) mustBe
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
                .success.value) mustBe
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

          "go to the Passport page when no is selected" in {
            navigator.nextPage(IsAuthorisedOfficialNinoPage(index), NormalMode,
              emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), false)
                .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), false))
                .success.value) mustBe
              authOfficialRoutes.AuthorisedOfficialsPassportController.onPageLoad(NormalMode,index)
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
                .success.value) mustBe
              addressLookupRoutes.AuthorisedOfficialsAddressLookupController.initializeJourney(Index(index), NormalMode)
          }
        }

        s"from the AuthorisedOfficialsPassportPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsPassportPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the AddressLookup page when clicked continue button" in {
            navigator.nextPage(AuthorisedOfficialsPassportPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
                .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("1223", "gb", LocalDate.now())))
                .success.value) mustBe
              addressLookupRoutes.AuthorisedOfficialsAddressLookupController.initializeJourney(index, NormalMode)
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
                .success.value) mustBe
              authOfficialRoutes.IsAuthorisedOfficialPreviousAddressController.onPageLoad(NormalMode, index)
          }
        }

        s"from the IsAuthorisedOfficialPreviousAddressPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the Previous Address Lookup flow when yes is selected" in {
            navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(index), NormalMode,
              emptyUserAnswers.set(IsAuthorisedOfficialPreviousAddressPage(0), true)
                .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), true))
                .success.value) mustBe
              addressLookupRoutes.AuthorisedOfficialsPreviousAddressLookupController.initializeJourney(index, NormalMode)
          }

          "go to the You have added one authorised official page when no is selected" in {
            navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(index), NormalMode,
              emptyUserAnswers.set(IsAuthorisedOfficialPreviousAddressPage(0), false)
                .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), false))
                .success.value) mustBe {
              index match {
                case 0 => authOfficialRoutes.AddedOneAuthorisedOfficialController.onPageLoad()
                case 1 => authOfficialRoutes.AddedSecondAuthorisedOfficialController.onPageLoad()
              }
            }
          }
        }

        s"from the AuthorisedOfficialPreviousAddressLookupPage for index $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialPreviousAddressLookupPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the authorised official playback page when no is selected" in {
            navigator.nextPage(AuthorisedOfficialPreviousAddressLookupPage(index), NormalMode,
              emptyUserAnswers.set(AuthorisedOfficialPreviousAddressLookupPage(0), address)
                .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(index), address))
                .success.value) mustBe {
              index match {
                case 0 => authOfficialRoutes.AddedOneAuthorisedOfficialController.onPageLoad()
                case 1 => authOfficialRoutes.AddedSecondAuthorisedOfficialController.onPageLoad()
              }
            }
          }
        }
      })

      s"from the IsAuthorisedOfficialPreviousAddressPage for index 3" must {
        "go to the You have added one authorised official page when no is selected" in {
          navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(2), NormalMode,
            emptyUserAnswers.set(IsAuthorisedOfficialPreviousAddressPage(0), false)
              .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(1), false))
              .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(2), false))
              .success.value) mustBe routes.SessionExpiredController.onPageLoad()
        }
      }

      "from the AddedOneAuthorisedOfficialPage" must {

        "go to the DoYouWantToAddAnotherAuthorisedOfficial page when user answer is empty for 1st loop" in {
          navigator.nextPage(AddedOneAuthorisedOfficialPage, NormalMode, emptyUserAnswers) mustBe
            authOfficialRoutes.IsAddAnotherAuthorisedOfficialController.onPageLoad(NormalMode)
        }
      }

      "from the AddedSecondAuthorisedOfficialPage" must {

        "go to the summary page when user answer is empty for 2nd loop" in {
          navigator.nextPage(AddedSecondAuthorisedOfficialPage, NormalMode, emptyUserAnswers) mustBe
            authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad()
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
            emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName).success.value) mustBe
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
            emptyUserAnswers.set(AuthorisedOfficialsDOBPage(0), LocalDate.now().minusYears(minYear)).success.value) mustBe
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
            emptyUserAnswers.set(AuthorisedOfficialsPhoneNumberPage(0), authorisedOfficialsPhoneNumber).success.value) mustBe
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
            emptyUserAnswers.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.BoardMember).success.value) mustBe
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
            emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), true).success.value) mustBe
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
            emptyUserAnswers.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C").success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      s"from the AuthorisedOfficialsPassportPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialsPassportPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the dead end page when clicked continue button" in {
          navigator.nextPage(AuthorisedOfficialsPassportPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
              .success.value) mustBe
            routes.DeadEndController.onPageLoad()
        }
      }

      "from the AuthorisedOfficialAddressLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(AuthorisedOfficialAddressLookupPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page when continue button is clicked" in {
          navigator.nextPage(AuthorisedOfficialAddressLookupPage(0), CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialAddressLookupPage(0), address).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }
      }

      "from the IsAuthorisedOfficialPreviousAddressPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(0), CheckMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Previous Address Lookup flow when yes is selected" in {
          navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(0), CheckMode,
            emptyUserAnswers.set(IsAuthorisedOfficialPreviousAddressPage(0), true).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }

        "go to the You have added one authorised official page when no is selected" in {
          navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(0), CheckMode,
            emptyUserAnswers.set(IsAuthorisedOfficialPreviousAddressPage(0), false).success.value) mustBe
            routes.DeadEndController.onPageLoad() // TODO when next page is ready
        }

        "from the AuthorisedOfficialPreviousAddressLookupPage" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialPreviousAddressLookupPage(0), CheckMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the summary page when continue button is clicked" in {
            navigator.nextPage(AuthorisedOfficialPreviousAddressLookupPage(0), CheckMode,
              emptyUserAnswers.set(AuthorisedOfficialPreviousAddressLookupPage(0), address).success.value) mustBe
              routes.DeadEndController.onPageLoad() // TODO when next page is ready
          }
        }

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

    "in Playback mode" when {

      def goToPlaybackPage(index: Int): Call = index match {
        case 0 => authOfficialRoutes.AddedOneAuthorisedOfficialController.onPageLoad()
        case 1 => authOfficialRoutes.AddedSecondAuthorisedOfficialController.onPageLoad()
        case _ => routes.SessionExpiredController.onPageLoad()
      }


      List(0,1).foreach(index => {
        s"in Playback mode for index $index" when {
          "from the AuthorisedOfficialsNamePage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsNamePage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(AuthorisedOfficialsNamePage(index), PlaybackMode,
                emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName)
                  .flatMap(_.set(AuthorisedOfficialsNamePage(index), authorisedOfficialsName)).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialsDOBPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsDOBPage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(AuthorisedOfficialsDOBPage(index), PlaybackMode,
                emptyUserAnswers.set(AuthorisedOfficialsDOBPage(0), LocalDate.now().minusYears(minYear))
                  .flatMap(_.set(AuthorisedOfficialsDOBPage(index), LocalDate.now().minusYears(minYear))).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialsPhoneNumberPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(index), PlaybackMode,
                emptyUserAnswers.set(AuthorisedOfficialsPhoneNumberPage(0), authorisedOfficialsPhoneNumber)
                  .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(index), authorisedOfficialsPhoneNumber)).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialsPositionPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsPositionPage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(AuthorisedOfficialsPositionPage(index), PlaybackMode,
                emptyUserAnswers.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.BoardMember)
                  .flatMap(_.set(AuthorisedOfficialsPositionPage(index), OfficialsPosition.BoardMember)).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the IsAuthorisedOfficialNinoPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(IsAuthorisedOfficialNinoPage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the AuthorisedOfficialsNINOPage if Yes is selected and previously the user's passport details were provided" in {
              navigator.nextPage(IsAuthorisedOfficialNinoPage(index), PlaybackMode,
                emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), true)
                  .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), true))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("123", "gb", LocalDate.now()))).success.value) mustBe
                authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(PlaybackMode, index)
            }

            "go to the AuthorisedOfficialsPassportPage if No is selected and previously the user's NINO details were provided" in {
              navigator.nextPage(IsAuthorisedOfficialNinoPage(index), PlaybackMode,
                emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), false)
                  .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), false))
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C"))
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(index), "QQ 12 34 56 C")).success.value) mustBe
                authOfficialRoutes.AuthorisedOfficialsPassportController.onPageLoad(PlaybackMode, index)
            }

            "go to the Playback page when Yes is selected and previously the user's NINO details were provided" in {
              navigator.nextPage(IsAuthorisedOfficialNinoPage(index), PlaybackMode,
                emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), true)
                  .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), true))
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C"))
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(index), "QQ 12 34 56 C")).success.value) mustBe
                goToPlaybackPage(index)
            }

            "go to the Playback page when No is selected and previously the user's passport details were provided" in {
              navigator.nextPage(IsAuthorisedOfficialNinoPage(index), PlaybackMode,
                emptyUserAnswers.set(IsAuthorisedOfficialNinoPage(0), false)
                  .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), false))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("123", "gb", LocalDate.now()))).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialsNINOPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsNinoPage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the What is [Full name]’s home address? when clicked continue button" in {
              navigator.nextPage(AuthorisedOfficialsNinoPage(index), PlaybackMode,
                emptyUserAnswers.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C")
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(index), "QQ 12 34 56 C")).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          s"from the AuthorisedOfficialsPassportPage for index $index" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsPassportPage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the Playback page when new passport is provided" in {
              navigator.nextPage(AuthorisedOfficialsPassportPage(index), PlaybackMode,
                emptyUserAnswers.set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("1223", "gb", LocalDate.now())))
                  .success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialAddressLookupPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialAddressLookupPage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the Have you previously changed your address page when continue button is clicked" in {
              navigator.nextPage(AuthorisedOfficialAddressLookupPage(index), PlaybackMode,
                emptyUserAnswers.set(AuthorisedOfficialAddressLookupPage(0), address)
                  .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), address)).success.value) mustBe
                authOfficialRoutes.IsAuthorisedOfficialPreviousAddressController.onPageLoad(PlaybackMode, index)
            }
          }

          "from the IsAuthorisedOfficialPreviousAddressPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(index), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the Previous Address Lookup flow when yes is selected" in {
              navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(index), PlaybackMode,
                emptyUserAnswers.set(IsAuthorisedOfficialPreviousAddressPage(0), true)
                  .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), true)).success.value) mustBe
                addressLookupRoutes.AuthorisedOfficialsPreviousAddressLookupController.initializeJourney(index, PlaybackMode)
            }

            "go to the You have added one authorised official page when no is selected" in {
              navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(index), PlaybackMode,
                emptyUserAnswers.set(IsAuthorisedOfficialPreviousAddressPage(0), false)
                  .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), false)).success.value) mustBe
                goToPlaybackPage(index)
            }
          }


          "from the AuthorisedOfficialPreviousAddressLookupPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialPreviousAddressLookupPage(0), PlaybackMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(AuthorisedOfficialPreviousAddressLookupPage(index), PlaybackMode,
                emptyUserAnswers.set(AuthorisedOfficialPreviousAddressLookupPage(0), address)
                  .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(index), address)).success.value) mustBe
                goToPlaybackPage(index)
            }
          }
        }
      })

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, PlaybackMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }

    }
  }
}
