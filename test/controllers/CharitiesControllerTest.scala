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

import org.scalatest.{BeforeAndAfterEach, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test._
import play.api.http.Status
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatest.Matchers

class CharitiesControllerTest extends UnitSpec with BeforeAndAfterEach with MockitoSugar with GuiceOneAppPerSuite {

  implicit val request = FakeRequest()
  lazy val helloWorldController = app.injector.instanceOf[HelloWorldController]
  "CharitiesController" should {
    "load the helloWorld page" in {
      val result = await(helloWorldController.helloWorld()(request))
      status(result) shouldBe Status.OK
    }
  }


}
