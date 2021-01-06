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

import models.{CharityOtherRegulatorDetails, UserAnswers}
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import pages.behaviours.PageBehaviours
import play.api.libs.json.Json

class IsCharityRegulatorPageSpec extends PageBehaviours {

  "IsCharityRegulatorPage" must {

    beRetrievable[Boolean](IsCharityRegulatorPage)

    beSettable[Boolean](IsCharityRegulatorPage)

    beRemovable[Boolean](IsCharityRegulatorPage)

    "cleanup" when {

      val userAnswer = UserAnswers("id", Json.obj()).set(CharityRegulatorPage, CharityRegulator.values.toSet)
        .flatMap(_.set(CharityCommissionRegistrationNumberPage, "registrationNumber")
        .flatMap(_.set(ScottishRegulatorRegNumberPage, "registrationNumber"))
        .flatMap(_.set(NIRegulatorRegNumberPage, "registrationNumber"))
        .flatMap(_.set(CharityOtherRegulatorDetailsPage, CharityOtherRegulatorDetails("ORegulatorName", "1234567")))
        .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.Other))
        .flatMap(_.set(WhyNotRegisteredWithCharityPage, "notRegisteredReason"))
        ).success.value

      "setting CharityRegulatorPage to true" must {

        val result = userAnswer.set(IsCharityRegulatorPage, true).success.value

        "remove SelectWhyNoRegulatorPage" in {

          result.get(SelectWhyNoRegulatorPage) mustNot be(defined)
        }

        "remove WhyNotRegisteredWithCharityPage" in {

          result.get(WhyNotRegisteredWithCharityPage) mustNot be(defined)
        }

        "not remove CharityRegulatorPage" in {

          result.get(CharityRegulatorPage) must be(defined)
        }

        "not remove CharityCommissionRegistrationNumberPage" in {

          result.get(CharityCommissionRegistrationNumberPage) must be(defined)
        }

        "not remove ScottishRegulatorRegNumberPage" in {

          result.get(ScottishRegulatorRegNumberPage) must be(defined)
        }

        "not remove NIRegulatorRegNumberPage" in {

          result.get(NIRegulatorRegNumberPage) must be(defined)
        }

        "not remove CharityOtherRegulatorDetailsPage" in {

          result.get(CharityOtherRegulatorDetailsPage) must be(defined)
        }
      }

      "setting CharityRegulatorPage to false" must {

        val result = userAnswer.set(IsCharityRegulatorPage, false).success.value

        "remove CharityRegulatorPage" in {

          result.get(CharityRegulatorPage) mustNot be(defined)
        }

        "remove CharityCommissionRegistrationNumberPage" in {

          result.get(CharityCommissionRegistrationNumberPage) mustNot be(defined)
        }

        "remove ScottishRegulatorRegNumberPage" in {

          result.get(ScottishRegulatorRegNumberPage) mustNot be(defined)
        }

        "remove NIRegulatorRegNumberPage" in {

          result.get(NIRegulatorRegNumberPage) mustNot be(defined)
        }

        "remove CharityOtherRegulatorDetailsPage" in {

          result.get(CharityOtherRegulatorDetailsPage) mustNot be(defined)
        }

        "not remove SelectWhyNoRegulatorPage" in {

          result.get(SelectWhyNoRegulatorPage) must be(defined)
        }

        "not remove WhyNotRegisteredWithCharityPage" in {

          result.get(WhyNotRegisteredWithCharityPage) must be(defined)
        }
      }
    }
  }
}
