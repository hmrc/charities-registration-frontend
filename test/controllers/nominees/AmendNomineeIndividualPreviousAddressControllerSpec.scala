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

package controllers.nominees

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import forms.common.AmendAddressFormProvider
import models.addressLookup.{AddressModel, AmendAddressModel, CountryModel}
import models.{Name, NormalMode, SelectTitle, UserAnswers}
import navigation.FakeNavigators.FakeNomineesNavigator
import navigation.NomineesNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.addressLookup.NomineeIndividualPreviousAddressLookupPage
import pages.contactDetails.AmendAddressPage
import pages.nominees.IndividualNomineeNamePage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import service.{CountryService, UserAnswerService}
import views.html.common.AmendAddressView

import scala.concurrent.Future

class AmendNomineeIndividualPreviousAddressControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)
  lazy val mockCountryService: CountryService        = mock(classOf[CountryService])

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[CountryService].toInstance(mockCountryService),
        bind[NomineesNavigator].toInstance(FakeNomineesNavigator),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
    reset(mockCountryService)
  }

  private val messageKeyPrefix                       = "amendNomineeIndividualPreviousAddress"
  private val view: AmendAddressView                 = inject[AmendAddressView]
  private val formProvider: AmendAddressFormProvider = inject[AmendAddressFormProvider]
  private val form: Form[AmendAddressModel]          = formProvider(messageKeyPrefix)

  private val controller: AmendNomineeIndividualPreviousAddressController =
    inject[AmendNomineeIndividualPreviousAddressController]

  private val requestArgs                   = Seq(
    "line1"    -> "23",
    "line2"    -> "Morrison street",
    "line3"    -> "",
    "town"     -> "Glasgow",
    "postcode" -> "G58AN",
    "country"  -> "GB"
  )
  private val localUserAnswers: UserAnswers = emptyUserAnswers
    .set(
      NomineeIndividualPreviousAddressLookupPage,
      AddressModel(
        Seq("7", "Morrison street near riverview gardens", "Glasgow"),
        Some("G58AN"),
        CountryModel("GB", "United Kingdom")
      )
    )
    .flatMap(_.set(IndividualNomineeNamePage, Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")))
    .success
    .value

  "AmendNomineeIndividualPreviousAddressController Controller " must {

    "return OK and the correct view for a GET" in {

      val amendNomineeIndividualPreviousAddress =
        AmendAddressModel("7", Some("Morrison street near riverview gardens"), None, "Glasgow", "G58AN", "GB")

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(amendNomineeIndividualPreviousAddress),
        messageKeyPrefix,
        controllers.nominees.routes.AmendNomineeIndividualPreviousAddressController.onSubmit(NormalMode),
        Some("Jim John Jones"),
        countries = Seq(("GB", "United Kingdom"))
      )(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = localUserAnswers
        .set(AmendAddressPage, AmendAddressModel("23", Some("Morrison street"), Some(""), "Glasgow", "G58AN", "GB"))
        .success
        .value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "populate the view correctly on a GET when the question has previously been answered is empty" in {

      val userAnswers = emptyUserAnswers
        .set(IndividualNomineeNamePage, Name(SelectTitle.Mr, "Jim", Some("John"), "Jones"))
        .success
        .value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)
      contentAsString(result) mustEqual view(
        form,
        messageKeyPrefix,
        controllers.nominees.routes.AmendNomineeIndividualPreviousAddressController.onSubmit(NormalMode),
        Some("Jim John Jones"),
        countries = Seq(("GB", "United Kingdom"))
      )(fakeRequest, messages, frontendAppConfig).toString

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "redirect to the next page when valid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody(requestArgs*)

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request = fakeRequest.withFormUrlEncodedBody()

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockCountryService.countries()(any())).thenReturn(Seq(("GB", "United Kingdom")))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustBe BAD_REQUEST
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockUserAnswerService, never).set(any())(any(), any())
      verify(mockCountryService, times(1)).countries()(any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val request = fakeRequest.withFormUrlEncodedBody(("value", "answer"))

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
