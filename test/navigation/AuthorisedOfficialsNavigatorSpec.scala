/*
 * Copyright 2025 HM Revenue & Customs
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
import base.data.constants.AddressModelConstants._
import controllers.addressLookup.{routes => addressLookupRoutes}
import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import controllers.routes
import models.authOfficials.OfficialsPosition
import models.{CharityName, CheckMode, Index, Name, NormalMode, Passport, PhoneNumber, PlaybackMode, SelectTitle}
import pages.IndexPage
import pages.addressLookup.{AuthorisedOfficialAddressLookupPage, AuthorisedOfficialPreviousAddressLookupPage}
import pages.authorisedOfficials._
import pages.contactDetails.CharityNamePage
import pages.sections.Section7Page
import play.api.mvc.Call

import java.time.LocalDate

class AuthorisedOfficialsNavigatorSpec extends SpecBase {

  private val navigator: AuthorisedOfficialsNavigator = inject[AuthorisedOfficialsNavigator]

  private val minYear                                     = 16
  private val authorisedOfficialsName: Name               =
    Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")
  private val authorisedOfficialsPhoneNumber: PhoneNumber = PhoneNumber("07700 900 982", Some("07700 900 982"))

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      List(0, 1).foreach { index =>
        s"from the AuthorisedOfficialsNamePage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsNamePage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the What is [Full name]'s date of birth? when clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialsNamePage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName)
                .flatMap(_.set(AuthorisedOfficialsNamePage(index), authorisedOfficialsName))
                .getOrElse {
                  emptyUserAnswers
                }
            ) mustBe
              authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(NormalMode, index)
          }
        }

        s"from the AuthorisedOfficialsDOBPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsDOBPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the AuthorisedOfficialsPhoneNumberPage when clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialsDOBPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialsDOBPage(0), LocalDate.now().minusYears(minYear))
                .flatMap(_.set(AuthorisedOfficialsDOBPage(index), LocalDate.now().minusYears(minYear)))
                .success
                .value
            ) mustBe
              authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(NormalMode, index)
          }
        }

        s"from the AuthorisedOfficialsPhoneNumberPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the AuthorisedOfficialsPositionPage when clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialsPhoneNumberPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialsPhoneNumberPage(0), authorisedOfficialsPhoneNumber)
                .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(index), authorisedOfficialsPhoneNumber))
                .success
                .value
            ) mustBe authOfficialRoutes.AuthorisedOfficialsPositionController.onPageLoad(NormalMode, index)
          }
        }

        s"from the AuthorisedOfficialsPositionPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsPositionPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to Does [Full name] have a National Insurance number? when clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialsPositionPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.BoardMember)
                .flatMap(_.set(AuthorisedOfficialsPositionPage(index), OfficialsPosition.BoardMember))
                .success
                .value
            ) mustBe
              authOfficialRoutes.IsAuthorisedOfficialNinoController.onPageLoad(NormalMode, Index(index))
          }
        }

        s"from the IsAuthorisedOfficialNinoPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(IsAuthorisedOfficialNinoPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the What is [full name]’s National Insurance number? when yes is selected" in {
            navigator.nextPage(
              IsAuthorisedOfficialNinoPage(index),
              NormalMode,
              emptyUserAnswers
                .set(IsAuthorisedOfficialNinoPage(0), true)
                .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), true))
                .success
                .value
            ) mustBe
              authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(NormalMode, index)
          }

          "go to the Passport page when No is selected" in {
            navigator.nextPage(
              IsAuthorisedOfficialNinoPage(index),
              NormalMode,
              emptyUserAnswers
                .set(IsAuthorisedOfficialNinoPage(0), false)
                .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), false))
                .success
                .value
            ) mustBe
              authOfficialRoutes.AuthorisedOfficialsPassportController.onPageLoad(NormalMode, index)
          }
        }

        s"from the AuthorisedOfficialsNINOPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsNinoPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the What is [Full name]’s home address? when clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialsNinoPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces)
                .flatMap(_.set(AuthorisedOfficialsNinoPage(index), nino2WithSpaces))
                .success
                .value
            ) mustBe
              addressLookupRoutes.AuthorisedOfficialsAddressLookupController.initializeJourney(Index(index), NormalMode)
          }

          "go to the ConfirmAuthorisedOfficialsAddressController page when AuthorisedOfficialAddressLookupPage is present and clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialsNinoPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces)
                .flatMap(_.set(AuthorisedOfficialsNinoPage(index), nino2WithSpaces))
                .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0), address))
                .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), address))
                .success
                .value
            ) mustBe
              controllers.authorisedOfficials.routes.ConfirmAuthorisedOfficialsAddressController
                .onPageLoad(Index(index))
          }
        }

        s"from the AuthorisedOfficialsPassportPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialsPassportPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the AddressLookup page when clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialsPassportPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
                .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("1223", "gb", LocalDate.now())))
                .success
                .value
            ) mustBe
              addressLookupRoutes.AuthorisedOfficialsAddressLookupController.initializeJourney(index, NormalMode)
          }

          "go to the ConfirmAuthorisedOfficialsAddressController page when AuthorisedOfficialAddressLookupPage is present and clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialsPassportPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
                .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("123", "gb", LocalDate.now())))
                .flatMap(_.set(AuthorisedOfficialAddressLookupPage(0), address))
                .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), address))
                .success
                .value
            ) mustBe
              controllers.authorisedOfficials.routes.ConfirmAuthorisedOfficialsAddressController
                .onPageLoad(Index(index))
          }
        }

        s"from the AuthorisedOfficialAddressLookupPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialAddressLookupPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the Has [Full name]’s home address changed in the last 12 months? page when clicked continue button" in {
            navigator.nextPage(
              AuthorisedOfficialAddressLookupPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialAddressLookupPage(0), address)
                .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), address))
                .success
                .value
            ) mustBe
              authOfficialRoutes.IsAuthorisedOfficialPreviousAddressController.onPageLoad(NormalMode, index)
          }

          "go to the Amend address page if one or more address lines length >35 characters when clicked Confirm and continue button" in {
            navigator.nextPage(
              AuthorisedOfficialAddressLookupPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialAddressLookupPage(0), addressModelMax)
                .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), addressModelMax))
                .success
                .value
            ) mustBe
              authOfficialRoutes.AmendAuthorisedOfficialsAddressController.onPageLoad(NormalMode, index)
          }

          "go to the Amend address page if address lines < 2  when clicked Confirm and continue button" in {
            navigator.nextPage(
              AuthorisedOfficialAddressLookupPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialAddressLookupPage(0), addressModelMin)
                .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), addressModelMin))
                .success
                .value
            ) mustBe
              authOfficialRoutes.AmendAuthorisedOfficialsAddressController.onPageLoad(NormalMode, index)
          }

        }

        s"from the IsAuthorisedOfficialPreviousAddressPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the Previous Address Lookup flow when yes is selected" in {
            navigator.nextPage(
              IsAuthorisedOfficialPreviousAddressPage(index),
              NormalMode,
              emptyUserAnswers
                .set(IsAuthorisedOfficialPreviousAddressPage(0), true)
                .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), true))
                .success
                .value
            ) mustBe
              addressLookupRoutes.AuthorisedOfficialsPreviousAddressLookupController
                .initializeJourney(index, NormalMode)
          }

          "go to the ConfirmAuthorisedOfficialsPreviousAddressController page when yes is selected and " +
            "AuthorisedOfficialPreviousAddressLookupPage is present and clicked continue button" in {
              navigator.nextPage(
                IsAuthorisedOfficialPreviousAddressPage(index),
                NormalMode,
                emptyUserAnswers
                  .set(IsAuthorisedOfficialPreviousAddressPage(0), true)
                  .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), true))
                  .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), address))
                  .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(index), address))
                  .success
                  .value
              ) mustBe
                controllers.authorisedOfficials.routes.ConfirmAuthorisedOfficialsPreviousAddressController
                  .onPageLoad(index)
            }

          "go to the You have added one authorised official page when no is selected" in {
            navigator.nextPage(
              IsAuthorisedOfficialPreviousAddressPage(index),
              NormalMode,
              emptyUserAnswers
                .set(IsAuthorisedOfficialPreviousAddressPage(0), false)
                .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), false))
                .success
                .value
            ) mustBe {
              index match {
                case 0 => authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(0))
                case 1 => authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(1))
              }
            }
          }
        }

        s"from the AuthorisedOfficialPreviousAddressLookupPage for index $index" must {

          "go to the PageNotFoundController page when user answer is empty" in {
            navigator.nextPage(AuthorisedOfficialPreviousAddressLookupPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.PageNotFoundController.onPageLoad()
          }

          "go to the authorised official playback page when no is selected" in {
            navigator.nextPage(
              AuthorisedOfficialPreviousAddressLookupPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialPreviousAddressLookupPage(0), address)
                .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(index), address))
                .success
                .value
            ) mustBe {
              index match {
                case 0 => authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(0))
                case 1 => authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(1))
              }
            }
          }

          "go to the Amend address page if one or more address lines length >35 characters when clicked Confirm and continue button" in {
            navigator.nextPage(
              AuthorisedOfficialPreviousAddressLookupPage(index),
              NormalMode,
              emptyUserAnswers
                .set(AuthorisedOfficialPreviousAddressLookupPage(0), addressModelMax)
                .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(index), addressModelMax))
                .success
                .value
            ) mustBe
              authOfficialRoutes.AmendAuthorisedOfficialsPreviousAddressController.onPageLoad(NormalMode, index)
          }
        }
      }

      "from the IsAuthorisedOfficialPreviousAddressPage for index 3" must {

        "go to the PageNotFound page when no is selected" in {

          navigator.nextPage(
            IsAuthorisedOfficialPreviousAddressPage(2),
            NormalMode,
            emptyUserAnswers
              .set(IsAuthorisedOfficialPreviousAddressPage(0), false)
              .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(1), false))
              .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(2), false))
              .success
              .value
          ) mustBe routes.PageNotFoundController.onPageLoad()
        }
      }

      "from the AddedAuthorisedOfficialPage" must {

        "go to the summary page when user answer is empty for 1st loop" in {
          navigator.nextPage(AddedAuthorisedOfficialPage(Index(0)), NormalMode, emptyUserAnswers) mustBe
            authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad
        }
      }

      "from the AddedSecondAuthorisedOfficialPage" must {

        "go to the summary page when user answer is empty for 2nd loop" in {
          navigator.nextPage(AddedAuthorisedOfficialPage(Index(1)), NormalMode, emptyUserAnswers) mustBe
            authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad
        }
      }

      "from the AuthorisedOfficialsSummaryPage" must {

        val nextPage = navigator.nextPage(AuthorisedOfficialsSummaryPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPage(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the index if Yes is selected and the section is complete" in {
          nextPage(
            emptyUserAnswers
              .set(IsAddAnotherAuthorisedOfficialPage, true)
              .flatMap(_.set(Section7Page, true))
              .success
              .value
          ) mustBe routes.IndexController.onPageLoad(None)
        }

        "go to the index if No is selected" in {

          nextPage(
            emptyUserAnswers
              .set(IsAddAnotherAuthorisedOfficialPage, false)
              .success
              .value
          ) mustBe routes.IndexController.onPageLoad(None)
        }

        "go to the 2nd authorised official's name page if Yes is selected and the section isn't completed yet" in {
          nextPage(
            emptyUserAnswers
              .set(IsAddAnotherAuthorisedOfficialPage, true)
              .flatMap(_.set(Section7Page, false))
              .success
              .value
          ) mustBe
            authOfficialRoutes.AuthorisedOfficialsNameController.onPageLoad(NormalMode, 1)
        }
      }

      "from the RemoveAuthorisedOfficialsPage" must {

        val nextPage = navigator.nextPage(RemoveAuthorisedOfficialsPage, NormalMode, _)

        "go to the PageNotFoundController page when user answer is empty" in {
          nextPage(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
        }

        "go to the AuthorisedOfficialsSummary if No is selected and AuthorisedOfficials are present" in {

          nextPage(
            emptyUserAnswers
              .set(RemoveAuthorisedOfficialsPage, false)
              .flatMap(_.set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName))
              .success
              .value
          ) mustBe authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad
        }

        "go to the AuthorisedOfficialsSummaryPage if Yes is selected and AuthorisedOfficials are present" in {
          nextPage(
            emptyUserAnswers
              .set(RemoveAuthorisedOfficialsPage, true)
              .flatMap(_.set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName))
              .success
              .value
          ) mustBe authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad
        }

        "go to the start of journey if removed all official details and AuthorisedOfficials are not present" in {
          nextPage(
            emptyUserAnswers
              .set(RemoveAuthorisedOfficialsPage, true)
              .flatMap(_.set(CharityNamePage, CharityName("ABC", Some("OpName"))))
              .success
              .value
          ) mustBe authOfficialRoutes.CharityAuthorisedOfficialsController.onPageLoad()
        }
      }

      "from any Unknown page" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe routes.IndexController.onPageLoad(None)
        }
      }
    }

    "in Check mode" when {

      def goToPlaybackPage(index: Int): Call =
        index match {
          case 0 => authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(0))
          case 1 => authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(1))
          case _ => routes.PageNotFoundController.onPageLoad()
        }

      List(0, 1).foreach(index => {
        s"in Playback mode for index $index" when {

          "from the AuthorisedOfficialsNamePage" must {

            "go to the PageNotFoundController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsNamePage(index), CheckMode, emptyUserAnswers) mustBe
                routes.PageNotFoundController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(
                AuthorisedOfficialsNamePage(index),
                CheckMode,
                emptyUserAnswers
                  .set(AuthorisedOfficialsNamePage(0), authorisedOfficialsName)
                  .flatMap(_.set(AuthorisedOfficialsNamePage(index), authorisedOfficialsName))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialsDOBPage" must {

            "go to the PageNotFoundController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsDOBPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.PageNotFoundController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(
                AuthorisedOfficialsDOBPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(AuthorisedOfficialsDOBPage(0), LocalDate.now().minusYears(minYear))
                  .flatMap(_.set(AuthorisedOfficialsDOBPage(index), LocalDate.now().minusYears(minYear)))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialsPhoneNumberPage" must {

            "go to the PageNotFoundController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.PageNotFoundController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(
                AuthorisedOfficialsPhoneNumberPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(AuthorisedOfficialsPhoneNumberPage(0), authorisedOfficialsPhoneNumber)
                  .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(index), authorisedOfficialsPhoneNumber))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialsPositionPage" must {

            "go to the PageNotFoundController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsPositionPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.PageNotFoundController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(
                AuthorisedOfficialsPositionPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.BoardMember)
                  .flatMap(_.set(AuthorisedOfficialsPositionPage(index), OfficialsPosition.BoardMember))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the IsAuthorisedOfficialNinoPage" must {

            "go to the PageNotFoundController page when user answer is empty" in {
              navigator.nextPage(IsAuthorisedOfficialNinoPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.PageNotFoundController.onPageLoad()
            }

            "go to the AuthorisedOfficialsNINOPage if Yes is selected and previously the user's passport details were provided" in {
              navigator.nextPage(
                IsAuthorisedOfficialNinoPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(IsAuthorisedOfficialNinoPage(0), true)
                  .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), true))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("123", "gb", LocalDate.now())))
                  .success
                  .value
              ) mustBe
                authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(CheckMode, index)
            }

            "go to the AuthorisedOfficialsPassportPage if No is selected and previously the user's NINO details were provided" in {
              navigator.nextPage(
                IsAuthorisedOfficialNinoPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(IsAuthorisedOfficialNinoPage(0), false)
                  .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), false))
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces))
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(index), ninoWithSpaces))
                  .success
                  .value
              ) mustBe
                authOfficialRoutes.AuthorisedOfficialsPassportController.onPageLoad(CheckMode, index)
            }

            "go to the Playback page when Yes is selected and previously the user's NINO details were provided" in {
              navigator.nextPage(
                IsAuthorisedOfficialNinoPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(IsAuthorisedOfficialNinoPage(0), true)
                  .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), true))
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces))
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(index), ninoWithSpaces))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }

            "go to the Playback page when No is selected and previously the user's passport details were provided" in {
              navigator.nextPage(
                IsAuthorisedOfficialNinoPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(IsAuthorisedOfficialNinoPage(0), false)
                  .flatMap(_.set(IsAuthorisedOfficialNinoPage(index), false))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("123", "gb", LocalDate.now())))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialsNINOPage" must {

            "go to the PageNotFoundController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsNinoPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.PageNotFoundController.onPageLoad()
            }

            "go to the What is [Full name]’s home address? when clicked continue button" in {
              navigator.nextPage(
                AuthorisedOfficialsNinoPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces)
                  .flatMap(_.set(AuthorisedOfficialsNinoPage(index), ninoWithSpaces))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }
          }

          s"from the AuthorisedOfficialsPassportPage for index $index" must {

            "go to the PageNotFoundController page when user answer is empty" in {
              navigator.nextPage(AuthorisedOfficialsPassportPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.PageNotFoundController.onPageLoad()
            }

            "go to the Playback page when new passport is provided" in {
              navigator.nextPage(
                AuthorisedOfficialsPassportPage(index),
                CheckMode,
                emptyUserAnswers
                  .set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
                  .flatMap(_.set(AuthorisedOfficialsPassportPage(index), Passport("1223", "gb", LocalDate.now())))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialAddressLookupPage" must {

            val nextPageCheckModeIndex = navigator.nextPage(AuthorisedOfficialAddressLookupPage(index), CheckMode, _)

            "go to the PageNotFoundController page when user answer is empty" in {
              nextPageCheckModeIndex(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
            }

            "go to the Have you previously changed your address page when continue button is clicked" in {
              nextPageCheckModeIndex(
                emptyUserAnswers
                  .set(AuthorisedOfficialAddressLookupPage(0), address)
                  .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), address))
                  .success
                  .value
              ) mustBe
                authOfficialRoutes.IsAuthorisedOfficialPreviousAddressController.onPageLoad(CheckMode, index)
            }

            "go to the Amend address page if one or more address lines length >35 characters when clicked Confirm and continue button" in {

              nextPageCheckModeIndex(
                emptyUserAnswers
                  .set(AuthorisedOfficialAddressLookupPage(0), addressModelMax)
                  .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), addressModelMax))
                  .success
                  .value
              ) mustBe
                authOfficialRoutes.AmendAuthorisedOfficialsAddressController.onPageLoad(CheckMode, index)
            }
          }

          "from the IsAuthorisedOfficialPreviousAddressPage" must {

            val nextPageCheckModeIndex =
              navigator.nextPage(IsAuthorisedOfficialPreviousAddressPage(index), CheckMode, _)

            "go to the PageNotFoundController page when user answer is empty" in {
              nextPageCheckModeIndex(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
            }

            "go to the Previous Address Lookup flow when yes is selected" in {
              nextPageCheckModeIndex(
                emptyUserAnswers
                  .set(IsAuthorisedOfficialPreviousAddressPage(0), true)
                  .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), true))
                  .success
                  .value
              ) mustBe
                addressLookupRoutes.AuthorisedOfficialsPreviousAddressLookupController
                  .initializeJourney(index, CheckMode)
            }

            "go to the You have added one authorised official page when no is selected" in {
              nextPageCheckModeIndex(
                emptyUserAnswers
                  .set(IsAuthorisedOfficialPreviousAddressPage(0), false)
                  .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), false))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }

            "go to the You have added one authorised official page when Yes is selected and home address is defined" in {
              nextPageCheckModeIndex(
                emptyUserAnswers
                  .set(IsAuthorisedOfficialPreviousAddressPage(0), false)
                  .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(index), true))
                  .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(index), address))
                  .success
                  .value
              ) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the AuthorisedOfficialPreviousAddressLookupPage" must {

            val nextPageCheckModeIndex =
              navigator.nextPage(AuthorisedOfficialPreviousAddressLookupPage(index), CheckMode, _)

            "go to the PageNotFoundController page when user answer is empty" in {
              nextPageCheckModeIndex(emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              nextPageCheckModeIndex(
                emptyUserAnswers
                  .set(AuthorisedOfficialPreviousAddressLookupPage(0), address)
                  .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(index), address))
                  .success
                  .value
              ) mustBe goToPlaybackPage(index)
            }

            "go to the Amend address page if one or more address lines length >35 characters when clicked Confirm and continue button" in {
              nextPageCheckModeIndex(
                emptyUserAnswers
                  .set(AuthorisedOfficialPreviousAddressLookupPage(0), addressModelMax)
                  .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(index), addressModelMax))
                  .success
                  .value
              ) mustBe
                authOfficialRoutes.AmendAuthorisedOfficialsPreviousAddressController.onPageLoad(CheckMode, index)
            }
          }
        }
      })

      "from any Unknown page" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe routes.IndexController.onPageLoad(None)
        }
      }

    }

    "in Playback mode" when {

      "any page is called should go to SessionExpired page" in {
        navigator.nextPage(IndexPage, PlaybackMode, emptyUserAnswers) mustBe routes.PageNotFoundController.onPageLoad()
      }
    }
  }
}
