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

package controllers.eligibility

import base.SpecBase
import controllers.actions._
import forms.eligibility.IsEligibleLocationOtherFormProvider
import models.NormalMode
import navigation.EligibilityNavigator
import navigation.FakeNavigators.FakeEligibilityNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, _}
import org.scalatest.BeforeAndAfterEach
import pages.eligibility.IsEligibleLocationOtherPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.SessionRepositoryImpl
import views.html.eligibility.IsEligibleLocationOtherView

import scala.concurrent.Future

class IsEligibleLocationOtherControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepositoryImpl].toInstance(mockSessionRepository),
        bind[EligibilityNavigator].toInstance(FakeEligibilityNavigator),
        bind[IdentifierAction].to[FakeIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSessionRepository)
  }

  val view: IsEligibleLocationOtherView = injector.instanceOf[IsEligibleLocationOtherView]
  val formProvider: IsEligibleLocationOtherFormProvider = injector.instanceOf[IsEligibleLocationOtherFormProvider]
  val form = formProvider()

  val controller: IsEligibleLocationOtherController = inject[IsEligibleLocationOtherController]

  "IsEligibleLocationOther Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(fakeRequest, messages, frontendAppConfig).toString
      verify(mockSessionRepository, times(1)).get(any())
    }

    "return OK and the correct view for a GET when user has already answered" in {

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(
        emptyUserAnswers.set(IsEligibleLocationOtherPage, true).getOrElse(emptyUserAnswers))))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), NormalMode)(fakeRequest, messages, frontendAppConfig).toString
      verify(mockSessionRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockSessionRepository, times(1)).get(any())
      verify(mockSessionRepository, times(1)).set(any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", ""))

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual BAD_REQUEST
      verify(mockSessionRepository, times(1)).get(any())
      verify(mockSessionRepository, never).set(any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      verify(mockSessionRepository, times(1)).get(any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      verify(mockSessionRepository, times(1)).get(any())
    }
  }
}
