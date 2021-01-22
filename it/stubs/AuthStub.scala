/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.Json
import utils.WireMockMethods

object AuthStub extends WireMockMethods {

  private val authoriseUri = "/auth/authorise"

  def authorised(userId: String = "providerId"): StubMapping = {
    val responseBody = s"""
                          |{
                          |  "optionalCredentials":{
                          |    "providerId": "$userId",
                          |    "providerType": "PrivilegedApplication"
                          |  }
                          |}
                          |""".stripMargin
    when(method = POST, uri = authoriseUri).thenReturn(status = OK, body = Json.parse(responseBody))
  }

  def unauthorised(): StubMapping =
    when(method = POST, uri = authoriseUri).thenReturn(status = UNAUTHORIZED)
}
