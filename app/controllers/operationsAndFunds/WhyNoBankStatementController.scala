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

package controllers.operationsAndFunds

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.operationsAndFunds.WhyNoBankStatementFormProvider
import models.Mode
import navigation.FundRaisingNavigator
import pages.operationsAndFunds.WhyNoBankStatementPage
import pages.sections.Section5Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.operationsAndFunds.WhyNoBankStatementView

import javax.inject.Inject
import scala.concurrent.Future

class WhyNoBankStatementController @Inject() (
  val sessionRepository: UserAnswerService,
  val navigator: FundRaisingNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: WhyNoBankStatementFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhyNoBankStatementView
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  val form: Form[String] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(WhyNoBankStatementPage) match {
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
                Future
                  .fromTry(request.userAnswers.set(WhyNoBankStatementPage, value).flatMap(_.set(Section5Page, false)))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(WhyNoBankStatementPage, mode, updatedAnswers))
        )
  }
}
