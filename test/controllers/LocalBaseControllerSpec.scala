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

import base.SpecBase
import config.FrontendAppConfig
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import javax.inject.Inject
import models.requests.DataRequest
import models.{Index, Name, UserAnswers}
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, MessagesControllerComponents, Result, Results}
import play.api.test.Helpers.{redirectLocation, status, _}
import repositories.UserAnswerRepository

import scala.concurrent.Future

class LocalBaseControllerSpec extends SpecBase with BeforeAndAfterEach {
  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerRepository].toInstance(mockUserAnswerRepository),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction],
      )


  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerRepository)
  }

  private def block(test: String): Future[Result] = {
    Future.successful(Results.Ok(test))
  }

  class TestAuthorisedOfficialsController @Inject()(
    val controllerComponents: MessagesControllerComponents
   )(implicit appConfig: FrontendAppConfig) extends LocalBaseController

  private lazy val controller: TestAuthorisedOfficialsController = new TestAuthorisedOfficialsController(messagesControllerComponents)
  private val addressUserAnswers: UserAnswers = emptyUserAnswers.set(
    AuthorisedOfficialsNamePage(0), Name("FName", Some("MName"), "LName")
  ).success.value

  "LocalBase Controller" must {

    "calling the .getAuthorisedOfficialName() is successful" in {

      val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, addressUserAnswers)
      val result = controller.getAuthorisedOfficialName(Index(0))(block)(request)

      status(result) mustEqual OK
    }

    "calling the .getAuthorisedOfficialName() is unsuccessful" in {

      val request: DataRequest[AnyContent] = DataRequest(fakeRequest, internalId, emptyUserAnswers)
      val result = controller.getAuthorisedOfficialName(Index(0))(block)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
