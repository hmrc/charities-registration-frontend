/*
 * Copyright 2024 HM Revenue & Customs
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

import audit.{AuditService, SubmissionAuditEvent}
import base.SpecBase
import connectors.CharitiesConnector
import connectors.httpParsers.{CharitiesInvalidJson, UnexpectedFailureException}
import models.requests.DataRequest
import models.{RegistrationResponse, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import pages.AcknowledgementReferencePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._

import scala.concurrent.Future

class CharitiesRegistrationServiceSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers]                           = Some(emptyUserAnswers)
  lazy val mockAuditService: AuditService                                      = MockitoSugar.mock[AuditService]
  lazy val mockCharitiesConnector: CharitiesConnector                          = MockitoSugar.mock[CharitiesConnector]
  lazy val userAnswersWithAcknowledgement: UserAnswers                         = emptyUserAnswers
    .set(AcknowledgementReferencePage, "acknowledgement")
    .success
    .value
  lazy val dataRequestWithAcknowledgement: DataRequest[AnyContentAsEmpty.type] =
    DataRequest(fakeRequest, internalId, userAnswersWithAcknowledgement)

  val expectedJsonObject: JsObject =
    Json
      .parse("""{
               |  "charityRegistration": {
               |    "common": {
               |      "bankDetails": {
               |        "sortCode": "000123",
               |        "accountNumber": "00000123"
               |      }
               |    }
               |  }
               |}""".stripMargin)
      .asInstanceOf[JsObject]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[AuditService].toInstance(mockAuditService),
        bind[CharitiesConnector].toInstance(mockCharitiesConnector)
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
    reset(mockAuditService)
    reset(mockCharitiesConnector)
  }

  private val service: CharitiesRegistrationService = inject[CharitiesRegistrationService]

  "Charities Registration Service" must {

    "redirect to next page if acknowledgement reference is already present" in {

      when(mockUserAnswerService.get(AcknowledgementReferencePage))
        .thenReturn(Future.successful(Some(userAnswersWithAcknowledgement)))

      val result = service.register(Json.obj(), noEmailPost = false)(dataRequestWithAcknowledgement, hc, ec)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.EmailOrPostController.onPageLoad.url)

    }

    "redirect to the next page after valid registration response and noEmailPost is disabled" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(userAnswers))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))
      when(mockCharitiesConnector.registerCharities(any(), any())(any(), any())).thenReturn(
        Future.successful(Right(RegistrationResponse("765432")))
      )

      doNothing().when(mockAuditService).sendEvent(any())(any(), any())

      val result = service.register(expectedJsonObject, noEmailPost = false)(fakeDataRequest, hc, ec)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.EmailOrPostController.onPageLoad.url)
      verify(mockCharitiesConnector, times(1)).registerCharities(any(), any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())

      verify(mockAuditService, times(1)).sendEvent(any())(any(), any())
      verify(mockAuditService, atLeastOnce()).sendEvent(any[SubmissionAuditEvent])(any(), any())
    }

    "redirect to the next page after valid registration response noEmailPost is enabled" in {
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(userAnswers))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))
      when(mockCharitiesConnector.registerCharities(any(), any())(any(), any())).thenReturn(
        Future.successful(Right(RegistrationResponse("765432")))
      )
      doNothing().when(mockAuditService).sendEvent(any())(any(), any())

      val result = service.register(expectedJsonObject, noEmailPost = true)(fakeDataRequest, hc, ec)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
      verify(mockCharitiesConnector, times(1)).registerCharities(any(), any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())

      verify(mockAuditService, times(1)).sendEvent(any())(any(), any())
      verify(mockAuditService, atLeastOnce()).sendEvent(any[SubmissionAuditEvent])(any(), any())
    }

    "redirect to the technical difficulties page if registration connector failed" in {
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(userAnswers))

      when(mockCharitiesConnector.registerCharities(any(), any())(any(), any())).thenReturn(
        Future.successful(Left(CharitiesInvalidJson))
      )

      intercept[UnexpectedFailureException] {
        await(service.register(expectedJsonObject, noEmailPost = false)(fakeDataRequest, hc, ec))
      }

      verify(mockCharitiesConnector, times(1)).registerCharities(any(), any())(any(), any())
      verify(mockUserAnswerService, never()).set(any())(any(), any())
      verify(mockAuditService, never()).sendEvent(any())(any(), any())
    }

    "redirect to the technical difficulties page if UserAnswer Repository failed" in {
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(userAnswers))

      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.failed(new RuntimeException("failed")))
      when(mockCharitiesConnector.registerCharities(any(), any())(any(), any())).thenReturn(
        Future.successful(Right(RegistrationResponse("765432")))
      )

      intercept[UnexpectedFailureException] {
        await(service.register(expectedJsonObject, noEmailPost = false)(fakeDataRequest, hc, ec))
      }

      verify(mockCharitiesConnector, times(1)).registerCharities(any(), any())(any(), any())
      verify(mockUserAnswerService, times(1)).set(any())(any(), any())
      verify(mockAuditService, never()).sendEvent(any())(any(), any())
    }

    "redirect to the technical difficulties page if submission data is missing keys that need to be transformed " in {
      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(userAnswers))
      when(mockUserAnswerService.set(any())(any(), any())).thenReturn(Future.successful(true))

      val result = intercept[UnexpectedFailureException] {
        await(service.register(Json.obj(), noEmailPost = true)(fakeDataRequest, hc, ec))
      }
      result.getMessage must include("JsonValidationError(List(error.path.missing)")

    }

  }

}
