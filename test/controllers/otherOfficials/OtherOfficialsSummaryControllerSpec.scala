/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.otherOfficials

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.{Name, SelectTitle, UserAnswers}
import navigation.FakeNavigators.FakeOtherOfficialsNavigator
import navigation.OtherOfficialsNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.otherOfficials.{IsAddAnotherOtherOfficialPage, OtherOfficialsNamePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{redirectLocation, status, _}
import service.UserAnswerService

import scala.concurrent.Future

class OtherOfficialsSummaryControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[OtherOfficialsNavigator].toInstance(FakeOtherOfficialsNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
  }

  private val controller: OtherOfficialsSummaryController = inject[OtherOfficialsSummaryController]

  "Other Officials summary Controller" must {

    "redirect to index page if rows are empty" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return OK if the form has data in it" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              .flatMap(_.set(IsAddAnotherOtherOfficialPage, true))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return OK if the form has data for two officials in it" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              .flatMap(_.set(IsAddAnotherOtherOfficialPage, true))
              .flatMap(
                _.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              )
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return OK if the form has data for three officials in it" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              .flatMap(_.set(IsAddAnotherOtherOfficialPage, true))
              .flatMap(
                _.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              )
              .flatMap(
                _.set(OtherOfficialsNamePage(2), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              )
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to index page if when rows are empty" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted with three rows of officials" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              .flatMap(_.set(IsAddAnotherOtherOfficialPage, true))
              .flatMap(
                _.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              )
              .flatMap(
                _.set(OtherOfficialsNamePage(2), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              )
              .success
              .value
          )
        )
      )
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "notACorrectValue"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              .flatMap(_.set(IsAddAnotherOtherOfficialPage, true))
              .flatMap(
                _.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              )
              .success
              .value
          )
        )
      )
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              .flatMap(_.set(IsAddAnotherOtherOfficialPage, true))
              .flatMap(
                _.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
              )
              .success
              .value
          )
        )
      )
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(request)

      status(result) mustBe SEE_OTHER
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
