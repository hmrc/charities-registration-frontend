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
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.checkEligibility.IsEligiblePurposePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.errors.{WeDeletedYourAnswersView, YouDeletedYourAnswersView}

import scala.concurrent.Future

class DeleteAnswersControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[IdentifierAction].to[FakeIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSessionRepository)
  }

  private val youDeletedAnswersview: YouDeletedYourAnswersView = injector.instanceOf[YouDeletedYourAnswersView]
  private val weDeletedAnswersview: WeDeletedYourAnswersView   = injector.instanceOf[WeDeletedYourAnswersView]

  private val controller: DeleteAnswersController = inject[DeleteAnswersController]

  "DeleteAnswersController Controller" when {

    "You Delete your answers view" must {

      "return OK and the correct view for a GET" in {

        when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))

        val result = controller.youDeletedAnswers()(fakeRequest)

        status(result) mustEqual OK
        contentAsString(result) mustEqual youDeletedAnswersview()(fakeRequest, messages, frontendAppConfig).toString
        verify(mockSessionRepository, times(1)).get(any())
        verify(mockSessionRepository, times(0)).delete(any())
      }

      "delete UserAnswers and return OK and the correct view for a GET when user has already answered" in {

        when(mockSessionRepository.get(any())).thenReturn(
          Future.successful(Some(emptyUserAnswers.set(IsEligiblePurposePage, true).getOrElse(emptyUserAnswers)))
        )

        val result = controller.youDeletedAnswers()(fakeRequest)

        status(result) mustEqual OK
        contentAsString(result) mustEqual youDeletedAnswersview()(fakeRequest, messages, frontendAppConfig).toString
        verify(mockSessionRepository, times(1)).get(any())
        verify(mockSessionRepository, times(1)).delete(any())
      }
    }

    "We Delete your answers view" must {

      "return OK and the correct view for a GET" in {

        when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))

        val result = controller.weDeletedYourAnswers()(fakeRequest)

        status(result) mustEqual OK
        contentAsString(result) mustEqual weDeletedAnswersview()(fakeRequest, messages, frontendAppConfig).toString
        verify(mockSessionRepository, times(1)).get(any())
        verify(mockSessionRepository, times(0)).delete(any())
      }

      "delete UserAnswers and return OK and the correct view for a GET when user has already answered" in {

        when(mockSessionRepository.get(any())).thenReturn(
          Future.successful(Some(emptyUserAnswers.set(IsEligiblePurposePage, true).getOrElse(emptyUserAnswers)))
        )

        val result = controller.weDeletedYourAnswers()(fakeRequest)

        status(result) mustEqual OK
        contentAsString(result) mustEqual weDeletedAnswersview()(fakeRequest, messages, frontendAppConfig).toString
        verify(mockSessionRepository, times(1)).get(any())
        verify(mockSessionRepository, times(1)).delete(any())
      }
    }
  }
}
