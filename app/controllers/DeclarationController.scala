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
import connectors.httpParsers.UnexpectedFailureException
import controllers.actions.{AuthIdentifierAction, DataRequiredAction, UserDataRetrievalAction}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.CharitiesRegistrationService
import transformers.submission.CharitySubmissionTransformer
import views.html.DeclarationView

import javax.inject.Inject
import scala.concurrent.Future

class DeclarationController @Inject() (
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  registrationService: CharitiesRegistrationService,
  transformer: CharitySubmissionTransformer,
  view: DeclarationView,
  val controllerComponents: MessagesControllerComponents
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  private val logger = Logger(this.getClass)

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    if (!isAllSectionsCompleted()) {
      Future(Redirect(controllers.routes.IndexController.onPageLoad(None)))
    } else {
      Future(Ok(view()))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.data.transform(transformer.userAnswersToSubmission) match {
      case JsSuccess(requestJson, _) =>
        logger.info("[DeclarationController][onSubmit] userAnswers to submission transformation successful")
        registrationService.register(requestJson, appConfig.noEmailPost)
      case JsError(err)              =>
        logger.error(
          "[DeclarationController][onSubmit] userAnswers to submission transformation failed with errors: " + err
        )
        throw UnexpectedFailureException(err.toString())
    }
  }
}
