/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers
import play.api.test.Helpers._
import stubs.AuthStub
import stubs.AuthStub.authorised
import stubs.CharitiesStub._
import utils.{CreateRequestHelper, IntegrationSpecBase, WireMockMethods}

import scala.concurrent.Future
import scala.io.Source

class DeclarationControllerISpec extends IntegrationSpecBase with CreateRequestHelper with WireMockMethods {

  private def readJsonFromFile(filePath: String): JsValue = {
    val source = Source.fromURL(getClass.getResource(filePath))
    val result =
      try source.mkString
      finally source.close()
    Json.parse(result)
  }

  trait LocalSetup {

    def internalId: String

    private val requestJson: String = s"/$internalId.json"

    def ua: UserAnswers = readJsonFromFile(requestJson).as[UserAnswers]

    stubUserAnswerGet(ua, internalId)
    stubUserAnswerPost(ua, internalId)
    authorised(internalId)
    stubScenario()

    def response: Future[Result] = route(
      app,
      buildPost(routes.DeclarationController.onSubmit.url)
    ).get

    status(response) mustBe SEE_OTHER
    Helpers.redirectLocation(response) mustBe Some(controllers.routes.RegistrationSentController.onPageLoad.url)
  }

  "Calling Charities service" when {

    "the user is authorised" must {

      "completed all the sections with minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {
            override def internalId: String = "scenario_1_request"
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 6" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {
            override def internalId: String = "scenario_2_request"
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 8" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {
            override def internalId: String = "scenario_3_request"
          }
        }
      }

      "completed all the sections with another set of minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {
            override def internalId: String = "scenario_4_request"
          }
        }
      }

      "completed all the sections with mixed data sets" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {
            override def internalId: String = "scenario_5_request"
          }
        }
      }

      "completed all the sections with mixed data sets with nominee as organisation" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {
            override def internalId: String = "scenario_6_request"
          }
        }
      }

      "completed all the sections with mixed data sets with nominee as individual" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {
            override def internalId: String = "scenario_7_request"
          }
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
