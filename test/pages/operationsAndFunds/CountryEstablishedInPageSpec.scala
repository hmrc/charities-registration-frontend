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

package pages.operationsAndFunds

import models.operations.CharityEstablishedOptions
import org.scalacheck.{Arbitrary, Gen}
import pages.behaviours.PageBehaviours

class CharityEstablishedInPageSpec extends PageBehaviours{

    implicit lazy val arbitraryCharityContactDetails: Arbitrary[CharityEstablishedOptions] =  Arbitrary {
      Gen.oneOf(CharityEstablishedOptions.values)
    }

  "WhatCountryWasTheCharityEstablishedIn" must {

    beRetrievable[CharityEstablishedOptions](CharityEstablishedInPage)

    beSettable[CharityEstablishedOptions](CharityEstablishedInPage)

    beRemovable[CharityEstablishedOptions](CharityEstablishedInPage)
  }

}
