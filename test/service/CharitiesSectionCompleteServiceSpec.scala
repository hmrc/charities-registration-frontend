/*
 * Copyright 2023 HM Revenue & Customs
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

package service

import audit.AuditService
import base.SpecBase
import models.UserAnswers
import models.oldCharities._
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import pages.sections.Section1Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import repositories.SessionRepository
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, SessionId}
import utils.TestData

import java.util.UUID
import scala.concurrent.Future

//scalastyle:off method.length
// scalastyle:off number.of.methods
// scalastyle:off line.size.limit
//scalastyle:off file.size.limit
// scalastyle:off magic.number

class CharitiesSectionCompleteServiceSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach with TestData {

  lazy val mockRepository: SessionRepository  = mock[SessionRepository]
  lazy val mockUserService: UserAnswerService = mock[UserAnswerService]
  lazy val mockAuditService: AuditService     = MockitoSugar.mock[AuditService]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(mockRepository),
        bind[UserAnswerService].toInstance(mockUserService),
        bind[AuditService].toInstance(mockAuditService)
      )

  override def beforeEach(): Unit =
    reset(mockAuditService)

  lazy val service: CharitiesSectionCompleteService = inject[CharitiesSectionCompleteService]

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  private val sessionId = s"session-${UUID.randomUUID}"

  override implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId)))

  trait LocalSetup {

    def mockContactDetails: Option[CharityContactDetails] = None

    def mockCharityAddress: Option[CharityAddress] = None

    def mockCorrespondenceAddress: Option[OptionalCharityAddress] = None

    def mockCharityRegulator: Option[CharityRegulator] = None

    def mockCharityGoverningDocument: Option[CharityGoverningDocument] = None

    def mockWhatYourCharityDoes: Option[WhatYourCharityDoes] = None

    def mockOperationAndFunds: Option[OperationAndFunds] = None

    def mockCharityBankAccountDetails: Option[CharityBankAccountDetails] = None

    def mockCharityHowManyAuthOfficials: Option[CharityHowManyAuthOfficials] = None

    def mockCharityAuthorisedOfficialIndividual1: Option[CharityAuthorisedOfficialIndividual] = None

    def mockCharityAuthorisedOfficialIndividual2: Option[CharityAuthorisedOfficialIndividual] = None

    def mockCharityHowManyOtherOfficials: Option[CharityHowManyOtherOfficials] = None

    def mockCharityOtherOfficialIndividual1: Option[CharityAuthorisedOfficialIndividual] = None

    def mockCharityOtherOfficialIndividual2: Option[CharityAuthorisedOfficialIndividual] = None

    def mockCharityOtherOfficialIndividual3: Option[CharityAuthorisedOfficialIndividual] = None

    def mockCharityAddNominee: Option[CharityAddNominee] = None

    def mockCharityNomineeStatus: Option[CharityNomineeStatus] = None

    def mockCharityNomineeIndividual: Option[CharityNomineeIndividual] = None

    def mockCharityNomineeOrganisation: Option[CharityNomineeOrganisation] = None

    def mockAcknowledgement: Option[Acknowledgement] = None

    def mockSessionId: SessionId = SessionId(sessionId)

    def mockEligibleJourneyId: Option[String] = None

//    def mockCache: Option[CacheMap] = Some(mockCacheMap)

    def mockRepositoryData: Option[UserAnswers] = None

    def removeResponse(): Future[HttpResponse] = Future.successful(HttpResponse.apply(204, ""))

    def initialiseCache(): Unit = {
      when(mockRepository.get(any())).thenReturn(Future.successful(mockRepositoryData))
      when(mockUserService.set(any())(any(), any())).thenReturn(Future.successful(true))
      doNothing.when(mockAuditService).sendEvent(any())(any(), any())
    }

  }

  "CharitiesSave4LaterService" when {

    "isCharityInformationStatusSectionCompleted" must {

      val data = Json.obj(
        "charityContactDetails"                      -> Json.parse("""{"emailAddress":"a@b.com","daytimePhone":"1234567890"}"""),
        "charityName"                                -> Json.parse("""{"fullName":"Test123"}"""),
        "isCharityInformationStatusSectionCompleted" -> false,
        "isSwitchOver"                               -> true,
        "charityOfficialAddress"                     -> Json.parse(
          """{"country":{"code":"GB","name":"United Kingdom"},"postcode":"postcode","lines":["Test123","line2"]}"""
        ),
        "charityPostalAddress"                       -> Json.parse(
          """{"country":{"code":"GB","name":"United Kingdom"},"postcode":"postcode","lines":["Test123","line2"]}"""
        )
      )

      "return false when sections are not completed" in new LocalSetup {

        val ua: UserAnswers = UserAnswers("8799940975137654", data)

        val result = service.isCharityInformationStatusSectionCompleted(ua)

        result.get.get(Section1Page) mustBe Some(false)
      }

      "return false when all sections are completed and charity name is more than 60 characters" in new LocalSetup {

        val ua: UserAnswers = UserAnswers(
          "8799940975137654",
          data ++ Json.obj("canWeSendLettersToThisAddress" -> false)
            ++ Json.obj(
              "charityName" -> Json.parse(
                """{"fullName":"canWeSendLettersToThisAddresscanWeSendLettersToThisAddress123"}"""
              )
            )
        )

        val result = service.isCharityInformationStatusSectionCompleted(ua)

        result.get.get(Section1Page) mustBe Some(false)
      }

      "return true when all sections are completed" in new LocalSetup {

        val ua: UserAnswers =
          UserAnswers("8799940975137654", data ++ Json.obj("canWeSendLettersToThisAddress" -> false))

        val result          = service.isCharityInformationStatusSectionCompleted(ua)

        result.get.get(Section1Page) mustBe Some(true)
      }
    }
  }
}
//scalastyle:on method.length
// scalastyle:on number.of.methods
// scalastyle:on line.size.limit
//scalastyle:on file.size.limit
// scalastyle:on magic.number
