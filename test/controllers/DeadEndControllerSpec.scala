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
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import connectors.CharitiesConnector
import views.html.DeadEndView

import scala.concurrent.Future

class DeadEndControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[CharitiesConnector].toInstance(mockCharitiesConnector),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCharitiesConnector)
  }

  private val view: DeadEndView = injector.instanceOf[DeadEndView]

  private val controller: DeadEndController = inject[DeadEndController]

  "DeadEnd Controller" must {

    "return OK and the correct view for a GET" in {
      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(emptyUserAnswers))))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view()(fakeRequest, messages, frontendAppConfig).toString
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(None)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

  }
}
