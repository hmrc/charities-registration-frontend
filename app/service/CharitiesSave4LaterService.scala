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

package service

import config.FrontendAppConfig
import connectors.CharitiesShortLivedCache
import javax.inject.{Inject, Singleton}
import models.UserAnswers
import models.oldCharities._
import models.requests.OptionalDataRequest
import models.transformers.TransformerKeeper
import pages.sections.{Section1Page, Section7Page, Section8Page, Section9Page}
import pages.{IsSwitchOverUserPage, OldServiceSubmissionPage}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.Call
import repositories.AbstractRepository
import transformers.{CharitiesJsObject, UserAnswerTransformer}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.logging.SessionId
import utils.ImplicitDateFormatter
import viewmodels.authorisedOfficials.AuthorisedOfficialsStatusHelper
import viewmodels.charityInformation.CharityInformationStatusHelper
import viewmodels.nominees.NomineeStatusHelper
import viewmodels.otherOfficials.OtherOfficialStatusHelper

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

@Singleton
class CharitiesSave4LaterService @Inject()(
  cache: CharitiesShortLivedCache,
  userAnswerTransformer: UserAnswerTransformer,
  sessionRepository: AbstractRepository,
  userAnswerService: UserAnswerService,
  appConfig: FrontendAppConfig) extends ImplicitDateFormatter {

  private val logger = Logger(this.getClass)

  private def isSection1Completed(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.get(Section1Page) match {
      case Some(_) => userAnswers.set(Section1Page, CharityInformationStatusHelper.checkComplete(userAnswers))
      case _ => Success(userAnswers)
    }
  }

  private def isSection7Completed(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.get(Section7Page) match {
      case Some(_) => userAnswers.set(Section7Page,
        AuthorisedOfficialsStatusHelper.checkComplete(userAnswers) && AuthorisedOfficialsStatusHelper.validateDataFromOldService(userAnswers))
      case _ => Success(userAnswers)
    }
  }

  private def isSection9Completed(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.get(Section9Page) match {
      case Some(_) =>
        userAnswers.set(Section9Page,
        NomineeStatusHelper.checkComplete(userAnswers) && NomineeStatusHelper.validateDataFromOldService(userAnswers))
      case _ => Success(userAnswers)
    }
  }

  private def isSection8Completed(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.get(Section8Page) match {
      case Some(_) => userAnswers.set(Section8Page,
        OtherOfficialStatusHelper.checkComplete(userAnswers) && OtherOfficialStatusHelper.validateDataFromOldService(userAnswers))
      case _ => Success(userAnswers)
    }
  }

  private def expiryTimeIfCompleted(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.get(OldServiceSubmissionPage) match {
      case Some(submitted) =>
        Success(userAnswers.copy(expiresAt = oldStringToDate(submitted.submissionDate).plusDays(
          appConfig.timeToLiveInDays).atTime(12, 0))) //scalastyle:off magic.number
      case _ => Success(userAnswers)
    }
  }

  private def getSwitchOverJsonData(cacheMap: CacheMap): TransformerKeeper = {
    TransformerKeeper(Json.obj(), Seq.empty)
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
      .getJson[CharityAddNominee](cacheMap, userAnswerTransformer.toUserAnswersCharityAddNominee, "charityAddNominee")
      .getJson[CharityNomineeStatus](cacheMap, userAnswerTransformer.toUserAnswersCharityNomineeStatus, "charityNomineeStatus")
      .getJson[CharityNomineeIndividual](cacheMap, userAnswerTransformer.toUserAnswersCharityNomineeIndividual, "charityNomineeIndividual")
      .getJson[CharityNomineeOrganisation](cacheMap, userAnswerTransformer.toUserAnswersCharityNomineeOrganisation, "charityNomineeOrganisation")
      .getJson[Acknowledgement](cacheMap, userAnswerTransformer.toUserAnswersOldAcknowledgement, "acknowledgement-Reference")
  }

  private def updateSwitchOverUserAnswer(userAnswers: UserAnswers, result: TransformerKeeper)(
    implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Call, UserAnswers]] = {
    if (userAnswers.data.fields.nonEmpty) {
      Future.fromTry(result = isSection1Completed(userAnswers)
        .flatMap(userAnswers => isSection7Completed(userAnswers))
        .flatMap(userAnswers => isSection8Completed(userAnswers))
        .flatMap(userAnswers => isSection9Completed(userAnswers))
        .flatMap(userAnswers => expiryTimeIfCompleted(userAnswers))
        .flatMap(_.set(IsSwitchOverUserPage, true))
      ).flatMap { userAnswers =>
        userAnswerService.set(userAnswers).map { _ =>
          if (result.errors.isEmpty) {
            Right(userAnswers)
          } else {
            Left(controllers.routes.SwitchOverErrorController.onPageLoad())
          }
        }
      }
    } else {
      if(result.errors.nonEmpty) {
        userAnswerService.set(userAnswers).map(_ => Left(controllers.routes.SwitchOverAnswersLostErrorController.onPageLoad()))
      } else {
        userAnswerService.set(userAnswers).map(_ => Right(userAnswers))
      }
    }
  }

  private def checkForValidApplicationJourney(request: OptionalDataRequest[_], lastSessionId: Option[String])(
    implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Call, UserAnswers]] = {
    (request.userAnswers, lastSessionId) match {
      case (Some(userAnswers), _) => userAnswerService.set(userAnswers).map(_ => Right(userAnswers))
      case (None, Some(sessionId)) =>
        sessionRepository.get(sessionId).flatMap {
          case None =>
            logger.error(s"[CharitiesSave4LaterService][getCacheData] no eligibility data found")
            Future.successful(Left(controllers.routes.CannotFindApplicationController.onPageLoad()))
          case Some(_) => val userAnswers = UserAnswers(request.internalId)
            userAnswerService.set(userAnswers).map(_ => Right(userAnswers))
        }
      case _ => logger.error(s"[CharitiesSave4LaterService][getCacheData] no eligibility data and current session data found")
        Future.successful(Left(controllers.routes.CannotFindApplicationController.onPageLoad()))
    }
  }

  def getCacheData(request: OptionalDataRequest[_], sessionId: SessionId, eligibleJourneyId: Option[String])(
    implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Call, UserAnswers]] = {

    cache.fetch(request.internalId).flatMap {
      case Some(cacheMap) if request.userAnswers.isEmpty =>
        for {
          result <- Future.successful(getSwitchOverJsonData(cacheMap))
          _ <- cache.cache(sessionId.value, IsSwitchOverUserPage, true)
          updatedAnswersWithErrors <- updateSwitchOverUserAnswer(UserAnswers(request.internalId, result.accumulator), result)
        } yield updatedAnswersWithErrors
      case _ =>
        checkForValidApplicationJourney(request, eligibleJourneyId)
    } recover {
      case errors: Throwable =>
        logger.error(s"[CharitiesSave4LaterService][getCacheData] get existing charities data failed with error $errors")
        throw errors
    }
  }
}