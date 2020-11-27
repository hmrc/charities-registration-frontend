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

package service

import connectors.CharitiesShortLivedCache
import javax.inject.Inject
import models.UserAnswers
import models.oldCharities._
import models.requests.OptionalDataRequest
import models.transformers.TransformerKeeper
import pages.IsSwitchOverUserPage
import pages.sections.{Section1Page, Section7Page, Section8Page}
import play.api.Logger
import play.api.libs.json._
import repositories.SessionRepository
import transformers.UserAnswerTransformer
import transformers.CharitiesJsObject
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.authorisedOfficials.AuthorisedOfficialsStatusHelper
import viewmodels.charityInformation.CharityInformationStatusHelper.checkComplete
import viewmodels.otherOfficials.OtherOfficialStatusHelper

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class CharitiesKeyStoreService @Inject()(cache: CharitiesShortLivedCache,
  userAnswerTransformer: UserAnswerTransformer,
  val sessionRepository: SessionRepository){

  private val logger = Logger(this.getClass)

  private def isSection8Completed(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.get(Section8Page) match {
      case Some(_) => userAnswers.set(Section8Page,
        OtherOfficialStatusHelper.checkComplete(userAnswers) && OtherOfficialStatusHelper.validateDataFromOldService(userAnswers))
      case _ => Success(userAnswers)
    }
  }

  // scalastyle:off method.length
  def getCacheData(request: OptionalDataRequest[_])(
    implicit hc: HeaderCarrier, ec: ExecutionContext): Future[(UserAnswers, Seq[(JsPath, Seq[JsonValidationError])])] ={

    cache.fetch(request.internalId).flatMap {
      case Some(cacheMap) if request.userAnswers.isEmpty =>
        val result = TransformerKeeper(Json.obj(), Seq.empty)
          .getJson[CharityContactDetails](cacheMap, userAnswerTransformer.toUserAnswerCharityContactDetails, "charityContactDetails")
          .getJson[CharityAddress](cacheMap, userAnswerTransformer.toUserAnswerCharityOfficialAddress, "charityOfficialAddress")
          .getJson[OptionalCharityAddress](cacheMap, userAnswerTransformer.toUserAnswerCorrespondenceAddress, "correspondenceAddress")
          .getJson[CharityRegulator](cacheMap, userAnswerTransformer.toUserAnswersCharityRegulator, "charityRegulator")
          .getJson[CharityGoverningDocument](cacheMap, userAnswerTransformer.toUserAnswersCharityGoverningDocument, "charityGoverningDocument")
          .getJson[WhatYourCharityDoes](cacheMap, userAnswerTransformer.toUserAnswersWhatYourCharityDoes, "whatYourCharityDoes")
          .getJson[OperationAndFunds](cacheMap, userAnswerTransformer.toUserAnswersOperationAndFunds, "operationAndFunds")
          .getJson[CharityBankAccountDetails](cacheMap, userAnswerTransformer.toUserAnswersCharityBankAccountDetails, "charityBankAccountDetails")
          .getJson[CharityHowManyAuthOfficials](cacheMap, userAnswerTransformer.toUserAnswersCharityHowManyAuthOfficials, "charityHowManyAuthOfficials")
          .getJson[CharityAuthorisedOfficialIndividual](
            cacheMap, userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(0, "authorised"), "authorisedOfficialIndividual1")
          .getJsonOfficials[CharityAuthorisedOfficialIndividual](
            cacheMap, userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(1, "authorised"),
            "authorisedOfficialIndividual2", "authorisedOfficials")
          .getJson[CharityHowManyOtherOfficials](cacheMap, userAnswerTransformer.toUserAnswersCharityHowManyOtherOfficials, "charityHowManyOtherOfficials")
          .getJson[CharityAuthorisedOfficialIndividual](
            cacheMap, userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(0, "other"), "otherOfficialIndividual1")
          .getJsonOfficials[CharityAuthorisedOfficialIndividual](
            cacheMap, userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(1, "other"),
            "otherOfficialIndividual2", "otherOfficials")
          .getJsonOfficials[CharityAuthorisedOfficialIndividual](
            cacheMap, userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(2, "other"),
            "otherOfficialIndividual3", "otherOfficials")

        hc.sessionId match {
          case Some(sessionId) =>
            for {
              _ <- cache.cache(sessionId.value, IsSwitchOverUserPage, true)
              userAnswers <- cache.remove(request.internalId).map(_ => UserAnswers(request.internalId, result.accumulator))
              updatedAnswersWithErrors <- if (userAnswers.data.fields.nonEmpty) {
                Future.fromTry(result = userAnswers.set(Section1Page, checkComplete(userAnswers))
                  .flatMap(userAnswers => userAnswers.get(Section7Page) match {
                    case Some(_) => userAnswers.set(Section7Page,
                      AuthorisedOfficialsStatusHelper.checkComplete(userAnswers) && AuthorisedOfficialsStatusHelper.validateDataFromOldService(userAnswers))
                    case _ => Success(userAnswers)
                  })
                  .flatMap(userAnswers => isSection8Completed(userAnswers))
                ).flatMap(userAnswers => Future.successful((userAnswers, result.errors)))
              } else {
                Future.successful((userAnswers, result.errors))
              }
            } yield updatedAnswersWithErrors
          case _ =>
            logger.error(s"[CharitiesKeyStoreService][getCacheData] no valid session found")
            throw new RuntimeException("no valid session found")
        }

      case _ =>
        Future.successful((request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId)), Seq.empty))

    } recover {
      case errors: Throwable =>
        logger.error(s"[CharitiesKeyStoreService][getCacheData] get existing charities data failed with error $errors")
        throw errors
    }
  }
}
