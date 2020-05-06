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

package controllers

import controllers.auth.{AuthActionImpl, FakeAuthAction}
import controllers.connectors.{DataCacheConnector, DataCacheConnectorImpl}
import helpers.TestHelper
import models.CharityNamesModel
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.Future


class CharityNameDetailsControllerSpec extends TestHelper with BeforeAndAfterEach {

  lazy val mockDataCacheConnector : DataCacheConnector = mock[DataCacheConnectorImpl]

  override lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.sessionId -> "foo")

  override lazy val fakeApplication: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[DataCacheConnector].toInstance(mockDataCacheConnector),
      bind[AuthActionImpl].to[FakeAuthAction]
    ).build()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataCacheConnector)

  }

  def testController: CharityNameDetailsController=fakeApplication.injector.instanceOf[CharityNameDetailsController]

    "CharityNameDetailsController" should {

      "Successfully load the charity name details page" in {
        when(mockDataCacheConnector.fetch(any()))
          .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))

        lazy val result = testController.onPageLoad(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "redirect to the charity contact details page when valid data is submitted" in {
        when(mockDataCacheConnector.fetch(any()))
          .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))

        when(mockDataCacheConnector.save(any(), any(), any())(any()))
          .thenReturn(Future.successful(new CacheMap("foo", Map())))

        implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody(("charityFullName", "Good Samaritan"), ("charityOperatingName", ""))

        lazy val result = testController.onSubmit(request)

        status(result) shouldBe Status.SEE_OTHER
        result.header.headers("Location") shouldBe "/hmrc-register-charity-registration-details/contact-details"
        verify(mockDataCacheConnector, times(1)).save[CharityNamesModel](any(), any(), any())(any())
      }

      "return a Bad Request and errors when invalid data is submitted" in {
        when(mockDataCacheConnector.fetch(any()))
          .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))
        when(mockDataCacheConnector.save(any(), any(), any())(any()))
          .thenReturn(Future.successful(new CacheMap("foo", Map())))

        implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody(("charityFullName", ""), ("charityOperatingName", ""))

        lazy val result = testController.onSubmit(request)

        status(result) shouldBe Status.BAD_REQUEST
        verify(mockDataCacheConnector, never()).save[CharityNamesModel](any(), any(), any())(any())
      }

    }
}