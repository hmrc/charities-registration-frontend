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

package controllers.regulatorsAndDocuments

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.UserAnswers
import navigation.DocumentsNavigator
import navigation.FakeNavigators.FakeDocumentsNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, _}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{redirectLocation, status, _}
import repositories.UserAnswerRepository

import scala.concurrent.Future

class GoverningDocumentSummaryControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[DocumentsNavigator].toInstance(FakeDocumentsNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  private val controller: GoverningDocumentSummaryController = inject[GoverningDocumentSummaryController]

  "Regulators Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

  }
}
