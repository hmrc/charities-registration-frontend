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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import config.FrontendAppConfig
import models.requests.{BarsBankAccount, BarsValidateRequest}
import org.mockito.Mockito.*
import play.api.http.Status.*
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2

class BarsConnectorSpec extends SpecBase with WireMockHelper {

  lazy val mockFrontendAppConfig: FrontendAppConfig = mock(classOf[FrontendAppConfig])

  "CharitiesConnector" when {

    val httpClient: HttpClientV2                    = injector.instanceOf[HttpClientV2]
    lazy val barsConnector: BarsConnector = new BarsConnector(httpClient, mockFrontendAppConfig)

    when(mockFrontendAppConfig.barsBaseUrl) `thenReturn` getUrl

    "called validateBankDetails method" should {

      "for a successful response" must {

        "return a httpResponse" in {

          val request = BarsValidateRequest(BarsBankAccount(sortCode = "123456", accountNumber = "12345678"))

          stubFor(
            post(urlEqualTo("/validate/bank-details"))
              .withRequestBody(equalToJson(Json.toJson(request).toString))
              .willReturn(
                aResponse()
                  .withBody(Json.toJson(
                    """{
                      |"accountNumberIsWellFormatted": "yes",
                      |"nonStandardAccountDetailsRequiredForBacs": "yes",
                      |"sortCodeIsPresentOnEISCD":"yes",
                      |}""".stripMargin).toString())
                  .withStatus(OK)
              )
          )

          val expectedResult = HttpResponse(OK, """{
                                                  |"accountNumberIsWellFormatted": "yes",
                                                  |"nonStandardAccountDetailsRequiredForBacs": "yes",
                                                  |"sortCodeIsPresentOnEISCD":"yes",
                                                  |}""".stripMargin)
          val actualResult   = await(barsConnector.validateBankDetails(request)(hc))

          actualResult.status mustBe expectedResult.status

          WireMock.verify(postRequestedFor(urlEqualTo("/validate/bank-details")))
        }
      }

      "for an error response" must {

        "return a http response" in {
          
          val request = BarsValidateRequest(BarsBankAccount(sortCode = "123456", accountNumber = "12345678"))

          stubFor(
            post(urlEqualTo("/validate/bank-details"))
              .withRequestBody(equalToJson(Json.toJson(request).toString))
              .willReturn(
                aResponse()
                  .withBody(Json.toJson(
                    """{
                      |"accountNumberIsWellFormatted": "yes",
                      |"nonStandardAccountDetailsRequiredForBacs": "yes",
                      |"sortCodeIsPresentOnEISCD":"yes",
                      |}""".stripMargin).toString())
                  .withStatus(BAD_REQUEST)
              )
          )

          val expectedResult = HttpResponse(BAD_REQUEST, """{
                                                  |"accountNumberIsWellFormatted": "yes",
                                                  |"nonStandardAccountDetailsRequiredForBacs": "yes",
                                                  |"sortCodeIsPresentOnEISCD":"yes",
                                                  |}""".stripMargin)
          val actualResult   = await(barsConnector.validateBankDetails(request)(hc))

          actualResult.status mustBe expectedResult.status

          WireMock.verify(postRequestedFor(urlEqualTo("/validate/bank-details")))
        }
      }
    }
  }
}
