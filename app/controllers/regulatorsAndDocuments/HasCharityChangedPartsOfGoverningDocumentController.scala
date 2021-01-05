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

package controllers.regulatorsAndDocuments

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.regulatorsAndDocuments.HasCharityChangedPartsOfGoverningDocumentFormProvider
import javax.inject.Inject
import models.Mode
import navigation.DocumentsNavigator
import pages.regulatorsAndDocuments.HasCharityChangedPartsOfGoverningDocumentPage
import pages.sections.Section3Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.regulatorsAndDocuments.HasCharityChangedPartsOfGoverningDocumentView

import scala.concurrent.Future

class HasCharityChangedPartsOfGoverningDocumentController @Inject()(
    sessionRepository: UserAnswerRepository,
    navigator: DocumentsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: HasCharityChangedPartsOfGoverningDocumentFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: HasCharityChangedPartsOfGoverningDocumentView
 )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(HasCharityChangedPartsOfGoverningDocumentPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(HasCharityChangedPartsOfGoverningDocumentPage, value).flatMap(_.set(Section3Page, false)))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(HasCharityChangedPartsOfGoverningDocumentPage, mode, updatedAnswers))
      )
  }
}
