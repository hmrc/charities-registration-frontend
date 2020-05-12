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

package controllers

import base.SpecBase
import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.UserAnswerRepositoryImpl
import views.html.errors.SessionExpiredView

import scala.concurrent.Future

class SessionExpiredControllerSpec extends SpecBase with BeforeAndAfterEach {

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepositoryImpl].toInstance(mockUserAnswerRepository),
        bind[IdentifierAction].to[FakeIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  val view: SessionExpiredView = inject[SessionExpiredView]

  val controller = inject[SessionExpiredController]

  "SessionExpired Controller" when {

    "onPageLoad" must {

      "return OK and the correct view with no user action" in {

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view()(fakeRequest, messages, frontendAppConfig).toString
      }
    }

    "keepalive" must {

      "return OK and the correct view with user action" in {

        when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

        val result = controller.keepalive()(fakeRequest)

        status(result) mustEqual NO_CONTENT

      }

      "return OK and the correct view with No user action" in {

        when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

        val result = controller.keepalive()(fakeRequest)

        status(result) mustEqual NO_CONTENT

      }
    }
  }
}
