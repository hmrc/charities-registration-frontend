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

package controllers

import config.FrontendAppConfig
import connectors.CharitiesShortLivedCache
import controllers.actions.{AuthIdentifierAction, UserDataRetrievalAction}
import javax.inject.Inject
import models.UserAnswers
import pages.AcknowledgementReferencePage
import pages.IsSwitchOverUserPage
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import service.CharitiesKeyStoreService
import utils.TaskListHelper
import views.html.TaskList

import scala.concurrent.Future

class IndexController @Inject()(
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    charitiesKeyStoreService: CharitiesKeyStoreService,
    cache: CharitiesShortLivedCache,
    userAnswerRepository: UserAnswerRepository,
    taskListHelper: TaskListHelper,
    view: TaskList,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>

    hc.sessionId match {
      case Some(sessionId) =>
        request.userAnswers match {
          case Some(userAnswers) if userAnswers.get(AcknowledgementReferencePage).isDefined =>
            Future.successful(Redirect(routes.RegistrationSentController.onPageLoad()))
          case _ =>
            for {
              (userAnswers, validationErrors) <- charitiesKeyStoreService.getCacheData(request)
              _ <- userAnswerRepository.set(userAnswers)
              isSwitchOver <- cache.fetchAndGetEntry[Boolean](sessionId.value, IsSwitchOverUserPage)
            } yield {
              validationErrors match {
                case noErrors if noErrors.isEmpty =>
                  val result = taskListHelper.getTaskListRow(userAnswers)
                  val completed = result.reverse.tail.forall(_.state.equals("index.section.completed"))
                  Ok(view(result, status = completed, isSwitchOver))
                case _ =>
                  Redirect(routes.SessionExpiredController.onPageLoad()) // TODO make it redirect to a new yet-to-be-developed transformation error page
              }

            }
        }
      case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
    }

  }

  def keepalive: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId))
    userAnswerRepository.set(userAnswers).map { _ =>
      NoContent
    }
  }

}
