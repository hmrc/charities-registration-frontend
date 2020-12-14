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

package controllers

import base.SpecBase
import connectors.CharitiesShortLivedCache
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.{OldServiceSubmission, UserAnswers}
import models.requests.OptionalDataRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.{AcknowledgementReferencePage, OldServiceSubmissionPage}
import pages.sections.{Section1Page, Section2Page}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{SessionRepository, UserAnswerRepository}
import service.CharitiesSave4LaterService
import transformers.UserAnswerTransformer
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.logging.SessionId

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCharitiesShortLivedCache: CharitiesShortLivedCache = mock[CharitiesShortLivedCache]
  lazy val mockCacheMap: CacheMap = mock[CacheMap]
  lazy val mockUserAnswerTransformer: UserAnswerTransformer = mock[UserAnswerTransformer]

  class FakeCharitiesSave4LaterService@Inject()(
    cache: CharitiesShortLivedCache,
    userAnswerTransformer: UserAnswerTransformer,
    sessionRepository: SessionRepository,
    userAnswerRepository: UserAnswerRepository, userAnswers: Option[UserAnswers]) extends
    CharitiesSave4LaterService(cache, userAnswerTransformer, sessionRepository, userAnswerRepository, frontendAppConfig){
    override def getCacheData(request: OptionalDataRequest[_], sessionId: SessionId, eligibleJourneyId: Option[String])(
      implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Call, UserAnswers]] = {
      userAnswers match{
        case Some(ua) => Future.successful(Right(ua))
        case _ => Future.successful(Left(routes.ApplicationBeingProcessedController.onPageLoad()))
      }
    }
  }

  lazy val service = new FakeCharitiesSave4LaterService(mockCharitiesShortLivedCache, mockUserAnswerTransformer,
    mockSessionRepository, mockUserAnswerRepository, Some(emptyUserAnswers))

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[CacheMap].toInstance(mockCacheMap),
        bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
        bind[CharitiesSave4LaterService].toInstance(service),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository, mockCharitiesShortLivedCache)
  }

  lazy val controller: IndexController = inject[IndexController]

  "Index Controller" must {

    "Redirect to registration sent page if the acknowledgement reference number is already present" in {
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers
      .set(AcknowledgementReferencePage, "0123123").success.value)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.RegistrationSentController.onPageLoad().url
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "Redirect to registration sent page if the submission was completed in the old service" in {
      lazy val service = new FakeCharitiesSave4LaterService(mockCharitiesShortLivedCache, mockUserAnswerTransformer,
        mockSessionRepository, mockUserAnswerRepository, Some(emptyUserAnswers
          .set(OldServiceSubmissionPage, OldServiceSubmission("num", "date")).success.value))
      val app =
        new GuiceApplicationBuilder()
          .overrides(
            bind[CharitiesSave4LaterService].toInstance(service),
            bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
            bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
            bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
          ).build()

      val controller: IndexController = app.injector.instanceOf[IndexController]

      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers
        .set(OldServiceSubmissionPage, OldServiceSubmission("num", "date")).success.value)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      lazy val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ApplicationBeingProcessedController.onPageLoad().url
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "Redirect to error page if the charitiesSave4LaterService returing error" in {
      lazy val service = new FakeCharitiesSave4LaterService(mockCharitiesShortLivedCache, mockUserAnswerTransformer,
        mockSessionRepository, mockUserAnswerRepository, None)
      val app =
        new GuiceApplicationBuilder()
          .overrides(
            bind[CharitiesSave4LaterService].toInstance(service),
            bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
            bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
            bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
          ).build()

      val controller: IndexController = app.injector.instanceOf[IndexController]

      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers
        .set(OldServiceSubmissionPage, OldServiceSubmission("num", "date")).success.value)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      lazy val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ApplicationBeingProcessedController.onPageLoad().url
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "Set answers and redirect to the next page (start of journey page)" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))
      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "Fetch user answers and redirect to the next page (start of journey page)" in {

      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(Section1Page, true).flatMap(_.set(Section2Page, false)).success.value)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the session expired page if no valid session id for switchover journey" in {

      val requestWithoutSession = FakeRequest()
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(Section1Page, true).flatMap(_.set(Section2Page, false)).success.value)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(requestWithoutSession)

      status(result) mustEqual SEE_OTHER
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, never()).set(any())
    }

    "redirect to the task list without calling save4later service if external test is enabled" in {

      val app =
        new GuiceApplicationBuilder().configure("features.isExternalTest" -> "true")
          .overrides(
            bind[CharitiesSave4LaterService].toInstance(service),
            bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
            bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
            bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
          ).build()

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))
      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val controller: IndexController = app.injector.instanceOf[IndexController]

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
      verify(mockCharitiesShortLivedCache, never).fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())
    }

    "For keepalive" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.keepalive()(fakeRequest)

      status(result) mustEqual NO_CONTENT
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "For keepalive no UserAnswer" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.keepalive()(fakeRequest)

      status(result) mustEqual NO_CONTENT
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }
  }
}
