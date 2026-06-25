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
import forms.contactDetails.CharityNameFormProvider
import models.{BankDetails, CharityName, NormalMode, UserAnswers}
import navigation.CharityInformationNavigator
import navigation.FakeNavigators.FakeCharityInformationNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.contactDetails.CharityNamePage
import pages.operationsAndFunds.BankDetailsPage
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import connectors.CharitiesConnector
import views.html.contactDetails.CharityNameView

import scala.concurrent.Future

class CharityNameControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  private class Setup {
    val controller: CharityNameController = appBuilder.injector.instanceOf[CharityNameController]
  }

  val appBuilder: Application =
    new GuiceApplicationBuilder()
      .overrides(
        bind[CharitiesConnector].toInstance(mockCharitiesConnector),
        bind[CharityInformationNavigator].toInstance(FakeCharityInformationNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )
      .configure()
      .build()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCharitiesConnector)
  }

  private val view: CharityNameView                 = injector.instanceOf[CharityNameView]
  private val formProvider: CharityNameFormProvider = injector.instanceOf[CharityNameFormProvider]
  private val form: Form[CharityName]               = formProvider()

  "CharityNameController" must {

    "return OK and the correct view for a GET" in new Setup {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(emptyUserAnswers))))

      val result: Future[Result] = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(fakeRequest, messages, frontendAppConfig).toString
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in new Setup {

      val userAnswers: UserAnswers =
        emptyUserAnswers.set(CharityNamePage, charityName).success.value

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(userAnswers))))

      val result: Future[Result] = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted" in new Setup {

      val request: FakeRequest[AnyContent] =
        fakeRequest.withFormUrlEncodedBody("fullName" -> charityFullName, "operatingName" -> charityOperatingName)

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(emptyUserAnswers))))
      when(mockCharitiesConnector.saveUserAnswers(any())(any(), any())).thenReturn(Future(():Unit))

      val result: Future[Result] = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
      verify(mockCharitiesConnector, times(1)).saveUserAnswers(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted with BankDetails" in new Setup {

      val request: FakeRequest[AnyContent] =
        fakeRequest.withFormUrlEncodedBody("fullName" -> charityFullName, "operatingName" -> charityOperatingName)
      val userAnswers: UserAnswers         = emptyUserAnswers
        .set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName")))
        .success
        .value
      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(userAnswers))))
      when(mockCharitiesConnector.saveUserAnswers(any())(any(), any())).thenReturn(Future(():Unit))

      val result: Future[Result] = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
      verify(mockCharitiesConnector, times(1)).saveUserAnswers(any())(any(), any())
    }

    "return a Bad Request and errors when invalid data is submitted" in new Setup {

      val request: FakeRequest[AnyContent] = fakeRequest.withFormUrlEncodedBody(("value", ""))

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(Some(emptyUserAnswers))))

      val result: Future[Result] = controller.onSubmit(NormalMode)(request)

      status(result) mustBe BAD_REQUEST
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
      verify(mockCharitiesConnector, never).saveUserAnswers(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in new Setup {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(None)))

      val result: Future[Result] = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in new Setup {

      val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(None)))

      val result: Future[Result] = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }
  }
}
