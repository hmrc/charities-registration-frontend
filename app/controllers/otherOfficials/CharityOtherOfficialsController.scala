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

package controllers.otherOfficials

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import models.Index
import play.api.mvc._
import service.UserAnswerService
import views.html.otherOfficials.CharityOtherOfficialsView

import javax.inject.Inject
import scala.concurrent.Future

class CharityOtherOfficialsController @Inject() (
  val userAnswerService: UserAnswerService,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CharityOtherOfficialsView
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    Future.successful(Ok(view(Index(0))))
  }
}
