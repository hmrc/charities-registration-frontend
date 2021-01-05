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
import forms.operationsAndFunds.OverseasOperatingLocationSummaryFormProvider
import models.{Country, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeFundRaisingNavigator
import navigation.FundRaisingNavigator
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{reset, verify, _}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.operationsAndFunds.{OverseasOperatingLocationSummaryPage, WhatCountryDoesTheCharityOperateInPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.UserAnswerRepository
import service.CountryService
import views.html.operationsAndFunds.OverseasOperatingLocationSummaryView

import scala.concurrent.Future

class OverseasOperatingLocationSummaryControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCountryService: CountryService = MockitoSugar.mock[CountryService]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[CountryService].toInstance(mockCountryService),
        bind[FundRaisingNavigator].toInstance(FakeFundRaisingNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  private val view: OverseasOperatingLocationSummaryView = inject[OverseasOperatingLocationSummaryView]
  private val formProvider: OverseasOperatingLocationSummaryFormProvider = inject[OverseasOperatingLocationSummaryFormProvider]
  private val form: Form[Boolean] = formProvider()

  private val controller: OverseasOperatingLocationSummaryController = inject[OverseasOperatingLocationSummaryController]

  "OverseasOperatingLocationSummary Controller " must {

    "return OK and the correct view for a GET" in {

      when(mockCountryService.find(meq("TT"))(any())).thenReturn(Some(Country("TT", "Testland")))

      when(mockUserAnswerRepository.get(any())).thenReturn(
        Future.successful(Some(emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0), "TT").success.value)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }


    "populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountryService.find(meq("TT"))(any())).thenReturn(Some(Country("TT", "Testland")))

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(OverseasOperatingLocationSummaryPage, true)
        .flatMap(_.set(WhatCountryDoesTheCharityOperateInPage(0), "TT")).getOrElse(emptyUserAnswers))))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the correct page if no country is in the UserAnswers" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(OverseasOperatingLocationSummaryPage, true).success.value)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())

    }

    "redirect to the next page when valid data is submitted" in {
      when(mockCountryService.find(meq("TT"))(any())).thenReturn(Some(Country("TT", "Testland")))

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockUserAnswerRepository.get(any())).thenReturn(
        Future.successful(Some(emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0), "TT").success.value)))

      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "redirect to the next page when valid data is submitted and overseas country limit is reached" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      val userAnswers = emptyUserAnswers.set(WhatCountryDoesTheCharityOperateInPage(0) ,"TH").flatMap(
        _.set(WhatCountryDoesTheCharityOperateInPage(1) ,"IN")).flatMap(
        _.set(WhatCountryDoesTheCharityOperateInPage(2) ,"PT")).flatMap(
        _.set(WhatCountryDoesTheCharityOperateInPage(3) ,"PY")).flatMap(
        _.set(WhatCountryDoesTheCharityOperateInPage(4) ,"AF")
      ).success.value

      when(mockCountryService.find(meq("TH"))(any())).thenReturn(Some(Country("TH", "Thai")))
      when(mockCountryService.find(meq("IN"))(any())).thenReturn(Some(Country("IN", "India")))
      when(mockCountryService.find(meq("PT"))(any())).thenReturn(Some(Country("PT", "Portugal")))
      when(mockCountryService.find(meq("PY"))(any())).thenReturn(Some(Country("PY", "Paraguay")))
      when(mockCountryService.find(meq("AF"))(any())).thenReturn(Some(Country("AF", "Afghanistan")))

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("TH", "Thai"),("IN", "INDIA"),
        ("PT", "Portugal"),("PY", "Paraguay"),("AF", "Afghanistan")))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, never).set(any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }
  }
}
