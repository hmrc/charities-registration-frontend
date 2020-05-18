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
import controllers.summary.{routes => summaryRoutes}
import models._
import pages.charityInformation.{CharityContactDetailsPage, CharityNamePage, CharityUKAddressPage, IsCharityOfficialAddressInUKPage}
import pages.{IndexPage, QuestionPage}

class CharityInformationNavigatorSpec extends SpecBase {

  val navigator = new CharityInformationNavigator()

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

        "go to the CheckCharityDetailsController page when clicked continue button" in {
          navigator.nextPage(CharityContactDetailsPage, NormalMode,
            emptyUserAnswers.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", None, "abc@gmail.com")).getOrElse(emptyUserAnswers)) mustBe
            charityInfoRoutes.IsCharityOfficialAddressInUKController.onPageLoad(NormalMode)
        }
      }

      "from the IsCharityOfficialAddressInUKPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(IsCharityOfficialAddressInUKPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the IndexController page when yes is selected" in {
          navigator.nextPage(IsCharityOfficialAddressInUKPage, NormalMode, userAnsewers(IsCharityOfficialAddressInUKPage, true)) mustBe
            charityInfoRoutes.CharityUKAddressController.onPageLoad(NormalMode)
        }

        "go to the IndexController page when No is selected" in {
          navigator.nextPage(IsCharityOfficialAddressInUKPage, NormalMode, userAnsewers(IsCharityOfficialAddressInUKPage, false)) mustBe
            charityInfoRoutes.CharityUKAddressController.onPageLoad(NormalMode)
        }
      }

      "from the CharityUKAddressPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityUKAddressPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the CharityUKAddressController page when clicked continue button" in {
          navigator.nextPage(CharityUKAddressPage, NormalMode,
            emptyUserAnswers.set(CharityUKAddressPage, CharityUKAddress("123, Morrison St", None, None, "Edinburgh", "EH12 5WU")).getOrElse(emptyUserAnswers)) mustBe
            summaryRoutes.CheckCharityDetailsController.onPageLoad()
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

      "go to the CheckCharityDetailsController page when clicked continue button" in {
        navigator.nextPage(CharityNamePage, CheckMode,
          emptyUserAnswers.set(CharityNamePage, CharityName("CName", Some("OpName"))).getOrElse(emptyUserAnswers)) mustBe
          summaryRoutes.CheckCharityDetailsController.onPageLoad()
      }
    }

    "from the CharityContactDetailsPage" must {

      "go to the SessionExpiredController page when user answer is empty" in {
        navigator.nextPage(CharityContactDetailsPage, CheckMode, emptyUserAnswers) mustBe
          routes.SessionExpiredController.onPageLoad()
      }

      "go to the CheckCharityDetailsController page when clicked continue button" in {
        navigator.nextPage(CharityContactDetailsPage, CheckMode,
          emptyUserAnswers.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", None, "abc@gmail.com")).getOrElse(emptyUserAnswers)) mustBe
          summaryRoutes.CheckCharityDetailsController.onPageLoad()
      }
    }
  }


    def userAnsewers(page: QuestionPage[Boolean], value: Boolean): UserAnswers = {
      emptyUserAnswers.set(page, value).getOrElse(emptyUserAnswers)
    }
  }
}
