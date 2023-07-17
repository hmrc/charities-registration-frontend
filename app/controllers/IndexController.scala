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

package controllers

import config.FrontendAppConfig
import controllers.actions.{AuthIdentifierAction, UserDataRetrievalAction}
import models.UserAnswers
import models.requests.OptionalDataRequest
import pages.AcknowledgementReferencePage
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import service.{CharitiesSave4LaterService, UserAnswerService}
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import utils.TaskListHelper
import views.html.TaskList

import javax.inject.Inject
import scala.concurrent.Future

class IndexController @Inject() (
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  charitiesSave4LaterService: CharitiesSave4LaterService,
  userAnswerService: UserAnswerService,
  taskListHelper: TaskListHelper,
  view: TaskList,
  val controllerComponents: MessagesControllerComponents
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  def onPageLoad(eligibleJourneyId: Option[String] = None): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>
      (hc.sessionId, request.userAnswers) match {
        case (Some(_), Some(userAnswers)) if userAnswers.get(AcknowledgementReferencePage).isDefined =>
          Future.successful(Redirect(routes.RegistrationSentController.onPageLoad))
        case (Some(sessionId), _)                                                                    =>
          if (appConfig.isExternalTest) {
            val userAnswers = request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId))
            userAnswerService.set(userAnswers).map { _ =>
              val result    = taskListHelper.getTaskListRow(userAnswers)
              val completed = result.forall(_.state.equals("index.section.completed"))
              Ok(view(result = result, status = completed, isSwitchOver = None))
            }
          } else {
            val userAnswers = request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId))
            userAnswerService.set(userAnswers).map { _ =>
              val result    = taskListHelper.getTaskListRow(userAnswers)
              val completed = result.forall(_.state.equals("index.section.completed"))
              Ok(view(result = result, status = completed, isSwitchOver = None))
            }
          }
        case _                                                                                       =>
          Future.successful(Redirect(controllers.routes.PageNotFoundController.onPageLoad()))
      }

  }

  private def getTaskList(sessionId: SessionId, eligibleJourneyId: Option[String])(implicit
    request: OptionalDataRequest[_],
    hc: HeaderCarrier
  ): Future[Result] =
    charitiesSave4LaterService.checkForValidApplicationJourney(request, eligibleJourneyId).flatMap {
      case Right(userAnswers) =>
        val result    = taskListHelper.getTaskListRow(userAnswers)
        val completed = result.reverse.tail.forall(_.state.equals("index.section.completed"))
        Future(Ok(view(result, status = completed, None)))
      case Left(call)         =>
        Future(Redirect(call))
    }

  def keepalive: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId))
    userAnswerService.set(userAnswers).map { _ =>
      NoContent
    }
  }

  def signInDifferentAccount: Action[AnyContent] = Action { implicit request =>
    Redirect(
      appConfig.loginUrl,
      Map(
        appConfig.loginContinueKey -> Seq(appConfig.incorrectDetailsLoginContinueUrl),
        "origin"                   -> Seq(appConfig.appName)
      )
    )
  }

  def registerNewAccount: Action[AnyContent] = Action { implicit request =>
    Redirect(
      appConfig.registerUrl,
      Map(
        appConfig.registrationContinueKey -> Seq(appConfig.incorrectDetailsLoginContinueUrl),
        "origin"                          -> Seq(appConfig.appName),
        "accountType"                     -> Seq("organisation")
      )
    )
  }
}
