/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.errors.{WeDeletedYourAnswersView, YouDeletedYourAnswersView}

import javax.inject.Inject

class DeleteAnswersController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  getData: DataRetrievalAction,
  identify: SessionIdentifierAction,
  val sessionRepository: SessionRepository,
  weDeletedAnswersView: WeDeletedYourAnswersView,
  youDeletedAnswersView: YouDeletedYourAnswersView
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  def youDeletedAnswers: Action[AnyContent] = (identify andThen getData) { implicit request =>
    request.userAnswers.map { userAnswer =>
      sessionRepository.delete(userAnswer)
    }

    Ok(youDeletedAnswersView()).withNewSession
  }

  def weDeletedYourAnswers: Action[AnyContent] = (identify andThen getData) { implicit request =>
    request.userAnswers.map { userAnswer =>
      sessionRepository.delete(userAnswer)
    }
    Ok(weDeletedAnswersView()).withNewSession
  }
}
