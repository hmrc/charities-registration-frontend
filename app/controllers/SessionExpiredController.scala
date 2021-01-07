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

package controllers

import config.FrontendAppConfig
import controllers.actions.{DataRetrievalAction, SessionIdentifierAction}
import javax.inject.Inject
import models.UserAnswers
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepositoryImpl
import views.html.errors.SessionExpiredView

import scala.concurrent.Future


class SessionExpiredController @Inject()(sessionRepository: SessionRepositoryImpl,
  identify: SessionIdentifierAction,
  getData: DataRetrievalAction,
  view: SessionExpiredView,
  val controllerComponents: MessagesControllerComponents)(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  def onPageLoad: Action[AnyContent] = identify.async { implicit request =>
      Future.successful(Ok(view()))
  }

  def keepalive: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId))
    sessionRepository.set(userAnswers).map { _ =>
      NoContent
    }
  }
}
