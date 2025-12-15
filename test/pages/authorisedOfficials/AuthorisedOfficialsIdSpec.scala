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

package pages.authorisedOfficials

import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours
import play.api.libs.json.{JsObject, JsString}

class AuthorisedOfficialsIdSpec extends PageBehaviours {

  "AuthorisedOfficialsId" must {

    "have correct information" in {
      val pageId = AuthorisedOfficialsId(0)
      pageId.path.toString mustBe "/authorisedOfficials(0)"
      pageId.index mustBe 0
    }

    implicit lazy val arbitraryAuthorisedOfficialsId: Arbitrary[JsObject] = Arbitrary {
      JsObject(Seq("test" -> JsString("Test")))
    }

    beRetrievable[JsObject](AuthorisedOfficialsId(0))
    beSettable[JsObject](AuthorisedOfficialsId(0))
    beRemovable[JsObject](AuthorisedOfficialsId(0))
  }
}
