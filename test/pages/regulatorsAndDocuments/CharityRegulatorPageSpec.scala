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

package pages.regulatorsAndDocuments

import models.regulators.CharityRegulator
import org.scalacheck.{Arbitrary, Gen}
import models.{CharityOtherRegulatorDetails, UserAnswers}
import pages.behaviours.PageBehaviours
import play.api.libs.json.Json

class CharityRegulatorPageSpec extends PageBehaviours{

  implicit lazy val arbitraryCharityRegulatorCheckbox: Arbitrary[CharityRegulator] =
    Arbitrary {
      Gen.oneOf(CharityRegulator.values)
    }

  "CharityRegulatorPage" must {

    beRetrievable[Set[CharityRegulator]](CharityRegulatorPage)

    beSettable[Set[CharityRegulator]](CharityRegulatorPage)

    beRemovable[Set[CharityRegulator]](CharityRegulatorPage)

    "cleanup" when {

      val userAnswer = UserAnswers("id", Json.obj()).set(CharityRegulatorPage, CharityRegulator.values.toSet)
        .flatMap(_.set(CharityCommissionRegistrationNumberPage, "registrationNumber")
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "registrationNumber"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "registrationNumber"))
          .flatMap(_.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("ORegulatorName", "1234567")))
        ).success.value

      "setting CharityRegulatorPage to EnglandWales" must {

        val result = userAnswer.set(CharityRegulatorPage, Set[CharityRegulator](CharityRegulator.EnglandWales)).success.value

        "remove ScottishRegulatorRegNumberPage" in {

          result.get(ScottishRegulatorRegNumberPage) mustNot be(defined)
        }

        "remove NIRegulatorRegNumberPage" in {

          result.get(NIRegulatorRegNumberPage) mustNot be(defined)
        }

        "remove CharityOtherRegulatorDetailsPage" in {

          result.get(CharityOtherRegulatorDetailsPage) mustNot be(defined)
        }

        "not remove CharityCommissionRegistrationNumberPage" in {

          result.get(CharityCommissionRegistrationNumberPage) must be(defined)
        }
      }

      "setting CharityRegulatorPage to Scottish" must {

        val result = userAnswer.set(CharityRegulatorPage, Set[CharityRegulator](CharityRegulator.Scottish)).success.value

        "remove CharityCommissionRegistrationNumberPage" in {

          result.get(CharityCommissionRegistrationNumberPage) mustNot be(defined)
        }

        "remove NIRegulatorRegNumberPage" in {

          result.get(NIRegulatorRegNumberPage) mustNot be(defined)
        }

        "remove CharityOtherRegulatorDetailsPage" in {

          result.get(CharityOtherRegulatorDetailsPage) mustNot be(defined)
        }

        "not remove CharityRegulatorPage" in {

          result.get(ScottishRegulatorRegNumberPage) must be(defined)
        }
      }
    }
  }
}
