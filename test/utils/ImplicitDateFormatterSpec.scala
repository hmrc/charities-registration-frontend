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
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZonedDateTime}

class ImplicitDateFormatterSpec extends SpecBase with ImplicitDateFormatter {

  "The implicit date formatter" should {

    "format dates in correct style" in {
      val result: String = "1 April 2017"
      dateToString(LocalDate.of(2017, 4, 1)) mustBe result
    }

    "format months with single digit values in correct style" in {
      val result: String = "30 June 2017"
      dateToString(LocalDate.of(2017, 6, 30)) mustBe result
    }

    "format days with single digit values in correct style" in {
      val result: String = "1 June 2017"
      dateToString(LocalDate.of(2017, 6, 1)) mustBe result
    }

    "format string DateTimes in correct style" in {
      val result: String = "1 April 2017"
      dateToString(ZonedDateTime.parse("2017-04-01T11:23:45.123Z", DateTimeFormatter.ISO_ZONED_DATE_TIME).toLocalDate) mustBe result
    }
  }
}
