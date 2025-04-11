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

package service

import base.SpecBase
import connectors.BarsConnector
import models.responses.*
import models.responses.BarsAssessmentType.*
import uk.gov.hmrc.http.HttpResponse
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.http.Status.*

import scala.concurrent.Future

class BarsServiceSpec extends SpecBase with BeforeAndAfterEach {

  lazy val mockBarsConnector: BarsConnector = mock(classOf[BarsConnector])

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[BarsConnector].toInstance(mockBarsConnector)
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockBarsConnector)
  }

  private val service: BarsService = inject[BarsService]

  "Bars Service" must {

    "Validate bank account details" in {
      val barsConnectorResponse = """{
                                    |"accountNumberIsWellFormatted": "yes",
                                    |"nonStandardAccountDetailsRequiredForBacs": "no",
                                    |"sortCodeIsPresentOnEISCD":"yes"
        }""".stripMargin
      val response              = HttpResponse(status = OK, body = barsConnectorResponse)

      when(mockBarsConnector.validateBankDetails(any())(any())).thenReturn(Future.successful(response))
      val result = await(service.validateBankDetails(any())(any()))
      result mustBe Right(
        ValidateResponse(
          BarsValidateResponse(
            accountNumberIsWellFormatted = Yes,
            nonStandardAccountDetailsRequiredForBacs = No,
            sortCodeIsPresentOnEISCD = Yes
          )
        )
      )
      verify(mockBarsConnector, times(1)).validateBankDetails(any())(any())
    }

    "process error on sort code correctly" in {
      val barsConnectorResponse = """{
                                    |"code": "SORT_CODE_ON_DENY_LIST",
                                    |"desc": "..."
        }""".stripMargin
      val response              = HttpResponse(status = BAD_REQUEST, body = barsConnectorResponse)

      when(mockBarsConnector.validateBankDetails(any())(any())).thenReturn(Future.successful(response))
      val result = await(service.validateBankDetails(any())(any()))
      result mustBe Left(
        SortCodeOnDenyListErrorResponse(
          SortCodeOnDenyList(
            BarsErrorResponse(
              code = "SORT_CODE_ON_DENY_LIST",
              desc = "..."
            )
          )
        )
      )
      verify(mockBarsConnector, times(1)).validateBankDetails(any())(any())
    }

    "process error on account number correctly" in {
      val barsConnectorResponse = """{
                                    |"code": "INVALID_ACCOUNT_NUMBER",
                                    |"desc": "..."
        }""".stripMargin
      val response              = HttpResponse(status = BAD_REQUEST, body = barsConnectorResponse)

      when(mockBarsConnector.validateBankDetails(any())(any())).thenReturn(Future.successful(response))
      val result = await(service.validateBankDetails(any())(any()))
      result mustBe Left(
        InvalidAccountNumberErrorResponse(
          InvalidAccountNumber(
            BarsErrorResponse(
              code = "INVALID_ACCOUNT_NUMBER",
              desc = "..."
            )
          )
        )
      )
      verify(mockBarsConnector, times(1)).validateBankDetails(any())(any())
    }

    "process validate failure properly for Account Number Not Well Formatted" in {
      val barsConnectorResponse = """{
                                    |"accountNumberIsWellFormatted": "no",
                                    |"nonStandardAccountDetailsRequiredForBacs": "yes",
                                    |"sortCodeIsPresentOnEISCD":"yes"
        }""".stripMargin
      val response              = HttpResponse(status = OK, body = barsConnectorResponse)

      when(mockBarsConnector.validateBankDetails(any())(any())).thenReturn(Future.successful(response))
      val result = await(service.validateBankDetails(any())(any()))
      result mustBe Left(
        AccountNumberNotWellFormattedValidateResponse(
          ValidateResponse(
            BarsValidateResponse(
              accountNumberIsWellFormatted = No,
              nonStandardAccountDetailsRequiredForBacs = Yes,
              sortCodeIsPresentOnEISCD = Yes
            )
          )
        )
      )
      verify(mockBarsConnector, times(1)).validateBankDetails(any())(any())
    }

    "process validate failure properly for Sort Code Not Present On Eiscd" in {
      val barsConnectorResponse =
        """{
          |"accountNumberIsWellFormatted": "yes",
          |"nonStandardAccountDetailsRequiredForBacs": "yes",
          |"sortCodeIsPresentOnEISCD":"no"
        }""".stripMargin
      val response              = HttpResponse(status = OK, body = barsConnectorResponse)

      when(mockBarsConnector.validateBankDetails(any())(any())).thenReturn(Future.successful(response))
      val result = await(service.validateBankDetails(any())(any()))
      result mustBe Left(
        SortCodeNotPresentOnEiscdValidateResponse(
          ValidateResponse(
            BarsValidateResponse(
              accountNumberIsWellFormatted = Yes,
              nonStandardAccountDetailsRequiredForBacs = Yes,
              sortCodeIsPresentOnEISCD = No
            )
          )
        )
      )
      verify(mockBarsConnector, times(1)).validateBankDetails(any())(any())
    }
  }
}
