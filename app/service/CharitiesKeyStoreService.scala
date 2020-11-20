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
import pages.sections.Section1Page
import play.api.Logger
import play.api.libs.json._
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.charityInformation.CharityInformationStatusHelper.checkComplete

import scala.concurrent.{ExecutionContext, Future}

class CharitiesKeyStoreService @Inject()(cache: CharitiesShortLivedCache, userAnswerTransformer : UserAnswerTransformer){

  private val logger = Logger(this.getClass)

  def getCacheData(request: OptionalDataRequest[_])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[UserAnswers] ={

    cache.fetch(request.internalId).flatMap {
      case Some(cacheMap) if request.userAnswers.isEmpty =>
        val result = Json.obj().
          getJson[CharityContactDetails](cacheMap, userAnswerTransformer.toUserAnswerCharityContactDetails, "charityContactDetails").
          getJson[CharityAddress](cacheMap, userAnswerTransformer.toUserAnswerCharityOfficialAddress, "charityOfficialAddress").
          getJson[OptionalCharityAddress](cacheMap, userAnswerTransformer.toUserAnswerCorrespondenceAddress, "correspondenceAddress").
          getJson[CharityRegulator](cacheMap, userAnswerTransformer.toUserAnswersCharityRegulator, "charityRegulator").
          getJson[CharityGoverningDocument](cacheMap, userAnswerTransformer.toUserAnswersCharityGoverningDocument, "charityGoverningDocument").
          getJson[WhatYourCharityDoes](cacheMap, userAnswerTransformer.toUserAnswersWhatYourCharityDoes, "whatYourCharityDoes").
          getJson[OperationAndFunds](cacheMap, userAnswerTransformer.toUserAnswersOperationAndFunds, "operationAndFunds").
          getJson[CharityBankAccountDetails](cacheMap, userAnswerTransformer.toUserAnswersCharityBankAccountDetails, "charityBankAccountDetails")

        for {
          userAnswers <- cache.remove(request.internalId).map(_ => UserAnswers(request.internalId, result))
          updatedAnswers <- if(userAnswers.data.fields.nonEmpty){
              Future.fromTry(result = userAnswers.set(Section1Page, checkComplete(userAnswers)))
            } else{
              Future.successful(userAnswers)
            }
        } yield updatedAnswers

      case _ =>
        Future.successful(request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId)))

    } recover {
      case errors: Throwable =>
        logger.error(s"[CharitiesKeyStoreService][getCacheData] get existing charities data failed with error ${errors.getMessage}")
        throw errors
    }
  }
}
