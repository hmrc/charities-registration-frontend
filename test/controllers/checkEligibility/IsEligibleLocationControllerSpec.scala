/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.checkEligibility

import base.SpecBase
import controllers.actions._
import forms.checkEligibility.IsEligibleLocationFormProvider
import models.{NormalMode, UserAnswers}
import navigation.EligibilityNavigator
import navigation.FakeNavigators.FakeEligibilityNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, _}
import org.scalatest.BeforeAndAfterEach
import pages.checkEligibility.IsEligibleLocationPage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.checkEligibility.IsEligibleLocationView

import scala.concurrent.Future

class IsEligibleLocationControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[EligibilityNavigator].toInstance(FakeEligibilityNavigator),
        bind[IdentifierAction].to[FakeIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSessionRepository)
  }

  private val view: IsEligibleLocationView                 = injector.instanceOf[IsEligibleLocationView]
  private val formProvider: IsEligibleLocationFormProvider = injector.instanceOf[IsEligibleLocationFormProvider]
  private val form: Form[Boolean]                          = formProvider()

  private val controller: IsEligibleLocationController = inject[IsEligibleLocationController]

  "IsEligibleLocation Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(fakeRequest, messages, frontendAppConfig).toString
      verify(mockSessionRepository, times(1)).get(any())
    }

    "return OK and the correct view for a GET when user has already answered" in {

      when(mockSessionRepository.get(any())).thenReturn(
        Future.successful(Some(emptyUserAnswers.set(IsEligibleLocationPage, true).getOrElse(emptyUserAnswers)))
      )

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), NormalMode)(
        fakeRequest,
        messages,
        frontendAppConfig
      ).toString
      verify(mockSessionRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockSessionRepository.upsert(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockSessionRepository, times(1)).get(any())
      verify(mockSessionRepository, times(1)).upsert(any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", ""))

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual BAD_REQUEST
      verify(mockSessionRepository, times(1)).get(any())
      verify(mockSessionRepository, never).upsert(any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockSessionRepository, times(1)).get(any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockSessionRepository, times(1)).get(any())
    }
  }
}
