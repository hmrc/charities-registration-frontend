/*
 * Copyright 2026 HM Revenue & Customs
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

package pages.authorisedOfficials

import models.{Passport, UserAnswers}
import pages.behaviours.PageBehaviours
import play.api.libs.json.Json

class IsAuthorisedOfficialNinoPageSpec extends PageBehaviours {

  "IsAuthorisedOfficialNinoPage" must {

    beRetrievable[Boolean](IsAuthorisedOfficialNinoPage(0))
    beSettable[Boolean](IsAuthorisedOfficialNinoPage(0))
    beRemovable[Boolean](IsAuthorisedOfficialNinoPage(0))
  }

  "cleanup" when {

    val userAnswer = UserAnswers("id", Json.obj())
      .set(IsAuthorisedOfficialNinoPage(0), true)
      .flatMap(_.set(AuthorisedOfficialsPassportPage(0), passport))
      .flatMap(_.set(AuthorisedOfficialsNinoPage(0), nino))
      .success
      .value

    "setting IsAuthorisedOfficialNinoPage to false" must {

      val result = userAnswer.set(IsAuthorisedOfficialNinoPage(0), false).success.value

      "remove AuthorisedOfficialsNinoPage" in {

        result.get(AuthorisedOfficialsNinoPage(0)) mustNot be(defined)
      }
    }

    "setting IsAuthorisedOfficialNinoPage to true" must {

      val result = userAnswer.set(IsAuthorisedOfficialNinoPage(0), true).success.value

      "remove AuthorisedOfficialsPassportPage" in {

        result.get(AuthorisedOfficialsPassportPage(0)) mustNot be(defined)
      }
    }
  }

}
