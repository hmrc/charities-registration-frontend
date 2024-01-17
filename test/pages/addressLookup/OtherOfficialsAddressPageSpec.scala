/*
 * Copyright 2024 HM Revenue & Customs
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

package pages.addressLookup

import models.addressLookup.{AddressModel, CountryModel}
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class OtherOfficialsAddressPageSpec extends PageBehaviours {

  "OtherOfficialsAddressPageSpec" must {

    implicit lazy val arbitraryAddressModel: Arbitrary[AddressModel] = Arbitrary {
      AddressModel(Seq("7", "Morrison street"), Some("G58AN"), CountryModel("UK", "United Kingdom"))
    }

    beRetrievable[AddressModel](OtherOfficialAddressLookupPage(0))

    beSettable[AddressModel](OtherOfficialAddressLookupPage(0))

    beRemovable[AddressModel](OtherOfficialAddressLookupPage(0))
  }
}
