/*
 * Copyright 2025 HM Revenue & Customs
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
import forms.nominees.OrganisationNomineeContactDetailsFormProvider
import models.nominees.OrganisationNomineeContactDetails
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNomineesNavigator
import navigation.NomineesNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.nominees.{OrganisationNomineeContactDetailsPage, OrganisationNomineeNamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.UserAnswerService
import views.html.nominees.OrganisationNomineeContactDetailsView

import scala.concurrent.Future

class OrganisationNomineeContactDetailsControllerSpec extends SpecBase with BeforeAndAfterEach {

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

  private val view: OrganisationNomineeContactDetailsView                 = injector.instanceOf[OrganisationNomineeContactDetailsView]
  private val formProvider: OrganisationNomineeContactDetailsFormProvider =
    injector.instanceOf[OrganisationNomineeContactDetailsFormProvider]
  private val form: Form[OrganisationNomineeContactDetails]               = formProvider()

  private val controller: OrganisationNomineeContactDetailsController =
    inject[OrganisationNomineeContactDetailsController]

  private val requestArgs = Seq("phoneNumber" -> "0123123123", "email" -> "test@email.com")

  private val company = "TestCompany"

  private val localUserAnswers: UserAnswers = emptyUserAnswers.set(OrganisationNomineeNamePage, company).success.value

  "OrganisationNomineeContactDetailsController Spec" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, company, NormalMode)(
        fakeRequest,
        messages,
        frontendAppConfig
      ).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = localUserAnswers
        .set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "test@email.com"))
        .success
        .value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(requestArgs: _*)

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
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
      verify(mockUserAnswerService, never).set(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(requestArgs: _*)

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, never).set(any())(any(), any())
    }
  }
}
