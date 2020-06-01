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

package base

import config.FrontendAppConfig
import controllers.actions._
import models.UserAnswers
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.scalatest.TryValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Call, MessagesControllerComponents}
import play.api.test.CSRFTokenHelper._
import play.api.test.{FakeRequest, Injecting}
import repositories.{SessionRepository, SessionRepositoryImpl, UserAnswerRepository, UserAnswerRepositoryImpl}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import viewmodels.ErrorHandler

import scala.concurrent.duration.{Duration, FiniteDuration, _}
import scala.concurrent.{Await, ExecutionContext, Future}

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite with TryValues with ScalaFutures with IntegrationPatience with MaterializerSupport with Injecting {

  def onwardRoute = Call("GET", "/foo")

  val internalId = "id"

  val emptyUserAnswers = UserAnswers(internalId, Json.obj())
  lazy val userAnswers: Option[UserAnswers] = None

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(
    SessionKeys.sessionId -> "foo").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  def fakeDataRequest(headers: (String, String)*): DataRequest[_] = DataRequest(fakeRequest.withHeaders(headers:_*), internalId, emptyUserAnswers)

  lazy val fakeDataRequest = DataRequest(fakeRequest, internalId, emptyUserAnswers)

  def fakeDataRequest(userAnswers: UserAnswers): DataRequest[_] = DataRequest(fakeRequest, internalId, userAnswers)

  implicit val defaultTimeout: FiniteDuration = 5.seconds
  def await[A](future: Future[A])(implicit timeout: Duration): A = Await.result(future, timeout)

  def title(heading: String, section: Option[String] = None)(implicit messages: Messages) =
    s"$heading - ${section.fold("")(_ + " - ")}${messages("service.name")} - ${messages("site.govuk")}"

  def titleOf(result: String): String = Jsoup.parse(result).title

  lazy val injector: Injector = app.injector

  implicit lazy val frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]

  lazy val messagesControllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  implicit lazy val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  lazy val dataRequiredAction: DataRequiredActionImpl = injector.instanceOf[DataRequiredActionImpl]

  implicit val hc: HeaderCarrier = HeaderCarrier()

  lazy val mockSessionRepository: SessionRepositoryImpl = MockitoSugar.mock[SessionRepositoryImpl]
  lazy val mockUserAnswerRepository: UserAnswerRepositoryImpl = MockitoSugar.mock[UserAnswerRepositoryImpl]

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[UserDataRetrievalAction].toInstance(new FakeUserDataRetrievalAction(userAnswers))
      )

  override lazy val app: Application = applicationBuilder().build()
}