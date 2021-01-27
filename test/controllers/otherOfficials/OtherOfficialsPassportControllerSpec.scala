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

package controllers.otherOfficials

import java.time.LocalDate

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import forms.common.DateOfBirthFormProvider
import models.{Index, Name, NormalMode, Passport, SelectTitle, UserAnswers}
import navigation.FakeNavigators.FakeOtherOfficialsNavigator
import navigation.OtherOfficialsNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, _}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.otherOfficials.{OtherOfficialsNamePage, OtherOfficialsPassportPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.{CountryService, UserAnswerService}
import views.html.common.PassportView

import scala.concurrent.Future

class OtherOfficialsPassportControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCountryService: CountryService = MockitoSugar.mock[CountryService]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[CountryService].toInstance(mockCountryService),
        bind[OtherOfficialsNavigator].toInstance(FakeOtherOfficialsNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService, mockCountryService)
  }

  private val messageKeyPrefix = "otherOfficialsPassport"
  private val view: PassportView = inject[PassportView]
  private val formProvider: DateOfBirthFormProvider = inject[DateOfBirthFormProvider]
  private val form: Form[LocalDate] = formProvider(messageKeyPrefix)

  private val controller: OtherOfficialsPassportController = inject[OtherOfficialsPassportController]

  private val futureDate: LocalDate = LocalDate.now().plusDays(1)

  private val requestArgs = Seq("passportNumber" -> "123", "country" -> "United Kingdom",
    "expiryDate.year" -> futureDate.getYear.toString,
    "expiryDate.month" -> futureDate.getMonthValue.toString,
    "expiryDate.day" -> futureDate.getDayOfMonth.toString)
  private val localUserAnswers: UserAnswers = emptyUserAnswers.set(OtherOfficialsNamePage(0),
    Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")).success.value


  "OtherOfficialsPassportController Controller " must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onPageLoad(NormalMode,Index(0))(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "Jim John Jones", messageKeyPrefix,
        controllers.otherOfficials.routes.OtherOfficialsPassportController.onSubmit(NormalMode, Index(0)),
        Seq(("GB", "United Kingdom")))(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = localUserAnswers.set(OtherOfficialsPassportPage(0),
        Passport("hello", "gb", LocalDate.now.plusDays(1))).success.value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(requestArgs :_*)

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
      verify(mockCountryService, never).countries()(any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
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
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
