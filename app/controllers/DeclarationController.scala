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
import controllers.actions.{AuthIdentifierAction, DataRequiredAction, UserDataRetrievalAction}
import pages.{AcknowledgementReferencePage, ApplicationSubmissionDatePage}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import service.CharitiesRegistrationService
import transformers.submission.CharitySubmissionTransformer
import views.html.DeclarationView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future

class DeclarationController @Inject()(
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    registrationService: CharitiesRegistrationService,
    transformer: CharitySubmissionTransformer,
    userAnswerRepository: UserAnswerRepository,
    view: DeclarationView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val logger = Logger(this.getClass)

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    Future.successful(Ok(view()))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    if(appConfig.isExternalTest) {
      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(AcknowledgementReferencePage, "0123 4567 8901")
          .flatMap(_.set(ApplicationSubmissionDatePage, LocalDate.now())))
        _ <- userAnswerRepository.set(updatedAnswers)
      } yield
        Redirect(controllers.routes.EmailOrPostController.onPageLoad())
    }
    else {
      request.userAnswers.data.transform(transformer.userAnswersToSubmission) match {
        case JsSuccess(requestJson, _) =>
          logger.info("[DeclarationController][onSubmit] userAnswers to submission transformation successful")
          registrationService.register(requestJson)

        case JsError(err) =>
          logger.error("[DeclarationController][onSubmit] userAnswers to submission transformation failed with errors: " + err)
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
    }
  }
}
