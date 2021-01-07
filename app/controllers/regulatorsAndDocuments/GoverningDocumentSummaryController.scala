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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import models.NormalMode
import navigation.DocumentsNavigator
import pages.IndexPage
import pages.regulatorsAndDocuments.GoverningDocumentSummaryPage
import pages.sections.Section3Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import viewmodels.regulatorsAndDocuments.GoverningDocumentStatusHelper.checkComplete
import viewmodels.regulatorsAndDocuments.GoverningDocumentSummaryHelper
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class GoverningDocumentSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: DocumentsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    view: CheckYourAnswersView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val governingDocumentAnswersHelper = new GoverningDocumentSummaryHelper(request.userAnswers)

    if (governingDocumentAnswersHelper.rows.isEmpty) {
      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))
    } else {

      Ok(view(governingDocumentAnswersHelper.rows, GoverningDocumentSummaryPage,
        controllers.regulatorsAndDocuments.routes.GoverningDocumentSummaryController.onSubmit()))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(Section3Page, checkComplete(request.userAnswers)))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(GoverningDocumentSummaryPage, NormalMode, updatedAnswers))
  }

}
