/*
 * Copyright 2025 HM Revenue & Customs
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
import models.addressLookup.{AddressModel, CountryModel}
import models.{Index, Name, NormalMode, SelectTitle, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.addressLookup.OtherOfficialPreviousAddressLookupPage
import pages.otherOfficials.OtherOfficialsNamePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.UserAnswerService
import views.html.common.ConfirmAddressView

import scala.concurrent.Future

class ConfirmOtherOfficialsPreviousAddressControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
  }

  private val view: ConfirmAddressView                                   = injector.instanceOf[ConfirmAddressView]
  private val controller: ConfirmOtherOfficialsPreviousAddressController =
    inject[ConfirmOtherOfficialsPreviousAddressController]
  private val messageKeyPrefix                                           = "otherOfficialPreviousAddress"
  private val otherOfficialPreviousAddressLookup                         = List("12", "Banner Way", "United Kingdom")

  "ConfirmAuthorisedOfficialsPreviousAddressController Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", Some("John"), "Jones"))
              .flatMap(
                _.set(
                  OtherOfficialPreviousAddressLookupPage(0),
                  AddressModel(None, List("12", "Banner Way"), None, CountryModel("GB", "United Kingdom"))
                )
              )
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad(Index(0))(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view
        .apply(
          otherOfficialPreviousAddressLookup,
          messageKeyPrefix,
          controllers.otherOfficials.routes.AddedOtherOfficialController.onPageLoad(0),
          controllers.addressLookup.routes.OtherOfficialsPreviousAddressLookupController
            .initializeJourney(0, NormalMode),
          Some("Jim John Jones")
        )(fakeRequest, messages, frontendAppConfig)
        .toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return submitCall as Amend Address if address length is > 35" in {

      val otherOfficialPreviousAddressMax = List("12", "Banner Way near south riverview gardens", "United Kingdom")

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", Some("John"), "Jones"))
              .flatMap(
                _.set(
                  OtherOfficialPreviousAddressLookupPage(0),
                  AddressModel(
                    None,
                    List("12", "Banner Way near south riverview gardens"),
                    None,
                    CountryModel("GB", "United Kingdom")
                  )
                )
              )
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad(Index(0))(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view
        .apply(
          otherOfficialPreviousAddressMax,
          messageKeyPrefix,
          controllers.otherOfficials.routes.AmendOtherOfficialsPreviousAddressController.onPageLoad(NormalMode, 0),
          controllers.addressLookup.routes.OtherOfficialsPreviousAddressLookupController
            .initializeJourney(0, NormalMode),
          Some("Jim John Jones")
        )(fakeRequest, messages, frontendAppConfig)
        .toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no data found for address" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad(0)(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(0)(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

  }
}
