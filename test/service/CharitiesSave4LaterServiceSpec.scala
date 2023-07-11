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
//import connectors.CharitiesShortLivedCache
import models.UserAnswers
import models.oldCharities._
import models.requests.OptionalDataRequest
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.scalatest.{BeforeAndAfterEach, PrivateMethodTester}
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import pages.sections.Section1Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import repositories.SessionRepository
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, SessionId, SessionKeys}
import utils.TestData

import java.util.UUID
import scala.concurrent.Future

//scalastyle:off method.length
// scalastyle:off number.of.methods
// scalastyle:off line.size.limit
//scalastyle:off file.size.limit
// scalastyle:off magic.number

class CharitiesSave4LaterServiceSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach with TestData {

  lazy val mockRepository: SessionRepository  = mock[SessionRepository]
  lazy val mockUserService: UserAnswerService = mock[UserAnswerService]
  lazy val mockCacheMap: CacheMap             = mock[CacheMap]
  lazy val mockAuditService: AuditService     = MockitoSugar.mock[AuditService]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(mockRepository),
        bind[UserAnswerService].toInstance(mockUserService),
        bind[AuditService].toInstance(mockAuditService),
        bind[CacheMap].toInstance(mockCacheMap)
      )

  override def beforeEach(): Unit =
    reset(mockAuditService, mockCacheMap)

  lazy val service: CharitiesSave4LaterService = inject[CharitiesSave4LaterService]

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  private val sessionId           = s"session-${UUID.randomUUID}"
  private val lastSessionId       = s"session-${UUID.randomUUID}"
  private val requestWithSession  = fakeRequest.withSession(SessionKeys.sessionId -> sessionId)
  private val optionalDataRequest = OptionalDataRequest(requestWithSession, "8799940975137654", None)

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

    def mockCache: Option[CacheMap] = Some(mockCacheMap)

    def mockRepositoryData: Option[UserAnswers] = None

    def removeResponse(): Future[HttpResponse] = Future.successful(HttpResponse.apply(204, ""))

    def initialiseCache(): Unit = {
      when(
        mockCacheMap.getEntry[CharityContactDetails](meq("charityContactDetails"))(meq(CharityContactDetails.formats))
      ).thenReturn(mockContactDetails)
      when(mockCacheMap.getEntry[CharityAddress](meq("charityOfficialAddress"))(meq(CharityAddress.formats)))
        .thenReturn(mockCharityAddress)
      when(
        mockCacheMap.getEntry[OptionalCharityAddress](meq("correspondenceAddress"))(meq(OptionalCharityAddress.formats))
      ).thenReturn(mockCorrespondenceAddress)
      when(mockCacheMap.getEntry[CharityRegulator](meq("charityRegulator"))(meq(CharityRegulator.formats)))
        .thenReturn(mockCharityRegulator)
      when(
        mockCacheMap.getEntry[CharityGoverningDocument](meq("charityGoverningDocument"))(
          meq(CharityGoverningDocument.formats)
        )
      ).thenReturn(mockCharityGoverningDocument)
      when(mockCacheMap.getEntry[WhatYourCharityDoes](meq("whatYourCharityDoes"))(meq(WhatYourCharityDoes.formats)))
        .thenReturn(mockWhatYourCharityDoes)
      when(mockCacheMap.getEntry[OperationAndFunds](meq("operationAndFunds"))(meq(OperationAndFunds.formats)))
        .thenReturn(mockOperationAndFunds)
      when(
        mockCacheMap.getEntry[CharityBankAccountDetails](meq("charityBankAccountDetails"))(
          meq(CharityBankAccountDetails.formats)
        )
      ).thenReturn(mockCharityBankAccountDetails)
      when(
        mockCacheMap.getEntry[CharityHowManyAuthOfficials](meq("charityHowManyAuthOfficials"))(
          meq(CharityHowManyAuthOfficials.formats)
        )
      ).thenReturn(mockCharityHowManyAuthOfficials)
      when(
        mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("authorisedOfficialIndividual1"))(
          meq(CharityAuthorisedOfficialIndividual.formats)
        )
      ).thenReturn(mockCharityAuthorisedOfficialIndividual1)
      when(
        mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("authorisedOfficialIndividual2"))(
          meq(CharityAuthorisedOfficialIndividual.formats)
        )
      ).thenReturn(mockCharityAuthorisedOfficialIndividual2)
      when(
        mockCacheMap.getEntry[CharityHowManyOtherOfficials](meq("charityHowManyOtherOfficials"))(
          meq(CharityHowManyOtherOfficials.formats)
        )
      ).thenReturn(mockCharityHowManyOtherOfficials)
      when(
        mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("otherOfficialIndividual1"))(
          meq(CharityAuthorisedOfficialIndividual.formats)
        )
      ).thenReturn(mockCharityOtherOfficialIndividual1)
      when(
        mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("otherOfficialIndividual2"))(
          meq(CharityAuthorisedOfficialIndividual.formats)
        )
      ).thenReturn(mockCharityOtherOfficialIndividual2)
      when(
        mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("otherOfficialIndividual3"))(
          meq(CharityAuthorisedOfficialIndividual.formats)
        )
      ).thenReturn(mockCharityOtherOfficialIndividual3)
      when(mockCacheMap.getEntry[CharityAddNominee](meq("charityAddNominee"))(meq(CharityAddNominee.formats)))
        .thenReturn(mockCharityAddNominee)
      when(mockCacheMap.getEntry[CharityNomineeStatus](meq("charityNomineeStatus"))(meq(CharityNomineeStatus.formats)))
        .thenReturn(mockCharityNomineeStatus)
      when(
        mockCacheMap.getEntry[CharityNomineeIndividual](meq("charityNomineeIndividual"))(
          meq(CharityNomineeIndividual.formats)
        )
      ).thenReturn(mockCharityNomineeIndividual)
      when(
        mockCacheMap.getEntry[CharityNomineeOrganisation](meq("charityNomineeOrganisation"))(
          meq(CharityNomineeOrganisation.formats)
        )
      ).thenReturn(mockCharityNomineeOrganisation)
      when(mockCacheMap.getEntry[Acknowledgement](meq("acknowledgement-Reference"))(meq(Acknowledgement.formats)))
        .thenReturn(mockAcknowledgement)
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
