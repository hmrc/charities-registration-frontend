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

package models

import org.scalatest.{MustMatchers, OptionValues, WordSpec}

class CommonModelSpec extends WordSpec with MustMatchers with OptionValues {

  "Name object" must {

    "all parameters defined" in {

      val authorisedOfficialsName = Name(
        firstName = "Jack",
        middleName = Some("and"),
        lastName = "Jill")

      authorisedOfficialsName.getFullName mustBe "Jack and Jill"

    }

    "middleName is not defined" in {

      val authorisedOfficialsName = Name(
        firstName = "Jack",
        middleName = None,
        lastName = "Jill")

      authorisedOfficialsName.getFullName mustBe "Jack Jill"

    }

    "toString" in {

      Name.toString mustBe "name"
    }
  }

  "PhoneNumber object" must {

    "all parameters defined" in {

      val authorisedOfficialsPhoneNumber = PhoneNumber(
        daytimePhone = "01632 960 001",
        mobilePhone = Some("01632 960 001"))

      authorisedOfficialsPhoneNumber.daytimePhone mustBe "01632 960 001"
      authorisedOfficialsPhoneNumber.mobilePhone mustBe Some("01632 960 001")

    }

    "alternativePhoneNumber is not defined" in {

      val authorisedOfficialsPhoneNumber = PhoneNumber(
        daytimePhone = "01632 960 001",
        mobilePhone = None)

      authorisedOfficialsPhoneNumber.daytimePhone mustBe "01632 960 001"
      authorisedOfficialsPhoneNumber.mobilePhone mustBe None
    }

    "toString" in {

      PhoneNumber.toString mustBe "phoneNumber"
    }
  }

}
