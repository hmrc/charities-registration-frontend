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

package controllers.auth

import config.AppConfig
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.{Action, AnyContent, Controller, MessagesControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import play.mvc.Http.Status
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.retrieve.Credentials
import uk.gov.hmrc.auth.core.{SessionRecordNotFound, UnsupportedAffinityGroup}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterEach with MockitoSugar with Injecting {

  val mockAuthConnector = mock[AuthConnector]
  implicit val executionContext: ExecutionContext = ExecutionContext.global
  lazy val appConfig = app.injector.instanceOf[AppConfig]
  lazy val mockMessagesController: MessagesControllerComponents = inject[MessagesControllerComponents]

  class Harness(authAction: AuthAction) extends Controller {
    def onPageLoad(): Action[AnyContent] = authAction { _ =>
      Ok
    }
  }

  type AuthRetrievals = Option[Credentials]

  val fakeCredentials = Credentials("foo", "bar")
  val fakeAffinityGroup = Organisation

  "A user with no active session" should {
    "be redirected to the charities start page" in {
      when(mockAuthConnector.authorise(any(), any())(any(), any()))
        .thenReturn(Future.failed(SessionRecordNotFound()))

      val authAction = new AuthActionImpl(mockAuthConnector, appConfig, mockMessagesController, executionContext)
      val harness = new Harness(authAction)
      val result = harness.onPageLoad()(FakeRequest("GET", "/eligible-account"))

      status(result) shouldBe Status.SEE_OTHER
      result.header.headers("Location") shouldBe "http://localhost:9025/gg/sign-in?continue=http%3A%2F%2Flocalhost%3A9457%2Fhmrc-register-charity-registration-details%2Fcontact-details&origin=charities-registration-frontend"
    }
  }

  "A user without an organisation affinity group" should {
    "redirect to register an organisation page" in {
      when(mockAuthConnector.authorise(any(), any())(any(), any()))
        .thenReturn(Future.failed(UnsupportedAffinityGroup()))

      val authAction = new AuthActionImpl(mockAuthConnector, appConfig, mockMessagesController, executionContext)
      val harness = new Harness(authAction)
      val result = harness.onPageLoad()(FakeRequest("GET", "/eligible-account"))

      status(result) shouldBe Status.SEE_OTHER
      result.header.headers("Location") shouldBe controllers.routes.CharitiesRegisterOrganisationController.onPageLoad().url
      //redirectLocation(result).get `must-revalidate` endWith("/hmrc-register-charity-details/register-as-an-organisation")
    }
  }

  "A user with organisation enrolment" should {
    "create an authenticated request" in {

      val retrievalResult: Future[AuthRetrievals] = Future.successful(Some(fakeCredentials))

      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any()))
        .thenReturn(retrievalResult)

      val authAction = new AuthActionImpl(mockAuthConnector, appConfig, mockMessagesController, executionContext)
      val harness = new Harness(authAction)
      val result = harness.onPageLoad()(FakeRequest("GET", "/hello-world"))

      status(result) shouldBe Status.OK
    }
  }
}
