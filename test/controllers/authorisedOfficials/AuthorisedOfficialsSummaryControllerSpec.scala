/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.authorisedOfficials

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.{Name, UserAnswers}
import navigation.AuthorisedOfficialsNavigator
import navigation.FakeNavigators.FakeAuthorisedOfficialsNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, IsAddAnotherAuthorisedOfficialPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{redirectLocation, status, *}
import connectors.CharitiesConnector

import scala.concurrent.Future

class AuthorisedOfficialsSummaryControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[CharitiesConnector].toInstance(mockCharitiesConnector),
        bind[AuthorisedOfficialsNavigator].toInstance(FakeAuthorisedOfficialsNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCharitiesConnector)
  }

  private val controller: AuthorisedOfficialsSummaryController = inject[AuthorisedOfficialsSummaryController]

  "AuthorisedOfficials Controller" must {

    "redirect to index page if rows are empty" in {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any()))
        .thenReturn(Future.successful(Right(Some(emptyUserAnswers))))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "return OK if the form has data in it" in {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(
        Future.successful(
          Right(
            Some(
              emptyUserAnswers
                .set(AuthorisedOfficialsNamePage(0), personNameWithoutMiddle)
                .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
                .success
                .value
            )
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "return OK if the form has data for two officials in it" in {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(
        Future.successful(
          Right(
            Some(
              emptyUserAnswers
                .set(AuthorisedOfficialsNamePage(0), personNameWithoutMiddle)
                .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
                .flatMap(
                  _.set(
                    AuthorisedOfficialsNamePage(1),
                    personNameWithoutMiddle
                  )
                )
                .success
                .value
            )
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "return OK and the correct view for a GET" in {

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(
        Future.successful(
          Right(
            Some(
              emptyUserAnswers
                .set(AuthorisedOfficialsNamePage(0), personNameWithoutMiddle)
                .success
                .value
            )
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any()))
        .thenReturn(Future.successful(Right(Some(emptyUserAnswers))))
      when(mockCharitiesConnector.saveUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(():Unit)))

      val result = controller.onSubmit()(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted with two rows of officials" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any())).thenReturn(
        Future.successful(
          Right(
            Some(
              emptyUserAnswers
                .set(AuthorisedOfficialsNamePage(0), personNameWithoutMiddle)
                .flatMap(_.set(AuthorisedOfficialsNamePage(1), personNameWithoutMiddle))
                .success
                .value
            )
          )
        )
      )
      when(mockCharitiesConnector.saveUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(():Unit)))

      val result = controller.onSubmit()(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

    "redirect to the next page when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "notACorrectValue"))

      when(mockCharitiesConnector.getUserAnswers(any())(any(), any()))
        .thenReturn(Future.successful(Right(Some(emptyUserAnswers))))
      when(mockCharitiesConnector.saveUserAnswers(any())(any(), any())).thenReturn(Future.successful(Right(():Unit)))

      val result = controller.onSubmit()(request)

      status(result) mustBe BAD_REQUEST
      verify(mockCharitiesConnector, times(1)).getUserAnswers(any())(any(), any())
    }

  }
}
