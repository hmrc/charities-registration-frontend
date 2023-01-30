/*
 * Copyright 2023 HM Revenue & Customs
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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.UserAnswers
import pages.{AcknowledgementReferencePage, ApplicationSubmissionDatePage}
import play.api.http.Status.{ACCEPTED, OK}
import play.api.libs.json.{JsValue, Json}
import utils.WireMockMethods

import java.time.LocalDate

object CharitiesStub extends WireMockMethods {

  private val charitiesRegistration = "^/org/-?([0-9]*)/submissions/application"
  private val saveUserAnswer        = "/charities-registration/saveUserAnswer/"
  private val getUserAnswer         = "/charities-registration/getUserAnswer/"

  def stubScenario(requestJson: String): StubMapping =
    when(method = POST, uri = charitiesRegistration, body = Some(requestJson))
      .thenReturn(status = ACCEPTED, body = Json.parse("""{"acknowledgementReference":"765432"}"""))

  def stubUserAnswerPost(ua: UserAnswers, userId: String): StubMapping = {
    val requestJson = Json.stringify(
      Json.toJson(
        ua.set(AcknowledgementReferencePage, "765432")
          .flatMap(_.set(ApplicationSubmissionDatePage, LocalDate.now()))
          .get
      )
    )

    when(method = POST, uri = s"$saveUserAnswer$userId", body = Some(requestJson))
      .thenReturn(status = OK, body = Json.parse("""{"status":true}"""))
  }

  def stubUserAnswerGet(ua: UserAnswers, userId: String): StubMapping =
    when(method = GET, uri = s"$getUserAnswer$userId").thenReturn(status = OK, body = Json.toJson(ua))
}
