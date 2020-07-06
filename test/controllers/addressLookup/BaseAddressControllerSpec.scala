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
import config.FrontendAppConfig
import connectors.addressLookup.AddressLookupConnector
import connectors.addressLookup.httpParsers.AddressLookupInitializationHttpParser.AddressLookupOnRamp
import connectors.addressLookup.httpParsers.{AddressMalformed, NoLocationHeaderReturned}
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import javax.inject.Inject
import models.UserAnswers
import models.requests.DataRequest
import navigation.CharityInformationNavigator
import navigation.FakeNavigators.FakeCharityInformationNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when, _}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.sections.Section1Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import play.api.test.Helpers.{redirectLocation, status, _}
import repositories.UserAnswerRepository
import viewmodels.ErrorHandler

import scala.concurrent.Future

class BaseAddressControllerSpec extends SpecBase with BeforeAndAfterEach {
  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  private val mockAddressLookupConnector: AddressLookupConnector = MockitoSugar.mock[AddressLookupConnector]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[CharityInformationNavigator].toInstance(FakeCharityInformationNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction],
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository, mockAddressLookupConnector)
  }

  class TestAddressLookupController @Inject()(
                                               sessionRepository: UserAnswerRepository,
                                               navigator: CharityInformationNavigator,
                                               addressLookupConnector: AddressLookupConnector,
                                               errorHandler: ErrorHandler,
                                               val controllerComponents: MessagesControllerComponents
                                             )(implicit appConfig: FrontendAppConfig) extends BaseAddressController


  lazy val controller: TestAddressLookupController = new TestAddressLookupController(mockUserAnswerRepository,
    FakeCharityInformationNavigator, mockAddressLookupConnector, inject[ErrorHandler], messagesControllerComponents)
  val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, emptyUserAnswers)

  "BaseAddress Controller" when {

    "calling the .addressLookupInitialize() endpoint" when {

      "address lookup feature is enabled" must {

        "when the on ramp call is successful" must {

          "redirect to the on ramp" in {

            when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
            when(mockAddressLookupConnector.initialize(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(AddressLookupOnRamp("/foo"))))

            val result = controller.addressLookupInitialize(mockAddressLookupConnector, errorHandler, "testCallback", "testPrefix")(request, implicitly)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some("/foo")
          }
        }


        "when the on ramp call is unsuccessful" must {

          "render ISE" in {

            when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
            when(mockAddressLookupConnector.initialize(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Left(NoLocationHeaderReturned)))

            val result = controller.addressLookupInitialize(mockAddressLookupConnector, errorHandler, "testCallback", "testPrefix")(request, implicitly)

            status(result) mustEqual INTERNAL_SERVER_ERROR
            contentAsString(result) mustBe errorHandler.internalServerErrorTemplate(fakeDataRequest).toString
          }
        }
      }
    }

    "calling the .addressLookupCallback(id) endpoint" when {

      "address lookup feature is enabled" when {

        "an ID is provided" when {

          "an address is retrieved" must {

            "redirect to the next page" in {

              when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
              when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))
              when(mockAddressLookupConnector.retrieveAddress(any())(any(), any())).thenReturn(Future.successful(Right(ConfirmedAddressConstants.address)))

              val result = controller.addressLookupCallback(mockAddressLookupConnector,
                errorHandler,
                mockUserAnswerRepository,
                CharityOfficialAddressLookupPage,
                Section1Page,
                FakeCharityInformationNavigator,
                Some("id"))(request)

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(onwardRoute.url)
              verify(mockAddressLookupConnector, times(1)).retrieveAddress(any())(any(), any())
            }
          }

          "an address is not retrieved successfully" must {

            "render ISE for invalid address" in {

              when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
              when(mockAddressLookupConnector.retrieveAddress(any())(any(), any())).thenReturn(Future.successful(Left(AddressMalformed)))

              val result = controller.addressLookupCallback(mockAddressLookupConnector,
                errorHandler,
                mockUserAnswerRepository,
                CharityOfficialAddressLookupPage,
                Section1Page,
                FakeCharityInformationNavigator,
                Some("id"))(request)

              status(result) mustEqual INTERNAL_SERVER_ERROR
              contentAsString(result) mustBe errorHandler.internalServerErrorTemplate(fakeDataRequest).toString
              verify(mockAddressLookupConnector, times(1)).retrieveAddress(any())(any(), any())
            }

            "render ISE" in {

              when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
              when(mockAddressLookupConnector.retrieveAddress(any())(any(), any())).thenReturn(Future.successful(Right(ConfirmedAddressConstants.address)))

              val result = controller.addressLookupCallback(mockAddressLookupConnector,
                errorHandler,
                mockUserAnswerRepository,
                CharityOfficialAddressLookupPage,
                Section1Page,
                FakeCharityInformationNavigator,
                None)(request)

              status(result) mustEqual INTERNAL_SERVER_ERROR
              contentAsString(result) mustBe errorHandler.internalServerErrorTemplate(fakeDataRequest).toString
              verify(mockAddressLookupConnector, never()).retrieveAddress(any())(any(), any())
            }
          }
        }
      }
    }
  }
}
