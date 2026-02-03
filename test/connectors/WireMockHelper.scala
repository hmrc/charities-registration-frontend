/*
 * Copyright 2026 HM Revenue & Customs
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

package connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import common.TestData
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

trait WireMockHelper extends BeforeAndAfterAll with BeforeAndAfterEach with TestData {
  this: Suite =>

  private val wireHost                   = "localhost"
  private val wirePort                   = 20001
  private val wireMockServer             = new WireMockServer(wirePort)
  protected lazy val wireMockUrl: String = s"http://$wireHost:$wirePort"
  protected def getUrl: String           = s"http://$wireHost:$wirePort"

  override def beforeAll(): Unit = {
    wireMockServer.start()
    WireMock.configureFor(wireHost, wirePort)
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    wireMockServer.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    wireMockServer.stop()
  }

  def readJsonFromFile(filePath: String): JsValue = {
    val source = Source.fromURL(getClass.getResource(filePath))
    val json   =
      try source.mkString
      finally source.close()
    Json.parse(replacePlaceholders(json))
  }
}
