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

package models.regulators

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}


class CharityRegulatorSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "CharityRegulatorCheckbox" must {

    "deserialise valid values" in {

      val gen = arbitrary[CharityRegulator]

      forAll(gen) {
        charityRegulatorCheckbox =>

          JsString(charityRegulatorCheckbox.toString).validate[CharityRegulator].asOpt.value mustEqual charityRegulatorCheckbox
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!CharityRegulator.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[CharityRegulator] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = arbitrary[CharityRegulator]

      forAll(gen) {
        charityRegulatorCheckbox =>

          Json.toJson(charityRegulatorCheckbox) mustEqual JsString(charityRegulatorCheckbox.toString)
      }
    }
  }
}



