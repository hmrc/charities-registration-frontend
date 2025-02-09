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
import models.UserAnswers
import pages.sections.Section1Page
import play.api.libs.json._

class CharitiesSectionCompleteServiceSpec extends SpecBase {

  lazy val service: CharitiesSectionCompleteService = inject[CharitiesSectionCompleteService]

  "CharitiesSectionCompleteService" when {

    "isCharityInformationStatusSectionCompleted" must {

      val data = Json.obj(
        "charityContactDetails"                      -> Json.parse("""{"emailAddress":"a@b.com","daytimePhone":"1234567890"}"""),
        "charityName"                                -> Json.parse("""{"fullName":"Test123"}"""),
        "isCharityInformationStatusSectionCompleted" -> false,
        "isSwitchOver"                               -> true,
        "charityOfficialAddress"                     -> Json.parse(
          """{"country":{"code":"GB","name":"United Kingdom"},"postcode":"postcode","lines":["Test123","line2"]}"""
        ),
        "charityPostalAddress"                       -> Json.parse(
          """{"country":{"code":"GB","name":"United Kingdom"},"postcode":"postcode","lines":["Test123","line2"]}"""
        )
      )

      "return false when sections are not completed" in {

        val ua: UserAnswers = UserAnswers("8799940975137654", data)

        val result = service.isCharityInformationStatusSectionCompleted(ua)

        result.get.get(Section1Page) mustBe Some(false)
      }

      "return false when all sections are completed and charity name is more than 60 characters" in {

        val ua: UserAnswers = UserAnswers(
          "8799940975137654",
          data ++ Json.obj("canWeSendLettersToThisAddress" -> false)
            ++ Json.obj(
              "charityName" -> Json.parse(
                """{"fullName":"canWeSendLettersToThisAddresscanWeSendLettersToThisAddress123"}"""
              )
            )
        )

        val result = service.isCharityInformationStatusSectionCompleted(ua)

        result.get.get(Section1Page) mustBe Some(false)
      }

      "return true when all sections are completed" in {

        val ua: UserAnswers =
          UserAnswers("8799940975137654", data ++ Json.obj("canWeSendLettersToThisAddress" -> false))

        val result = service.isCharityInformationStatusSectionCompleted(ua)

        result.get.get(Section1Page) mustBe Some(true)
      }
    }
  }
}
