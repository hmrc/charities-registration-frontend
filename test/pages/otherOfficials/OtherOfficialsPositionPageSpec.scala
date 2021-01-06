/*
 * Copyright 2021 HM Revenue & Customs
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

package pages.otherOfficials

import models.authOfficials.OfficialsPosition
import org.scalacheck.{Arbitrary, Gen}
import pages.behaviours.PageBehaviours

class OtherOfficialsPositionPageSpec extends PageBehaviours{

  "OtherOfficialsPosition" must {

    implicit lazy val arbitraryCharityContactDetails: Arbitrary[OfficialsPosition] =  Arbitrary {
      Gen.oneOf(OfficialsPosition.values)
    }

    beRetrievable[OfficialsPosition](OtherOfficialsPositionPage(0))

    beSettable[OfficialsPosition](OtherOfficialsPositionPage(0))

    beRemovable[OfficialsPosition](OtherOfficialsPositionPage(0))
  }

}
