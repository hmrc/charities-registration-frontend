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

package controllers.operationsAndFunds

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeFundRaisingNavigator
import navigation.FundRaisingNavigator
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.operationsAndFunds.{OverseasOperatingLocationSummaryPage, WhatCountryDoesTheCharityOperateInPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.{CountryService, UserAnswerService}

import scala.concurrent.Future

class OverseasOperatingLocationSummaryControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCountryService: CountryService        = mock(classOf[CountryService])

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

  private val controller: OverseasOperatingLocationSummaryController =
    inject[OverseasOperatingLocationSummaryController]

  "OverseasOperatingLocationSummary Controller " must {

    "return OK and the correct view for a GET" in {

      when(mockCountryService.find(meq(thCountryCode))(any())).thenReturn(Some(thCountry))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0), thCountryCode).success.value)
        )
      )

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountryService.find(meq(thCountryCode))(any())).thenReturn(Some(thCountry))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OverseasOperatingLocationSummaryPage, true)
              .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), thCountryCode))
              .getOrElse(emptyUserAnswers)
          )
        )
      )

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the correct page if no country is in the UserAnswers" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(Some(emptyUserAnswers.set(OverseasOperatingLocationSummaryPage, true).success.value))
      )

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())

    }

    "redirect to the next page when valid data is submitted" in {
      when(mockCountryService.find(meq(thCountryCode))(any())).thenReturn(Some(thCountry))

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0), thCountryCode).success.value)
        )
      )

      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted and overseas country limit is reached" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      val userAnswers = emptyUserAnswers
        .set(WhatCountryDoesTheCharityOperateInPage(0), thCountryCode)
        .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(1), inCountryCode))
        .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(2), thCountryCode))
        .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(3), frCountryCode))
        .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(4), chCountryCode))
        .success
        .value

      when(mockCountryService.find(meq(thCountryCode))(any())).thenReturn(Some(thCountry))
      when(mockCountryService.find(meq(inCountryCode))(any())).thenReturn(Some(inCountry))
      when(mockCountryService.find(meq(frCountryCode))(any())).thenReturn(Some(frCountry))
      when(mockCountryService.find(meq(usCountryCode))(any())).thenReturn(Some(usCountry))
      when(mockCountryService.find(meq(chCountryCode))(any())).thenReturn(Some(chCountry))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))
      when(mockCountryService.countries()(any()))
        .thenReturn(Seq(thCountryTuple, inCountryTuple, frCountryTuple, usCountryTuple, chCountryTuple))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, never).set(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
