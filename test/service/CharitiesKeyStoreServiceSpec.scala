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
import org.joda.time.{LocalDate, MonthDay}
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, SessionKeys}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.logging.SessionId

import scala.concurrent.Future

class CharitiesKeyStoreServiceSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach {

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

  val contactDetails: CharityContactDetails = CharityContactDetails("Test123", None, "1234567890", None, None, None)
  val charityAddress: CharityAddress = CharityAddress("Test123", "line2", "", "", "postcode", "")
  val correspondenceAddress: OptionalCharityAddress = OptionalCharityAddress(Some("true"), CharityAddress("Test123", "line2", "", "", "postcode", ""))
  val charityRegulator: CharityRegulator = CharityRegulator(CharityRegulatorInfoDetails(isCharityRegulatorSelected = true, "ccewTestRegulator"),
    CharityRegulatorInfoDetails(isCharityRegulatorSelected = true, "ccewTestRegulator"), CharityRegulatorInfoDetails(isCharityRegulatorSelected = false, ""),
    CharityRegulatorOtherInfoDetails(isCharityOtherRegulatorSelected = true, "otherRegulatorName", "otherRegulatorRegistrationNumber"), CharityReasonForNotRegistering(None, None))
  val charityGoverningDocument: CharityGoverningDocument = CharityGoverningDocument("2", "", "test", Some(LocalDate.parse("1990-11-11")), Some(true))
  val whatYourCharityDoes: WhatYourCharityDoes = WhatYourCharityDoes("objectives", reliefOfPoverty = true, education = false, animalWelfare = false,
    healthOrSavingOfLives = false, citizenshipOrCommunityDevelopment = false, reliefOfThoseInNeed = false, religion = false,
    amateurSport = false, humanRights = false, artsCultureHeritageOrScience = false, environmentalProtectionOrImprovement = false,
    promotionOfEfficiencyInArmedForcesPoliceFireAndRescueService = false, whatYourCharityDoesOther = false,  "otherReason", "benefitThePublic")
  val operationAndFunds: OperationAndFunds = OperationAndFunds(
    OperationAndFundsCommon(ScalaMonthDay(MonthDay.fromDateFields(new LocalDate(2020, 1, 1).toDate)), Some(true), Some(true), Some("noBankStatements")),
    FutureFunds(donations = true, fundraising = false, grants = false, membershipSubscriptions = false, tradingIncome = false, tradingSubsidiaries = false, investmentIncome = false, other = false),
    Some("fundsOther"), Some(100), Some(100), Some(false),
    WhereWillCharityOperate(englandAndWales = true, scotland = false, northernIreland = false,ukWide = false, overseas = false),
    OtherCountriesOfOperation(Some("EN"), Some("EN"), None, None, None))
  val charityBankAccountDetails: CharityBankAccountDetails = CharityBankAccountDetails("Tesco", "123456", "12345678", Some("rollNumber"))

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
    def removeResponse: Future[HttpResponse] = Future.successful(HttpResponse.apply(204, ""))

    def initialiseCache(){
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
      when(mockCharitiesShortLivedCache.remove(any())(any(), any())).thenReturn(removeResponse)
    }

  }

  "CharitiesKeyStoreService" when {

    "getCacheData" must {

      "return none if its empty" in new LocalSetup {

        initialiseCache()

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe Json.obj()
      }

      "return valid object when its valid for contactDetails only" in new LocalSetup {

        override val mockContactDetails: Option[CharityContactDetails] = Some(CharityContactDetails("Test123", None, "1234567890", None, None, None))

        initialiseCache()

        val responseJson: UserAnswers = UserAnswers("8799940975137654", Json.obj(
          "charityContactDetails" -> Json.parse("""{"emailAddress":"","daytimePhone":"1234567890"}"""),
          "charityName" -> Json.parse("""{"fullName":"Test123"}"""),
          "isSection1Completed" -> false))

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe responseJson.data
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

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe responseJson.data
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

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe responseJson.data
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

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe responseJson.data
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress, regulator and charityGoverningDocument" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)
        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)
        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)
        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)
        override def mockCharityGoverningDocument: Option[CharityGoverningDocument] = Some(charityGoverningDocument)

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

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe responseJson.data
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress, regulator, charityGoverningDocument and whatYourCharityDoes" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)
        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)
        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)
        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)
        override def mockCharityGoverningDocument: Option[CharityGoverningDocument] = Some(charityGoverningDocument)
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

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe responseJson.data
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress, regulator, charityGoverningDocument, whatYourCharityDoes and operationAndFunds" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)
        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)
        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)
        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)
        override def mockCharityGoverningDocument: Option[CharityGoverningDocument] = Some(charityGoverningDocument)
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
          "isFinancialAccounts"-> true,
          "otherFundRaising"-> "fundsOther",
          "publicBenefits" -> "benefitThePublic",
          "isApprovedGoverningDocument"-> true,
          "isSection5Completed" -> false,
           "whatYourCharityDoesOtherReason" -> "otherReason",
           "operatingLocation" -> Json.parse("""["1","2"]"""),
           "selectFundRaising" -> Json.parse("""["donations"]"""),
           "whenGoverningDocumentApproved" ->  "1990-11-11",
           "governingDocumentName" -> "",
           "publicBenefits" -> "benefitThePublic",
           "isBankStatements" -> true,
            "whyNoBankStatement" -> "noBankStatements",
            "accountingPeriodEndDate" -> "--1-1",
           "isSection5Completed" -> false
          ))

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe responseJson.data
      }

      "return valid object when its valid for contactDetails, charityAddress, correspondenceAddress, regulator, charityGoverningDocument, whatYourCharityDoes, operationAndFunds and charityBankAccountDetails" in new LocalSetup {

        override def mockContactDetails: Option[CharityContactDetails] = Some(contactDetails)
        override def mockCharityAddress: Option[CharityAddress] = Some(charityAddress)
        override def mockCorrespondenceAddress: Option[OptionalCharityAddress] = Some(correspondenceAddress)
        override def mockCharityRegulator: Option[CharityRegulator] = Some(charityRegulator)
        override def mockCharityGoverningDocument: Option[CharityGoverningDocument] = Some(charityGoverningDocument)
        override def mockWhatYourCharityDoes: Option[WhatYourCharityDoes] = Some(whatYourCharityDoes)
        override def mockOperationAndFunds: Option[OperationAndFunds] = Some(operationAndFunds)
        override def mockCharityBankAccountDetails: Option[CharityBankAccountDetails] = Some(charityBankAccountDetails)

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
          "isFinancialAccounts"-> true,
          "otherFundRaising"-> "fundsOther",
          "publicBenefits" -> "benefitThePublic",
          "isApprovedGoverningDocument"-> true,
          "isSection5Completed" -> false,
           "whatYourCharityDoesOtherReason" -> "otherReason",
           "operatingLocation" -> Json.parse("""["1","2"]"""),
           "selectFundRaising" -> Json.parse("""["donations"]"""),
           "whenGoverningDocumentApproved" ->  "1990-11-11",
           "governingDocumentName" -> "",
           "publicBenefits" -> "benefitThePublic",
           "isBankStatements" -> true,
            "whyNoBankStatement" -> "noBankStatements",
            "accountingPeriodEndDate" -> "--1-1",
           "isSection5Completed" -> false,
           "bankDetails" -> Json.parse("""{"accountName":"Tesco","accountNumber":"123456","sortCode":"12345678","rollNumber":"rollNumber"}"""),
           "isSection6Completed" -> false
          ))

        val result: UserAnswers = await(service.getCacheData(optionalDataRequest))

        result.data mustBe responseJson.data
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

        intercept[RuntimeException]{
          await(service.getCacheData(optionalDataRequest))
        }

      }

      "throw error if exception is returned from CharitiesShortLivedCache" in {

        when(mockCharitiesShortLivedCache.fetch(any())(any(), any())).thenReturn(Future.failed(new RuntimeException()))

        intercept[RuntimeException]{
          await(service.getCacheData(optionalDataRequest))
        }
      }

    }

  }

}
