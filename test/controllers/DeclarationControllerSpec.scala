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

package controllers

import base.SpecBase
import connectors.CharitiesConnector
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.{RegistrationResponse, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import pages.sections.*
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.*
import service.UserAnswerService
import views.html.DeclarationView

import scala.concurrent.Future

class DeclarationControllerSpec extends SpecBase with BeforeAndAfterEach {

  lazy val mockCharitiesConnector: CharitiesConnector =
    mock(classOf[CharitiesConnector])

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[CharitiesConnector].toInstance(mockCharitiesConnector),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
    reset(mockCharitiesConnector)
  }

  private val view: DeclarationView = injector.instanceOf[DeclarationView]

  private val controller: DeclarationController = inject[DeclarationController]

  val localUserAnswers: Option[UserAnswers] = Some(
    emptyUserAnswers
      .set(Section1Page, true)
      .flatMap(_.set(Section2Page, true))
      .flatMap(_.set(Section3Page, true))
      .flatMap(_.set(Section4Page, true))
      .flatMap(_.set(Section5Page, true))
      .flatMap(_.set(Section6Page, true))
      .flatMap(_.set(Section7Page, true))
      .flatMap(_.set(Section8Page, true))
      .flatMap(_.set(Section9Page, true))
      .success
      .value
  )

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(localUserAnswers))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view()(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the next page after valid transformation" in {
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockCharitiesConnector.registerCharities(any())(any(), any())).thenReturn(
        Future.successful(Right(RegistrationResponse("ackRef")))
      )

      val result = controller.onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCharitiesConnector, times(1)).registerCharities(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCharitiesConnector, never()).registerCharities(any())(any(), any())
    }

    "redirect to Tasklist for a GET if SectionPage is not completed" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(Section1Page, false)
              .flatMap(_.set(Section2Page, true))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.IndexController.onPageLoad(None).url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
