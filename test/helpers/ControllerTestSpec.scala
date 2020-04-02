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
import org.scalatest.mockito.MockitoSugar
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import config.AppConfig
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

trait ControllerTestSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  implicit val mat: Materializer = fakeApplication.injector.instanceOf[Materializer]
  implicit val lang: Lang = Lang("en")

  lazy val messages: MessagesApi = fakeApplication.injector.instanceOf[MessagesApi]
  lazy val mcc: MessagesControllerComponents = fakeApplication.injector.instanceOf[MessagesControllerComponents]
  lazy val mockConfig: AppConfig = fakeApplication.injector.instanceOf[AppConfig]

}
