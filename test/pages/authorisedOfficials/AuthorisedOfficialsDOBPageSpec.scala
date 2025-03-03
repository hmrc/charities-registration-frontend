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

package pages.authorisedOfficials

import models.Index
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class AuthorisedOfficialsDOBPageSpec extends PageBehaviours {

  private val year       = 2002
  private val month      = 1
  private val dayInMonth = 1

  implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
    LocalDate.of(year, month, dayInMonth)
  }

  "AuthorisedOfficialsDOBPage" must {

    beRetrievable[LocalDate](AuthorisedOfficialsDOBPage(Index(0)))

    beSettable[LocalDate](AuthorisedOfficialsDOBPage(Index(0)))

    beRemovable[LocalDate](AuthorisedOfficialsDOBPage(Index(0)))
  }

}
