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

package helpers

import akka.stream.Materializer
import config.AppConfig
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

trait TestHelper extends UnitSpec with MockitoSugar with WithFakeApplication {

  implicit val mat: Materializer = fakeApplication.injector.instanceOf[Materializer]
  implicit val lang: Lang = Lang("en")

  lazy val messages: MessagesApi = fakeApplication.injector.instanceOf[MessagesApi]
  lazy val mcc: MessagesControllerComponents = fakeApplication.injector.instanceOf[MessagesControllerComponents]
  implicit lazy val mockAppConfig: AppConfig = fakeApplication.injector.instanceOf[AppConfig]

  implicit val application = fakeApplication
  val http = mock[DefaultHttpClient]
  implicit lazy val fakeRequest = FakeRequest()
  implicit val mockMessage = fakeApplication.injector.instanceOf[MessagesControllerComponents].messagesApi.preferred(fakeRequest)

}
