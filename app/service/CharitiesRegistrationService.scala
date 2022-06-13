/*
 * Copyright 2022 HM Revenue & Customs
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

import audit.{AuditService, SubmissionAuditEvent}
import connectors.CharitiesConnector
import connectors.httpParsers.UnexpectedFailureException
import javax.inject.Inject
import models.requests.DataRequest
import pages.{AcknowledgementReferencePage, ApplicationSubmissionDatePage}
import play.api.Logger
import play.api.libs.json.{JsBoolean, JsObject}
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.http.HeaderCarrier
import utils.TimeMachine

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class CharitiesRegistrationService @Inject()(
  userAnswerService: UserAnswerService,
  auditService: AuditService,
  charitiesConnector: CharitiesConnector,
  timeMachine: TimeMachine
  ) {

  private val logger = Logger(this.getClass)

  def register(requestJson: JsObject, noEmailPost: Boolean)(implicit request: DataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = {

    request.userAnswers.get(AcknowledgementReferencePage) match {
      case None =>
        charitiesConnector.registerCharities(requestJson, Random.nextInt()).flatMap {
          case Right(result) =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AcknowledgementReferencePage, result.acknowledgementReference)
                .flatMap(_.set(ApplicationSubmissionDatePage, timeMachine.now())))
              _ <- userAnswerService.set(updatedAnswers)
              _ <- Future.successful(auditService.sendEvent(SubmissionAuditEvent(requestJson + ("declaration" -> JsBoolean(true)))))
            } yield{
              if(noEmailPost){
                Redirect(controllers.routes.RegistrationSentController.onPageLoad)
              } else {
                Redirect(controllers.routes.EmailOrPostController.onPageLoad)
              }
            }

          case Left(errors) =>
            logger.error(s"[CharitiesRegistrationService][register] registration failed with error $errors")
            throw UnexpectedFailureException(errors.body)

        } recover {
          case errors =>
            logger.error(s"[CharitiesRegistrationService][register] registration failed with error $errors")
            throw UnexpectedFailureException(errors.getMessage)
        }

      case Some(_) => Future.successful(Redirect(controllers.routes.EmailOrPostController.onPageLoad))
    }

  }

}
