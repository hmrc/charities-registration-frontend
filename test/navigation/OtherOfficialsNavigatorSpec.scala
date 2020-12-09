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
import models.addressLookup.{AddressModel, CountryModel}
import models.authOfficials.OfficialsPosition
import models.{CharityName, CheckMode, Index, Name, NormalMode, Passport, PhoneNumber, PlaybackMode, SelectTitle}
import pages.IndexPage
import pages.addressLookup.{OtherOfficialAddressLookupPage, OtherOfficialPreviousAddressLookupPage}
import pages.charityInformation.CharityNamePage
import pages.otherOfficials._
import pages.sections.Section8Page
import play.api.mvc.Call

class OtherOfficialsNavigatorSpec extends SpecBase {

  private val navigator: OtherOfficialsNavigator = inject[OtherOfficialsNavigator]
  private val otherOfficialsName: Name = Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")
  private val otherOfficialsPhoneNumber: PhoneNumber = PhoneNumber("07700 900 982", Some("07700 900 982"))
  private val address: AddressModel = AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))
  private val minYear = 16

  def goToPlaybackPage(index: Int): Call = index match {
    case 0 => otherOfficialRoutes.AddedOtherOfficialController.onPageLoad(Index(0))
    case 1 => otherOfficialRoutes.AddedOtherOfficialController.onPageLoad(Index(1))
    case 2 => otherOfficialRoutes.AddedOtherOfficialController.onPageLoad(Index(2))
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  def previousOrSameIndex(index: Int): Int = index match {
    case nonZero: Int if nonZero > 0 => nonZero - 1
    case _ => 0
  }

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the OtherOfficialsNamePage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsNamePage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]'s date of birth? when save and continue button clicked" in {
          navigator.nextPage(OtherOfficialsNamePage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsNamePage(0), otherOfficialsName).success.value) mustBe
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
            emptyUserAnswers.set(OtherOfficialsDOBPage(0), LocalDate.now().minusYears(minYear)).success.value) mustBe
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
            emptyUserAnswers.set(OtherOfficialsPhoneNumberPage(0), otherOfficialsPhoneNumber).success.value) mustBe
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
            emptyUserAnswers.set(OtherOfficialsPositionPage(0), OfficialsPosition.BoardMember).success.value) mustBe
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
            emptyUserAnswers.set(IsOtherOfficialNinoPage(0), true).success.value) mustBe
            otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(NormalMode, Index(0))
        }

        "go to the OtherOfficialsPassport page when no is selected" in {
          navigator.nextPage(IsOtherOfficialNinoPage(0), NormalMode,
            emptyUserAnswers.set(IsOtherOfficialNinoPage(0),false).success.value) mustBe
            otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(NormalMode, Index(0))
        }
      }

      "from the OtherOfficialsPassportPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsPassportPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]’s home address? when clicked continue button" in {
          navigator.nextPage(OtherOfficialsPassportPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
              .flatMap(_.set(OtherOfficialsPassportPage(0), Passport("1223", "gb", LocalDate.now())))
              .success.value) mustBe
            addressLookupRoutes.OtherOfficialsAddressLookupController.initializeJourney(0, NormalMode)
        }
      }

      "from the OtherOfficialsNinoPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsNinoPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the What is [Full name]’s home address? when clicked continue button" in {
          navigator.nextPage(OtherOfficialsNinoPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C").success.value) mustBe
            addressLookupRoutes.OtherOfficialsAddressLookupController.initializeJourney(Index(0), NormalMode)
        }
      }

      "from the OtherOfficialAddressLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialAddressLookupPage(0), NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Has [Full name]’s home address changed in the last 12 months? page when clicked continue button" in {
          navigator.nextPage(OtherOfficialAddressLookupPage(0), NormalMode,
            emptyUserAnswers.set(OtherOfficialAddressLookupPage(0), address).success.value) mustBe
            otherOfficialRoutes.IsOtherOfficialsPreviousAddressController.onPageLoad(NormalMode, Index(0))
        }
      }

      List(0, 1, 2).foreach(index => {

        s"from the IsOtherOfficialsPreviousAddressPage for $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(IsOtherOfficialsPreviousAddressPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the Previous Address Lookup flow when yes is selected" in {
            navigator.nextPage(IsOtherOfficialsPreviousAddressPage(index), NormalMode,
              emptyUserAnswers.set(IsOtherOfficialsPreviousAddressPage(0), true)
                .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(previousOrSameIndex(index)), true))
                .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(index), true)).success.value) mustBe
              addressLookupRoutes.OtherOfficialsPreviousAddressLookupController.initializeJourney(index, NormalMode)
          }

          "go to the You have added one/second/third other official page when no is selected" in {
            navigator.nextPage(IsOtherOfficialsPreviousAddressPage(index), NormalMode,
              emptyUserAnswers.set(IsOtherOfficialsPreviousAddressPage(0), false)
                .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(previousOrSameIndex(index)), false))
                .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(index), false)).success.value) mustBe
              goToPlaybackPage(index)
          }
        }

        s"from the OtherOfficialPreviousAddressLookupPage for $index" must {

          "go to the SessionExpiredController page when user answer is empty" in {
            navigator.nextPage(OtherOfficialPreviousAddressLookupPage(index), NormalMode, emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }

          "go to the You have added one/second/third other official page when no is selected" in {
            navigator.nextPage(OtherOfficialPreviousAddressLookupPage(index), NormalMode,
              emptyUserAnswers.set(OtherOfficialPreviousAddressLookupPage(0), address)
                .flatMap(_.set(OtherOfficialPreviousAddressLookupPage(previousOrSameIndex(index)), address))
                .flatMap(_.set(OtherOfficialPreviousAddressLookupPage(index), address)).success.value) mustBe
              goToPlaybackPage(index)
          }
        }

      })

      "from the IsOtherOfficialsPreviousAddressPage" must {

        "go to you have added three other official when No is selected and index is 3" in {
          navigator.nextPage(IsOtherOfficialsPreviousAddressPage(3), NormalMode,
            emptyUserAnswers.set(IsOtherOfficialsPreviousAddressPage(0), true).flatMap
            (_.set(IsOtherOfficialsPreviousAddressPage(1), true)).flatMap
            (_.set(IsOtherOfficialsPreviousAddressPage(2), false)).flatMap
            (_.set(IsOtherOfficialsPreviousAddressPage(3), false)).success.value) mustBe
            goToPlaybackPage(3)
        }
      }

      "from the OtherOfficialPreviousAddressLookupPage" must {

        "go to you have added three other official when address is selected and index is 3" in {
          navigator.nextPage(OtherOfficialPreviousAddressLookupPage(3), NormalMode,
            emptyUserAnswers.set(OtherOfficialPreviousAddressLookupPage(0), address).flatMap
            (_.set(OtherOfficialPreviousAddressLookupPage(1), address)).flatMap
            (_.set(OtherOfficialPreviousAddressLookupPage(2), address)).flatMap
            (_.set(OtherOfficialPreviousAddressLookupPage(3), address)).success.value) mustBe
            goToPlaybackPage(3)
        }
      }

      "from the AddedOtherOfficialPage" must {

        "go to the AddSecondOtherOfficials page when user answer is empty" in {
          navigator.nextPage(AddedOtherOfficialPage(0), NormalMode, emptyUserAnswers) mustBe
            otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
        }
      }

      "from the AddedSecondOtherOfficialPage" must {

        "go to the DoYouWantToAddAnotherOtherOfficial page when user answer is empty" in {
          navigator.nextPage(AddedOtherOfficialPage(1), NormalMode, emptyUserAnswers) mustBe
            otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
        }
      }

      "from the AddedThirdOtherOfficialPage" must {

        "go to the Summary page when user answer is empty" in {
          navigator.nextPage(AddedOtherOfficialPage(2), NormalMode, emptyUserAnswers) mustBe
            otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
        }
      }

      "from the summary page" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the index if Yes is selected and the section is complete" in {
          navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, emptyUserAnswers
            .set(IsAddAnotherOtherOfficialPage, true)
            .flatMap(_.set(Section8Page, true)).success.value
          ) mustBe routes.IndexController.onPageLoad()
        }

        "go to the index if No is selected and the section is complete" in {
          navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, emptyUserAnswers
            .set(IsAddAnotherOtherOfficialPage, false)
            .flatMap(_.set(Section8Page, true)).success.value
          ) mustBe routes.IndexController.onPageLoad()
        }

        "go to the 2nd other official's name page if section isn't completed yet" in {
          navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, emptyUserAnswers
            .set(OtherOfficialsNamePage(0), otherOfficialsName)
            .flatMap(_.set(Section8Page, false)).success.value
          ) mustBe otherOfficialRoutes.OtherOfficialsNameController.onPageLoad(NormalMode, 1)
        }

        "go to the 3rd other official's name page if Yes is selected and the section isn't completed yet" in {
          navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, emptyUserAnswers
            .set(IsAddAnotherOtherOfficialPage, true)
            .flatMap(_.set(OtherOfficialsNamePage(0), otherOfficialsName))
            .flatMap(_.set(OtherOfficialsNamePage(1), otherOfficialsName))
            .flatMap(_.set(Section8Page, false)).success.value
          ) mustBe otherOfficialRoutes.OtherOfficialsNameController.onPageLoad(NormalMode, 2)
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

      List(0, 1, 2).foreach(index => {
        s"in Playback mode for index $index" when {

          "from the OtherOfficialsNamePage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(OtherOfficialsNamePage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(OtherOfficialsNamePage(index), CheckMode,
                emptyUserAnswers.set(OtherOfficialsNamePage(0), otherOfficialsName)
                  .flatMap(_.set(OtherOfficialsNamePage(previousOrSameIndex(index)), otherOfficialsName))
                  .flatMap(_.set(OtherOfficialsNamePage(index), otherOfficialsName)).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the OtherOfficialsDOBPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(OtherOfficialsDOBPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(OtherOfficialsDOBPage(index), CheckMode,
                emptyUserAnswers.set(OtherOfficialsDOBPage(0), LocalDate.now().minusYears(minYear))
                  .flatMap(_.set(OtherOfficialsDOBPage(previousOrSameIndex(index)), LocalDate.now().minusYears(minYear)))
                  .flatMap(_.set(OtherOfficialsDOBPage(index), LocalDate.now().minusYears(minYear))).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the OtherOfficialsPhoneNumberPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(OtherOfficialsPhoneNumberPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(OtherOfficialsPhoneNumberPage(index), CheckMode,
                emptyUserAnswers.set(OtherOfficialsPhoneNumberPage(0), otherOfficialsPhoneNumber)
                  .flatMap(_.set(OtherOfficialsPhoneNumberPage(previousOrSameIndex(index)), otherOfficialsPhoneNumber))
                  .flatMap(_.set(OtherOfficialsPhoneNumberPage(index), otherOfficialsPhoneNumber)).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the OtherOfficialsPositionPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(OtherOfficialsPositionPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the summary page when continue button is clicked" in {
              navigator.nextPage(OtherOfficialsPositionPage(index), CheckMode,
                emptyUserAnswers.set(OtherOfficialsPositionPage(0), OfficialsPosition.BoardMember)
                  .flatMap(_.set(OtherOfficialsPositionPage(previousOrSameIndex(index)), OfficialsPosition.BoardMember))
                  .flatMap(_.set(OtherOfficialsPositionPage(index), OfficialsPosition.BoardMember)).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the IsOtherOfficialNinoPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(IsOtherOfficialNinoPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the OtherOfficialsNINOPage if Yes is selected and previously the user's passport details were provided" in {
              navigator.nextPage(IsOtherOfficialNinoPage(index), CheckMode,
                emptyUserAnswers.set(IsOtherOfficialNinoPage(0), true)
                  .flatMap(_.set(IsOtherOfficialNinoPage(previousOrSameIndex(index)), true))
                  .flatMap(_.set(IsOtherOfficialNinoPage(index), true))
                  .flatMap(_.set(OtherOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(OtherOfficialsPassportPage(previousOrSameIndex(index)), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(OtherOfficialsPassportPage(index), Passport("123", "gb", LocalDate.now()))).success.value) mustBe
                  otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(CheckMode, index)
            }

            "go to the OtherOfficialsPassportPage if No is selected and previously the user's NINO details were provided" in {
              navigator.nextPage(IsOtherOfficialNinoPage(index), CheckMode,
                emptyUserAnswers.set(IsOtherOfficialNinoPage(0), false)
                  .flatMap(_.set(IsOtherOfficialNinoPage(previousOrSameIndex(index)), false))
                  .flatMap(_.set(IsOtherOfficialNinoPage(index), false))
                  .flatMap(_.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C"))
                  .flatMap(_.set(OtherOfficialsNinoPage(previousOrSameIndex(index)), "QQ 12 34 56 C"))
                  .flatMap(_.set(OtherOfficialsNinoPage(index), "QQ 12 34 56 C")).success.value) mustBe
                otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(CheckMode, index)
            }

            "go to the Playback page when Yes is selected and previously the user's NINO details were provided" in {
              navigator.nextPage(IsOtherOfficialNinoPage(index), CheckMode,
                emptyUserAnswers.set(IsOtherOfficialNinoPage(0), true)
                  .flatMap(_.set(IsOtherOfficialNinoPage(previousOrSameIndex(index)), true))
                  .flatMap(_.set(IsOtherOfficialNinoPage(index), true))
                  .flatMap(_.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C"))
                  .flatMap(_.set(OtherOfficialsNinoPage(previousOrSameIndex(index)), "QQ 12 34 56 C"))
                  .flatMap(_.set(OtherOfficialsNinoPage(index), "QQ 12 34 56 C")).success.value) mustBe
                goToPlaybackPage(index)
            }

            "go to the Playback page when No is selected and previously the user's passport details were provided" in {
              navigator.nextPage(IsOtherOfficialNinoPage(index), CheckMode,
                emptyUserAnswers.set(IsOtherOfficialNinoPage(0), false)
                  .flatMap(_.set(IsOtherOfficialNinoPage(previousOrSameIndex(index)), false))
                  .flatMap(_.set(IsOtherOfficialNinoPage(index), false))
                  .flatMap(_.set(OtherOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(OtherOfficialsPassportPage(previousOrSameIndex(index)), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(OtherOfficialsPassportPage(index), Passport("123", "gb", LocalDate.now()))).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the OtherOfficialsNINOPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(OtherOfficialsNinoPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the What is [Full name]’s home address? when clicked continue button" in {
              navigator.nextPage(OtherOfficialsNinoPage(index), CheckMode,
                emptyUserAnswers.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C")
                  .flatMap(_.set(OtherOfficialsNinoPage(previousOrSameIndex(index)), "QQ 12 34 56 C"))
                  .flatMap(_.set(OtherOfficialsNinoPage(index), "QQ 12 34 56 C")).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the OtherOfficialAddressLookupPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(OtherOfficialAddressLookupPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the Have you previously changed your address page when continue button is clicked" in {
              navigator.nextPage(OtherOfficialAddressLookupPage(index), CheckMode,
                emptyUserAnswers.set(OtherOfficialAddressLookupPage(0), address)
                  .flatMap(_.set(OtherOfficialAddressLookupPage(previousOrSameIndex(index)), address))
                  .flatMap(_.set(OtherOfficialAddressLookupPage(index), address)).success.value) mustBe
                otherOfficialRoutes.IsOtherOfficialsPreviousAddressController.onPageLoad(CheckMode, index)
            }
          }

          "from the IsOtherOfficialsPreviousAddressPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(IsOtherOfficialsPreviousAddressPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the check your answers page when yes and prev address is defined is selected" in {
              navigator.nextPage(IsOtherOfficialsPreviousAddressPage(index), CheckMode,
                emptyUserAnswers.set(IsOtherOfficialsPreviousAddressPage(0), true)
                  .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(previousOrSameIndex(index)), true))
                  .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(index), true))
                  .flatMap(_.set(OtherOfficialPreviousAddressLookupPage(0), address))
                  .flatMap(_.set(OtherOfficialPreviousAddressLookupPage(previousOrSameIndex(index)), address))
                  .flatMap(_.set(OtherOfficialPreviousAddressLookupPage(index), address)).success.value) mustBe
                goToPlaybackPage(index)
            }

            "go to the Previous Address Lookup flow when yes is selected" in {
              navigator.nextPage(IsOtherOfficialsPreviousAddressPage(index), CheckMode,
                emptyUserAnswers.set(IsOtherOfficialsPreviousAddressPage(0), true)
                  .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(previousOrSameIndex(index)), true))
                  .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(index), true)).success.value) mustBe
                addressLookupRoutes.OtherOfficialsPreviousAddressLookupController.initializeJourney(index, CheckMode)
            }

            "go to the You have added one other official page when no is selected" in {
              navigator.nextPage(IsOtherOfficialsPreviousAddressPage(index), CheckMode,
                emptyUserAnswers.set(IsOtherOfficialsPreviousAddressPage(0), false)
                  .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(previousOrSameIndex(index)), false))
                  .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(index), false)).success.value) mustBe
                goToPlaybackPage(index)
            }

          }

          "from the OtherOfficialsPassportPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(OtherOfficialsPassportPage(0), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the check your answers page when clicked continue button" in {
              navigator.nextPage(OtherOfficialsPassportPage(index), CheckMode,
                emptyUserAnswers.set(IsOtherOfficialNinoPage(0), false)
                  .flatMap(_.set(IsOtherOfficialNinoPage(previousOrSameIndex(index)), false))
                  .flatMap(_.set(IsOtherOfficialNinoPage(index), false))
                  .flatMap(_.set(OtherOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(OtherOfficialsPassportPage(previousOrSameIndex(index)), Passport("123", "gb", LocalDate.now())))
                  .flatMap(_.set(OtherOfficialsPassportPage(index), Passport("123", "gb", LocalDate.now()))).success.value) mustBe
                goToPlaybackPage(index)
            }
          }

          "from the OtherOfficialPreviousAddressLookupPage" must {

            "go to the SessionExpiredController page when user answer is empty" in {
              navigator.nextPage(OtherOfficialPreviousAddressLookupPage(index), CheckMode, emptyUserAnswers) mustBe
                routes.SessionExpiredController.onPageLoad()
            }

            "go to the You have added one other official page when no is selected" in {
              navigator.nextPage(OtherOfficialPreviousAddressLookupPage(index), CheckMode,
                emptyUserAnswers.set(OtherOfficialPreviousAddressLookupPage(0), address)
                  .flatMap(_.set(OtherOfficialPreviousAddressLookupPage(previousOrSameIndex(index)), address))
                  .flatMap(_.set(OtherOfficialPreviousAddressLookupPage(index), address)).success.value) mustBe
                goToPlaybackPage(index)
            }

          }
        }
      })

      "go to the SessionExpiredController page when continue button is clicked on any of loop page and index is invalid" in {
        navigator.nextPage(OtherOfficialsNamePage(3), CheckMode,
          emptyUserAnswers.set(OtherOfficialsNamePage(0), otherOfficialsName)
            .flatMap(_.set(OtherOfficialsNamePage(1), otherOfficialsName))
            .flatMap(_.set(OtherOfficialsNamePage(2), otherOfficialsName))
            .flatMap(_.set(OtherOfficialsNamePage(3), otherOfficialsName)).success.value) mustBe
          routes.SessionExpiredController.onPageLoad()
      }

      "from the RemoveOtherOfficialsPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(RemoveOtherOfficialsPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the summary page if No is selected and OtherOfficials are present" in {
          navigator.nextPage(RemoveOtherOfficialsPage, NormalMode, emptyUserAnswers
            .set(RemoveOtherOfficialsPage, false).flatMap(
            _.set(OtherOfficialsNamePage(0), otherOfficialsName)).success.value
          ) mustBe otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
        }

        "go to the summary page if Yes is selected and OtherOfficials are present" in {
          navigator.nextPage(RemoveOtherOfficialsPage, NormalMode, emptyUserAnswers
            .set(RemoveOtherOfficialsPage, true).flatMap(
            _.set(OtherOfficialsNamePage(0), otherOfficialsName)).success.value
          ) mustBe otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
        }

        "go to the start of journey if removed all official details and OtherOfficials are not present" in {
          navigator.nextPage(RemoveOtherOfficialsPage, NormalMode, emptyUserAnswers
            .set(RemoveOtherOfficialsPage, true).flatMap(
            _.set(CharityNamePage, CharityName("ABC", Some("OpName")))).success.value
          ) mustBe otherOfficialRoutes.CharityOtherOfficialsController.onPageLoad()
        }

        "go to the index if No is selected" in {
          navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, emptyUserAnswers
            .set(IsAddAnotherOtherOfficialPage, false).success.value
          ) mustBe routes.SessionExpiredController.onPageLoad()
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

      "from any UnKnownPage" must {

        "go to the SessionExpired page when user answer is empty" in {
          navigator.nextPage(IndexPage, PlaybackMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }
  }
}
