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

package controllers.eligibility

import base.SpecBase
import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import models.NormalMode
import navigation.EligibilityNavigator
import navigation.FakeNavigators.FakeEligibilityNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.SessionRepositoryImpl
import views.html.eligibility.InEligibleView

import scala.concurrent.Future

class InEligibleControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepositoryImpl].toInstance(mockSessionRepository),
        bind[EligibilityNavigator].toInstance(FakeEligibilityNavigator),
        bind[IdentifierAction].to[FakeIdentifierAction]
      )

  val view: InEligibleView = injector.instanceOf[InEligibleView]

  val controller: InEligibleController = inject[InEligibleController]

  "InEligible Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view()(fakeRequest, messages, frontendAppConfig).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockSessionRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER  

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
