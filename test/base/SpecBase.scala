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

package base

import config.FrontendAppConfig
import controllers.actions._
import models.UserAnswers
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.mockito.MockitoSugar
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{EitherValues, OptionValues, TryValues}
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
import repositories.SessionRepository
import service.UserAnswerService
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import viewmodels.ErrorHandler

import java.time.temporal.ChronoUnit
import scala.concurrent.duration.{Duration, FiniteDuration, _}
import scala.concurrent.{Await, ExecutionContext, Future}

trait SpecBase
    extends PlaySpec
    with GuiceOneAppPerSuite
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with MaterializerSupport
    with Injecting
    with OptionValues
    with EitherValues {

  lazy val injector: Injector                   = app.injector
  lazy val internalId: String                   = "id"
  lazy val baseInternalUserAnswers: UserAnswers = UserAnswers(internalId, Json.obj())
  lazy val emptyUserAnswers: UserAnswers        = baseInternalUserAnswers.copy(
    lastUpdated = baseInternalUserAnswers.lastUpdated.truncatedTo(ChronoUnit.MILLIS),
    expiresAt = baseInternalUserAnswers.expiresAt.truncatedTo(ChronoUnit.MILLIS)
  )
  lazy val userAnswers: Option[UserAnswers]     = None

  lazy val messagesControllerComponents: MessagesControllerComponents =
    injector.instanceOf[MessagesControllerComponents]
  lazy val dataRequiredAction: DataRequiredActionImpl                 = injector.instanceOf[DataRequiredActionImpl]
  lazy val mockSessionRepository: SessionRepository                   = MockitoSugar.mock[SessionRepository]
  lazy val mockUserAnswerService: UserAnswerService                   = MockitoSugar.mock[UserAnswerService]

  implicit val defaultTimeout: FiniteDuration       = 5.seconds
  implicit val frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  implicit val ec: ExecutionContext                 = injector.instanceOf[ExecutionContext]
  implicit val messagesApi: MessagesApi             = injector.instanceOf[MessagesApi]
  implicit val messages: Messages                   = messagesApi.preferred(fakeRequest)
  implicit val errorHandler: ErrorHandler           = injector.instanceOf[ErrorHandler]
  implicit val hc: HeaderCarrier                    = HeaderCarrier()

  //with session id
  lazy val fakeSessionId                                        = "this must be 50 chars long and so on and so forth!"
  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type]     = FakeRequest("", "")
    .withSession(SessionKeys.sessionId -> fakeSessionId)
    .withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  lazy val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(fakeRequest, internalId, emptyUserAnswers)

  //without session id
  lazy val fakeRequestNoSessionId: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val fakeDataRequestNoSessionId: DataRequest[AnyContentAsEmpty.type] =
    DataRequest(fakeRequestNoSessionId, internalId, emptyUserAnswers)

  //with empty session id
  lazy val fakeRequestEmptySessionId: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
    .withSession(SessionKeys.sessionId -> "")
    .withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val fakeDataRequestEmptySessionId: DataRequest[AnyContentAsEmpty.type] =
    DataRequest(fakeRequestEmptySessionId, internalId, emptyUserAnswers)

  //with less length than required session id
  lazy val fakeRequestShortSessionId: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
    .withSession(SessionKeys.sessionId -> "short id here not 50 chars")
    .withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val fakeDataRequestShortSessionId: DataRequest[AnyContentAsEmpty.type] =
    DataRequest(fakeRequestShortSessionId, internalId, emptyUserAnswers)

  //with more length than required session id
  lazy val fakeRequestTooLongSessionId: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
    .withSession(SessionKeys.sessionId -> "short id here not 50 chars" * 10)
    .withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val fakeDataRequestTooLongSessionId: DataRequest[AnyContentAsEmpty.type] =
    DataRequest(fakeRequestTooLongSessionId, internalId, emptyUserAnswers)

  def onwardRoute: Call = Call("GET", "/foo")

  def fakeDataRequest(headers: (String, String)*): DataRequest[_] =
    DataRequest(fakeRequest.withHeaders(headers: _*), internalId, emptyUserAnswers)

  def fakeDataRequest(userAnswers: UserAnswers): DataRequest[_] = DataRequest(fakeRequest, internalId, userAnswers)

  def await[A](future: Future[A])(implicit timeout: Duration): A = Await.result(future, timeout)

  def title(heading: String, section: Option[String] = None)(implicit messages: Messages): String =
    s"$heading - ${section.fold("")(_ + " - ")}${messages("service.name")} - ${messages("site.govuk")}"

  def titleOf(result: String): String = Jsoup.parse(result).title

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[UserDataRetrievalAction].toInstance(new FakeUserDataRetrievalAction(userAnswers))
      )

  override lazy val app: Application = applicationBuilder().build()
}
