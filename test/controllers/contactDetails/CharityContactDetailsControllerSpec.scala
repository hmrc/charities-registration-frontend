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

package controllers.contactDetails

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import forms.contactDetails.CharityContactDetailsFormProvider
import models.{CharityContactDetails, NormalMode, UserAnswers}
import navigation.CharityInformationNavigator
import navigation.FakeNavigators.FakeCharityInformationNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.contactDetails.CharityContactDetailsPage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import connectors.CharitiesConnector
import views.html.contactDetails.CharityContactDetailsView

import scala.concurrent.Future

class CharityContactDetailsControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[CharitiesConnector].toInstance(mockCharitiesConnector),
        bind[CharityInformationNavigator].toInstance(FakeCharityInformationNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCharitiesConnector)
  }

  private val view: CharityContactDetailsView                 = injector.instanceOf[CharityContactDetailsView]
  private val formProvider: CharityContactDetailsFormProvider = injector.instanceOf[CharityContactDetailsFormProvider]
  private val form: Form[CharityContactDetails]               = formProvider()

  private val controller: CharityContactDetailsController = inject[CharityContactDetailsController]

  private val requestArgs = Seq(
    "mainPhoneNumber"        -> daytimePhone,
    "alternativePhoneNumber" -> daytimePhone,
    "emailAddress"           -> charityEmail
  )

  "CharityContactDetails Controller " must {

    "return OK and the correct view for a GET" in {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(emptyUserAnswers))))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(fakeRequest, messages, frontendAppConfig).toString
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(CharityContactDetailsPage, charityContactDetails)
        .success
        .value

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(userAnswers))))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(requestArgs*)

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(emptyUserAnswers))))
      when(mockCharitiesConnector.saveUserAnswers(any())(any(), any())).thenReturn(Future(():Unit))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
      verify(mockCharitiesConnector, times(1)).saveUserAnswers(any())(any(), any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(emptyUserAnswers))))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe BAD_REQUEST
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
      verify(mockCharitiesConnector, never).saveUserAnswers(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(None)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
      verify(mockCharitiesConnector, never).saveUserAnswers(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(requestArgs*)

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(None)))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
      verify(mockCharitiesConnector, never).saveUserAnswers(any())(any(), any())
    }
  }
}
