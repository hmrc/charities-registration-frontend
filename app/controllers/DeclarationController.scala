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
import controllers.actions.{AuthIdentifierAction, DataRequiredAction, UserDataRetrievalAction}
import javax.inject.Inject
import models.submission.CharitySubmissionTransformer
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.CharitiesRegistrationService
import views.html.DeclarationView

import scala.concurrent.Future

class DeclarationController @Inject()(
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    registrationService: CharitiesRegistrationService,
    transformer: CharitySubmissionTransformer,
    view: DeclarationView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val logger = Logger(this.getClass)

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    Future.successful(Ok(view()))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.data.transform(transformer.userAnswersToSubmission).asOpt match {
      case Some(requestJson) =>
        registrationService.register(requestJson)
      case _ =>
        logger.error("[DeclarationController][onSubmit] [CharitySubmissionTransformer] Json transformer errors")
        Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
    }
  }
}
