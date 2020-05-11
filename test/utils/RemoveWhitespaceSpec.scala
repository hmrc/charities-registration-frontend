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

package utils

import base.SpecBase

class RemoveWhitespaceSpec extends SpecBase with RemoveWhitespace {

  ".removeWhitespace(string: String)" must {

    "correctly format and remove spaces from string" which {

      "has space before, after and in between" in {
        removeWhitespace("   1 2   3  ") mustBe "123"
      }

      "has space before" in {
        removeWhitespace("   123") mustBe "123"
      }

      "has space after" in {
        removeWhitespace("123     ") mustBe "123"
      }

      "has space in between" in {
        removeWhitespace("12 3") mustBe "123"
      }

      "has only spaces" in {
        removeWhitespace("    ") mustBe ""
      }

      "has no spaces" in {
        removeWhitespace("") mustBe ""
      }
    }
  }
}
