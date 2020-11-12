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

import base.SpecBase
import connectors.CharitiesShortLivedCache
import models.oldCharities.{CharityAddress, CharityContactDetails}
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap

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

  "CharitiesKeyStoreService" when {

    "getCacheData" must {

      "return valid object when its valid for contactDetails only" in {

        val responseJson = Json.parse("""{"charityContactDetails":{"fullName":"Test123","daytimePhone":"1234567890"}}""")
        val contactDetails = CharityContactDetails("Test123", None, "1234567890", None, None, None)
        when(mockCharitiesShortLivedCache.fetch(any())(any(), any())).thenReturn(Future.successful(Some(mockCacheMap)))
        when(mockCacheMap.getEntry[CharityContactDetails](meq("charityContactDetails"))(meq(CharityContactDetails.formats))).thenReturn(Some(contactDetails))

        val result = await(service.getCacheData("8799940975137654"))

        result mustBe Some(responseJson)
      }

      "return none if its empty" in {

        when(mockCharitiesShortLivedCache.fetch(any())(any(), any())).thenReturn(Future.successful(Some(mockCacheMap)))
        when(mockCacheMap.getEntry[CharityContactDetails](meq("charityContactDetails"))(meq(CharityContactDetails.formats))).thenReturn(None)

        val result = await(service.getCacheData("8799940975137654"))

        result mustBe None
      }

      "throw error if exception is returned from CharitiesShortLivedCache" in {

        when(mockCharitiesShortLivedCache.fetch(any())(any(), any())).thenReturn(Future.successful(Some(mockCacheMap)))
        when(mockCharitiesShortLivedCache.fetch(any())(any(), any())).thenReturn(Future.failed(new RuntimeException()))

        intercept[RuntimeException]{
          await(service.getCacheData("8799940975137654"))
        }
      }

    }

  }

}
