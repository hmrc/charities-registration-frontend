/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.common.PhoneNumberFormProvider
import models.{Name, NormalMode, PhoneNumber, SelectTitle, UserAnswers}
import navigation.FakeNavigators.FakeNomineesNavigator
import navigation.NomineesNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.nominees.{IndividualNomineeNamePage, IndividualNomineesPhoneNumberPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.UserAnswerService
import views.html.common.PhoneNumberView

import scala.concurrent.Future

class IndividualNomineePhoneNumberControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[NomineesNavigator].toInstance(FakeNomineesNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
  }

  private val messageKeyPrefix                      = "individualNomineesPhoneNumber"
  private val view: PhoneNumberView                 = inject[PhoneNumberView]
  private val formProvider: PhoneNumberFormProvider = inject[PhoneNumberFormProvider]
  private val form: Form[PhoneNumber]               = formProvider(messageKeyPrefix)

  private val controller: IndividualNomineesPhoneNumberController = inject[IndividualNomineesPhoneNumberController]

  private val requestArgs                   = Seq("mainPhoneNumber" -> "07700 900 982", "alternativePhoneNumber" -> "07700 900 982")
  private val localUserAnswers: UserAnswers =
    emptyUserAnswers.set(IndividualNomineeNamePage, Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")).success.value

  "IndividualNomineesPhoneNumberController " must {

    "return OK and the correct view for a GET" in {
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form,
        "Jim John Jones",
        messageKeyPrefix,
        controllers.nominees.routes.IndividualNomineesPhoneNumberController.onSubmit(NormalMode)
      )(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = localUserAnswers
        .set(IndividualNomineesPhoneNumberPage, PhoneNumber("07700 900 982", Some("07700 900 982")))
        .success
        .value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = localUserAnswers
        .set(IndividualNomineesPhoneNumberPage, PhoneNumber("07700 900 982", Some("07700 900 982")))
        .success
        .value

      val request = fakeRequest.withFormUrlEncodedBody(requestArgs: _*)

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))

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
