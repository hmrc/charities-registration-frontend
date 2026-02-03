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

package connectors.addressLookup

import base.SpecBase
import base.data.constants.ConfirmedAddressConstants
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import config.FrontendAppConfig
import connectors.WireMockHelper
import connectors.httpParsers.AddressLookupInitializationHttpParser.AddressLookupOnRamp
import connectors.httpParsers.{AddressMalformed, AddressNotFound, NoLocationHeaderReturned, UnexpectedFailure}
import org.mockito.Mockito._
import play.api.http.HeaderNames
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import models.addressLookup.AddressLookupConfigurationModel.*

class AddressLookupConnectorSpec extends SpecBase with WireMockHelper {

  lazy val mockFrontendAppConfig: FrontendAppConfig = mock(classOf[FrontendAppConfig])

  "AddressLookupConnector" should {
    val httpClient: HttpClientV2                            = injector.instanceOf[HttpClientV2]
    lazy val addressLookupConnector: AddressLookupConnector =
      new AddressLookupConnector(httpClient, mockFrontendAppConfig)

    when(mockFrontendAppConfig.addressLookupFrontend) `thenReturn` getUrl
    when(mockFrontendAppConfig.host) `thenReturn` "http://localhost:9457"
    when(mockFrontendAppConfig.contactFormServiceIdentifier) `thenReturn` "iCharities"
    when(mockFrontendAppConfig.timeout) `thenReturn` 900
    when(mockFrontendAppConfig.retrieveAddressUrl) `thenReturn` (getUrl + "/api/confirmed")

    "For the .initialize() method" should {

      "for a successful response" must {

        "return a Right(Success response)" in {

          stubFor(
            post(urlEqualTo("/api/v2/init"))
              .withRequestBody(
                equalToJson(
                  Json.toJson(toAddressLookupConfigurationModel("/url/test", "test", Some("xyz"), None)).toString()
                )
              )
              .willReturn(
                aResponse()
                  .withHeader(HeaderNames.LOCATION, "/foo")
                  .withStatus(ACCEPTED)
              )
          )

          val expectedResult = Right(AddressLookupOnRamp("/foo"))
          val actualResult   =
            await(addressLookupConnector.initialize("/url/test", "test", Some("xyz"), None)(hc, ec, messagesApi))

          actualResult mustBe expectedResult

          WireMock.verify(postRequestedFor(urlEqualTo("/api/v2/init")))
        }
      }

      "for an error response" must {

        "return a Left(Invalid) when no location returns" in {

          stubFor(
            post(urlEqualTo("/api/v2/init"))
              .withRequestBody(
                equalToJson(
                  Json.toJson(toAddressLookupConfigurationModel("/url/test", "test", Some("xyz"), None)).toString()
                )
              )
              .willReturn(
                aResponse()
                  .withStatus(ACCEPTED)
              )
          )

          val expectedResult = Left(NoLocationHeaderReturned)
          val actualResult   =
            await(addressLookupConnector.initialize("/url/test", "test", Some("xyz"), None)(hc, ec, messagesApi))

          actualResult mustBe expectedResult

          WireMock.verify(postRequestedFor(urlEqualTo("/api/v2/init")))
        }

        "return a Left(DefaultedUnexpectedFailure) when unexpected response" in {

          stubFor(
            post(urlEqualTo("/api/v2/init"))
              .withRequestBody(
                equalToJson(
                  Json.toJson(toAddressLookupConfigurationModel("/url/test", "test", Some("xyz"), None)).toString()
                )
              )
              .willReturn(
                aResponse()
                  .withStatus(INTERNAL_SERVER_ERROR)
              )
          )

          val expectedResult = Left(UnexpectedFailure(INTERNAL_SERVER_ERROR))
          val actualResult   =
            await(addressLookupConnector.initialize("/url/test", "test", Some("xyz"), None)(hc, ec, messagesApi))

          actualResult mustBe expectedResult

          WireMock.verify(postRequestedFor(urlEqualTo("/api/v2/init")))
        }

      }
    }

    "For the .retrieve(id) method" should {

      "for a successful response" must {

        "return a Right(AddressModel)" in {

          stubFor(
            get(urlEqualTo("/api/confirmed?id=id"))
              .willReturn(
                aResponse()
                  .withStatus(OK)
                  .withBody(Json.obj("address" -> Json.toJson(ConfirmedAddressConstants.address)).toString())
              )
          )

          val expectedResult = Right(ConfirmedAddressConstants.address)
          val actualResult   = await(addressLookupConnector.retrieveAddress("id")(hc, ec))

          actualResult mustBe expectedResult

          WireMock.verify(getRequestedFor(urlEqualTo("/api/confirmed?id=id")))
        }
      }

      "for an error response" must {

        "return a Left(AddressMalformed)" in {

          stubFor(
            get(urlEqualTo("/api/confirmed?id=id"))
              .willReturn(
                aResponse()
                  .withStatus(OK)
                  .withBody(Json.toJson(ConfirmedAddressConstants.address).toString())
              )
          )

          val expectedResult = Left(AddressMalformed)
          val actualResult   = await(addressLookupConnector.retrieveAddress("id")(hc, ec))

          actualResult mustBe expectedResult

          WireMock.verify(getRequestedFor(urlEqualTo("/api/confirmed?id=id")))
        }

        "return Left(AddressNotFound) when address couldn't found" in {

          stubFor(
            get(urlEqualTo("/api/confirmed?id=id"))
              .willReturn(aResponse().withStatus(NOT_FOUND))
          )

          val expectedResult = Left(AddressNotFound)
          val actualResult   = await(addressLookupConnector.retrieveAddress("id")(hc, ec))

          actualResult mustBe expectedResult

          WireMock.verify(getRequestedFor(urlEqualTo("/api/confirmed?id=id")))
        }

        "return a Left(DefaultedUnexpectedFailure) when unexpected response" in {

          stubFor(
            get(urlEqualTo("/api/confirmed?id=id"))
              .willReturn(
                aResponse()
                  .withStatus(INTERNAL_SERVER_ERROR)
              )
          )

          val expectedResult = Left(UnexpectedFailure(INTERNAL_SERVER_ERROR))
          val actualResult   = await(addressLookupConnector.retrieveAddress("id")(hc, ec))

          actualResult mustBe expectedResult

          WireMock.verify(getRequestedFor(urlEqualTo("/api/confirmed?id=id")))
        }

      }

    }
  }
}
