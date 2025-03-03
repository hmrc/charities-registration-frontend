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
import pages.{AcknowledgementReferencePage, EmailOrPostPage}
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.UserAnswerService
import views.html.contactDetails.CharityNameView

import scala.concurrent.Future

class CharityNameControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  private class Setup(noEmailPost: Boolean = true) {
    val controller: CharityNameController = applicationBuilder(noEmailPost).injector.instanceOf[CharityNameController]
  }

  def applicationBuilder(noEmailPost: Boolean): Application =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[CharityInformationNavigator].toInstance(FakeCharityInformationNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )
      .configure("features.noEmailPost" -> noEmailPost)
      .build()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
  }

  private val view: CharityNameView                 = injector.instanceOf[CharityNameView]
  private val formProvider: CharityNameFormProvider = injector.instanceOf[CharityNameFormProvider]
  private val form: Form[CharityName]               = formProvider()

  "CharityNameController" must {

    "redirect to EmailOrPost page when acknowledgement reference is present and the feature is turned off" in new Setup(
      noEmailPost = false
    ) {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(AcknowledgementReferencePage, "0123123")
              .flatMap(_.set(EmailOrPostPage, true))
              .success
              .value
          )
        )
      )

      val result: Future[Result] = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.EmailOrPostController.onPageLoad.url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return OK and the correct view for a GET" in new Setup {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result: Future[Result] = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in new Setup {

      val userAnswers: UserAnswers =
        emptyUserAnswers.set(CharityNamePage, CharityName("CName", Some("OpName"))).success.value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))

      val result: Future[Result] = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted" in new Setup {

      val request: FakeRequest[AnyContent] =
        fakeRequest.withFormUrlEncodedBody("fullName" -> "CName", "operatingName" -> "OpName")

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result: Future[Result] = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted with BankDetails" in new Setup {

      val request: FakeRequest[AnyContent] =
        fakeRequest.withFormUrlEncodedBody("fullName" -> "CName", "operatingName" -> "OpName")
      val userAnswers: UserAnswers         = emptyUserAnswers
        .set(BankDetailsPage, BankDetails("fullName", "123456", "12345678", Some("operatingName")))
        .success
        .value
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result: Future[Result] = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "return a Bad Request and errors when invalid data is submitted" in new Setup {

      val request: FakeRequest[AnyContent] = fakeRequest.withFormUrlEncodedBody(("value", ""))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result: Future[Result] = controller.onSubmit(NormalMode)(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, never).set(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in new Setup {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result: Future[Result] = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in new Setup {

      val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result: Future[Result] = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
