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

package service

import base.SpecBase
import play.api.i18n.Messages
import play.api.mvc.Cookie
import play.api.test.FakeRequest

class CountryServiceSpec extends SpecBase {

  private val welshRequest            = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "cy"))
  private val welshMessages: Messages = messagesApi.preferred(welshRequest)

  val service: CountryService = inject[CountryService]

  "all countries" must {

    "return list of countries ordered by name" in {

      val found = service.countries()
      found.head._1 must be("AF")
      found.head._2 must be("Afghanistan")
      found.last._1 must be("ZW")
      found.last._2 must be("Zimbabwe")
    }

    "return list of countries ordered by name in Welsh" in {

      val found = service.countries()(welshMessages)
      found.head._1 must be("AF")
      found.head._2 must be("Affganistan")
      found.last._1 must be("ZW")
      found.last._2 must be("Zimbabwe")
    }
  }

  "find with country code" must {

    "keep reference to UK" in {

      val found = service.find(code = gbCountry.code)
      found.get.name must be(gbCountry.name)
    }

    "keep reference to UK in Welsh" in {

      val found = service.find(code = gbCountryCode)(welshMessages)
      found.get.name must be("Y Deyrnas Unedig")
    }
  }
}
