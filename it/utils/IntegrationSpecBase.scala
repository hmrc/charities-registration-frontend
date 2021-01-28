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

package utils

import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{TryValues, _}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.{Application, Environment, Mode}
import service.UserAnswerService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait IntegrationSpecBase extends WordSpec with GivenWhenThen with TestSuite with ScalaFutures
  with IntegrationPatience with MustMatchers with WiremockHelper with GuiceOneServerPerSuite with TryValues
  with BeforeAndAfterEach with BeforeAndAfterAll with Eventually with CreateRequestHelper with CustomMatchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: String = WiremockHelper.wiremockPort.toString

  def config: Map[String, String] = Map(
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.address-lookup-frontend.host" -> mockHost,
    "microservice.services.address-lookup-frontend.port" -> mockPort,
    "microservice.services.charities.host" -> mockHost,
    "microservice.services.charities.port" -> mockPort
  )

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession("sessionId" -> "sessionId")

  lazy val dataFakeRequest: FakeRequest[AnyContentAsEmpty.type] = fakeRequest

  override def beforeEach(): Unit = {
    resetWiremock()
  }

  override def beforeAll(): Unit = {
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
  }
}
