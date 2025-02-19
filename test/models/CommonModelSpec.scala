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

package models

import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

import java.time.LocalDate

class CommonModelSpec extends SpecBase with ScalaCheckPropertyChecks with OptionValues {

  "SelectTitle" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(SelectTitle.values)

      forAll(gen) { selectTitle =>
        JsString(selectTitle.toString).validate[SelectTitle].asOpt.value mustEqual selectTitle
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!SelectTitle.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[SelectTitle] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(SelectTitle.values)

      forAll(gen) { selectTitle =>
        Json.toJson(selectTitle) mustEqual JsString(selectTitle.toString)
      }
    }
  }

  "Name object" must {

    "get correct Name with Title" in {

      val authorisedOfficialsName =
        Name(SelectTitle.Mr, firstName = "Jack", middleName = Some("Joe"), lastName = "Jill")

      authorisedOfficialsName.getFullNameWithTitle mustBe "Mr Jack Joe Jill"

    }

    "all parameters defined" in {

      val authorisedOfficialsName =
        Name(SelectTitle.Mr, firstName = "Jack", middleName = Some("and"), lastName = "Jill")

      authorisedOfficialsName.getFullName mustBe "Jack and Jill"

    }

    "middleName is not defined" in {

      val authorisedOfficialsName = Name(SelectTitle.Mr, firstName = "Jack", middleName = None, lastName = "Jill")

      authorisedOfficialsName.getFullName mustBe "Jack Jill"

    }

    "toString" in {

      Name.toString mustBe "name"
    }

    "json - serialise and deserialise" in {

      val name = Name(SelectTitle.Mr, "Jack", Some("Joe"), "Jill")

      val json = Json.toJson(name)

      json.validate[Name].asOpt.value mustBe name
    }

  }

  "PhoneNumber object" must {

    "all parameters defined" in {

      val authorisedOfficialsPhoneNumber =
        PhoneNumber(daytimePhone = "01632 960 001", mobilePhone = Some("01632 960 001"))

      authorisedOfficialsPhoneNumber.daytimePhone mustBe "01632 960 001"
      authorisedOfficialsPhoneNumber.mobilePhone.get mustBe "01632 960 001"

    }

    "toString" in {

      PhoneNumber.toString mustBe "phoneNumber"
    }

    "json - serialise and deserialise" in {

      val phoneNumber = PhoneNumber("01632 960 001", Some("01632 960 001"))

      val json = Json.toJson(phoneNumber)

      json.validate[PhoneNumber].asOpt.value mustBe phoneNumber
    }

  }

  "Passport object" must {

    "all parameters defined" in {

      val authorisedOfficialsPassport = Passport("GB123456", "GB", LocalDate.now)

      authorisedOfficialsPassport.passportNumber mustBe "GB123456"
      authorisedOfficialsPassport.country mustBe "GB"
      authorisedOfficialsPassport.expiryDate mustBe LocalDate.now

    }

    "toString" in {

      Passport.toString mustBe "passport"
    }

    "json - serialise and deserialise" in {

      val passport = Passport("GB123456", "GB", LocalDate.now)

      val json = Json.toJson(passport)

      json.validate[Passport].asOpt.value mustBe passport
    }

  }

  "Country object" must {

    "all parameters defined" in {

      val country = Country("GB", "United Kingdom")

      country.code mustBe "GB"
      country.name mustBe "United Kingdom"
    }
  }

  "FcoCountry object" must {

    "all parameters defined" in {

      val country = FcoCountry("GB", "United Kingdom")

      country.country mustBe "GB"
      country.name mustBe "United Kingdom"
    }

    "json - serialise and deserialise" in {

      val fcoCountry = FcoCountry("GB", "United Kingdom")

      val json = Json.toJson(fcoCountry)

      json.validate[FcoCountry].asOpt.value mustBe fcoCountry
    }

  }

  "CharityName object" must {

    "all parameters defined" in {

      val charityName = CharityName("Name", Some("op number"))

      charityName.fullName mustBe "Name"
      charityName.operatingName mustBe Some("op number")
    }

    "toString" in {
      CharityName.toString mustBe "charityNamesDetail"
    }
  }

  "CharityContactDetails object" must {

    "all parameters defined" in {

      val charityContactDetails = CharityContactDetails("1234567890", Some("1234567890"), "a@b.com")

      charityContactDetails.daytimePhone mustBe "1234567890"
      charityContactDetails.mobilePhone mustBe Some("1234567890")
      charityContactDetails.emailAddress mustBe "a@b.com"
    }

    "toString" in {
      CharityContactDetails.toString mustBe "charityContactDetails"
    }
  }

  "CharityOtherRegulatorDetails object" must {

    "all parameters defined" in {

      val charityOtherRegulatorDetails = CharityOtherRegulatorDetails("1234567890", "1234567890")

      charityOtherRegulatorDetails.regulatorName mustBe "1234567890"
      charityOtherRegulatorDetails.registrationNumber mustBe "1234567890"
    }

    "toString" in {
      CharityOtherRegulatorDetails.toString mustBe "charityOtherRegulatorDetails"
    }
  }

  "SaveStatus" must {

    "status defined" in {

      val status = SaveStatus(true)

      status.status mustBe true
    }

    "json - serialise and deserialise" in {

      val status = SaveStatus(true)

      val json = Json.toJson(status)

      json.validate[SaveStatus].asOpt.value mustBe status
    }

  }

}
