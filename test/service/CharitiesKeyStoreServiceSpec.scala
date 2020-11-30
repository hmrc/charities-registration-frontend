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

package service

import java.util.UUID

import base.SpecBase
import connectors.CharitiesShortLivedCache
import models.UserAnswers
import models.oldCharities._
import models.requests.OptionalDataRequest
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsPath, Json, JsonValidationError}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.logging.SessionId
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, SessionKeys}
import utils.TestData

import scala.concurrent.Future

class CharitiesKeyStoreServiceSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach with TestData {

  lazy val mockCacheMap: CacheMap = mock[CacheMap]
  lazy val mockCharitiesShortLivedCache: CharitiesShortLivedCache = mock[CharitiesShortLivedCache]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[CacheMap].toInstance(mockCacheMap),
        bind[CharitiesShortLivedCache].toInstance(mockCharitiesShortLivedCache)
      )

  override def beforeEach(): Unit = {
    reset(mockCharitiesShortLivedCache, mockCacheMap)
  }

  val service: CharitiesKeyStoreService = inject[CharitiesKeyStoreService]

  private val sessionId = s"session-${UUID.randomUUID}"
  private val requestWithSession = fakeRequest.withSession(SessionKeys.sessionId -> sessionId)

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

    def removeResponse(): Future[HttpResponse] = Future.successful(HttpResponse.apply(204, ""))

    def initialiseCache() {
      when(mockCharitiesShortLivedCache.fetch(any())(any(), any())).thenReturn(Future.successful(Some(mockCacheMap)))
      when(mockCharitiesShortLivedCache.cache(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(mockCacheMap))
      when(mockCacheMap.getEntry[CharityContactDetails](meq("charityContactDetails"))(meq(CharityContactDetails.formats))).thenReturn(mockContactDetails)
      when(mockCacheMap.getEntry[CharityAddress](meq("charityOfficialAddress"))(meq(CharityAddress.formats))).thenReturn(mockCharityAddress)
      when(mockCacheMap.getEntry[OptionalCharityAddress](meq("correspondenceAddress"))(meq(OptionalCharityAddress.formats))).thenReturn(mockCorrespondenceAddress)
      when(mockCacheMap.getEntry[CharityRegulator](meq("charityRegulator"))(meq(CharityRegulator.formats))).thenReturn(mockCharityRegulator)
      when(mockCacheMap.getEntry[CharityGoverningDocument](meq("charityGoverningDocument"))(meq(CharityGoverningDocument.formats))).thenReturn(mockCharityGoverningDocument)
      when(mockCacheMap.getEntry[WhatYourCharityDoes](meq("whatYourCharityDoes"))(meq(WhatYourCharityDoes.formats))).thenReturn(mockWhatYourCharityDoes)
      when(mockCacheMap.getEntry[OperationAndFunds](meq("operationAndFunds"))(meq(OperationAndFunds.formats))).thenReturn(mockOperationAndFunds)
      when(mockCacheMap.getEntry[CharityBankAccountDetails](meq("charityBankAccountDetails"))(meq(CharityBankAccountDetails.formats))).thenReturn(mockCharityBankAccountDetails)
      when(mockCacheMap.getEntry[CharityHowManyAuthOfficials](meq("charityHowManyAuthOfficials"))(meq(CharityHowManyAuthOfficials.formats))).thenReturn(mockCharityHowManyAuthOfficials)
      when(mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("authorisedOfficialIndividual1"))(meq(CharityAuthorisedOfficialIndividual.formats))).thenReturn(mockCharityAuthorisedOfficialIndividual1)
      when(mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("authorisedOfficialIndividual2"))(meq(CharityAuthorisedOfficialIndividual.formats))).thenReturn(mockCharityAuthorisedOfficialIndividual2)
      when(mockCacheMap.getEntry[CharityHowManyOtherOfficials](meq("charityHowManyOtherOfficials"))(meq(CharityHowManyOtherOfficials.formats))).thenReturn(mockCharityHowManyOtherOfficials)
      when(mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("otherOfficialIndividual1"))(meq(CharityAuthorisedOfficialIndividual.formats))).thenReturn(mockCharityOtherOfficialIndividual1)
      when(mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("otherOfficialIndividual2"))(meq(CharityAuthorisedOfficialIndividual.formats))).thenReturn(mockCharityOtherOfficialIndividual2)
      when(mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("otherOfficialIndividual3"))(meq(CharityAuthorisedOfficialIndividual.formats))).thenReturn(mockCharityOtherOfficialIndividual3)
      when(mockCacheMap.getEntry[CharityAddNominee](meq("charityAddNominee"))(meq(CharityAddNominee.formats))).thenReturn(mockCharityAddNominee)
      when(mockCacheMap.getEntry[CharityNomineeStatus](meq("charityNomineeStatus"))(meq(CharityNomineeStatus.formats))).thenReturn(mockCharityNomineeStatus)
      when(mockCacheMap.getEntry[CharityNomineeIndividual](meq("charityNomineeIndividual"))(meq(CharityNomineeIndividual.formats))).thenReturn(mockCharityNomineeIndividual)
      when(mockCacheMap.getEntry[CharityNomineeOrganisation](meq("charityNomineeOrganisation"))(meq(CharityNomineeOrganisation.formats))).thenReturn(mockCharityNomineeOrganisation)
      when(mockCharitiesShortLivedCache.remove(any())(any(), any())).thenReturn(removeResponse)
    }

  }

  "CharitiesKeyStoreService" when {

    "getCacheData" must {

      "return none if its empty" in new LocalSetup {

        initialiseCache()

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe Json.obj()
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for contactDetails only" in new LocalSetup {

        override val mockContactDetails: Option[CharityContactDetails] = Some(CharityContactDetails("Test123", None, "1234567890", None, None, None))

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for contactDetails and charityAddress" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)

        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false,
          "charityOfficialAddress" -> Json.parse("""{"postcode":"postcode","country":{"code":"GB","name":"GB"},"lines":["Test123","line2"]}""")))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for contactDetails, charityAddress and correspondenceAddress" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)

        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)

        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false,
          "canWeSendLettersToThisAddress" -> false,
          "charityOfficialAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}""")))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress and regulator" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)

        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)

        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)

        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false,
          "canWeSendLettersToThisAddress" -> false,
          "charityOfficialAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "isSection2Completed" -> false,
          "charityRegulator" -> Json.parse("""["ccew","oscr","otherRegulator"]"""),
          "isCharityRegulator" -> true,
          "charityOtherRegulatorDetails" -> Json.parse("""{"regulatorName":"otherRegulatorName","registrationNumber":"otherRegulatorRegistrationNumber"}"""),
          "scottishRegulatorRegNumber" -> "ccewTestRegulator",
          "nIRegulatorRegNumber" -> "",
          "charityCommissionRegistrationNumber" -> "ccewTestRegulator"
        ))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress, regulator and charityGoverningDocument2" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)

        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)

        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)

        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)

        override def mockCharityGoverningDocument: Option[CharityGoverningDocument] = Some(charityGoverningDocument2)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false,
          "canWeSendLettersToThisAddress" -> false,
          "charityOfficialAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "isSection2Completed" -> false,
          "charityRegulator" -> Json.parse("""["ccew","oscr","otherRegulator"]"""),
          "isCharityRegulator" -> true,
          "charityOtherRegulatorDetails" -> Json.parse("""{"regulatorName":"otherRegulatorName","registrationNumber":"otherRegulatorRegistrationNumber"}"""),
          "scottishRegulatorRegNumber" -> "ccewTestRegulator",
          "nIRegulatorRegNumber" -> "",
          "charityCommissionRegistrationNumber" -> "ccewTestRegulator",
          "whenGoverningDocumentApproved" -> "1990-11-11",
          "sectionsChangedGoverningDocument" -> "test",
          "governingDocumentName" -> "",
          "isApprovedGoverningDocument" -> true,
          "isSection3Completed" -> false,
          "selectGoverningDocument" -> "1"
        ))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress, regulator, charityGoverningDocument2 and whatYourCharityDoes" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)

        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)

        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)

        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)

        override def mockCharityGoverningDocument: Option[CharityGoverningDocument] = Some(charityGoverningDocument2)

        override def mockWhatYourCharityDoes: Option[WhatYourCharityDoes] = Some(whatYourCharityDoes)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false,
          "canWeSendLettersToThisAddress" -> false,
          "charityOfficialAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "isSection2Completed" -> false,
          "charityRegulator" -> Json.parse("""["ccew","oscr","otherRegulator"]"""),
          "isCharityRegulator" -> true,
          "charityOtherRegulatorDetails" -> Json.parse("""{"regulatorName":"otherRegulatorName","registrationNumber":"otherRegulatorRegistrationNumber"}"""),
          "scottishRegulatorRegNumber" -> "ccewTestRegulator",
          "nIRegulatorRegNumber" -> "",
          "charityCommissionRegistrationNumber" -> "ccewTestRegulator",
          "whenGoverningDocumentApproved" -> "1990-11-11",
          "sectionsChangedGoverningDocument" -> "test",
          "governingDocumentName" -> "",
          "isApprovedGoverningDocument" -> true,
          "isSection3Completed" -> false,
          "selectGoverningDocument" -> "1",
          "charitableObjectives" -> "objectives",
          "whatYourCharityDoesOtherReason" -> "otherReason",
          "charitablePurposes" -> Json.parse("""["reliefOfPoverty"]"""),
          "publicBenefits" -> "benefitThePublic",
          "isSection4Completed" -> false
        ))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress, regulator, charityGoverningDocument2, whatYourCharityDoes and operationAndFunds" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)

        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)

        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)

        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)

        override def mockCharityGoverningDocument: Option[CharityGoverningDocument] = Some(charityGoverningDocument2)

        override def mockWhatYourCharityDoes: Option[WhatYourCharityDoes] = Some(whatYourCharityDoes)

        override def mockOperationAndFunds: Option[OperationAndFunds] = Some(operationAndFunds)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false,
          "canWeSendLettersToThisAddress" -> false,
          "charityOfficialAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "isSection2Completed" -> false,
          "charityRegulator" -> Json.parse("""["ccew","oscr","otherRegulator"]"""),
          "isCharityRegulator" -> true,
          "charityOtherRegulatorDetails" -> Json.parse("""{"regulatorName":"otherRegulatorName","registrationNumber":"otherRegulatorRegistrationNumber"}"""),
          "scottishRegulatorRegNumber" -> "ccewTestRegulator",
          "nIRegulatorRegNumber" -> "",
          "charityCommissionRegistrationNumber" -> "ccewTestRegulator",
          "whenGoverningDocumentApproved" -> "1990-11-11",
          "sectionsChangedGoverningDocument" -> "test",
          "governingDocumentName" -> "",
          "isApprovedGoverningDocument" -> true,
          "isSection3Completed" -> false,
          "selectGoverningDocument" -> "1",
          "charitableObjectives" -> "objectives",
          "whatYourCharityDoesOtherReason" -> "otherReason",
          "charitablePurposes" -> Json.parse("""["reliefOfPoverty"]"""),
          "publicBenefits" -> "benefitThePublic",
          "isSection4Completed" -> false,
          "estimatedIncome" -> 100,
          "actualIncome" -> 100,
          "isFinancialAccounts" -> true,
          "otherFundRaising" -> "fundsOther",
          "publicBenefits" -> "benefitThePublic",
          "isApprovedGoverningDocument" -> true,
          "isSection5Completed" -> false,
          "whatYourCharityDoesOtherReason" -> "otherReason",
          "operatingLocation" -> Json.parse("""["1","2"]"""),
          "selectFundRaising" -> Json.parse("""["donations"]"""),
          "whenGoverningDocumentApproved" -> "1990-11-11",
          "governingDocumentName" -> "",
          "publicBenefits" -> "benefitThePublic",
          "isBankStatements" -> true,
          "whyNoBankStatement" -> "noBankStatements",
          "accountingPeriodEndDate" -> "--1-1",
          "isSection5Completed" -> false
        ))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for how many auth officials and two auth officials" in new LocalSetup {

        override def mockCharityHowManyAuthOfficials: Option[CharityHowManyAuthOfficials] = Some(charityHowManyAuthOfficials)

        override def mockCharityAuthorisedOfficialIndividual1: Option[CharityAuthorisedOfficialIndividual] = Some(charityAuthorisedOfficialIndividual1)

        override def mockCharityAuthorisedOfficialIndividual2: Option[CharityAuthorisedOfficialIndividual] = Some(charityAuthorisedOfficialIndividual2)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "isAddAnotherOfficial" -> true,
          "isSection7Completed" -> false,
          "authorisedOfficials" -> Json.arr(Json.parse(
            """
              |{
              |            "isOfficialPreviousAddress": true,
              |            "officialsPhoneNumber": {
              |                "mobilePhone": "0123123124",
              |                "daytimePhone": "0123123123"
              |            },
              |            "officialsPosition": "01",
              |            "officialsDOB": "1990-01-01",
              |            "isOfficialNino": true,
              |            "officialAddress": {
              |                "country": {
              |                    "code": "GB",
              |                    "name": "GB"
              |                },
              |                "postcode": "AA1 1AA",
              |                "lines": [
              |                    "current",
              |                    "address"
              |                ]
              |            },
              |            "officialPreviousAddress": {
              |                "country": {
              |                    "code": "GB",
              |                    "name": "GB"
              |                },
              |                "postcode": "AA2 2AA",
              |                "lines": [
              |                    "previous",
              |                    "address"
              |                ]
              |            },
              |            "officialsName": {
              |                "firstName": "First",
              |                "lastName": "Last",
              |                "middleName": "Middle",
              |                "title": "0001"
              |            },
              |            "officialsNino": "AB111111A"
              |        }""".stripMargin), Json.parse(
            """
              |{
              |            "isOfficialPreviousAddress": true,
              |            "officialsPhoneNumber": {
              |                "mobilePhone": "0123123124",
              |                "daytimePhone": "0123123123"
              |            },
              |            "officialsPosition": "01",
              |            "officialsDOB": "1990-01-01",
              |            "isOfficialNino": false,
              |            "officialAddress": {
              |                "country": {
              |                    "code": "GB",
              |                    "name": "GB"
              |                },
              |                "postcode": "AA1 1AA",
              |                "lines": [
              |                    "current",
              |                    "address"
              |                ]
              |            },
              |            "officialPreviousAddress": {
              |                "country": {
              |                    "code": "GB",
              |                    "name": "GB"
              |                },
              |                "postcode": "AA2 2AA",
              |                "lines": [
              |                    "previous",
              |                    "address"
              |                ]
              |            },
              |            "officialsName": {
              |                "firstName": "First",
              |                "lastName": "Last",
              |                "middleName": "Middle",
              |                "title": "unsupported"
              |            },
              |            "officialsPassport": {
              |              "country": "Country",
              |              "expiryDate": "2100-01-01",
              |              "passportNumber": "PaspNum"
              |            }
              |        }""".stripMargin))
        ))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress, regulator, charityGoverningDocument, whatYourCharityDoes, operationAndFunds, charityBankAccountDetails, how many auth officials and two auth officials, how many other officials and two other officials" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)

        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)

        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)

        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)

        override def mockCharityGoverningDocument: Option[CharityGoverningDocument] = Some(charityGoverningDocument2)

        override def mockWhatYourCharityDoes: Option[WhatYourCharityDoes] = Some(whatYourCharityDoes)

        override def mockOperationAndFunds: Option[OperationAndFunds] = Some(operationAndFunds)

        override def mockCharityBankAccountDetails: Option[CharityBankAccountDetails] = Some(charityBankAccountDetails)

        override def mockCharityHowManyAuthOfficials: Option[CharityHowManyAuthOfficials] = Some(charityHowManyAuthOfficials)

        override def mockCharityAuthorisedOfficialIndividual1: Option[CharityAuthorisedOfficialIndividual] = Some(charityAuthorisedOfficialIndividual1)

        override def mockCharityAuthorisedOfficialIndividual2: Option[CharityAuthorisedOfficialIndividual] = Some(charityAuthorisedOfficialIndividual2)

        override def mockCharityHowManyOtherOfficials: Option[CharityHowManyOtherOfficials] = Some(charityHowManyOtherOfficials)

        override def mockCharityOtherOfficialIndividual1: Option[CharityAuthorisedOfficialIndividual] = Some(charityOtherOfficialIndividual1)

        override def mockCharityOtherOfficialIndividual2: Option[CharityAuthorisedOfficialIndividual] = Some(charityOtherOfficialIndividual2)

        override def mockCharityOtherOfficialIndividual3: Option[CharityAuthorisedOfficialIndividual] = Some(charityOtherOfficialIndividual3)


        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false,
          "canWeSendLettersToThisAddress" -> false,
          "charityOfficialAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "charityPostalAddress" -> Json.parse("""{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]}"""),
          "isSection2Completed" -> false,
          "charityRegulator" -> Json.parse("""["ccew","oscr","otherRegulator"]"""),
          "isCharityRegulator" -> true,
          "charityOtherRegulatorDetails" -> Json.parse("""{"regulatorName":"otherRegulatorName","registrationNumber":"otherRegulatorRegistrationNumber"}"""),
          "scottishRegulatorRegNumber" -> "ccewTestRegulator",
          "nIRegulatorRegNumber" -> "",
          "charityCommissionRegistrationNumber" -> "ccewTestRegulator",
          "whenGoverningDocumentApproved" -> "1990-11-11",
          "sectionsChangedGoverningDocument" -> "test",
          "governingDocumentName" -> "",
          "isApprovedGoverningDocument" -> true,
          "isSection3Completed" -> false,
          "selectGoverningDocument" -> "1",
          "charitableObjectives" -> "objectives",
          "whatYourCharityDoesOtherReason" -> "otherReason",
          "charitablePurposes" -> Json.parse("""["reliefOfPoverty"]"""),
          "publicBenefits" -> "benefitThePublic",
          "isSection4Completed" -> false,
          "estimatedIncome" -> 100,
          "actualIncome" -> 100,
          "isFinancialAccounts" -> true,
          "otherFundRaising" -> "fundsOther",
          "publicBenefits" -> "benefitThePublic",
          "isApprovedGoverningDocument" -> true,
          "isSection5Completed" -> false,
          "whatYourCharityDoesOtherReason" -> "otherReason",
          "operatingLocation" -> Json.parse("""["1","2"]"""),
          "selectFundRaising" -> Json.parse("""["donations"]"""),
          "whenGoverningDocumentApproved" -> "1990-11-11",
          "governingDocumentName" -> "",
          "publicBenefits" -> "benefitThePublic",
          "isBankStatements" -> true,
          "whyNoBankStatement" -> "noBankStatements",
          "accountingPeriodEndDate" -> "--1-1",
          "isSection5Completed" -> false,
          "bankDetails" -> Json.parse("""{"accountName":"Tesco","accountNumber":"123456","sortCode":"12345678","rollNumber":"rollNumber"}"""),
          "isSection6Completed" -> false,
          "isAddAnotherOfficial" -> true,
          "isSection7Completed" -> false,
          "authorisedOfficials" -> Json.arr(Json.parse(
            """
              |{
              |            "isOfficialPreviousAddress": true,
              |            "officialsPhoneNumber": {
              |                "mobilePhone": "0123123124",
              |                "daytimePhone": "0123123123"
              |            },
              |            "officialsPosition": "01",
              |            "officialsDOB": "1990-01-01",
              |            "isOfficialNino": true,
              |            "officialAddress": {
              |                "country": {
              |                    "code": "GB",
              |                    "name": "GB"
              |                },
              |                "postcode": "AA1 1AA",
              |                "lines": [
              |                    "current",
              |                    "address"
              |                ]
              |            },
              |            "officialPreviousAddress": {
              |                "country": {
              |                    "code": "GB",
              |                    "name": "GB"
              |                },
              |                "postcode": "AA2 2AA",
              |                "lines": [
              |                    "previous",
              |                    "address"
              |                ]
              |            },
              |            "officialsName": {
              |                "firstName": "First",
              |                "lastName": "Last",
              |                "middleName": "Middle",
              |                "title": "0001"
              |            },
              |            "officialsNino": "AB111111A"
              |        }""".stripMargin), Json.parse(
            """
              |{
              |            "isOfficialPreviousAddress": true,
              |            "officialsPhoneNumber": {
              |                "mobilePhone": "0123123124",
              |                "daytimePhone": "0123123123"
              |            },
              |            "officialsPosition": "01",
              |            "officialsDOB": "1990-01-01",
              |            "isOfficialNino": false,
              |            "officialAddress": {
              |                "country": {
              |                    "code": "GB",
              |                    "name": "GB"
              |                },
              |                "postcode": "AA1 1AA",
              |                "lines": [
              |                    "current",
              |                    "address"
              |                ]
              |            },
              |            "officialPreviousAddress": {
              |                "country": {
              |                    "code": "GB",
              |                    "name": "GB"
              |                },
              |                "postcode": "AA2 2AA",
              |                "lines": [
              |                    "previous",
              |                    "address"
              |                ]
              |            },
              |            "officialsName": {
              |                "firstName": "First",
              |                "lastName": "Last",
              |                "middleName": "Middle",
              |                "title": "unsupported"
              |            },
              |            "officialsPassport": {
              |              "country": "Country",
              |              "expiryDate": "2100-01-01",
              |              "passportNumber": "PaspNum"
              |            }
              |        }""".stripMargin)),
          "addAnotherOtherOfficial" -> true,
          "isSection8Completed" -> false,

          "otherOfficials" -> Json.arr(
            Json.parse(
              """{
                |  "isOfficialNino": true,
                |  "isOfficialsPreviousAddress": true,
                |  "officialAddress": {
                |    "country": {
                |      "code": "GB",
                |      "name": "GB"
                |    },
                |    "lines": [
                |      "current",
                |      "address"
                |    ],
                |    "postcode": "AA1 1AA"
                |  },
                |  "officialPreviousAddress": {
                |    "country": {
                |      "code": "GB",
                |      "name": "GB"
                |    },
                |    "lines": [
                |      "previous",
                |      "address"
                |    ],
                |    "postcode": "AA2 2AA"
                |  },
                |  "officialsDOB": "1990-01-01",
                |  "officialsName": {
                |    "firstName": "First",
                |    "lastName": "Last",
                |    "middleName": "Middle",
                |    "title": "0001"
                |  },
                |  "officialsNino": "AB111111A",
                |  "officialsPhoneNumber": {
                |    "daytimePhone": "0123123123",
                |    "mobilePhone": "0123123124"
                |  },
                |  "officialsPosition": "01"
                |}
                |""".stripMargin),
            Json.parse(
              """
                |{
                |  "isOfficialNino": false,
                |  "isOfficialsPreviousAddress": true,
                |  "officialAddress": {
                |    "country": {
                |      "code": "GB",
                |      "name": "GB"
                |    },
                |    "lines": [
                |      "current",
                |      "address"
                |    ],
                |    "postcode": "AA1 1AA"
                |  },
                |  "officialPreviousAddress": {
                |    "country": {
                |      "code": "GB",
                |      "name": "GB"
                |    },
                |    "lines": [
                |      "previous",
                |      "address"
                |    ],
                |    "postcode": "AA2 2AA"
                |  },
                |  "officialsDOB": "1990-01-01",
                |  "officialsName": {
                |    "firstName": "First",
                |    "lastName": "Last",
                |    "middleName": "Middle",
                |    "title": "unsupported"
                |  },
                |  "officialsPassport": {
                |    "country": "Country",
                |    "expiryDate": "2100-01-01",
                |    "passportNumber": "PaspNum"
                |  },
                |  "officialsPhoneNumber": {
                |    "daytimePhone": "0123123123",
                |    "mobilePhone": "0123123124"
                |  },
                |  "officialsPosition": "01"
                |}
                |""".stripMargin),
            Json.parse(
              """
                |{
                |  "isOfficialNino": true,
                |  "isOfficialsPreviousAddress": true,
                |  "officialAddress": {
                |    "country": {
                |      "code": "GB",
                |      "name": "GB"
                |    },
                |    "lines": [
                |      "current",
                |      "address"
                |    ],
                |  "postcode": "AA1 1AA"
                |  },
                |  "officialPreviousAddress": {
                |    "country": {
                |      "code": "GB",
                |      "name": "GB"
                |    },
                |    "lines": [
                |      "previous",
                |      "address"
                |    ],
                |    "postcode": "AA2 2AA"
                |  },
                |  "officialsDOB": "1990-01-01",
                |  "officialsName": {
                |    "firstName": "First",
                |    "lastName": "Last",
                |    "middleName": "Middle",
                |    "title": "unsupported"
                |  },
                |  "officialsNino": "AB111111A",
                |  "officialsPhoneNumber": {
                |    "daytimePhone": "0123123123",
                |    "mobilePhone": "0123123124"
                |  },
                |  "officialsPosition": "01"
                |}
                |""".stripMargin)
          )

        ))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid charityAddNominee" in new LocalSetup {

        override def mockCharityAddNominee: Option[CharityAddNominee] = Some(charityNoNominee)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "isSection9Completed" -> true,
          "nominee" -> Json.parse("""{"isAuthoriseNominee":false}""")))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid charityNomineeStatus" in new LocalSetup {

        override def mockCharityAddNominee: Option[CharityAddNominee] = Some(charityAddNominee)

        override def mockCharityNomineeStatus: Option[CharityNomineeStatus] = Some(charityNomineeStatusInd)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "isSection9Completed" -> false,
          "nominee" -> Json.parse("""{"chooseNominee":true,"isAuthoriseNominee":true}""")))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid charityNomineeStatus, charityNomineeIndividual" in new LocalSetup {

        override def mockCharityAddNominee: Option[CharityAddNominee] = Some(charityAddNominee)

        override def mockCharityNomineeStatus: Option[CharityNomineeStatus] = Some(charityNomineeStatusInd)

        override def mockCharityNomineeIndividual: Option[CharityNomineeIndividual] = Some(charityNomineeIndividual)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "isSection9Completed" -> false,
          "nominee" -> Json.parse(
            """{"chooseNominee":true,"isAuthoriseNominee":true,
							|"individual":{"isIndividualPreviousAddress":false,"individualDOB":"2000-10-10","isIndividualNino":true,
							|"individualAddress":{"country":{"code":"UK","name":"UK"},"postcode":"","lines":["Line1","Line2","Line3","Line5"]},
							|"isIndividualNomineePayments":false,"individualNino":"CS700100A",
							|"individualName":{"firstName":"firstName","lastName":"lastName","middleName":"middleName","title":"unsupported"},
							|"individualPhoneNumber":{"daytimePhone":""}}}""".stripMargin)))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "return valid object when its valid charityNomineeStatus, charityNomineeOrganisation" in new LocalSetup {

        override def mockCharityAddNominee: Option[CharityAddNominee] = Some(charityAddNominee)

        override def mockCharityNomineeStatus: Option[CharityNomineeStatus] = Some(charityNomineeStatusOrg)

        override def mockCharityNomineeOrganisation: Option[CharityNomineeOrganisation] = Some(charityNomineeOrganisation)

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "isSection9Completed" -> false,
          "nominee" -> Json.parse(
            """{"chooseNominee":false,"isAuthoriseNominee":true,
							|"organisation":{"isOrganisationPreviousAddress":true,"isOrganisationNino":false,
							|"isOrganisationNomineePayments":true,"organisationName":"Tesco",
							|"organisationPreviousAddress":{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]},
							|"organisationAuthorisedPersonName":{"firstName":"firstName","lastName":"lastName","middleName":"middleName","title":"unsupported"},
							|"organisationAuthorisedPersonDOB":"2000-10-10",
							|"organisationAddress":{"country":{"code":"GB","name":"GB"},"postcode":"postcode","lines":["Test123","line2"]},
							|"organisationBankDetails":{"accountName":"AABB","rollNumber":"BB","accountNumber":"12345678","sortCode":"123456"},
							|"organisationAuthorisedPersonPassport":{"passportNumber":"AK123456K","expiryDate":"2000-10-10","country":"UK"},
							|"organisationContactDetails":{"phoneNumber":"1234567890","email":""}}}""".stripMargin)))

        val result: (UserAnswers, Seq[(JsPath, Seq[JsonValidationError])]) = await(service.getCacheData(optionalDataRequest))

        result._1.data mustBe responseJson.data
        result._2 mustBe Seq.empty
      }

      "throw error if session is not valid" in new LocalSetup {
        implicit val hc: HeaderCarrier = HeaderCarrier()
        override val mockContactDetails: Option[CharityContactDetails] = Some(CharityContactDetails("Test123", None, "1234567890", None, None, None))
        override def removeResponse: Future[HttpResponse] = Future.failed(new RuntimeException())

        initialiseCache()

        intercept[RuntimeException]{
          await(service.getCacheData(optionalDataRequest))
        }

      }

      "throw error if exception is returned from CharitiesShortLivedCache.remove" in new LocalSetup {

        override val mockContactDetails: Option[CharityContactDetails] = Some(CharityContactDetails("Test123", None, "1234567890", None, None, None))

        override def removeResponse: Future[HttpResponse] = Future.failed(new RuntimeException())

        initialiseCache()

        intercept[RuntimeException] {
          await(service.getCacheData(optionalDataRequest))
        }

      }

      "throw error if exception is returned from CharitiesShortLivedCache" in {

        when(mockCharitiesShortLivedCache.fetch(any())(any(), any())).thenReturn(Future.failed(new RuntimeException()))

        intercept[RuntimeException] {
          await(service.getCacheData(optionalDataRequest))
        }
      }

    }

  }

}
