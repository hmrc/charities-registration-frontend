/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.operationsAndFunds.FundRaisingFormProvider
import javax.inject.Inject
import models.Mode
import models.operations.FundRaisingOptions
import navigation.FundRaisingNavigator
import pages.operationsAndFunds.FundRaisingPage
import pages.sections.Section5Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.operationsAndFunds.FundRaisingView

import scala.concurrent.Future

class FundRaisingController @Inject()(
    val sessionRepository: UserAnswerService,
    val navigator: FundRaisingNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: FundRaisingFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: FundRaisingView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  val form: Form[Set[FundRaisingOptions]] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(FundRaisingPage) match {
      case None => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      formWithErrors =>
        Future.successful(BadRequest(view(formWithErrors, mode))),

      value =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(FundRaisingPage, value).flatMap(_.set(Section5Page, false)))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(FundRaisingPage, mode, updatedAnswers))
    )
  }
}
