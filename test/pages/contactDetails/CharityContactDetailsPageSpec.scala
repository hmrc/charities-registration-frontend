/*
 * Copyright 2022 HM Revenue & Customs
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

import models.CharityContactDetails
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class CharityContactDetailsPageSpec extends PageBehaviours {

  "CharityContactDetailsPage" must {

    implicit lazy val arbitraryCharityContactDetails: Arbitrary[CharityContactDetails] = Arbitrary {
      CharityContactDetails(
        daytimePhone = "07700 900 982",
        mobilePhone = Some("07700 900 982"),
        emailAddress = "abc@gmail.com"
      )
    }

    beRetrievable[CharityContactDetails](CharityContactDetailsPage)

    beSettable[CharityContactDetails](CharityContactDetailsPage)

    beRemovable[CharityContactDetails](CharityContactDetailsPage)
  }
}
