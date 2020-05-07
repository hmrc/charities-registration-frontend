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
import models.{CharityNamesModel, ContactDetailsModel}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, times, verify, when}
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


class CharitiesContactDetailsControllerSpec extends TestHelper with BeforeAndAfterEach{
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

  def testController: CharitiesContactDetailsController=fakeApplication.injector.instanceOf[CharitiesContactDetailsController]

    "CharitiesContactDetailsController" should {

     "Successfully load the contactDetails page" in {
       when(mockDataCacheConnector.fetch(any()))
         .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))

        lazy val result = testController.onPageLoad(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "successfully process a valid submit of the charity Contact details page with just mandatory fields filled" in {
        when(mockDataCacheConnector.fetch(any()))
          .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))

        when(mockDataCacheConnector.save(any(), any(), any())(any()))
          .thenReturn(Future.successful(new CacheMap("foo", Map())))

        implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody(("daytimePhone", "+44 0808 157 0192"), ("emailAddress", "name@example.com"))
        lazy val result = testController.onSubmit(request)
        status(result) shouldBe Status.SEE_OTHER

        result.header.headers("Location") shouldBe "/hmrc-register-charity-registration-details/hello-world"
        verify(mockDataCacheConnector, times(1)).save[ContactDetailsModel](any(), any(), any())(any())

      }

           "show an error if an incorrect Daytime phone number is provided" in {
             when(mockDataCacheConnector.fetch(any()))
               .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))

             when(mockDataCacheConnector.save(any(), any(), any())(any()))
               .thenReturn(Future.successful(new CacheMap("foo", Map())))

             implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] =
               fakeRequest.withFormUrlEncodedBody(("daytimePhone", "01632"), ("mobilePhone", "01632 960 001"),
                 ("emailAddress", "name@example.com"))

              lazy val result = testController.onSubmit(request)
              status(result) shouldBe Status.BAD_REQUEST
             verify(mockDataCacheConnector, times(1)).save[ContactDetailsModel](any(), any(), any())(any())
            }

        "show an error if an incorrect email address is provided" in {
          when(mockDataCacheConnector.fetch(any()))
            .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))

          when(mockDataCacheConnector.save(any(), any(), any())(any()))
            .thenReturn(Future.successful(new CacheMap("foo", Map())))

          implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] =
            fakeRequest.withFormUrlEncodedBody(("daytimePhone", "+44 0808 157 0192"), ("mobilePhone", "01632 960 001"),
              ("emailAddress", "name@examplecom"))

          lazy val result = testController.onSubmit(request)
          status(result) shouldBe Status.BAD_REQUEST
          verify(mockDataCacheConnector, times(1)).save[ContactDetailsModel](any(), any(), any())(any())

          }

       "show an error if mandatory data is not provided" in {
         when(mockDataCacheConnector.fetch(any()))
           .thenReturn(Future.successful(Some(new CacheMap("foo", Map()))))

         when(mockDataCacheConnector.save(any(), any(), any())(any()))
           .thenReturn(Future.successful(new CacheMap("foo", Map())))

         implicit val request : FakeRequest[AnyContentAsFormUrlEncoded] =
           fakeRequest.withFormUrlEncodedBody( ("daytimePhone", ""), ("mobilePhone", ""), ("emailAddress", ""))

         lazy val result = testController.onSubmit(request)
         status(result) shouldBe Status.BAD_REQUEST
         verify(mockDataCacheConnector, never()).save[ContactDetailsModel](any(), any(), any())(any())

       }

    }
}