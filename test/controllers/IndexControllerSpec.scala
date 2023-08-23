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

import audit.AuditService
import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import pages.AcknowledgementReferencePage
import pages.sections.{Section1Page, Section2Page}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.SessionRepository
import service.{CharitiesSectionCompleteService, UserAnswerService}

import javax.inject.Inject
import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockAuditService: AuditService            = MockitoSugar.mock[AuditService]

  class FakeCharitiesSectionCompleteService @Inject() (
    sessionRepository: SessionRepository,
    userAnswerService: UserAnswerService,
    auditService: AuditService
  ) extends CharitiesSectionCompleteService(
        sessionRepository,
        userAnswerService,
        auditService
      )

  lazy val service =
    new FakeCharitiesSectionCompleteService(
      mockSessionRepository,
      mockUserAnswerService,
      mockAuditService
    )

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[CharitiesSectionCompleteService].toInstance(service),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
  }

  lazy val controller: IndexController = inject[IndexController]

  "Index Controller" when {

    "the acknowledgement reference number is already present" must {

      "redirect to RegistrationSent page" in {
        when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
          Future(
            Some(
              emptyUserAnswers
                .set(AcknowledgementReferencePage, "0123123")
                .success
                .value
            )
          )
        )

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.RegistrationSentController.onPageLoad.url
        verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      }
    }

    "there are no user-answers and no session id" must {

      "redirect to CannotFindApplication page" in {

        val controller: IndexController = applicationBuilder().injector.instanceOf[IndexController]

        when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future(None))

        lazy val result = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual 303
        redirectLocation(result).value mustBe controllers.routes.CannotFindApplicationController.onPageLoad.url
        verify(mockUserAnswerService, times(1)).get(any())(any(), any())

      }
    }

    "there are  empty useranswers and a session id" must {

      "redirect to the task-list page" in {

        when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future(Some(emptyUserAnswers)))
        when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future(true))

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual OK
        titleOf(contentAsString(result)) mustBe
          "Add information about the charity - Register your charity’s details with HMRC - GOV.UK"
        verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      }
    }

    "Fetch user answers and redirect to the task-list page" in {

      val userAnswers =
        emptyUserAnswers
          .set(Section1Page, true)
          .flatMap(_.set(Section2Page, false))
          .success
          .value

      when(mockUserAnswerService.get(any())(any(), any()))
        .thenReturn(Future(Some(userAnswers)))

      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future(true))
      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      titleOf(contentAsString(result)) mustBe
        "Add information about the charity - Register your charity’s details with HMRC - GOV.UK"

      verify(mockUserAnswerService, times(1)).get(any())(any(), any())

    }

    "redirect to the session expired page if no valid session id for switchover journey" in {

      val userAnswers =
        emptyUserAnswers
          .set(Section1Page, true)
          .flatMap(_.set(Section2Page, false))
          .success
          .value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future(Some(userAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future(true))

      val result = controller.onPageLoad()(fakeRequestNoSessionId)

      status(result) mustEqual SEE_OTHER
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, never).set(any())(any(), any())
    }

    "for keepalive" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future(Some(emptyUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future(true))

      val result = controller.keepalive()(fakeRequest)

      status(result) mustEqual NO_CONTENT
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "for keepalive no UserAnswer" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future(None))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future(true))

      val result = controller.keepalive()(fakeRequest)

      status(result) mustEqual NO_CONTENT
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "signInDifferentAccount" should {

      "redirect to the loginUrl" in {

        val result = controller.signInDifferentAccount()(fakeRequest)
        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(
          "http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A9457%2Fregister-charity-hmrc" +
            "%2Fcheck-eligibility%2Fcharitable-purposes&origin=charities-registration-frontend"
        )
      }
    }

    "registerNewAccount" should {

      "redirect to the registerUrl" in {
        val result = controller.registerNewAccount()(fakeRequest)
        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(
          "http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A9457%2Fregister-charity-hmrc" +
            "%2Fcheck-eligibility%2Fcharitable-purposes&origin=charities-registration-frontend&accountType=organisation"
        )
      }
    }
  }
}
