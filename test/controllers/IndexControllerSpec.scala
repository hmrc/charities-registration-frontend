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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers._

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with BeforeAndAfterEach {

  val controller: IndexController = inject[IndexController]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  "Index Controller" must {

    "Set answers and redirect to the next page (start of journey page)" in {

      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "For keepalive" in {

      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.keepalive()(fakeRequest)

      status(result) mustEqual NO_CONTENT
      verify(mockUserAnswerRepository, times(1)).set(any())
    }
  }
}
