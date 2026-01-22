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

package pages.regulatorsAndDocuments

import models.UserAnswers
import models.regulators.SelectWhyNoRegulator
import org.scalacheck.{Arbitrary, Gen}
import pages.behaviours.PageBehaviours
import play.api.libs.json.Json

class SelectWhyNoRegulatorPageSpec extends PageBehaviours {

  "SelectWhyNoRegulatorPage" must {

    implicit lazy val arbitraryCharityContactDetails: Arbitrary[SelectWhyNoRegulator] = Arbitrary {
      Gen.oneOf(SelectWhyNoRegulator.values.toIndexedSeq)
    }

    beRetrievable[SelectWhyNoRegulator](SelectWhyNoRegulatorPage)
    beSettable[SelectWhyNoRegulator](SelectWhyNoRegulatorPage)
    beRemovable[SelectWhyNoRegulator](SelectWhyNoRegulatorPage)

    "cleanup" when {
      val userAnswer = UserAnswers("id", Json.obj())
        .set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other)
        .flatMap(_.set(WhyNotRegisteredWithCharityPage, "office closed"))
        .success
        .value

      "setting SelectWhyNoRegulator to ExemptOrExcepted" must {

        val result = userAnswer.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.ExemptOrExcepted).success.value

        "remove WhyNotRegisteredWithCharityPage" in {

          result.get(WhyNotRegisteredWithCharityPage) mustNot be(defined)
        }

        "not remove SelectWhyNoRegulatorPage" in {

          result.get(SelectWhyNoRegulatorPage) must be(defined)
        }
      }
    }
  }
}
