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

import connectors.{DataCacheConnector, DataCacheConnectorImpl, DataShortCacheConnector}
import helpers.TestHelper
import models.YesNoModel
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


class ValidBankEligibilityControllerSpec extends TestHelper with BeforeAndAfterEach {

  lazy val mockDataShortCacheConnector : DataShortCacheConnector = mock[DataShortCacheConnector]
  lazy val mockDataCacheConnector : DataCacheConnector = mock[DataCacheConnectorImpl]

  override lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.sessionId -> "foo")

  override lazy val fakeApplication: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[DataShortCacheConnector].toInstance(mockDataShortCacheConnector),
      bind[DataCacheConnector].toInstance(mockDataCacheConnector),
    ).build()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataShortCacheConnector,mockDataCacheConnector)

  }

  def testController: ValidBankEligibilityController=fakeApplication.injector.instanceOf[ValidBankEligibilityController]

    "ValidBankEligibilityController" should {

      "Successfully load the valid bank page" in {
        when(mockDataCacheConnector.fetch(any()))
          .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))
        lazy val result = testController.onPageLoad(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "redirect to eligible location page when 'Yes' is submitted in valid bank eligibility page" in {
        when(mockDataCacheConnector.fetch(any()))
          .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))
        when(mockDataShortCacheConnector.save[YesNoModel](any(), any(), any())(any()))
          .thenReturn(Future.successful(new CacheMap("foo", Map())))
        val form = ("value", "Yes")

        implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(form)

        lazy val result = testController.onSubmit(request)

        status(result) shouldBe Status.SEE_OTHER
        result.header.headers("Location") shouldBe "/hmrc-register-charity-registration-details/eligible-location"
        verify(mockDataShortCacheConnector, times(1)).save[YesNoModel](any(), any(), any())(any())

      }

      "redirect to not eligible page when 'No' is submitted in valid bank eligibility page" in {
        when(mockDataCacheConnector.fetch(any()))
          .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))
        when(mockDataShortCacheConnector.save[YesNoModel](any(), any(), any())(any()))
          .thenReturn(Future.successful(new CacheMap("foo", Map())))
        val form = ("value", "No")

        implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(form)
        lazy val result = testController.onSubmit(request)

        status(result) shouldBe Status.SEE_OTHER
        result.header.headers("Location") shouldBe "/hmrc-register-charity-registration-details/ineligible-for-registration"
        verify(mockDataShortCacheConnector, times(1)).save[YesNoModel](any(), any(), any())(any())

      }

      "show an error if nothing is selected" in {
        when(mockDataCacheConnector.fetch(any()))
          .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))
        when(mockDataShortCacheConnector.save[YesNoModel](any(), any(), any())(any()))
          .thenReturn(Future.successful(new CacheMap("foo", Map())))
        val form = ("value", "")
        implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(form)

        lazy val result = testController.onSubmit(request)

        status(result) shouldBe Status.BAD_REQUEST
        verify(mockDataShortCacheConnector, never()).save[YesNoModel](any(), any(), any())(any())
      }
    }
}