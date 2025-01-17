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

package models.operations

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class CharitablePurposesSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "CharitablePurposes" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(CharitablePurposes.values)

      forAll(gen) { charitablePurposesCheckbox =>
        JsString(charitablePurposesCheckbox.toString)
          .validate[CharitablePurposes]
          .asOpt
          .value mustEqual charitablePurposesCheckbox
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!CharitablePurposes.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[CharitablePurposes] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(CharitablePurposes.values)

      forAll(gen) { charitablePurposesCheckbox =>
        Json.toJson(charitablePurposesCheckbox) mustEqual JsString(charitablePurposesCheckbox.toString)
      }
    }

    "order" in {

      CharitablePurposes.values.sortBy(_.order) mustBe CharitablePurposes.values
    }
  }
}
