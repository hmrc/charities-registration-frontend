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

package utils

import java.time._
import java.time.format.DateTimeFormatter

import base.SpecBase
import play.api.i18n.Messages
import play.api.mvc.Cookie
import play.api.test.FakeRequest

class ImplicitDateFormatterSpec extends SpecBase with ImplicitDateFormatter {

  "The implicit date formatter" should {

    "format dates in correct style" in {
      val result: String = "1 April 2017"
      dateToString(apr1st2017) mustBe result
    }

    "format dates far in the past in the correct style" in {
      val result: String = "1 January 1111"
      dateToString(jan1st1111) mustBe result
    }
    "format months with single digit values in correct style" in {
      val result: String = "30 June 2017"
      dateToString(jun30tt2017) mustBe result
    }

    "format days with single digit values in correct style" in {
      val result: String = "1 June 2017"
      dateToString(jun1st2017) mustBe result
    }

    "format string DateTimes in correct style" in {
      val result: String = "1 April 2017"
      dateToString(
        ZonedDateTime.parse("2017-04-01T11:23:45.123Z", DateTimeFormatter.ISO_ZONED_DATE_TIME).toLocalDate
      ) mustBe result
    }
  }

  "The implicit date formatter for Welsh" should {

    implicit val localRequest: FakeRequest[?] = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "cy"))
    implicit lazy val localMessages: Messages = messagesApi.preferred(localRequest)

    "format dates with single digit values in correct style" in {
      val result: String = "1 Ebrill 2017"
      dateToString(apr1st2017)(localMessages) mustBe result
    }

    "format months in correct style" in {
      val result: String = "30 Mehefin 2017"
      dateToString(jun30tt2017)(localMessages) mustBe result
    }

    "format MonthDay in correct style" in {
      val result: String = "30 Mehefin"
      monthToString(
        MonthDay.from(
          jun30tt2017
        )
      )(localMessages) mustBe result
    }

    "format MonthDay with single digit values in correct style" in {
      val result: String = "1 Tachwedd"
      monthToString(
        MonthDay.from(nov1st2017)
      )(localMessages) mustBe result
    }

    "for invalid lang" in {
      implicit val localRequest: FakeRequest[?] = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "QQ"))
      implicit lazy val localMessages: Messages = messagesApi.preferred(localRequest)
      val result: String                        = "1 April 2017"
      dateToString(apr1st2017)(localMessages) mustBe result
    }

    "for no lang" in {
      implicit val localRequest: FakeRequest[?] = FakeRequest()
      implicit lazy val localMessages: Messages = messagesApi.preferred(localRequest)
      val result: String                        = "1 April 2017"
      dateToString(apr1st2017)(localMessages) mustBe result
    }

  }

  "The date formatter" should {

    "format dates in correct style" in {
      val result: String = "Monday 14 September 2020"
      dayToString(sep14th2020) mustBe result
    }

    "format dates in correct style for day prior to 20" in {
      val result: String = "Tuesday 1 September 2020"
      dayToString(sep1st2020) mustBe result
    }

    "format dates in correct style for day after 20" in {
      val result: String = "Tuesday 22 September 2020"
      dayToString(sep22nd2020) mustBe result
    }

    "format dates in correct style for day ending with 3" in {
      val result: String = "Wednesday 23 September 2020"
      dayToString(sep23rd2020) mustBe result
    }

    "format dates in correct style for Welsh" in {
      val welshRequest            = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "cy"))
      val welshMessages: Messages = messagesApi.preferred(welshRequest)
      val result: String          = "Dydd Mercher 23 Medi 2020"
      dayToString(sep23rd2020)(welshMessages) mustBe result
    }

    "format dates in correct style for Welsh without day of week" in {
      val welshRequest            = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "cy"))
      val welshMessages: Messages = messagesApi.preferred(welshRequest)
      val result: String          = "23 Medi 2020"
      dayToString(
        date = sep23rd2020,
        dayOfWeek = false
      )(welshMessages) mustBe result
    }

  }
}
