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

package controllers.charityInformation

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import forms.charityInformation.CanWeSendToThisAddressFormProvider
import models.addressLookup.{AddressModel, CountryModel}
import models.NormalMode
import navigation.CharityInformationNavigator
import navigation.FakeNavigators.FakeCharityInformationNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, _}
import org.scalatest.BeforeAndAfterEach
import pages.addressLookup.CharityInformationAddressLookupPage
import pages.charityInformation.CanWeSendToThisAddressPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import repositories.UserAnswerRepository
import views.html.charityInformation.CanWeSendToThisAddressView

import scala.concurrent.Future

class CanWeSendToThisAddressControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[CharityInformationNavigator].toInstance(FakeCharityInformationNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  val view: CanWeSendToThisAddressView = inject[CanWeSendToThisAddressView]
  val formProvider: CanWeSendToThisAddressFormProvider = inject[CanWeSendToThisAddressFormProvider]
  val form = formProvider()

  val country = new CountryModel("UK", "United Kingdom")

  val addressLookupData = AddressModel(Seq("4 Other Place", "Some District", "Anytown"),Some("ZZ1 1ZZ"), country)

  val addressLookupDataParsed = "4 Other Place, Some District, Anytown, ZZ1 1ZZ, United Kingdom"

  val addressUserAnswers = Some(emptyUserAnswers.set(CharityInformationAddressLookupPage, addressLookupData).getOrElse(emptyUserAnswers))

  val controller: CanWeSendToThisAddressController = inject[CanWeSendToThisAddressController]

  "CanWeSendToThisAddress Controller " must {

      "return OK and the correct view for a GET" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(addressUserAnswers))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, addressLookupDataParsed)(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerRepository, times(1)).get(any())
      }

    "return OK and the correct view for a GET with no postcode in the address model" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(CharityInformationAddressLookupPage, AddressModel(Seq("4 Other Place", "Some District", "Anytown"), None, country)).getOrElse(emptyUserAnswers))))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form, NormalMode, "4 Other Place, Some District, Anytown, United Kingdom"
      )(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(CanWeSendToThisAddressPage, true).success.value

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(userAnswers.
        set(CharityInformationAddressLookupPage, addressLookupData).getOrElse(emptyUserAnswers))))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, times(1)).set(any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(addressUserAnswers))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerRepository, times(1)).get(any())
      verify(mockUserAnswerRepository, never).set(any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to Session Expired for a GET if no existing address data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers.
        set(CanWeSendToThisAddressPage, true).getOrElse(emptyUserAnswers))))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }
  }
}