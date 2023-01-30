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

package controllers.regulatorsAndDocuments

import java.time.LocalDate

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.regulatorsAndDocuments.WhenGoverningDocumentApprovedFormProvider
import javax.inject.Inject
import models.Mode
import navigation.DocumentsNavigator
import pages.regulatorsAndDocuments.{SelectGoverningDocumentPage, WhenGoverningDocumentApprovedPage}
import pages.sections.Section3Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.regulatorsAndDocuments.WhenGoverningDocumentApprovedView

import scala.concurrent.Future

class WhenGoverningDocumentApprovedController @Inject() (
  val sessionRepository: UserAnswerService,
  val navigator: DocumentsNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: WhenGoverningDocumentApprovedFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhenGoverningDocumentApprovedView
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  def form: Form[LocalDate] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getDocumentNameKey(SelectGoverningDocumentPage) { documentName =>
        val preparedForm = request.userAnswers.get(WhenGoverningDocumentApprovedPage) match {
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
                updatedAnswers <- Future.fromTry(
                                    request.userAnswers
                                      .set(WhenGoverningDocumentApprovedPage, value)
                                      .flatMap(_.set(Section3Page, false))
                                  )
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(WhenGoverningDocumentApprovedPage, mode, updatedAnswers))
          )
      }
  }
}
