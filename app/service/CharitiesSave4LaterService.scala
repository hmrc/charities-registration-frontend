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
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import pages.contactDetails.CharityNamePage
import pages.otherOfficials.OtherOfficialsNamePage
import pages.sections._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Call, RequestHeader}
import repositories.SessionRepository
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
  sessionRepository: SessionRepository,
  userAnswerService: UserAnswerService,
  auditService: AuditService
//  appConfig: FrontendAppConfig
) extends ImplicitDateFormatter {

  // scalastyle:off method.length

  private val logger = Logger(this.getClass)

  private lazy val timeHour: Int    = 12
  private lazy val timeMinutes: Int = 0
  private lazy val time: LocalTime  = LocalTime.of(timeHour, timeMinutes)

//  def isEligibiltyComplete(userAnswers: UserAnswers): Try[UserAnswers] =
//    userAnswers.get(EligibiltyPage) match {
//      case Some(_) =>
//        userAnswers.set(EligibiltyPage, EligibiltyStatusHelper.checkComplete(userAnswers))
//      case _       => Success(userAnswers)
//    }

  private[service] def isCharityInformationStatusSectionCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
    if (
      CharityInformationStatusHelper.checkComplete(userAnswers) &&
      userAnswers.get(CharityNamePage).fold(false)(_.fullName.length < 60)
    ) {
      userAnswers.set(Section1Page, true)
    } else {
      userAnswers.set(Section1Page, false)
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

//  private[service] def expiryTimeIfCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
//    userAnswers.get(OldServiceSubmissionPage) match {
//      case Some(submitted) =>
//        Success(
//          userAnswers.copy(expiresAt =
//            oldStringToDate(submitted.submissionDate).plusDays(appConfig.timeToLiveInDays).atTime(time)
//          )
//        )
//      case _               => Success(userAnswers)
//    }

//  def checkAllSectionsCompleted(userAnswers: UserAnswers): Try[UserAnswers] =
//    for {
//      checkCharityInfo                     <- isCharityInformationStatusSectionCompleted(userAnswers)
//      checkAuthorisedOfficials             <- isAuthorisedOfficialsSectionCompleted(checkCharityInfo)
//      checkOtherOfficialStatus             <- isOtherOfficialStatusSectionCompleted(checkAuthorisedOfficials)
//      checkNomineeStatus                   <- isNomineeStatusSectionCompleted(checkOtherOfficialStatus)
//      checkAllSectionsWithUpdatedExpiryTime =
//        checkNomineeStatus.copy(expiresAt =
//          checkNomineeStatus.expiresAt.plusDays(appConfig.timeToLiveInDays).toLocalDate.atTime(time)
//        )
//    } yield checkAllSectionsWithUpdatedExpiryTime

  def checkForValidApplicationJourney(request: OptionalDataRequest[_], lastSessionId: Option[String])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    rh: RequestHeader
  ): Future[Either[Call, UserAnswers]] =
    (request.userAnswers, lastSessionId) match {
      case (Some(userAnswers), _)  =>
        Future
          .fromTry(
            isAuthorisedOfficialsSectionCompleted(userAnswers)
              .flatMap(userAnswers => isOtherOfficialStatusSectionCompleted(userAnswers))
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
}
