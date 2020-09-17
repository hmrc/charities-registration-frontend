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

package controllers.addressLookup

import assets.constants.ConfirmedAddressConstants
import base.SpecBase
import connectors.addressLookup.AddressLookupConnector
import connectors.httpParsers.AddressLookupInitializationHttpParser.AddressLookupOnRamp
import connectors.httpParsers.{AddressMalformed, NoLocationHeaderReturned}
import controllers.actions.{AuthIdentifierAction, DataRequiredAction, FakeAuthIdentifierAction, UserDataRetrievalAction}
import models.requests.DataRequest
import models.{Index, Name, SelectTitle, UserAnswers}
import navigation.FakeNavigators.FakeOtherOfficialsNavigator
import navigation.OtherOfficialsNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.otherOfficials.OtherOfficialsNamePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.{status, _}
import repositories.UserAnswerRepository
import viewmodels.ErrorHandler

import scala.concurrent.Future

class OtherOfficialsAddressLookupControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  private val mockAddressLookupConnector : AddressLookupConnector = MockitoSugar.mock[AddressLookupConnector]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[OtherOfficialsNavigator].toInstance(FakeOtherOfficialsNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction],
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository, mockAddressLookupConnector)
  }

  private lazy val controller: OtherOfficialsAddressLookupController = new OtherOfficialsAddressLookupController(mockUserAnswerRepository,
    FakeOtherOfficialsNavigator, inject[FakeAuthIdentifierAction], inject[UserDataRetrievalAction], inject[DataRequiredAction],
    mockAddressLookupConnector, inject[ErrorHandler], messagesControllerComponents)

  private val localUserAnswers: UserAnswers = emptyUserAnswers.set(
    OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")).success.value

  override lazy val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(fakeRequest, internalId, localUserAnswers)


  "OtherOfficialsAddressLookup Controller" when {

    "calling the .initializeJourney() endpoint" when {

      "address lookup feature is enabled" must {

        "when the on ramp call is successful" must {

          "redirect to the on ramp" in {

            when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(localUserAnswers)))
            when(mockAddressLookupConnector.initialize(any(), any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(AddressLookupOnRamp("/foo"))))

            val result = controller.initializeJourney(Index(0))(fakeDataRequest)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some("/foo")
          }
        }

        "when the on ramp call is unsuccessful" must {

          "render ISE" in {

            when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(localUserAnswers)))
            when(mockAddressLookupConnector.initialize(any(), any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Left(NoLocationHeaderReturned)))

            val result = controller.initializeJourney(Index(0))(fakeDataRequest)

            status(result) mustEqual INTERNAL_SERVER_ERROR
            contentAsString(result) mustBe errorHandler.internalServerErrorTemplate(fakeDataRequest).toString
          }
        }

        "redirect to Session Expired for a GET if no existing data is found" in {

          when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

          val result = controller.initializeJourney(Index(0))(fakeRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
          verify(mockUserAnswerRepository, times(1)).get(any())
          verify(mockUserAnswerRepository, never).set(any())
        }
      }

    }

    "calling the .callback(id) endpoint" when {

      "address lookup feature is enabled" when {

        "an ID is provided" when {

            "an address is retrieved" must {

              "redirect to the next page" in {

                when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
                when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))
                when(mockAddressLookupConnector.retrieveAddress(any())(any(), any())).thenReturn(Future.successful(Right(ConfirmedAddressConstants.address)))

                val result = controller.callback(Index(0), Some("id"))(fakeDataRequest)

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe Some(onwardRoute.url)
                verify(mockAddressLookupConnector, times(1)).retrieveAddress(any())(any(), any())
              }
            }

            "an address is not retrieved successfully" must {

              "render ISE for invalid address" in {

                when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
                when(mockAddressLookupConnector.retrieveAddress(any())(any(), any())).thenReturn(Future.successful(Left(AddressMalformed)))

                val result = controller.callback(Index(0), Some("id"))(fakeDataRequest)

                status(result) mustEqual INTERNAL_SERVER_ERROR
                contentAsString(result) mustBe errorHandler.internalServerErrorTemplate(fakeDataRequest).toString
                verify(mockAddressLookupConnector, times(1)).retrieveAddress(any())(any(), any())
              }

              "render ISE" in {

                when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
                when(mockAddressLookupConnector.retrieveAddress(any())(any(), any())).thenReturn(Future.successful(Right(ConfirmedAddressConstants.address)))

                val result = controller.callback(Index(0), None)(fakeDataRequest)

                status(result) mustEqual INTERNAL_SERVER_ERROR
                contentAsString(result) mustBe errorHandler.internalServerErrorTemplate(fakeDataRequest).toString
                verify(mockAddressLookupConnector, never()).retrieveAddress(any())(any(), any())
              }
            }


          "redirect to Session Expired for a GET if no existing data is found" in {

            when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

            val result = controller.callback(Index(0), Some("id"))(fakeRequest)

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
            verify(mockUserAnswerRepository, times(1)).get(any())
            verify(mockUserAnswerRepository, never).set(any())
          }
        }
      }
    }
  }
}
