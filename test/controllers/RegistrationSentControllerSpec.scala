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

package controllers

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.{AcknowledgementReferencePage, ApplicationSubmissionDatePage, EmailOrPostPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.UserAnswerService
import utils.TimeMachine
import views.ViewUtils.dayToString
import views.html.RegistrationSentView

import scala.concurrent.Future

class RegistrationSentControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
  }

  private val view: RegistrationSentView = injector.instanceOf[RegistrationSentView]

  private val controller: RegistrationSentController = inject[RegistrationSentController]

  private val acknowledgementReferenceNo: String = "123456789"
  private val daysToAdd: Long                    = 28

  "RegistrationSent Controller" must {

    "return OK and the correct view for a GET for email and noEmailPost disabled" in {

      val app = new GuiceApplicationBuilder()
        .configure("features.noEmailPost" -> "false")
        .overrides(
          bind[UserAnswerService].toInstance(mockUserAnswerService),
          bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
        )
        .build()

      val controller: RegistrationSentController = app.injector.instanceOf[RegistrationSentController]

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(AcknowledgementReferencePage, "123456789")
              .flatMap(_.set(ApplicationSubmissionDatePage, inject[TimeMachine].now()))
              .flatMap(_.set(EmailOrPostPage, true))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        dayToString(inject[TimeMachine].now().plusDays(daysToAdd)),
        dayToString(inject[TimeMachine].now(), dayOfWeek = false),
        acknowledgementReferenceNo,
        emailOrPost = true,
        noEmailOrPost = false,
        Seq("requiredDocuments.governingDocumentName.answerTrue"),
        None
      )(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return OK and the correct view for a GET for email and noEmailPost enabled" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(AcknowledgementReferencePage, "123456789")
              .flatMap(_.set(ApplicationSubmissionDatePage, inject[TimeMachine].now()))
              .flatMap(_.set(EmailOrPostPage, true))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        dayToString(inject[TimeMachine].now().plusDays(daysToAdd)),
        dayToString(inject[TimeMachine].now(), dayOfWeek = false),
        acknowledgementReferenceNo,
        emailOrPost = true,
        noEmailOrPost = true,
        Seq("requiredDocuments.governingDocumentName.answerTrue"),
        None
      )(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return OK and the correct view for a GET when noEmailPost enabled" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(AcknowledgementReferencePage, acknowledgementReferenceNo)
              .flatMap(_.set(ApplicationSubmissionDatePage, inject[TimeMachine].now()))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        dayToString(inject[TimeMachine].now().plusDays(daysToAdd)),
        dayToString(inject[TimeMachine].now(), dayOfWeek = false),
        "123456789",
        emailOrPost = false,
        noEmailOrPost = true,
        Seq("requiredDocuments.governingDocumentName.answerTrue"),
        None
      )(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return SEE_OTHER and the correct view for a GET with no EmailOrPostPage answered" in {

      val app = new GuiceApplicationBuilder()
        .configure("features.noEmailPost" -> "false")
        .overrides(
          bind[UserAnswerService].toInstance(mockUserAnswerService),
          bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
        )
        .build()

      val controller: RegistrationSentController = app.injector.instanceOf[RegistrationSentController]

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(AcknowledgementReferencePage, "123456789")
              .flatMap(_.set(ApplicationSubmissionDatePage, inject[TimeMachine].now()))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.EmailOrPostController.onPageLoad.url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no acknowledgement reference is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return SEE_OTHER and the correct view when changing answer" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(AcknowledgementReferencePage, "123456789")
              .flatMap(_.set(EmailOrPostPage, true))
              .success
              .value
          )
        )
      )
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onChange()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.RegistrationSentController.onPageLoad.url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "redirect to Session Expired when changing if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onChange()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
