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
import controllers.actions.{AuthIdentifierAction, RegistrationDataRequiredAction, UserDataRetrievalAction}
import pages.{AcknowledgementReferencePage, ApplicationSubmissionDatePage, EmailOrPostPage}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import utils.ImplicitDateFormatter
import viewmodels.RequiredDocumentsHelper
import views.html.RegistrationSentView

import javax.inject.Inject
import scala.concurrent.Future

//scalastyle:off magic.number
class RegistrationSentController @Inject()(
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    userAnswerRepository: UserAnswerRepository,
    requireData: RegistrationDataRequiredAction,
    view: RegistrationSentView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends ImplicitDateFormatter with LocalBaseController {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    (request.userAnswers.get(AcknowledgementReferencePage),
      request.userAnswers.get(ApplicationSubmissionDatePage)) match {
      case (Some(acknowledgementReference), Some(applicationSubmissionDate)) =>
        request.userAnswers.get(EmailOrPostPage) match {
          case Some(emailOrPost) =>
            Future.successful(Ok(view(dayToString(applicationSubmissionDate.plusDays(
              appConfig.servicesConfig.getInt("mongodb.user-answers.timeToLiveInDays"))),
              dayToString(applicationSubmissionDate, dayOfWeek = false), acknowledgementReference, emailOrPost,
              RequiredDocumentsHelper.getRequiredDocuments(request.userAnswers),
              RequiredDocumentsHelper.getForeignOfficialsMessages(request.userAnswers)
            )))
          case _ => Future.successful(Redirect(controllers.routes.EmailOrPostController.onPageLoad()))
        }
      case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
    }
  }

  def onChange: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(EmailOrPostPage) match {
      case Some(emailOrPost) =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(EmailOrPostPage, !emailOrPost))
          _              <- userAnswerRepository.set(updatedAnswers)
        } yield Redirect(routes.RegistrationSentController.onPageLoad())
      case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
    }
  }
}
