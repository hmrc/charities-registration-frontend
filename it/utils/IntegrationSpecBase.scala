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

package utils

import java.util.concurrent.TimeUnit

import stubs.AuthStub
import models.UserAnswers
import models.requests.DataRequest
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{TryValues, _}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import pages.QuestionPage
import play.api.i18n.MessagesApi
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{Json, Writes}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.{Application, Environment, Mode}
import repositories.UserAnswerRepository

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

trait IntegrationSpecBase extends WordSpec with GivenWhenThen with TestSuite with ScalaFutures
  with IntegrationPatience with MustMatchers with WiremockHelper with GuiceOneServerPerSuite with TryValues
  with BeforeAndAfterEach with BeforeAndAfterAll with Eventually with CreateRequestHelper with CustomMatchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

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

  lazy val mongo: UserAnswerRepository = app.injector.instanceOf[UserAnswerRepository]

  val emptyUserAnswers: UserAnswers = UserAnswers(s"providerId", Json.obj())

  implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession("sessionId" -> "sessionId")

  lazy val dataFakeRequest: FakeRequest[AnyContentAsEmpty.type] = fakeRequest

  def setAnswers(userAnswers: UserAnswers)(implicit timeout: Duration): Unit = Await.result(mongo.set(userAnswers), timeout)

  def getAnswers(id: String)(implicit timeout: Duration): Option[UserAnswers] = Await.result(mongo.get(id), timeout)

  def setAnswers[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A], timeout: Duration): Unit =
    setAnswers(emptyUserAnswers.set(page, value).success.value)

  override def beforeEach(): Unit = {
    resetWiremock()
    getAnswers("providerId").map {userAnswers=>
      Await.result(mongo.delete(userAnswers), Duration.Inf)
      Await.result(mongo.set(UserAnswers("providerId")), Duration.Inf)
    }
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }
}
