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

package controllers.regulatorsAndDocuments

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.regulatorsAndDocuments.IsApprovedGoverningDocumentFormProvider
import models.Mode
import navigation.DocumentsNavigator
import pages.regulatorsAndDocuments.{IsApprovedGoverningDocumentPage, SelectGoverningDocumentPage}
import pages.sections.Section3Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.regulatorsAndDocuments.IsApprovedGoverningDocumentView

import javax.inject.Inject
import scala.concurrent.Future

class IsApprovedGoverningDocumentController @Inject() (
  sessionRepository: UserAnswerService,
  navigator: DocumentsNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: IsApprovedGoverningDocumentFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsApprovedGoverningDocumentView
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getDocumentNameKey(SelectGoverningDocumentPage) { documentName =>
        val preparedForm = request.userAnswers.get(IsApprovedGoverningDocumentPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Future.successful(Ok(view(preparedForm, mode, documentName)))
      }

  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getDocumentNameKey(SelectGoverningDocumentPage) { documentName =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, documentName))),
            value =>
              for {
                updatedAnswers <-
                  Future.fromTry(
                    request.userAnswers.set(IsApprovedGoverningDocumentPage, value).flatMap(_.set(Section3Page, false))
                  )
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(IsApprovedGoverningDocumentPage, mode, updatedAnswers))
          )
      }
  }
}
