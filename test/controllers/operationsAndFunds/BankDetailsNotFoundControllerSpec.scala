/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.operationsAndFunds

import base.SpecBase
import controllers.actions.{AuthIdentifierAction, FakeAuthIdentifierAction}
import models.{BankDetails, CharityName, CheckMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import pages.contactDetails.CharityNamePage
import pages.operationsAndFunds.BankDetailsPage
import pages.sections.Section1Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.*
import service.UserAnswerService
import views.html.errors.BankAccountNotFoundView

import scala.concurrent.Future

class BankDetailsNotFoundControllerSpec extends SpecBase with BeforeAndAfterEach {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[UserAnswerService].toInstance(mockUserAnswerService),
        bind[AuthIdentifierAction].to[FakeAuthIdentifierAction]
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswerService)
  }

  private val view: BankAccountNotFoundView = injector.instanceOf[BankAccountNotFoundView]

  private val controller: BankDetailsNotFoundController = inject[BankDetailsNotFoundController]

  private val bankDetails = BankDetails(
    accountName = "fullName",
    sortCode = "123456",
    accountNumber = "12345678",
    rollNumber = Some("operatingName")
  )

  "BankDetailsNotFound Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(
        Future.successful(
          Some(
            emptyUserAnswers
              .set(CharityNamePage, CharityName("CName", Some("OpName")))
              .flatMap(_.set(Section1Page, true))
              .success
              .value
          )
        )
      )

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(CheckMode),
        controllers.routes.IndexController.onPageLoad(eligibleJourneyId = None)
      )(fakeRequest, messages, frontendAppConfig).toString
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(BankDetailsPage, bankDetails)
        .flatMap(_.set(CharityNamePage, CharityName("CName", Some("OpName"))))
        .flatMap(_.set(Section1Page, true))
        .success
        .value

      when(mockUserAnswerService.get(any())(any(), any())).thenReturn(Future.successful(Some(userAnswers)))

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      verify(mockUserAnswerService, times(1)).get(any())(any(), any())
    }
  }
}
