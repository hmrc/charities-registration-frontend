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

package pages.regulatorsAndDocuments

import models.CharityOtherRegulatorDetails
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class CharityOtherRegulatorDetailsPageSpec extends PageBehaviours {

  "CharityOtherRegulatorDetailsPage" must {

    implicit lazy val arbitraryCharityContactDetails: Arbitrary[CharityOtherRegulatorDetails] = Arbitrary {
      CharityOtherRegulatorDetails(regulatorName = "ORegulatorName", registrationNumber = "1234567")
    }

    beRetrievable[CharityOtherRegulatorDetails](CharityOtherRegulatorDetailsPage)

    beSettable[CharityOtherRegulatorDetails](CharityOtherRegulatorDetailsPage)

    beRemovable[CharityOtherRegulatorDetails](CharityOtherRegulatorDetailsPage)
  }
}
