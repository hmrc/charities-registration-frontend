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

package service

import audit.{AuditService, NormalUserAuditEvent}
import config.FrontendAppConfig
import models.requests.OptionalDataRequest
import models.{AuditTypes, UserAnswers}
import pages.OldServiceSubmissionPage
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import pages.contactDetails.CharityNamePage
import pages.otherOfficials.OtherOfficialsNamePage
import pages.sections._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Call, RequestHeader}
import repositories.SessionRepository
import transformers.UserAnswerTransformer
import uk.gov.hmrc.http.HeaderCarrier
import utils.ImplicitDateFormatter
import viewmodels.EligibiltyStatusHelper
import viewmodels.authorisedOfficials.AuthorisedOfficialsStatusHelper
import viewmodels.charityInformation.CharityInformationStatusHelper
import viewmodels.nominees.NomineeStatusHelper
import viewmodels.otherOfficials.OtherOfficialStatusHelper

import java.time.LocalTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

@Singleton
class CharitiesSave4LaterService @Inject() (
//  cache: CharitiesShortLivedCache,
  userAnswerTransformer: UserAnswerTransformer,
  sessionRepository: SessionRepository,
  userAnswerService: UserAnswerService,
  auditService: AuditService,
  appConfig: FrontendAppConfig
) extends ImplicitDateFormatter {

  // scalastyle:off method.length

  private val logger = Logger(this.getClass)

  private lazy val timeHour: Int    = 12
  private lazy val timeMinutes: Int = 0
  private lazy val time: LocalTime  = LocalTime.of(timeHour, timeMinutes)

  def isEligibiltyComplete(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(EligibiltyPage) match {
      case Some(_) =>
        userAnswers.set(EligibiltyPage, EligibiltyStatusHelper.checkComplete(userAnswers))
      case _       => Success(userAnswers)
    }

  private[service] def isCharityInformationStatusSectionCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(Section1Page) match {
      case Some(_) =>
        userAnswers.set(
          Section1Page,
          CharityInformationStatusHelper.checkComplete(userAnswers) &&
            userAnswers.get(CharityNamePage).fold(false)(_.fullName.length < 60)
        )
      case _       => Success(userAnswers)
    }

  private[service] def isAuthorisedOfficialsSectionCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(AuthorisedOfficialsNamePage(0)) match {
      case Some(_) =>
        userAnswers.set(
          Section7Page,
          AuthorisedOfficialsStatusHelper.checkComplete(userAnswers) && AuthorisedOfficialsStatusHelper
            .validateDataFromOldService(userAnswers)
        )
      case _       => userAnswers.remove(Section7Page)
    }

  private[service] def isOtherOfficialStatusSectionCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(OtherOfficialsNamePage(0)) match {
      case Some(_) =>
        userAnswers.set(
          Section8Page,
          OtherOfficialStatusHelper.checkComplete(userAnswers) &&
            OtherOfficialStatusHelper.validateDataFromOldService(userAnswers)
        )
      case _       => userAnswers.remove(Section8Page)
    }

  private[service] def isNomineeStatusSectionCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(Section9Page) match {
      case Some(_) =>
        userAnswers.set(
          Section9Page,
          NomineeStatusHelper.checkComplete(userAnswers) &&
            NomineeStatusHelper.validateDataFromOldService(userAnswers)
        )
      case _       => Success(userAnswers)
    }

  private[service] def expiryTimeIfCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(OldServiceSubmissionPage) match {
      case Some(submitted) =>
        Success(
          userAnswers.copy(expiresAt =
            oldStringToDate(submitted.submissionDate).plusDays(appConfig.timeToLiveInDays).atTime(time)
          )
        )
      case _               => Success(userAnswers)
    }

  //TODO: New method
  def checkAllSectionsCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
    for {
      checkEligibilty          <- isEligibiltyComplete(userAnswers)
      checkCharityInfo         <- isCharityInformationStatusSectionCompleted(checkEligibilty)
      checkAuthorisedOfficials <- isAuthorisedOfficialsSectionCompleted(checkCharityInfo)
      checkOtherOfficialStatus <- isOtherOfficialStatusSectionCompleted(checkAuthorisedOfficials)
      checkNomineeStatus       <- isNomineeStatusSectionCompleted(checkOtherOfficialStatus)
      allSectionsComplete      <- expiryTimeIfCompleted(checkNomineeStatus)
    } yield allSectionsComplete

//  private def getSwitchOverJsonData(cacheMap: CacheMap): TransformerKeeper =
//    TransformerKeeper(Json.obj(), Seq.empty)
//      .getJson[CharityContactDetails](
//        cacheMap,
//        userAnswerTransformer.toUserAnswerCharityContactDetails,
//        "charityContactDetails"
//      )
//      .getJson[CharityAddress](
//        cacheMap,
//        userAnswerTransformer.toUserAnswerCharityOfficialAddress,
//        "charityOfficialAddress"
//      )
//      .getJson[OptionalCharityAddress](
//        cacheMap,
//        userAnswerTransformer.toUserAnswerCorrespondenceAddress,
//        "correspondenceAddress"
//      )
//      .getJson[CharityRegulator](cacheMap, userAnswerTransformer.toUserAnswersCharityRegulator, "charityRegulator")
//      .getJson[CharityGoverningDocument](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityGoverningDocument,
//        "charityGoverningDocument"
//      )
//      .getJson[WhatYourCharityDoes](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersWhatYourCharityDoes,
//        "whatYourCharityDoes"
//      )
//      .getJson[OperationAndFunds](cacheMap, userAnswerTransformer.toUserAnswersOperationAndFunds, "operationAndFunds")
//      .getJson[CharityBankAccountDetails](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityBankAccountDetails,
//        "charityBankAccountDetails"
//      )
//      .getJson[CharityHowManyAuthOfficials](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityHowManyAuthOfficials,
//        "charityHowManyAuthOfficials"
//      )
//      .getJson[CharityAuthorisedOfficialIndividual](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(0, "authorised"),
//        "authorisedOfficialIndividual1"
//      )
//      .getJsonOfficials[CharityAuthorisedOfficialIndividual](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(1, "authorised"),
//        "authorisedOfficialIndividual2",
//        "authorisedOfficials"
//      )
//      .getJson[CharityHowManyOtherOfficials](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityHowManyOtherOfficials,
//        "charityHowManyOtherOfficials"
//      )
//      .getJson[CharityAuthorisedOfficialIndividual](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(0, "other"),
//        "otherOfficialIndividual1"
//      )
//      .getJsonOfficials[CharityAuthorisedOfficialIndividual](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(1, "other"),
//        "otherOfficialIndividual2",
//        "otherOfficials"
//      )
//      .getJsonOfficials[CharityAuthorisedOfficialIndividual](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(2, "other"),
//        "otherOfficialIndividual3",
//        "otherOfficials"
//      )
//      .getJson[CharityAddNominee](cacheMap, userAnswerTransformer.toUserAnswersCharityAddNominee, "charityAddNominee")
//      .getJson[CharityNomineeStatus](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityNomineeStatus,
//        "charityNomineeStatus"
//      )
//      .getJson[CharityNomineeIndividual](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityNomineeIndividual,
//        "charityNomineeIndividual"
//      )
//      .getJson[CharityNomineeOrganisation](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersCharityNomineeOrganisation,
//        "charityNomineeOrganisation"
//      )
//      .getJson[Acknowledgement](
//        cacheMap,
//        userAnswerTransformer.toUserAnswersOldAcknowledgement,
//        "acknowledgement-Reference"
//      )

//  private def updateSwitchOverUserAnswer(userAnswers: UserAnswers, result: TransformerKeeper)(implicit
//    hc: HeaderCarrier,
//    ec: ExecutionContext,
//    rh: RequestHeader
//  ): Future[Either[Call, UserAnswers]] =
//    if (userAnswers.data.fields.nonEmpty) {
//      Future
//        .fromTry(result =
//          isCharityInformationStatusSectionCompleted(userAnswers)
//            .flatMap(userAnswers => isAuthorisedOfficialsSectionCompleted(userAnswers))
//            .flatMap(userAnswers => isOtherOfficialStatusSectionCompleted(userAnswers))
//            .flatMap(userAnswers => isNomineeStatusSectionCompleted(userAnswers))
//            .flatMap(userAnswers => expiryTimeIfCompleted(userAnswers))
//            .flatMap(_.set(IsSwitchOverUserPage, true))
//        )
//        .flatMap { userAnswers =>
//          userAnswerService.set(userAnswers).map { _ =>
//            if (result.errors.isEmpty) {
//              logger.warn(s"CharitiesSwitchOver: ${AuditTypes.CompleteUserTransfer}")
//              auditService.sendEvent(
//                SwitchOverAuditEvent(
//                  Json.obj("id" -> userAnswers.id) + ("data" -> userAnswers.data),
//                  AuditTypes.CompleteUserTransfer
//                )
//              )
//              Right(userAnswers)
//            } else {
//              logger.warn(s"CharitiesSwitchOver: ${AuditTypes.PartialUserTransfer}")
//              auditService.sendEvent(
//                SwitchOverAuditEvent(
//                  Json.obj("id" -> userAnswers.id) + ("data" -> userAnswers.data),
//                  AuditTypes.PartialUserTransfer
//                )
//              )
//              Left(controllers.routes.SwitchOverErrorController.onPageLoad)
//            }
//          }
//        }
//    } else {
//      if (result.errors.nonEmpty) {
//        logger.warn(s"CharitiesSwitchOver: ${AuditTypes.FailedUserTransfer}")
//        auditService.sendEvent(SwitchOverAuditEvent(Json.obj("id" -> userAnswers.id), AuditTypes.FailedUserTransfer))
//        userAnswerService
//          .set(userAnswers)
//          .map(_ => Left(controllers.routes.SwitchOverAnswersLostErrorController.onPageLoad))
//      } else {
//        logger.warn(s"CharitiesSwitchOver: ${AuditTypes.NewUser}")
//        auditService.sendEvent(SwitchOverAuditEvent(Json.obj("id" -> userAnswers.id), AuditTypes.NewUser))
//        userAnswerService.set(userAnswers).map(_ => Right(userAnswers))
//      }
//    }

  def checkForValidApplicationJourney(request: OptionalDataRequest[_], lastSessionId: Option[String])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    rh: RequestHeader
  ): Future[Either[Call, UserAnswers]] =
    (request.userAnswers, lastSessionId) match {
      case (Some(userAnswers), _)  =>
        Future
          .fromTry(result =
            checkAllSectionsCompleted(userAnswers)
//            isAuthorisedOfficialsSectionCompleted(userAnswers).flatMap(userAnswers => isOtherOfficialStatusSectionCompleted(userAnswers))
          )
          .flatMap { updatedUserAnswers =>
            userAnswerService.set(updatedUserAnswers).map(_ => Right(updatedUserAnswers))
          }
      case (None, Some(sessionId)) =>
        sessionRepository.get(sessionId).flatMap {
          case None    =>
            logger.warn(s"[CharitiesSave4LaterService][checkForValidApplicationJourney] no eligibility data found")
            Future.successful(Left(controllers.routes.CannotFindApplicationController.onPageLoad))
          case Some(_) =>
            val userAnswers = UserAnswers(request.internalId)
            logger.warn(s"CharitiesRewriteUser: ${AuditTypes.NewUser}")
            auditService.sendEvent(NormalUserAuditEvent(Json.obj("id" -> userAnswers.id), AuditTypes.NewUser))
            userAnswerService.set(userAnswers).map(_ => Right(userAnswers))
        }
      case _                       =>
        logger.error(
          s"[CharitiesSave4LaterService][checkForValidApplicationJourney] no eligibility data and no current session id/data found"
        )
        Future.successful(Left(controllers.routes.CannotFindApplicationController.onPageLoad))
    }

//  def getCacheData(request: OptionalDataRequest[_], sessionId: SessionId, eligibleJourneyId: Option[String])(implicit
//    hc: HeaderCarrier,
//    ec: ExecutionContext,
//    rh: RequestHeader
//  ): Future[Either[Call, UserAnswers]] =
////    cache
////      .fetch(request.internalId)
////      .flatMap {
////        case Some(cacheMap) if true =>
////          for {
////            result                   <- Future.successful(getSwitchOverJsonData(cacheMap))
////            _                        <- cache.cache(sessionId.value, IsSwitchOverUserPage, true)
////            updatedAnswersWithErrors <-
////              updateSwitchOverUserAnswer(UserAnswers(request.internalId, result.accumulator), result)
////          } yield updatedAnswersWithErrors
////        case _                                             =>
////          checkForValidApplicationJourney(request, eligibleJourneyId)
////      }
//    checkForValidApplicationJourney(request, eligibleJourneyId)
//
//  //      .recover { case errors: Throwable =>
////        logger.error(
////          s"[CharitiesSave4LaterService][getCacheData] get existing charities data failed with error $errors"
////        )
////        throw errors
////      }
}
