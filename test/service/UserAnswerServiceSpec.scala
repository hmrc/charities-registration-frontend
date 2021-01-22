/*
 * Copyright 2021 HM Revenue & Customs
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

package service

import base.SpecBase
import connectors.CharitiesConnector
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Future

class UserAnswerServiceSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCharitiesConnector: CharitiesConnector = MockitoSugar.mock[CharitiesConnector]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[CharitiesConnector].toInstance(mockCharitiesConnector)
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCharitiesConnector)
  }

  private val service: UserAnswerService = inject[UserAnswerService]

  "UserAnswer Service" must {

    ".get return valid user answers" in {
      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(userAnswers))
      val result = await(service.get(any())(any(), any()))
      result.get.data mustBe userAnswers.get.data
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    ".get return user answers as None" in {
      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(Future.successful(None))
      val result = await(service.get(any())(any(), any()))
      result mustBe None
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    ".set return true if user answers saved successfully" in {
      when(mockCharitiesConnector.saveUserAnswers(any())(any(), any())).thenReturn(Future.successful(true))
      val result = await(service.set(any())(any(), any()))
      result mustBe true
      verify(mockCharitiesConnector, times(1)).saveUserAnswers(any())(any(), any())
    }

    ".set return true if user answers not saved successfully" in {
      when(mockCharitiesConnector.saveUserAnswers(any())(any(), any())).thenReturn(Future.successful(false))
      val result = await(service.set(any())(any(), any()))
      result mustBe false
      verify(mockCharitiesConnector, times(1)).saveUserAnswers(any())(any(), any())
    }


  }

}
