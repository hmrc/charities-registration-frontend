/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.common.YesNoFormProvider
import models.{Country, Index, NormalMode, UserAnswers}
import navigation.FundRaisingNavigator
import navigation.FakeNavigators.FakeFundRaisingNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.mockito.MockitoSugar
import pages.operationsAndFunds.WhatCountryDoesTheCharityOperateInPage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.{CountryService, UserAnswerService}
import views.html.common.YesNoView

import scala.concurrent.Future

class IsRemoveOperatingCountryControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCountryService: CountryService        = MockitoSugar.mock[CountryService]

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
    reset(mockUserAnswerService)
  }

  private val messageKeyPrefix: String        = "isRemoveOperatingCountry"
  private val view: YesNoView                 = injector.instanceOf[YesNoView]
  private val formProvider: YesNoFormProvider = injector.instanceOf[YesNoFormProvider]
  private val form: Form[Boolean]             = formProvider(messageKeyPrefix)

  private val controller: IsRemoveOperatingCountryController = inject[IsRemoveOperatingCountryController]

  private val localUserAnswers: UserAnswers =
    emptyUserAnswers
      .set(WhatCountryDoesTheCharityOperateInPage(0), "XX")
      .success
      .value

  "IsRemoveOperatingCountry Controller" must {

    "redirect to correct start page is if country details are not present" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustEqual SEE_OTHER

    }

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockCountryService.find(any())(any())).thenReturn(Some(Country("XX", "CountryCountry")))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form,
        "CountryCountry",
        messageKeyPrefix,
        controllers.operationsAndFunds.routes.IsRemoveOperatingCountryController.onSubmit(NormalMode, Index(0)),
        "operationsAndFunds",
        Seq("CountryCountry")
      )(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())

    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))
      when(mockCountryService.find(any())(any())).thenReturn(Some(Country("XX", "CountryCountry")))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockCountryService.find(any())(any())).thenReturn(Some(Country("XX", "CountryCountry")))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, never).set(any())(any(), any())

    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockCountryService.find(any())(any())).thenReturn(Some(Country("XX", "CountryCountry")))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())

    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockCountryService.find(any())(any())).thenReturn(Some(Country("XX", "CountryCountry")))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())

    }

    "redirect to Session Expired for a POST if the country has no data for it" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockCountryService.find(any())(any())).thenReturn(Some(Country("XX", "CountryCountry")))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())

    }
  }
}
