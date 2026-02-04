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

package utils

import common.TestData
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen, TestSuite, TryValues}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.POST
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}

trait IntegrationSpecBase
    extends AnyWordSpec
    with GivenWhenThen
    with TestSuite
    with ScalaFutures
    with IntegrationPatience
    with Matchers
    with WiremockHelper
    with GuiceOneServerPerSuite
    with TryValues
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with Eventually
    with CustomMatchers
    with TestData {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit val hc: HeaderCarrier    = HeaderCarrier()

  val defaultSeconds                           = 5
  implicit val defaultDuration: FiniteDuration = Duration.apply(defaultSeconds, SECONDS)

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build()

  def buildPost(url: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(POST, url)
      .withSession(SessionKeys.sessionId -> UUID.randomUUID().toString, SessionKeys.authToken -> SessionKeys.authToken)
      .withHeaders("Csrf-Token" -> "nocheck")

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: String = WiremockHelper.wiremockPort.toString

  def config: Map[String, String] = Map(
    "play.filters.csrf.header.bypassHeaders.Csrf-Token"  -> "nocheck",
    "microservice.services.auth.host"                    -> mockHost,
    "microservice.services.auth.port"                    -> mockPort,
    "microservice.services.address-lookup-frontend.host" -> mockHost,
    "microservice.services.address-lookup-frontend.port" -> mockPort,
    "microservice.services.charities.host"               -> mockHost,
    "microservice.services.charities.port"               -> mockPort
  )

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withSession("sessionId" -> "sessionId")

  lazy val dataFakeRequest: FakeRequest[AnyContentAsEmpty.type] = fakeRequest

  override def beforeEach(): Unit =
    resetWiremock()

  override def beforeAll(): Unit =
    startWiremock()

  override def afterAll(): Unit =
    stopWiremock()
}
