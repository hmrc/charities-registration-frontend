/*
 * Copyright 2021 HM Revenue & Customs
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
import com.github.tomakehurst.wiremock.client.WireMock._
import config.FrontendAppConfig
import connectors.httpParsers.{CharitiesInvalidJson, DefaultedUnexpectedFailure, EtmpFailed}
import models.RegistrationResponse
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status._
import play.api.libs.json.{JsResultException, Json}
import uk.gov.hmrc.play.bootstrap.http.HttpClient


class CharitiesConnectorSpec extends SpecBase with WireMockHelper with MockitoSugar {

  lazy val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  "CharitiesConnector" when {

    val httpClient : HttpClient = injector.instanceOf[HttpClient]
    lazy val charitiesConnector : CharitiesConnector = new CharitiesConnector(httpClient, mockFrontendAppConfig)
    val requestJson = readJsonFromFile("/request.json")
    when(mockFrontendAppConfig.getCharitiesBackend) thenReturn getUrl

    "called registerCharities method" should {

      "for a successful response" must {

        "return a Right(Success response)" in {

          stubFor(post(urlEqualTo("/org/1234/submissions/application"))
            .withRequestBody(equalToJson(Json.stringify(requestJson)))
            .willReturn(aResponse().withBody(Json.parse("""{"acknowledgementReference":"765432"}""").toString())
            .withStatus(ACCEPTED)
            )
          )

          val expectedResult = Right(RegistrationResponse("765432"))
          val actualResult = await(charitiesConnector.registerCharities(requestJson, 1234)(hc, ec))

          actualResult mustBe expectedResult

          verify(postRequestedFor(urlEqualTo("/org/1234/submissions/application")))
        }

        "returns an exception if response is in incorrect format" in {

          stubFor(post(urlEqualTo("/org/1234/submissions/application"))
            .withRequestBody(equalToJson(Json.stringify(requestJson)))
            .willReturn(aResponse().withBody(Json.parse("""{"acknowledgementRef":"765432"}""").toString())
              .withStatus(ACCEPTED)
            )
          )

          intercept[JsResultException] {
            await(charitiesConnector.registerCharities(requestJson, 1234)(hc, ec))
          }

          verify(postRequestedFor(urlEqualTo("/org/1234/submissions/application")))
        }

      }

      "for an error response" must {

        "return a Left(CharitiesInvalidJson) when failed with invalid input json" in {

          stubFor(post(urlEqualTo("/org/1234/submissions/application"))
            .withRequestBody(equalToJson(Json.parse("""{"fullName":"Johnson"}""").toString()))
            .willReturn(aResponse().withStatus(NOT_ACCEPTABLE))
          )

          val expectedResult = Left(CharitiesInvalidJson)
          val actualResult = await(charitiesConnector.registerCharities(Json.parse("""{"fullName":"Johnson"}"""), 1234)(hc, ec))

          actualResult mustBe expectedResult

          verify(postRequestedFor(urlEqualTo("/org/1234/submissions/application")))
        }

        "return a Left(EtmpFailed) when failed with unexpected error" in {

          stubFor(post(urlEqualTo("/org/1234/submissions/application"))
            .withRequestBody(equalToJson(Json.parse("""{"fullName":"Johnson"}""").toString()))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
          )

          val expectedResult = Left(EtmpFailed)
          val actualResult = await(charitiesConnector.registerCharities(Json.parse("""{"fullName":"Johnson"}"""), 1234)(hc, ec))

          actualResult mustBe expectedResult

          verify(postRequestedFor(urlEqualTo("/org/1234/submissions/application")))
        }

        "return a Left(DefaultedUnexpectedFailure) for default errors" in {

          stubFor(post(urlEqualTo("/org/1234/submissions/application"))
            .withRequestBody(equalToJson(Json.stringify(requestJson)))
            .willReturn(aResponse().withStatus(CONFLICT))
          )

          val expectedResult = Left(DefaultedUnexpectedFailure(CONFLICT))
          val actualResult = await(charitiesConnector.registerCharities(requestJson, 1234)(hc, ec))

          actualResult mustBe expectedResult

          verify(postRequestedFor(urlEqualTo("/org/1234/submissions/application")))
        }
      }
    }

  }
}
