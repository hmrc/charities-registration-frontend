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

package controllers.nominees

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.UserAnswers
import navigation.FakeNavigators.FakeNomineesNavigator
import navigation.NomineesNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, _}
import org.scalatest.BeforeAndAfterEach
import pages.nominees.ChooseNomineePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{redirectLocation, status, _}
import repositories.{SessionRepository, UserAnswerRepository}

import scala.concurrent.Future

class NomineeDetailsSummaryControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[NomineesNavigator].toInstance(FakeNomineesNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  private val controller: NomineeDetailsSummaryController = inject[NomineeDetailsSummaryController]

  "NomineeDetailsSummaryController Controller" must {

    "redirect to index page no rows are defined" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "return OK and the correct view for a GET when Nominee is present" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(
        emptyUserAnswers.set(ChooseNomineePage, true).success.value)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "return OK and the correct view for a GET when Nominee is not present" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(
        emptyUserAnswers.set(ChooseNomineePage, false).success.value)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted with some pages answered if isExternalTest is true" in {

      val app =
        new GuiceApplicationBuilder().configure("features.isExternalTest" -> "true")
          .overrides(
            bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[NomineesNavigator].toInstance(FakeNomineesNavigator),
            bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
          ).build()

      val controller: NomineeDetailsSummaryController = app.injector.instanceOf[NomineeDetailsSummaryController]

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }
  }
}
