/*
 * Copyright 2024 HM Revenue & Customs
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

package pages.otherOfficials

import pages.behaviours.PageBehaviours

class OtherOfficialsIdSpec extends PageBehaviours {

  "OtherOfficialsId" must {

    "have correct information" in {
      val pageId = OtherOfficialsId(0)
      pageId.path.toString mustBe "/otherOfficials(0)"
      pageId.index mustBe 0
    }
  }
}
