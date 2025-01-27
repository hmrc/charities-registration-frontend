/*
 * Copyright 2025 HM Revenue & Customs
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
import viewmodels.authorisedOfficials.AuthorisedOfficialsStatusHelper
import viewmodels.charityInformation.CharityInformationStatusHelper
import viewmodels.otherOfficials.OtherOfficialStatusHelper

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class CharitiesSectionCompleteService @Inject() (
  sessionRepository: SessionRepository,
  userAnswerService: UserAnswerService,
  auditService: AuditService
) {

  private val logger = Logger(this.getClass)

  private[service] def isCharityInformationStatusSectionCompleted(userAnswers: UserAnswers): Try[UserAnswers] = {
    val sectionPredicate =
      CharityInformationStatusHelper.checkComplete(userAnswers) &&
        userAnswers.get(CharityNamePage).fold(false)(_.fullName.length < 60)

    userAnswers.set(Section1Page, sectionPredicate)
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
            logger
              .warn(
                s"[CharitiesSectionCompleteService][checkForValidApplicationJourney] No eligibility user-answers found from frontend mongo"
              )
            Future(Left(controllers.routes.CannotFindApplicationController.onPageLoad))
          case Some(_) =>
            val userAnswers = UserAnswers(request.internalId)
            logger.warn(s"[CharitiesSectionCompleteService][checkForValidApplicationJourney]: ${AuditTypes.NewUser}")
            auditService.sendEvent(NormalUserAuditEvent(Json.obj("id" -> userAnswers.id), AuditTypes.NewUser))
            userAnswerService.set(userAnswers).map(_ => Right(userAnswers))
        }
      case _                       =>
        logger.error(
          s"[CharitiesSectionCompleteService][checkForValidApplicationJourney] No charities backend user answers found and no current session id/data"
        )
        Future(Left(controllers.routes.CannotFindApplicationController.onPageLoad))
    }
}
