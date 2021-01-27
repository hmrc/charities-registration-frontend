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
import pages.{AcknowledgementReferencePage, ApplicationSubmissionDatePage}
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import stubs.AuthStub
import stubs.CharitiesStub._
import utils.{CreateRequestHelper, CustomMatchers, IntegrationSpecBase, WireMockMethods}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.io.Source

class DeclarationControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with WireMockMethods{

  private def readJsonFromFile(filePath: String): JsValue =  {
    val result = Source.fromURL(getClass.getResource(filePath)).mkString
    Json.parse(result)
  }

  private val today  = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  private val logger = Logger(this.getClass)

  "Calling Charities service" when {

    "user not authorised" must {
      "return SEE_OTHER (303)" in {

        logger.warn("**********scenario 1**********")

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

    "the user is authorised" must {
      "completed all the sections with minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            logger.warn("**********scenario 2**********")

            val ua = readJsonFromFile("/scenario_1_request.json").as[UserAnswers]
            stubUserAnswerGet(Json.toJson(ua), "scenario_1_request")
            stubUserAnswerPost(Json.stringify(Json.toJson(ua.set(AcknowledgementReferencePage, "765432").flatMap(
              _.set(ApplicationSubmissionDatePage, LocalDate.now())).get)), "scenario_1_request")

            AuthStub.authorised("scenario_1_request")

            val transformedRequestJson = readJsonFromFile("/scenario_1_transformed_request.json")

            stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

            val response = postRequest("/declare-and-send/declaration", Json.toJson("status" -> true))()

            whenReady(response)
            { result =>
              result must have(
                httpStatus(SEE_OTHER),
                redirectLocation(controllers.routes.EmailOrPostController.onPageLoad().url)
              )
            }
          }
        }
      }

      "completed all the sections with another set of minimum data" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            logger.warn("**********scenario 3**********")

            val ua = readJsonFromFile("/request_with_min_data.json").as[UserAnswers]
            stubUserAnswerGet(Json.toJson(ua), "providerId")
            stubUserAnswerPost(Json.stringify(Json.toJson(ua.set(AcknowledgementReferencePage, "765432").flatMap(
              _.set(ApplicationSubmissionDatePage, LocalDate.now())).get)), "providerId")

            AuthStub.authorised()

            val transformedRequestJson = readJsonFromFile("/transformed_request_for_min_data.json")

            stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

            val response = postRequest("/declare-and-send/declaration", Json.toJson("status" -> true))()

            whenReady(response)
            { result =>
              result must have(
                httpStatus(SEE_OTHER),
                redirectLocation(controllers.routes.EmailOrPostController.onPageLoad().url)
              )
            }
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 6" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            logger.warn("**********scenario 4**********")

            val ua = readJsonFromFile("/scenario_2_request.json").as[UserAnswers]
            stubUserAnswerGet(Json.toJson(ua), "scenario_2_request")
            stubUserAnswerPost(Json.stringify(Json.toJson(ua.set(AcknowledgementReferencePage, "765432").flatMap(
              _.set(ApplicationSubmissionDatePage, LocalDate.now())).get)), "scenario_2_request")

            AuthStub.authorised("scenario_2_request")

            val transformedRequestJson = readJsonFromFile("/scenario_2_transformed_request.json")

            stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

            val response = postRequest("/declare-and-send/declaration", Json.toJson("status" -> true))()

            whenReady(response)
            { result =>
              result must have(
                httpStatus(SEE_OTHER),
                redirectLocation(controllers.routes.EmailOrPostController.onPageLoad().url)
              )
            }
          }
        }
      }

      "completed all the sections with different data changes for section 1 to 8" must {
        "submitting the data for charities registration" must {
          "redirect to registration set page" in {

            logger.warn("**********scenario 5**********")

            val ua = readJsonFromFile("/scenario_3_request.json").as[UserAnswers]
            stubUserAnswerGet(Json.toJson(ua), "scenario_3_request")
            stubUserAnswerPost(Json.stringify(Json.toJson(ua.set(AcknowledgementReferencePage, "765432").flatMap(
              _.set(ApplicationSubmissionDatePage, LocalDate.now())).get)),"scenario_3_request")

            AuthStub.authorised("scenario_3_request")

            val transformedRequestJson = readJsonFromFile("/scenario_3_transformed_request.json")

            stubScenario(transformedRequestJson.toString().replaceAll("T_O_D_A_Y", today))

            val response = postRequest("/declare-and-send/declaration", Json.toJson("status" -> true))()

            whenReady(response)
            { result =>
              result must have(
                httpStatus(SEE_OTHER),
                redirectLocation(controllers.routes.EmailOrPostController.onPageLoad().url)
              )
            }
          }
        }
      }
    }
  }
}
