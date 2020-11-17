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

package models.oldCharities

import base.SpecBase
import play.api.libs.json.Json

class UserAnswerTransformerSpec extends SpecBase {
  //scalastyle:off magic.number
  val jsonTransformer: UserAnswerTransformer = new UserAnswerTransformer

  "UserAnswerTransformer" when {

    "toUserAnswerCharityContactDetails" must {

      "convert the ContactDetails" in {

        val contactDetails: CharityContactDetails = CharityContactDetails("Test123", None, "1234567890", None, None, None)

        Json.obj("charityContactDetails" -> Json.toJson(contactDetails)).transform(
          jsonTransformer.toUserAnswerCharityContactDetails).asOpt.value mustBe Json.obj(
           "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
           "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
           "isSection1Completed" -> false)
      }
    }

    "toUserAnswerCharityOfficialAddress" must {

      "convert the OfficialAddress" in {

        val charityAddress: CharityAddress = CharityAddress("Test123", "line2", "", "", "postcode", "")

        Json.obj("charityOfficialAddress" -> Json.toJson(charityAddress)).transform(
          jsonTransformer.toUserAnswerCharityOfficialAddress).asOpt.value mustBe Json.obj(
           "charityOfficialAddress" -> Json.parse("""{"postcode":"postcode","country":{"code":"GB","name":"GB"},"lines":["Test123","line2"]}"""))
      }
    }

    "toUserAnswerCorrespondenceAddress" must {

      "convert the CorrespondenceAddress" in {

        val correspondenceAddress: OptionalCharityAddress = OptionalCharityAddress(Some("true"), CharityAddress("Test123", "line2", "", "", "postcode", ""))

        Json.obj("correspondenceAddress" -> Json.toJson(correspondenceAddress)).transform(
          jsonTransformer.toUserAnswerCorrespondenceAddress).asOpt.value mustBe Json.obj(
           "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "canWeSendLettersToThisAddress" -> false
        )
      }
    }
  }

}
