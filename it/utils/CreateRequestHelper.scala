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

package utils

import org.scalatestplus.play.ServerProvider
import play.api.Application
import play.api.libs.json.JsValue
import play.api.mvc.AnyContentAsJson
import play.api.test.FakeRequest
import play.api.test.Helpers.POST
import uk.gov.hmrc.http.SessionKeys

import java.util.UUID
import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}

trait CreateRequestHelper extends ServerProvider {

  val defaultSeconds                           = 5
  implicit val defaultDuration: FiniteDuration = Duration.apply(defaultSeconds, SECONDS)

  val app: Application

  def buildPost(url: String, formJson: JsValue): FakeRequest[AnyContentAsJson] =
    FakeRequest(POST, url)
      .withJsonBody(formJson)
      .withSession(SessionKeys.sessionId -> UUID.randomUUID().toString, SessionKeys.authToken -> SessionKeys.authToken)
      .withHeaders("Csrf-Token" -> "nocheck")
}
