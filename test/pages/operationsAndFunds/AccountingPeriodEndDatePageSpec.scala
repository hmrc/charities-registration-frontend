/*
 * Copyright 2022 HM Revenue & Customs
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

package pages.operationsAndFunds
import models.MongoDateTimeFormats._
import org.joda.time.{LocalDate, MonthDay}
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class AccountingPeriodEndDatePageSpec extends PageBehaviours {

  val year       = 2020
  val yearInPast = 1000
  val month      = 1
  val dayInMonth = 1

  implicit lazy val arbitraryLocalDate: Arbitrary[MonthDay] = Arbitrary {
    MonthDay.fromDateFields(new LocalDate(year, month, dayInMonth).toDate)
  }

  "AccountingPeriodEndDate" must {

    beRetrievable[MonthDay](AccountingPeriodEndDatePage)

    beSettable[MonthDay](AccountingPeriodEndDatePage)

    beRemovable[MonthDay](AccountingPeriodEndDatePage)
  }

}
