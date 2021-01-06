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

package models.regulators

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}


class SelectWhyNoRegulatorSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "SelectWhyNoRegulator" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(SelectWhyNoRegulator.values)

      forAll(gen) {
        selectWhyNoRegulator =>

          JsString(selectWhyNoRegulator.toString).validate[SelectWhyNoRegulator].asOpt.value mustEqual selectWhyNoRegulator
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!SelectWhyNoRegulator.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[SelectWhyNoRegulator] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(SelectWhyNoRegulator.values)

      forAll(gen) {
        selectWhyNoRegulator =>

          Json.toJson(selectWhyNoRegulator) mustEqual JsString(selectWhyNoRegulator.toString)
      }
    }
  }
}
