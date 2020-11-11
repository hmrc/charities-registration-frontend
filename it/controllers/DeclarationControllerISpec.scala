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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import models.UserAnswers
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}
import stubs.{AuthStub, CharitiesStub}
import utils.{CreateRequestHelper, CustomMatchers, IntegrationSpecBase, WireMockMethods}

import scala.io.Source

class DeclarationControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with WireMockMethods{

  private def readJsonFromFile(filePath: String): JsValue =  {
    val result = Source.fromURL(getClass.getResource(filePath)).mkString
    Json.parse(result)
  }

  val today  = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  val logger = Logger(this.getClass)

  "Calling Charities service" when {

    "user not authorised" must {
      "return SEE_OTHER (303)" in {

        logger.warn("**********scenario 1**********")

        AuthStub.unauthorised()

        val res = getRequest("/declare-and-send/declaration")()

        whenReady(res) { result =>
          result must have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.checkEligibility.routes.IncorrectDetailsController.onPageLoad().url)
          )
        }
      }
    }

    "the user is authorised" must {

      "completed all the sections with minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            logger.warn("**********scenario 2**********")

            AuthStub.authorised()

            val response = postRequest("/declare-and-send/declaration", Json.toJson("test" -> "value"))()

            val requestJson = readJsonFromFile("/scenario_1_request.json")

            setAnswers(UserAnswers(s"providerId", requestJson.as[JsObject]))

            val transformedRequestJson = readJsonFromFile("/scenario_1_transformed_request.json")

            CharitiesStub.stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

            whenReady(response)
            { result =>
              result must have(
                httpStatus(SEE_OTHER),
                redirectLocation(controllers.routes.RegistrationSentController.onPageLoad().url)
              )
            }
          }
        }
      }

      "completed all the sections with another set of minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            logger.warn("**********scenario 3**********")

            AuthStub.authorised()

            val response = postRequest("/declare-and-send/declaration", Json.toJson("test" -> "value"))()

            val requestJson = readJsonFromFile("/request_with_min_data.json")

            setAnswers(UserAnswers(s"providerId", requestJson.as[JsObject]))

            val transformedRequestJson = readJsonFromFile("/transformed_request_for_min_data.json")

            CharitiesStub.stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

            whenReady(response)
            { result =>
              result must have(
                httpStatus(SEE_OTHER),
                redirectLocation(controllers.routes.RegistrationSentController.onPageLoad().url)
              )
            }
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 6" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            logger.warn("**********scenario 4**********")

            AuthStub.authorised()

            val response = postRequest("/declare-and-send/declaration", Json.toJson("test" -> "value"))()

            val requestJson = readJsonFromFile("/scenario_2_request.json")

            setAnswers(UserAnswers(s"providerId", requestJson.as[JsObject]))

            val transformedRequestJson = readJsonFromFile("/scenario_2_transformed_request.json")

            CharitiesStub.stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

            whenReady(response)
            { result =>
              result must have(
                httpStatus(SEE_OTHER),
                redirectLocation(controllers.routes.RegistrationSentController.onPageLoad().url)
              )
            }
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 8" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            logger.warn("**********scenario 5**********")

            AuthStub.authorised()

            val response = postRequest("/declare-and-send/declaration", Json.toJson("test" -> "value"))()

            val requestJson = readJsonFromFile("/scenario_3_request.json")

            setAnswers(UserAnswers(s"providerId", requestJson.as[JsObject]))

            val transformedRequestJson = readJsonFromFile("/scenario_3_transformed_request.json")

            CharitiesStub.stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

            whenReady(response)
            { result =>
              result must have(
                httpStatus(SEE_OTHER),
                redirectLocation(controllers.routes.RegistrationSentController.onPageLoad().url)
              )
            }
          }
        }
      }
    }
  }
}
