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

import org.joda.time.{LocalDate=>JLocalDate, MonthDay}
import play.api.Play
import play.api.i18n.Messages
import play.api.mvc.Cookie
import play.api.test.FakeRequest

class ImplicitDateFormatterSpec extends SpecBase with ImplicitDateFormatter {

  //scalastyle:off magic.number

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

  "The implicit date formatter for Welsh" should {

    implicit val localRequest: FakeRequest[_] = FakeRequest().withCookies(Cookie(Play.langCookieName(messagesApi), "cy"))
    implicit lazy val localMessages: Messages = messagesApi.preferred(localRequest)

    "format dates with single digit values in correct style" in {
      val result: String = "1 Ebrill 2017"
      dateToString(LocalDate.of(2017, 4, 1))(localMessages) mustBe result
    }

    "format months in correct style" in {
      val result: String = "30 Mehefin 2017"
      dateToString(LocalDate.of(2017, 6, 30))(localMessages) mustBe result
    }

    "format MonthDay in correct style" in {
      val result: String = "30 Mehefin"
      monthToString(MonthDay.fromDateFields(
        new JLocalDate(2017, 6, 30).toDate))(localMessages) mustBe result
    }

    "format MonthDay with single digit values in correct style" in {
      val result: String = "1 Tachwedd"
      monthToString(MonthDay.fromDateFields(
        new JLocalDate(2017, 11, 1).toDate))(localMessages) mustBe result
    }

    "for invalid lang" in {
      implicit val localRequest: FakeRequest[_] = FakeRequest().withCookies(Cookie(Play.langCookieName(messagesApi), "QQ"))
      implicit lazy val localMessages: Messages = messagesApi.preferred(localRequest)
      val result: String = "1 April 2017"
      dateToString(LocalDate.of(2017, 4, 1))(localMessages) mustBe result
    }

    "for no lang" in {
      implicit val localRequest: FakeRequest[_] = FakeRequest()
      implicit lazy val localMessages: Messages = messagesApi.preferred(localRequest)
      val result: String = "1 April 2017"
      dateToString(LocalDate.of(2017, 4, 1))(localMessages) mustBe result
    }

  }

  "The date formatter" should {

    "format dates in correct style" in {
      val result: String = "Monday 14th September 2020"
      dayToString(LocalDate.of(2020, 9, 14)) mustBe result
    }

   "format dates in correct style for day prior to 20" in {
      val result: String = "Tuesday 1st September 2020"
      dayToString(LocalDate.of(2020, 9, 1)) mustBe result
    }

    "format dates in correct style for day after 20" in {
      val result: String = "Tuesday 22nd September 2020"
      dayToString(LocalDate.of(2020, 9, 22)) mustBe result
    }

    "format dates in correct style for day ending with 3" in {
      val result: String = "Wednesday 23rd September 2020"
      dayToString(LocalDate.of(2020, 9, 23)) mustBe result
    }

  }
}
