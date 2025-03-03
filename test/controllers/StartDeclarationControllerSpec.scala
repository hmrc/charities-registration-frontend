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
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.sections._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.UserAnswerService
import views.html.StartDeclarationView

import scala.concurrent.Future

class StartDeclarationControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
  }

  private val view: StartDeclarationView = injector.instanceOf[StartDeclarationView]

  private val controller: StartDeclarationController = inject[StartDeclarationController]

  "StartDeclaration Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
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
        )
      )

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
