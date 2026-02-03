/*
 * Copyright 2026 HM Revenue & Customs
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

import models.UserAnswers
import play.api.libs.json.{JsResultException, JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers
import play.api.test.Helpers.*
import stubs.AuthStub
import stubs.AuthStub.authorised
import stubs.CharitiesStub.*
import utils.{IntegrationSpecBase, WireMockMethods}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.Source

class DeclarationControllerISpec extends IntegrationSpecBase with WireMockMethods {

  private def readJsonFromFile(filePath: String): JsValue = {
    val source = Source.fromURL(getClass.getResource(filePath))
    val result =
      try source.mkString
      finally source.close()
    Json.parse(replacePlaceholders(result))
  }

  private def runTest(internalId: String): Future[Result] = {

    val requestJsonFileName = s"/$internalId.json"
    val ua                  = readJsonFromFile(requestJsonFileName).as[UserAnswers]

    stubUserAnswerGet(ua, internalId)
    stubUserAnswerPost(ua, internalId)
    authorised(internalId)
    stubScenario()

    route(
      app,
      buildPost(routes.DeclarationController.onSubmit.url)
    ).get
  }

  "Calling Charities service" when {

    "the user is authorised" must {

      "completed all the sections with minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            val response = runTest("scenario_1_request")
            status(response) mustBe SEE_OTHER
            Helpers.redirectLocation(response) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 6" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {
            val response = runTest("scenario_2_request")
            status(response) mustBe SEE_OTHER
            Helpers.redirectLocation(response) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 8" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {
            val response = runTest("scenario_3_request")
            status(response) mustBe SEE_OTHER
            Helpers.redirectLocation(response) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
          }
        }
      }

      "completed all the sections with another set of minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {
            val response = runTest("scenario_4_request")
            status(response) mustBe SEE_OTHER
            Helpers.redirectLocation(response) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
          }
        }
      }

      "completed all the sections with mixed data sets" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {
            val response = runTest("scenario_5_request")
            status(response) mustBe SEE_OTHER
            Helpers.redirectLocation(response) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
          }
        }
      }

      "completed all the sections with mixed data sets with nominee as organisation" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {
            val response = runTest("scenario_6_request")
            status(response) mustBe SEE_OTHER
            Helpers.redirectLocation(response) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
          }
        }
      }

      "completed all the sections with mixed data sets with nominee as individual" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {
            val response = runTest("scenario_7_request")
            status(response) mustBe SEE_OTHER
            Helpers.redirectLocation(response) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
          }
        }
      }

      "submitting the data for charities registration where user answers json invalid" must {
        "throw jsresultexception" in {
          val internalId = "scenario_7_request"

          val requestJsonFileName = s"/$internalId.json"
          val ua                  = readJsonFromFile(requestJsonFileName).as[UserAnswers]

          stubUserAnswerGet(Json.obj("invalid" -> "invalid"), internalId)
          stubUserAnswerPost(ua, internalId)
          authorised(internalId)
          stubScenario()

          intercept[JsResultException](
            Await.result(
              route(
                app,
                buildPost(routes.DeclarationController.onSubmit.url)
              ).get,
              Duration.Inf
            )
          )
        }
      }
      "getting user answers data for charities registration where status is not OK"    must {
        "Redirect to page not found page" in {
          val internalId = "scenario_7_request"

          val requestJsonFileName = s"/$internalId.json"
          val ua                  = readJsonFromFile(requestJsonFileName).as[UserAnswers]

          stubUserAnswerGet(BAD_REQUEST, internalId)
          stubUserAnswerPost(ua, internalId)
          authorised(internalId)
          stubScenario()

          val response = route(
            app,
            buildPost(routes.DeclarationController.onSubmit.url)
          ).get
          status(response) mustBe SEE_OTHER
          Helpers.redirectLocation(response) mustBe Some(controllers.routes.PageNotFoundController.onPageLoad().url)
        }
      }

    }

    "user not authorised" must {
      "return SEE_OTHER (303)" in {

        AuthStub.unauthorised()

        val response = route(
          app,
          buildPost(routes.DeclarationController.onSubmit.url)
        ).get

        status(response) mustBe SEE_OTHER
        Helpers.redirectLocation(response) mustBe Some(
          controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad.url
        )
      }
    }
  }
}
