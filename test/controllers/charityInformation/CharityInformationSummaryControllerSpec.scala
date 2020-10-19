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
import models.addressLookup.{AddressModel, CountryModel}
import models.{CharityContactDetails, CharityName, UserAnswers}
import navigation.CharityInformationNavigator
import navigation.FakeNavigators.FakeCharityInformationNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, _}
import org.scalatest.BeforeAndAfterEach
import pages.addressLookup.{CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.charityInformation.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{redirectLocation, status, _}
import repositories.{SessionRepository, UserAnswerRepository}

import scala.concurrent.Future

class CharityInformationSummaryControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[CharityInformationNavigator].toInstance(FakeCharityInformationNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  private val controller: CharityInformationSummaryController = inject[CharityInformationSummaryController]

  "CharityInformationSummary Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(
        Future.successful(Some(emptyUserAnswers.set(CharityNamePage, CharityName("aaa", None)).success.value)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "return Redirect and the session expired view for a GET with no data stored" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted with no pages answered" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "redirect to the next page when valid data is submitted with some pages answered" in {

      when(mockUserAnswerRepository.get(any())).thenReturn(
        Future.successful(Some(emptyUserAnswers.set(CharityNamePage, CharityName("a charity", Some("another name"))).success.value)))
      when(mockUserAnswerRepository.set(any())).thenReturn(Future.successful(true))

      val result = controller.onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerRepository, times(1)).get(any())
    }

    "give the correct completion status" when {
      "no data is provided" in {
        val result = controller.checkComplete(emptyUserAnswers)

        result mustBe false
      }

      "some but not all data is provided" in {
        val result = controller.checkComplete(emptyUserAnswers.set(CharityNamePage, CharityName("a charity", Some("another name"))).success.value)

        result mustBe false
      }

      "unnecessary data is provided" in {

        val result = controller.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", None))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("0123123123", Some("07111111111"), "abc@email.com")))
          .flatMap(_.set(CharityOfficialAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, true))
          .flatMap(_.set(CharityPostalAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom")))).success.value
        )

        result mustBe false
      }

      "all data necessary is provided when postal address is the same as location" in {

        val result = controller.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", None))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("0123123123", Some("07111111111"), "abc@email.com")))
          .flatMap(_.set(CharityOfficialAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, true)).success.value
        )

        result mustBe true
      }

      "all data necessary is provided when postal address is different to location" in {

        val result = controller.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", None))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("0123123123", Some("07111111111"), "abc@email.com")))
          .flatMap(_.set(CharityOfficialAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, false))
          .flatMap(_.set(CharityPostalAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom")))).success.value
        )

        result mustBe true
      }

    }

  }
}
