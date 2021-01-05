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

import audit.{AuditService, DeclarationAuditEvent, SubmissionAuditEvent}
import connectors.CharitiesConnector
import javax.inject.Inject
import models.requests.DataRequest
import pages.{AcknowledgementReferencePage, ApplicationSubmissionDatePage}
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import repositories.UserAnswerRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.TimeMachine

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class CharitiesRegistrationService @Inject()(
    userAnswerRepository: UserAnswerRepository,
    auditService: AuditService,
    charitiesConnector: CharitiesConnector,
    timeMachine: TimeMachine
  ) {

  private val logger = Logger(this.getClass)

  def register(requestJson: JsValue)(implicit request: DataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = {

    request.userAnswers.get(AcknowledgementReferencePage) match {
      case None =>
        charitiesConnector.registerCharities(requestJson, Random.nextInt()).flatMap {
          case Right(result) =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AcknowledgementReferencePage, result.acknowledgementReference)
                .flatMap(_.set(ApplicationSubmissionDatePage, timeMachine.now())))
              _ <- userAnswerRepository.set(updatedAnswers)
              _ <- Future.successful(auditService.sendEvent(DeclarationAuditEvent(true)))
              _ <- Future.successful(auditService.sendEvent(SubmissionAuditEvent(requestJson)))
            } yield
              Redirect(controllers.routes.EmailOrPostController.onPageLoad())

          case Left(errors) =>
            logger.error(s"[CharitiesRegistrationService][register] registration failed with error $errors")
            Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))

        } recover {
          case errors =>
            logger.error(s"[CharitiesRegistrationService][register] registration failed with error $errors")
            Redirect(controllers.routes.SessionExpiredController.onPageLoad())
        }

      case Some(_) => Future.successful(Redirect(controllers.routes.EmailOrPostController.onPageLoad()))
    }

  }

}
