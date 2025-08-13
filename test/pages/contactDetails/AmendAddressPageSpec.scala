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

package pages.contactDetails

import models.addressLookup.AmendAddressModel
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class AmendAddressPageSpec extends PageBehaviours {

  "AmendCharityOfficialAddressPage" must {

    implicit lazy val arbitraryCharityContactDetails: Arbitrary[AmendAddressModel] = Arbitrary {
      AmendAddressModel(
        organisation = Some("Test Organisation"),
        line1 = "23",
        line2 = Some("Morrison street"),
        line3 = Some(""),
        town = "Glasgow",
        postcode = "G58AN",
        country = "GB"
      )
    }

    beRetrievable[AmendAddressModel](AmendAddressPage)

    beSettable[AmendAddressModel](AmendAddressPage)

    beRemovable[AmendAddressModel](AmendAddressPage)
  }
}
