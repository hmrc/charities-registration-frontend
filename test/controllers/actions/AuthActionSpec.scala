/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.actions

import base.SpecBase
import com.google.inject.Inject
import config.FrontendAppConfig
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.Helpers._
import repositories.AbstractRepository
import service.UserAnswerService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys, UnauthorizedException}

import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  class Harness(authAction: AuthIdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }

  "Auth Action" when {

    "the user hasn't logged in" must {

      "redirect the user to log in " in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "the user's session has expired" must {

      "redirect the user to log in " in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "the user doesn't have sufficient enrolments" must {

      "redirect the user to the incorrect Details page" in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad.url)
      }
    }

    "the user doesn't have sufficient confidence level" must {

      "redirect the user to the incorrect Details page" in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientConfidenceLevel), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad.url)
      }
    }

    "the user used an unaccepted auth provider" must {

      "redirect the user to the incorrect Details page" in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad.url)
      }
    }

    "the user has an unsupported affinity group" must {

      "redirect the user to the incorrect Details page" in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAffinityGroup), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad.url)
      }
    }

    "the user has an unsupported credential role" must {

      "redirect the user to the incorrect Details page" in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedCredentialRole), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad.url)
      }
    }

    "the Org user with supporting credential role" must {

      "redirect the user to correct" in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeSuccessAuthConnector(Some(Credentials("valid", "org"))), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe OK
      }

      "redirect the user to correct for external test environment with session Id" in {

        val application = new GuiceApplicationBuilder().configure("features.isExternalTest" -> "true").overrides(
          bind[AbstractRepository].toInstance(mockSessionRepository),
          bind[UserAnswerService].toInstance(mockUserAnswerService),
          bind[IdentifierAction].to[FakeIdentifierAction],
          bind[AuthIdentifierAction].to[FakeAuthIdentifierAction],
          bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
          bind[UserDataRetrievalAction].toInstance(new FakeUserDataRetrievalAction(userAnswers))
        ).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig: FrontendAppConfig = application.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeSuccessAuthConnector(Some(Credentials("valid", "org"))), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> "foo"))

        status(result) mustBe OK
      }

      "redirect the user to correct for external test environment without session id" in {

        val application = new GuiceApplicationBuilder().configure("features.isExternalTest" -> "true").overrides(
          bind[AbstractRepository].toInstance(mockSessionRepository),
          bind[UserAnswerService].toInstance(mockUserAnswerService),
          bind[IdentifierAction].to[FakeIdentifierAction],
          bind[AuthIdentifierAction].to[FakeAuthIdentifierAction],
          bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
          bind[UserDataRetrievalAction].toInstance(new FakeUserDataRetrievalAction(userAnswers))
        ).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig: FrontendAppConfig = application.injector.instanceOf[FrontendAppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeSuccessAuthConnector(Some(Credentials("valid", "org"))), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe OK
      }

    }

    "the Org user with not supporting credential role" must {

      "throw UnauthorizedException " in {

        val application = applicationBuilder().build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierAction(new FakeSuccessAuthConnector(None), frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)

        intercept[UnauthorizedException]{
          await(controller.onPageLoad()(fakeRequest))
        }

      }
    }
  }
}



class FakeSuccessAuthConnector[B] @Inject()(response: B) extends AuthConnector {
  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.successful(response.asInstanceOf[A])
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
