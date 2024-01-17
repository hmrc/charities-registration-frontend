/*
 * Copyright 2024 HM Revenue & Customs
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

package viewModels

import base.SpecBase
import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval}
import views.html.errors.{PageNotFoundView, TechnicalDifficultiesErrorView}

import scala.concurrent.Future

class ErrorHandlerSpec extends SpecBase with BeforeAndAfterEach {

  lazy val mockAuthConnector: AuthConnector = MockitoSugar.mock[AuthConnector]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[AuthConnector].toInstance(mockAuthConnector),
        bind[IdentifierAction].to[FakeIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSessionRepository)
    reset(mockAuthConnector)
  }

  private implicit val request: FakeRequest[_] = fakeRequest

  val technicalDifficultiesErrorView: TechnicalDifficultiesErrorView = inject[TechnicalDifficultiesErrorView]
  val pageNotFoundView: PageNotFoundView                             = inject[PageNotFoundView]

  "ErrorHandler" must {

    "standardErrorTemplate" should {

      "return the view with correct messages" in {

        errorHandler.standardErrorTemplate("", "", "test") mustBe
          technicalDifficultiesErrorView("", "", "")(request, messages, frontendAppConfig)
      }
    }

    "notFoundTemplate" should {

      "return true if user is logged in and load correct view with no user action" in {

        when(
          mockAuthConnector.authorise[Option[Credentials]](any(), any[Retrieval[Option[Credentials]]]())(any(), any())
        )
          .thenReturn(Future.successful(Some(Credentials("valid", "org"))))

        errorHandler.notFoundTemplate mustBe
          pageNotFoundView(signedIn = true)(request, messages, frontendAppConfig)
      }

      "return false if user is logged in and load correct view with no user action" in {

        when(
          mockAuthConnector.authorise[Option[Credentials]](any(), any[Retrieval[Option[Credentials]]]())(any(), any())
        )
          .thenReturn(Future.successful(None))

        errorHandler.notFoundTemplate mustBe
          pageNotFoundView(signedIn = false)(request, messages, frontendAppConfig)
      }

      "return false if authorisation failed and load correct view with no user action" in {

        when(mockAuthConnector.authorise(any(), any())(any(), any()))
          .thenReturn(Future.failed(new RuntimeException("Exception")))

        errorHandler.notFoundTemplate mustBe
          pageNotFoundView(signedIn = false)(request, messages, frontendAppConfig)
      }
    }
  }

}
