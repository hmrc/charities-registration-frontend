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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlEqualTo}
import config.FrontendAppConfig
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsString
import uk.gov.hmrc.crypto.{ApplicationCrypto, PlainText}
import uk.gov.hmrc.http.cache.client.CacheMap

class CharitiesShortLivedCacheSpec extends SpecBase with WireMockHelper with MockitoSugar {

  lazy val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[FrontendAppConfig].toInstance(mockFrontendAppConfig)
      )

  when(mockFrontendAppConfig.save4laterCacheBaseUrl) thenReturn getUrl
  when(mockFrontendAppConfig.save4laterDomain) thenReturn "keystore"

  val connector: CharitiesShortLivedCache = inject[CharitiesShortLivedCache]

  "CharitiesShortLivedCache" must {

    "fetch with data" in {

      val responseJson = readJsonFromFile("/keyStore_response_1.json")

      stubFor(
        get(urlEqualTo("/keystore/charities-frontend/8799940975137654"))
          .willReturn(
            aResponse()
              .withBody(responseJson.toString())
              .withStatus(OK)
          )
      )

      val result = await(connector.fetch("8799940975137654"))

      result mustBe Some(
        CacheMap(
          "8799940975137654",
          Map(
            "charityContactDetails"  -> JsString(
              "4qzUjOWJ6HphbjwvSR/lfreYvnR8MhW9QYXJu4i/QCg7HyoScxGYYoJZIYbpr0CVVObRC7E04IiX5zkjHOEheg=="
            ),
            "charityOfficialAddress" -> JsString(
              "F5Thos0HUSsOgbEobmArahKVB6ims41e3ESts4t+nFGJ8KPqMprK5FagjH8h8BNrDqebMCMpGVS6JEfCV6Yrs7mkqXmWU9ScmKGCcKBXHeVLret25PRg5jp2Ibxe6zVXQpYex/9nt7xK97gcOzjRF7BeEGPdXf9CG5/2dgV7EdUbkVQyvm1o1UfQLJ3Bl1DEVObRC7E04IiX5zkjHOEheg=="
            )
          )
        )
      )

    }

    "fetch without data" in {

      stubFor(
        get(urlEqualTo("/keystore/charities-frontend/8799940975137654"))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
          )
      )

      val result = await(connector.fetch("8799940975137654"))

      result mustBe None

    }

    "crypto" in {

      connector.crypto.encrypt(PlainText("test")) mustBe
        inject[ApplicationCrypto].JsonCrypto.encrypt(PlainText("test"))
    }

    "defaultSource" in {

      connector.shortLiveCache.defaultSource mustBe "charities-frontend"
    }

    "baseUri" in {

      connector.shortLiveCache.baseUri mustBe "http://localhost:20001"
    }

    "domain" in {

      connector.shortLiveCache.domain mustBe "keystore"
    }

  }

}
