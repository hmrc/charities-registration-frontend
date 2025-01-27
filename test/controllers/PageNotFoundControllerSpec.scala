/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers

import base.SpecBase
import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval}
import views.html.errors.PageNotFoundView

import scala.concurrent.Future

class PageNotFoundControllerSpec extends SpecBase with BeforeAndAfterEach {

  lazy val mockAuthConnector: AuthConnector = mock(classOf[AuthConnector])

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

  private val view: PageNotFoundView = inject[PageNotFoundView]

  private val controller: PageNotFoundController = inject[PageNotFoundController]

  "PageNotFound Controller" when {

    "onPageLoad" must {

      "return true if user is logged in and load correct view with no user action" in {

        when(
          mockAuthConnector.authorise[Option[Credentials]](any(), any[Retrieval[Option[Credentials]]]())(any(), any())
        )
          .thenReturn(Future.successful(Some(Credentials("valid", "org"))))

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(signedIn = true)(fakeRequest, messages, frontendAppConfig).toString
      }

      "return false if user is logged in and load correct view with no user action" in {

        when(
          mockAuthConnector.authorise[Option[Credentials]](any(), any[Retrieval[Option[Credentials]]]())(any(), any())
        )
          .thenReturn(Future.successful(None))

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(signedIn = false)(fakeRequest, messages, frontendAppConfig).toString
      }

      "return false if authorisation failed and load correct view with no user action" in {

        when(mockAuthConnector.authorise(any(), any())(any(), any()))
          .thenReturn(Future.failed(new RuntimeException("Exception")))

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(signedIn = false)(fakeRequest, messages, frontendAppConfig).toString
      }
    }

    "redirectToStartOfJourney" must {

      "return true if user is already logged in" in {

        when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
        when(
          mockAuthConnector.authorise[Option[Credentials]](any(), any[Retrieval[Option[Credentials]]]())(any(), any())
        )
          .thenReturn(Future.successful(Some(Credentials("valid", "org"))))
        val result = controller.redirectToStartOfJourney()(fakeRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.IndexController.onPageLoad(None).url)
      }

      "return false if user is not logged in" in {

        when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))
        when(
          mockAuthConnector.authorise[Option[Credentials]](any(), any[Retrieval[Option[Credentials]]]())(any(), any())
        ).thenReturn(Future.successful(None))

        val result = controller.redirectToStartOfJourney()(fakeRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(frontendAppConfig.signOutUrl)
      }

      "return false and authorisation failed" in {

        when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))
        when(mockAuthConnector.authorise(any(), any())(any(), any()))
          .thenReturn(Future.failed(new RuntimeException("Exception")))

        val result = controller.redirectToStartOfJourney()(fakeRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(frontendAppConfig.signOutUrl)
      }
    }
  }
}
