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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.{AcknowledgementReferencePage, OldServiceSubmissionPage}
import pages.sections.{Section1Page, Section2Page}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsPath, JsonValidationError}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{UserAnswerRepository, UserAnswerRepositoryImpl}
import service.CharitiesKeyStoreService

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCharitiesKeyStoreService: CharitiesKeyStoreService = MockitoSugar.mock[CharitiesKeyStoreService]
  lazy val mockCharitiesShortLivedCache: CharitiesShortLivedCache = MockitoSugar.mock[CharitiesShortLivedCache]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[CharitiesKeyStoreService].toInstance(mockCharitiesKeyStoreService),
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository, mockCharitiesShortLivedCache)
  }

  private val controller: IndexController = inject[IndexController]

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
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockCharitiesKeyStoreService.getCacheData(any())(any(), any())).thenReturn(Future.successful((emptyUserAnswers
        .set(OldServiceSubmissionPage, OldServiceSubmission("num", "date")).success.value, Seq.empty)))
      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ApplicationBeingProcessedController.onPageLoad().url
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "Set answers and redirect to the next page (start of journey page)" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))
      when(mockCharitiesKeyStoreService.getCacheData(any())(any(), any())).thenReturn(Future.successful((emptyUserAnswers, Seq.empty)))
      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "Fetch user answers and redirect to the next page (start of journey page)" in {

      when(mockCharitiesKeyStoreService.getCacheData(any())(any(), any())).thenReturn(Future.successful((emptyUserAnswers, Seq.empty)))
      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(Section1Page, true).flatMap(_.set(Section2Page, false)).success.value)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "redirect to the session expired page if no valid session id for switchover journey" in {

      val requestWithoutSession = FakeRequest()
      when(mockCharitiesKeyStoreService.getCacheData(any())(any(), any())).thenReturn(Future.successful((emptyUserAnswers, Seq.empty)))
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(Section1Page, true).flatMap(_.set(Section2Page, false)).success.value)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(requestWithoutSession)

      status(result) mustEqual SEE_OTHER
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, never()).set(any())
    }

    "redirect to the session expired page if the transformation failed" in {

      when(mockCharitiesKeyStoreService.getCacheData(any())(any(), any()))
        .thenReturn(Future.successful((emptyUserAnswers, Seq((JsPath, Seq(JsonValidationError("a message")))))))
      when(mockCharitiesShortLivedCache.fetchAndGetEntry[Boolean](any(), any())(any(), any(), any())).thenReturn(Future.successful(Some(true)))
      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(Section1Page, true).flatMap(_.set(Section2Page, false)).success.value)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())

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
