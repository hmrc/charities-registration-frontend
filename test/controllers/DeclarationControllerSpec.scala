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

package controllers

import base.SpecBase
import connectors.CharitiesConnector
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.authOfficials.OfficialsPosition
import models.operations.CharitablePurposes.{AmateurSport, AnimalWelfare}
import models.operations.{CharitablePurposes, CharityEstablishedOptions, FundRaisingOptions, OperatingLocationOptions}
import models.regulators.SelectGoverningDocument.MemorandumArticlesAssociation
import models.regulators.SelectWhyNoRegulator
import models.{MongoDateTimeFormats, RegistrationResponse, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import pages.operationsAndFunds.*
import pages.addressLookup.*
import pages.contactDetails.*
import pages.otherOfficials.*
import pages.authorisedOfficials.*
import pages.regulatorsAndDocuments.*
import pages.sections.*
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.*
import service.UserAnswerService
import views.html.DeclarationView

import java.time.{LocalDate, MonthDay}
import scala.collection.immutable.SortedSet
import scala.concurrent.Future

class DeclarationControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers]  = Some(emptyUserAnswers)
  lazy val mockCharitiesConnector: CharitiesConnector =
    mock(classOf[CharitiesConnector])

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[CharitiesConnector].toInstance(mockCharitiesConnector),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
    reset(mockCharitiesConnector)
  }

  private val view: DeclarationView = injector.instanceOf[DeclarationView]

  private val controller: DeclarationController = inject[DeclarationController]

  val localUserAnswers: UserAnswers = emptyUserAnswers
    .set(BankDetailsPage, bankDetailsWithoutRollNumber)
    .flatMap(
      _.set(
        CharityOfficialAddressLookupPage,
        address.copy(postcode = None, country = inCountryModel)
      )
    )
    .flatMap(_.set(CanWeSendToThisAddressPage, true))
    .flatMap(
      _.set(CharityContactDetailsPage, charityContactDetails)
    )
    .flatMap(_.set(CharityNamePage, charityNameNoOperatingName))
    .flatMap(_.set(IsCharityRegulatorPage, false))
    .flatMap(_.set(AuthorisedOfficialsNamePage(0), personNameWithMiddle))
    .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
    .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.parse("2000-12-11")))
    .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
    .flatMap(_.set(AuthorisedOfficialsNinoPage(0), nino))
    .flatMap(
      _.set(
        AuthorisedOfficialAddressLookupPage(0),
        addressAllLines
      )
    )
    .flatMap(_.set(OtherOfficialsNamePage(0), personName2WithMiddle))
    .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar))
    .flatMap(_.set(OtherOfficialsDOBPage(0), LocalDate.parse("2000-12-11")))
    .flatMap(_.set(OtherOfficialsPhoneNumberPage(0), phoneNumbers))
    .flatMap(_.set(OtherOfficialsNinoPage(0), nino))
    .flatMap(
      _.set(
        OtherOfficialAddressLookupPage(0),
        addressAllLines
      )
    )
    .flatMap(
      _.set(
        PublicBenefitsPage,
        "qweqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre66"
      )
    )
    .flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
    .flatMap(_.set(WhenGoverningDocumentApprovedPage, LocalDate.parse("2014-07-01")))
    .flatMap(_.set(SelectWhyNoRegulatorPage, SelectWhyNoRegulator.EnglandWalesUnderThreshold))
    .flatMap(_.set(SelectGoverningDocumentPage, MemorandumArticlesAssociation))
    .flatMap(_.set(GoverningDocumentNamePage, "Other Documents for Charity"))
    .flatMap(_.set(IsApprovedGoverningDocumentPage, false))
    .flatMap(_.set(HasCharityChangedPartsOfGoverningDocumentPage, false))
    .flatMap(
      _.set(
        AccountingPeriodEndDatePage,
        MonthDay.from(LocalDate.parse("2020-01-01"))
      )(MongoDateTimeFormats.localDayMonthWrite)
        .flatMap(_.set(IsFinancialAccountsPage, true))
        .flatMap(_.set(EstimatedIncomePage, BigDecimal("123")))
        .flatMap(_.set(ActualIncomePage, BigDecimal("121")))
        .flatMap(_.set(FundRaisingPage, SortedSet.from(FundRaisingOptions.valuesIndexed)))
        .flatMap(_.set(CharityEstablishedInPage, CharityEstablishedOptions.Wales))
        .flatMap(_.set(OperatingLocationPage, Set[OperatingLocationOptions](OperatingLocationOptions.England)))
        .flatMap(_.set(CharitablePurposesPage, Set[CharitablePurposes](AmateurSport, AnimalWelfare)))
        .flatMap(
          _.set(
            CharitableObjectivesPage,
            "qwet\tqwewqesdfsdfdgxccvbcbre664354wfffgdfgdq34tggnchjn4w7q3bearvfxasxe14crtgvqweqwewqesdfsdfdgxccvbcbre\r\n66"
          )
        )
    )
    .flatMap(_.set(OtherOfficialsNamePage(0), personNameWithoutMiddle))
    .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Director))
    .flatMap(
      _.set(
        OtherOfficialAddressLookupPage(0),
        addressWithTown.copy(postcode = None, country = itCountryModel)
      )
    )
    .success
    .value

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(Section1Page, true)
              .flatMap(_.set(Section2Page, true))
              .flatMap(_.set(Section3Page, true))
              .flatMap(_.set(Section4Page, true))
              .flatMap(_.set(Section5Page, true))
              .flatMap(_.set(Section6Page, true))
              .flatMap(_.set(Section7Page, true))
              .flatMap(_.set(Section8Page, true))
              .flatMap(_.set(Section9Page, true))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view()(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.PageNotFoundController.onPageLoad().url
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "redirect to the next page after valid transformation" in {
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(localUserAnswers)))
      when(mockCharitiesConnector.registerCharities(any())(any(), any())).thenReturn(
        Future.successful(Right(RegistrationResponse("ackRef")))
      )

      val result = controller.onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCharitiesConnector, times(1)).registerCharities(any())(any(), any())
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(None))

      val result = controller.onSubmit()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
      verify(mockCharitiesConnector, never()).registerCharities(any())(any(), any())
    }

    "redirect to Tasklist for a GET if SectionPage is not completed" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(Section1Page, false)
              .flatMap(_.set(Section2Page, true))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.IndexController.onPageLoad(None).url)
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
