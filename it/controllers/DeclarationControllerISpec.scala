/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import stubs.AuthStub
import stubs.CharitiesStub._
import utils.{CreateRequestHelper, CustomMatchers, IntegrationSpecBase, WireMockMethods}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.Future
import scala.io.Source

class DeclarationControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with WireMockMethods{

  private def readJsonFromFile(filePath: String): JsValue =  {
    val result = Source.fromURL(getClass.getResource(filePath)).mkString
    Json.parse(result)
  }

  private val today  = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  private val logger = Logger(this.getClass)

  trait LocalSetup {
    def internalId: String
    def requestJson: String
    def transformedJson: String

    def ua: UserAnswers = readJsonFromFile(requestJson).as[UserAnswers]
    stubUserAnswerGet(ua, internalId)
    stubUserAnswerPost(ua, internalId)

    AuthStub.authorised(internalId)

    def transformedRequestJson: JsValue = readJsonFromFile(transformedJson)
    stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

    def response: Future[WSResponse] = postRequest("/declare-and-send/declaration", transformedRequestJson)()

    whenReady(response)
    { result =>
      result must have(
        httpStatus(SEE_OTHER),
        redirectLocation(controllers.routes.RegistrationSentController.onPageLoad().url)
      )
    }

  }

  "Calling Charities service" when {

    "the user is authorised" must {

      "completed all the sections with minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {

            logger.warn("**********scenario 1**********")

            override def internalId: String = "scenario_1_request"
            override def requestJson: String = "/scenario_1_request.json"
            override def transformedJson: String = "/scenario_1_transformed_request.json"
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 6" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {

            logger.warn("**********scenario 2**********")

            override def internalId: String = "scenario_2_request"
            override def requestJson: String = "/scenario_2_request.json"
            override def transformedJson: String = "/scenario_2_transformed_request.json"
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 8" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {

            logger.warn("**********scenario 3**********")

            override def internalId: String = "scenario_3_request"
            override def requestJson: String = "/scenario_3_request.json"
            override def transformedJson: String = "/scenario_3_transformed_request.json"
          }
        }
      }

      "completed all the sections with another set of minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {

            logger.warn("**********scenario 4**********")

            override def internalId: String = "scenario_4_request"
            override def requestJson: String = "/scenario_4_request.json"
            override def transformedJson: String = "/scenario_4_transformed_request.json"
          }
        }
      }

      "completed all the sections with mixed data sets" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {

            logger.warn("**********scenario 5**********")

            override def internalId: String = "scenario_5_request"
            override def requestJson: String = "/scenario_5_request.json"
            override def transformedJson: String = "/scenario_5_transformed_request.json"
          }
        }
      }

      "completed all the sections with mixed data sets with nominee as organisation" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {

            logger.warn("**********scenario 6**********")

            override def internalId: String = "scenario_6_request"
            override def requestJson: String = "/scenario_6_request.json"
            override def transformedJson: String = "/scenario_6_transformed_request.json"
          }
        }
      }

      "completed all the sections with mixed data sets with nominee as individual" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in new LocalSetup {

            logger.warn("**********scenario 7**********")

            override def internalId: String = "scenario_7_request"
            override def requestJson: String = "/scenario_7_request.json"
            override def transformedJson: String = "/scenario_7_transformed_request.json"
          }
        }
      }
    }

    "user not authorised" must {
      "return SEE_OTHER (303)" in {

        logger.warn("**********scenario 8**********")

        AuthStub.unauthorised()

        val res = postRequest("/declare-and-send/declaration", Json.toJson("status" -> true))()

        whenReady(res) { result =>
          result must have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad().url)
          )
        }
      }
    }
  }
}
