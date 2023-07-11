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
import connectors.CharitiesShortLivedCache
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.requests.OptionalDataRequest
import models.{OldServiceSubmission, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import pages.sections.{Section1Page, Section2Page}
import pages.{AcknowledgementReferencePage, OldServiceSubmissionPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Call, RequestHeader}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import service.{CharitiesSave4LaterService, UserAnswerService}
import transformers.UserAnswerTransformer
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import uk.gov.hmrc.http.cache.client.CacheMap

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers]              = Some(emptyUserAnswers)
  lazy val mockCharitiesShortLivedCache: CharitiesShortLivedCache = mock[CharitiesShortLivedCache]
  lazy val mockCacheMap: CacheMap                                 = mock[CacheMap]
  lazy val mockUserAnswerTransformer: UserAnswerTransformer       = mock[UserAnswerTransformer]
  lazy val mockAuditService: AuditService                         = MockitoSugar.mock[AuditService]

  class FakeCharitiesSave4LaterService @Inject() (
    cache: CharitiesShortLivedCache,
    userAnswerTransformer: UserAnswerTransformer,
    sessionRepository: SessionRepository,
    userAnswerService: UserAnswerService,
    auditService: AuditService,
    userAnswers: Option[UserAnswers]
  ) extends CharitiesSave4LaterService(
//        cache,
        userAnswerTransformer,
        sessionRepository,
        userAnswerService,
        auditService,
        frontendAppConfig
      ) {
//    override def getCacheData(
//      request: OptionalDataRequest[_],
//      sessionId: SessionId,
//      eligibleJourneyId: Option[String]
//    )(implicit hc: HeaderCarrier, ec: ExecutionContext, rh: RequestHeader): Future[Either[Call, UserAnswers]] =
//      userAnswers match {
//        case Some(ua) => Future.successful(Right(ua))
//        case _        => Future.successful(Left(routes.ApplicationBeingProcessedController.onPageLoad))
//      }
  }

  lazy val service = new FakeCharitiesSave4LaterService(
    mockCharitiesShortLivedCache,
    mockUserAnswerTransformer,
    mockSessionRepository,
    mockUserAnswerService,
    mockAuditService,
    Some(emptyUserAnswers)
  )

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[CacheMap].toInstance(mockCacheMap),
        bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
        bind[CharitiesSave4LaterService].toInstance(service),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService, mockCharitiesShortLivedCache)
  }

  lazy val controller: IndexController = inject[IndexController]

  "Index Controller" must {

    "Redirect to registration sent page if the acknowledgement reference number is already present" in {
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
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

    "Redirect to registration sent page if the submission was completed in the old service" in {
      lazy val service = new FakeCharitiesSave4LaterService(
        mockCharitiesShortLivedCache,
        mockUserAnswerTransformer,
        mockSessionRepository,
        mockUserAnswerService,
        mockAuditService,
        Some(
          emptyUserAnswers
            .set(OldServiceSubmissionPage, OldServiceSubmission("num", "date"))
            .success
            .value
        )
      )
      val app          =
        new GuiceApplicationBuilder()
          .overrides(
            bind[CharitiesSave4LaterService].toInstance(service),
            bind[UserAnswerService].toInstance(mockUserAnswerService),
            bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
            bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
          )
          .build()

      val controller: IndexController = app.injector.instanceOf[IndexController]

      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(None))
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OldServiceSubmissionPage, OldServiceSubmission("num", "date"))
              .success
              .value
          )
        )
      )
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      lazy val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ApplicationBeingProcessedController.onPageLoad.url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "Redirect to error page if the charitiesSave4LaterService returing error" in {
      lazy val service = new FakeCharitiesSave4LaterService(
        mockCharitiesShortLivedCache,
        mockUserAnswerTransformer,
        mockSessionRepository,
        mockUserAnswerService,
        mockAuditService,
        None
      )
      val app          =
        new GuiceApplicationBuilder()
          .overrides(
            bind[CharitiesSave4LaterService].toInstance(service),
            bind[UserAnswerService].toInstance(mockUserAnswerService),
            bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
            bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
          )
          .build()

      val controller: IndexController = app.injector.instanceOf[IndexController]

      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(None))
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OldServiceSubmissionPage, OldServiceSubmission("num", "date"))
              .success
              .value
          )
        )
      )
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      lazy val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ApplicationBeingProcessedController.onPageLoad.url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "Set answers and redirect to the next page (start of journey page)" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "Fetch user answers and redirect to the next page (start of journey page)" in {

      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(emptyUserAnswers.set(Section1Page, true).flatMap(_.set(Section2Page, false)).success.value)
        )
      )
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the session expired page if no valid session id for switchover journey" in {

      val requestWithoutSession = FakeRequest()
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(emptyUserAnswers.set(Section1Page, true).flatMap(_.set(Section2Page, false)).success.value)
        )
      )
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(requestWithoutSession)

      status(result) mustEqual SEE_OTHER
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, never).set(any())(any(), any())
    }

    "redirect to the task list without calling save4later service if external test is enabled" in {

      val app =
        new GuiceApplicationBuilder()
          .configure("features.isExternalTest" -> "true")
          .overrides(
            bind[CharitiesSave4LaterService].toInstance(service),
            bind[UserAnswerService].toInstance(mockUserAnswerService),
            bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
            bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
          )
          .build()

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val controller: IndexController = app.injector.instanceOf[IndexController]

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
      verify(mockCharitiesShortLivedCache, never).fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())
    }

    "For keepalive" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.keepalive()(fakeRequest)

      status(result) mustEqual NO_CONTENT
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
    }

    "For keepalive no UserAnswer" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

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
