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
import pages.addressLookup.CharityInformationAddressLookupPage
import pages.charityInformation.{CharityContactDetailsPage, CharityNamePage}

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

        "go to the CharityInformationAddressLookupController page when clicked continue button" in {
          navigator.nextPage(CharityContactDetailsPage, NormalMode,
            emptyUserAnswers.set(CharityContactDetailsPage, CharityContactDetails("07700 900 982", None, "abc@gmail.com")).getOrElse(emptyUserAnswers)) mustBe
            controllers.addressLookup.routes.CharityInformationAddressLookupController.initializeJourney()
        }
      }

      "from the CharityInformationAddressLookupPage" must {

        "go to the SessionExpiredController page when user answer is empty" in {
          navigator.nextPage(CharityInformationAddressLookupPage, NormalMode, emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "go to the Send letters page when clicked continue button" in {
          navigator.nextPage(CharityInformationAddressLookupPage, NormalMode,
            emptyUserAnswers.set(CharityInformationAddressLookupPage,
              AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))).getOrElse(emptyUserAnswers)) mustBe
            routes.IndexController.onPageLoad() // TODO once send letter page created
        }
      }

      "from any UnKnownPage" must {

        "go to the IndexController page when user answer is empty" in {
          navigator.nextPage(IndexPage, NormalMode, emptyUserAnswers) mustBe
            routes.IndexController.onPageLoad()
        }
      }

      "in Check mode" when {

        "from any UnKnownPage" must {

          "go to the IndexController page when user answer is empty" in {
            navigator.nextPage(IndexPage, CheckMode, emptyUserAnswers) mustBe
              routes.IndexController.onPageLoad()
          }
        }
      }
    }

  }
}
