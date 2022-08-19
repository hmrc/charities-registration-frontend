/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.contactDetails

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.UserAnswers
import models.addressLookup.{AddressModel, CountryModel}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.addressLookup.CharityPostalAddressLookupPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.UserAnswerService
import views.html.common.ConfirmAddressView

import scala.concurrent.Future

class ConfirmCharityPostalAddressControllerSpec extends SpecBase with BeforeAndAfterEach {

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

  private val view: ConfirmAddressView                          = injector.instanceOf[ConfirmAddressView]
  private val controller: ConfirmCharityPostalAddressController = inject[ConfirmCharityPostalAddressController]
  private val messageKeyPrefix                                  = "charityPostalAddress"
  private val charityInformationAddressLookup                   = List("12", "Banner Way", "United Kingdom")

  "ConfirmCharityPostalAddressController Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(
                CharityPostalAddressLookupPage,
                AddressModel(List("12", "Banner Way"), None, CountryModel("GB", "United Kingdom"))
              )
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view
        .apply(
          charityInformationAddressLookup,
          messageKeyPrefix,
          controllers.contactDetails.routes.CharityInformationSummaryController.onPageLoad(),
          controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney,
          None
        )(fakeRequest, messages, frontendAppConfig)
        .toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "return submitCall as Amend Address if address length is > 35" in {

      val charityInformationAddressMax = List("12", "Banner Way near south riverview gardens", "United Kingdom")

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(
                CharityPostalAddressLookupPage,
                AddressModel(
                  List("12", "Banner Way near south riverview gardens"),
                  None,
                  CountryModel("GB", "United Kingdom")
                )
              )
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view
        .apply(
          charityInformationAddressMax,
          messageKeyPrefix,
          controllers.contactDetails.routes.AmendCharityPostalAddressController.onPageLoad(),
          controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney,
          None
        )(fakeRequest, messages, frontendAppConfig)
        .toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no data found for address" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

  }
}
