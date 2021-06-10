/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.operationsAndFunds

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import forms.operationsAndFunds.WhatCountryDoesTheCharityOperateInFormProvider
import models.{Country, Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeFundRaisingNavigator
import navigation.FundRaisingNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, _}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.operationsAndFunds.WhatCountryDoesTheCharityOperateInPage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.{CountryService, UserAnswerService}
import views.html.operationsAndFunds.WhatCountryDoesTheCharityOperateInView

import scala.concurrent.Future

class WhatCountryDoesTheCharityOperateInControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCountryService: CountryService = MockitoSugar.mock[CountryService]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[CountryService].toInstance(mockCountryService),
        bind[FundRaisingNavigator].toInstance(FakeFundRaisingNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService, mockCountryService)
  }

  private val view: WhatCountryDoesTheCharityOperateInView = inject[WhatCountryDoesTheCharityOperateInView]
  private val formProvider: WhatCountryDoesTheCharityOperateInFormProvider = inject[WhatCountryDoesTheCharityOperateInFormProvider]
  private val form: Form[String] = formProvider()

  private val controller: WhatCountryDoesTheCharityOperateInController = inject[WhatCountryDoesTheCharityOperateInController]

  "WhatCountryDoesTheCharityOperateIn Controller " must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("TH", "Thai")))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, Index(0), Seq(("TH", "Thai")), None)(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCountryService, never()).find(any())(any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "return OK and the correct view for a GET for Welsh" in {

      val welshRequest = FakeRequest().withCookies(Cookie(messagesApi.langCookieName, "cy"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers.set(
        WhatCountryDoesTheCharityOperateInPage(0), "TH").success.value)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("TH", "Thai")))
      when(mockCountryService.find(any())(any())).thenReturn(Some(Country("TH", "Thai")))
      when(mockCountryService.isWelsh(any())).thenReturn(true)

      val result = controller.onPageLoad(NormalMode, Index(0))(welshRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCountryService, times(1)).find(any())(any())
      verify(mockCountryService, times(1)).isWelsh(any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0) , "United Kingdom").success.value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("TH", "Thai")))
      when(mockCountryService.find(any())(any())).thenReturn(Some(Country("TH", "Thai")))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("country", "United Kingdom"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("TH", "Thai")))
      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, never).set(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
