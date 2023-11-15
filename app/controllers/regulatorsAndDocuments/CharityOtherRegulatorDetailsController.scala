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

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.regulatorsAndDocuments.CharityOtherRegulatorDetailsFormProvider
import models.{CharityOtherRegulatorDetails, Mode}
import navigation.RegulatorsAndDocumentsNavigator
import pages.regulatorsAndDocuments.CharityOtherRegulatorDetailsPage
import pages.sections.Section2Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.regulatorsAndDocuments.CharityOtherRegulatorDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class CharityOtherRegulatorDetailsController @Inject() (
  val sessionRepository: UserAnswerService,
  val navigator: RegulatorsAndDocumentsNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: CharityOtherRegulatorDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: CharityOtherRegulatorDetailsView
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {
  val form: Form[CharityOtherRegulatorDetails] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(CharityOtherRegulatorDetailsPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <-
                Future.fromTry(
                  request.userAnswers.set(CharityOtherRegulatorDetailsPage, value).flatMap(_.set(Section2Page, false))
                )
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(CharityOtherRegulatorDetailsPage, mode, updatedAnswers))
        )
  }
}
