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
import controllers.actions._
import forms.common.YesNoFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import pages.EmailOrPostPage
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.RequiredDocumentsHelper
import views.html.EmailOrPostView

import scala.concurrent.Future

class EmailOrPostController @Inject()(
    val userAnswerRepository: UserAnswerRepository,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: RegistrationDataRequiredAction,
    formProvider: YesNoFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: EmailOrPostView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  val messagePrefix: String = "emailOrPost"
  val form: Form[Boolean] = formProvider(messagePrefix)

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request: DataRequest[_] =>
    request.userAnswers match {
      case userAnswers if userAnswers.get(EmailOrPostPage).isDefined => Redirect(routes.RegistrationSentController.onPageLoad())
      case userAnswers =>
        Ok(view(form,
          RequiredDocumentsHelper.getRequiredDocuments(userAnswers),
          RequiredDocumentsHelper.getForeignOfficialsMessages(userAnswers)))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      formWithErrors =>
        Future.successful(BadRequest(view(formWithErrors,
          RequiredDocumentsHelper.getRequiredDocuments(request.userAnswers),
          RequiredDocumentsHelper.getForeignOfficialsMessages(request.userAnswers)
        ))),

      value =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(EmailOrPostPage, value))
          _              <- userAnswerRepository.set(updatedAnswers)

        } yield Redirect(controllers.routes.RegistrationSentController.onPageLoad())
    )
  }
}
