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

package controllers.otherOfficials

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import forms.common.PhoneNumberFormProvider
import models.{Index, Name, NormalMode, PhoneNumber, SelectTitle, UserAnswers}
import navigation.FakeNavigators.FakeOtherOfficialsNavigator
import navigation.OtherOfficialsNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, _}
import org.scalatest.BeforeAndAfterEach
import pages.otherOfficials.{OtherOfficialsNamePage, OtherOfficialsPhoneNumberPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.UserAnswerRepository
import views.html.common.PhoneNumberView

import scala.concurrent.Future

class OtherOfficialsPhoneNumberControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[OtherOfficialsNavigator].toInstance(FakeOtherOfficialsNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  private val messageKeyPrefix = "otherOfficialsPhoneNumber"
  private val view: PhoneNumberView = inject[PhoneNumberView]
  private val formProvider: PhoneNumberFormProvider = inject[PhoneNumberFormProvider]
  private val form: Form[PhoneNumber] = formProvider(messageKeyPrefix)

  private val controller: OtherOfficialsPhoneNumberController = inject[OtherOfficialsPhoneNumberController]

  private val requestArgs = Seq("mainPhoneNumber" -> "07700 900 982","alternativePhoneNumber"->"07700 900 982")
  private val localUserAnswers: UserAnswers = emptyUserAnswers.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")).success.value


  "OtherOfficialsPhoneNumberController Controller " must {

    "return OK and the correct view for a GET" in {
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(localUserAnswers)))

      val result = controller.onPageLoad(NormalMode,Index(0))(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "Jim John Jones", messageKeyPrefix,
        controllers.otherOfficials.routes.OtherOfficialsPhoneNumberController.onSubmit(NormalMode, Index(0)))(
        fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = localUserAnswers.set(OtherOfficialsPhoneNumberPage(0),
        PhoneNumber("07700 900 982", Some("07700 900 982"))).success.value

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = localUserAnswers.set(OtherOfficialsPhoneNumberPage(0),
        PhoneNumber("07700 900 982", Some("07700 900 982"))).success.value

      val request = fakeRequest.withFormUrlEncodedBody(requestArgs :_*)

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(localUserAnswers)))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, never).set(any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode, Index(0))(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode, Index(0))(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }
  }
}
