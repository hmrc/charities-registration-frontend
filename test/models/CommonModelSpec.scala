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

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class CommonModelSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "SelectTitle" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(SelectTitle.values)

      forAll(gen) {
        selectTitle =>

          JsString(selectTitle.toString).validate[SelectTitle].asOpt.value mustEqual selectTitle
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!SelectTitle.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[SelectTitle] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(SelectTitle.values)

      forAll(gen) {
        selectTitle =>

          Json.toJson(selectTitle) mustEqual JsString(selectTitle.toString)
      }
    }
  }

  "Name object" must {

    "all parameters defined" in {

      val authorisedOfficialsName = Name(
        SelectTitle.Mr,
        firstName = "Jack",
        middleName = Some("and"),
        lastName = "Jill")

      authorisedOfficialsName.getFullName mustBe "Jack and Jill"

    }

    "middleName is not defined" in {

      val authorisedOfficialsName = Name(
        SelectTitle.Mr,
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
        mobilePhone = "01632 960 001")

      authorisedOfficialsPhoneNumber.daytimePhone mustBe "01632 960 001"
      authorisedOfficialsPhoneNumber.mobilePhone mustBe "01632 960 001"

    }

    "toString" in {

      PhoneNumber.toString mustBe "phoneNumber"
    }
  }

}
