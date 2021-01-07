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

package controllers.operationsAndFunds

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.common.CurrencyFormProvider
import javax.inject.Inject
import models.Mode
import navigation.FundRaisingNavigator
import pages.operationsAndFunds.EstimatedIncomePage
import pages.sections.Section5Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.CurrencyView

import scala.concurrent.Future

class EstimatedIncomeController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: FundRaisingNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: CurrencyFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: CurrencyView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  val form: Form[BigDecimal] = formProvider("estimatedIncome")

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(EstimatedIncomePage) match {
      case None => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, "estimatedIncome", controllers.operationsAndFunds.routes.EstimatedIncomeController.onSubmit(mode)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      formWithErrors =>
        Future.successful(BadRequest(view(formWithErrors, "estimatedIncome", controllers.operationsAndFunds.routes.EstimatedIncomeController.onSubmit(mode)))),
      value =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(EstimatedIncomePage, value).flatMap(_.set(Section5Page, false)))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(EstimatedIncomePage, mode, updatedAnswers))
    )
  }
}
