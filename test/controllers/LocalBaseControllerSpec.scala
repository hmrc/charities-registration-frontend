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

package controllers

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.addressLookup.AddressModel
import models.regulators.SelectGoverningDocument
import models.requests.DataRequest
import models.{Country, Index, Name, UserAnswers}
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import pages.nominees.OrganisationNomineeNamePage
import pages.regulatorsAndDocuments.*
import pages.sections.*
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.*
import play.api.test.Helpers.*
import service.UserAnswerService

import javax.inject.Inject
import scala.concurrent.Future

class LocalBaseControllerSpec extends SpecBase with BeforeAndAfterEach {
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

  private def block(test: String): Future[Result] =
    Future.successful(Results.Ok(test))

  private def blockForAddress(test: Seq[String]): Future[Result] =
    Future.successful(Results.Ok(test.mkString(",")))

  class TestAuthorisedOfficialsController @Inject() (
    val controllerComponents: MessagesControllerComponents
  ) extends LocalBaseController

  private lazy val controller: TestAuthorisedOfficialsController = new TestAuthorisedOfficialsController(
    messagesControllerComponents
  )
  private val addressUserAnswers: UserAnswers                    = emptyUserAnswers
    .set(
      AuthorisedOfficialsNamePage(0),
      personNameWithMiddle
    )
    .success
    .value

  private val contactDetailsNominee: UserAnswers = emptyUserAnswers
    .set(
      OrganisationNomineeNamePage,
      "testName"
    )
    .success
    .value

  private val sectionUserAnswers: UserAnswers = emptyUserAnswers
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

  "LocalBase Controller" must {

    "calling the .getAuthorisedOfficialName() is successful" in {

      val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, addressUserAnswers)
      val result                           = controller.getFullName(AuthorisedOfficialsNamePage(Index(0)))(block)(request)

      status(result) mustEqual OK
    }

    "calling the .getAuthorisedOfficialName() is unsuccessful" in {

      val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, emptyUserAnswers)
      val result                           = controller.getFullName(AuthorisedOfficialsNamePage(Index(0)))(block)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.PageNotFoundController.onPageLoad().url)
    }

    "calling the .getOrganisationName() is successful" in {

      val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, contactDetailsNominee)
      val result                           = controller.getOrganisationName(OrganisationNomineeNamePage)(block)(request)

      status(result) mustEqual OK
    }

    "calling the .getOrganisationName() is unsuccessful" in {

      val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, emptyUserAnswers)
      val result                           = controller.getOrganisationName(OrganisationNomineeNamePage)(block)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.PageNotFoundController.onPageLoad().url)
    }

    "calling the .getAddress() is successful" in {

      val request: DataRequest[AnyContent] = DataRequest(
        fakeRequest,
        internalId,
        emptyUserAnswers
          .set(
            CharityOfficialAddressLookupPage,
            AddressModel(List("12", "Banner Way"), Some("NE128UZ"), gbCountryModel)
          )
          .success
          .value
      )
      val result                           =
        controller.getAddress(CharityOfficialAddressLookupPage)((test: Seq[String], _: Country) =>
          blockForAddress(test)
        )(request)

      status(result) mustEqual OK
    }

    "calling getDocumentName" when {

      "return other document name" in {

        val request: DataRequest[AnyContent] = DataRequest(
          fakeRequest,
          internalId,
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .flatMap(_.set(GoverningDocumentNamePage, "other doc name"))
            .success
            .value
        )

        val result = controller.getDocumentNameKey(SelectGoverningDocumentPage)(block)(request)

        status(result) mustEqual OK
      }

      "return SessionExpired if other document name not present" in {

        val request: DataRequest[AnyContent] = DataRequest(
          fakeRequest,
          internalId,
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Other)
            .success
            .value
        )

        val result = controller.getDocumentNameKey(SelectGoverningDocumentPage)(block)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.PageNotFoundController.onPageLoad().url)
      }

      "return RoyalCharacter if its selected" in {

        val request: DataRequest[AnyContent] = DataRequest(
          fakeRequest,
          internalId,
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.RoyalCharacter)
            .success
            .value
        )

        val result = controller.getDocumentNameKey(SelectGoverningDocumentPage)(block)(request)

        status(result) mustEqual OK
      }

      "return other document types if its selected" in {

        val request: DataRequest[AnyContent] = DataRequest(
          fakeRequest,
          internalId,
          emptyUserAnswers
            .set(SelectGoverningDocumentPage, SelectGoverningDocument.Will)
            .success
            .value
        )

        val result = controller.getDocumentNameKey(SelectGoverningDocumentPage)(block)(request)

        status(result) mustEqual OK
      }

      "return SessionExpired if document type is not selected" in {

        val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, emptyUserAnswers)

        val result = controller.getDocumentNameKey(SelectGoverningDocumentPage)(block)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.PageNotFoundController.onPageLoad().url)
      }
    }

    "calling the .isAllSectionsCompleted is not successful" in {

      val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, emptyUserAnswers)
      val result                           = controller.isAllSectionsCompleted()(request)

      result mustBe false
    }

    "calling the .isAllSectionsCompleted is successful" in {

      val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, sectionUserAnswers)
      val result                           = controller.isAllSectionsCompleted()(request)

      result mustBe true
    }
  }
}
