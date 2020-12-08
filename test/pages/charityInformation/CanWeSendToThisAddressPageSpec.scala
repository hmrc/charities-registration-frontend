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

package pages.charityInformation

import models.UserAnswers
import models.addressLookup.{AddressModel, CountryModel}
import pages.addressLookup.CharityPostalAddressLookupPage
import pages.behaviours.PageBehaviours
import play.api.libs.json.Json

class CanWeSendToThisAddressPageSpec extends PageBehaviours {

  "CanWeSendToThisAddressPage" must {

    beRetrievable[Boolean](CanWeSendToThisAddressPage)

    beSettable[Boolean](CanWeSendToThisAddressPage)

    beRemovable[Boolean](CanWeSendToThisAddressPage)

    "cleanup" when {

      val userAnswer = UserAnswers("id", Json.obj()).set(CharityPostalAddressLookupPage,
        AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))).success.value


      "setting CanWeSendLettersToThisAddress to true" must {

        val result = userAnswer.set(CanWeSendToThisAddressPage, true).success.value

        "remove CharityPostalAddressLookupPage" in {

          result.get(CharityPostalAddressLookupPage) mustNot be(defined)
        }
      }

      "setting CanWeSendLettersToThisAddress to false" must {

        val result = userAnswer.set(CanWeSendToThisAddressPage, false).success.value

        "not remove CharityPostalAddressLookupPage" in {

          result.get(CharityPostalAddressLookupPage) must be(defined)
        }
      }
    }
  }
}
