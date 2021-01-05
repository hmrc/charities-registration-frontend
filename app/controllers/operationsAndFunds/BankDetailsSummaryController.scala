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
import javax.inject.Inject
import models.NormalMode
import navigation.BankDetailsNavigator
import pages.IndexPage
import pages.operationsAndFunds.BankDetailsSummaryPage
import pages.sections.Section6Page
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.operationsAndFunds.BankDetailsStatusHelper.checkComplete
import viewmodels.operationsAndFunds.BankDetailsSummaryHelper
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class BankDetailsSummaryController @Inject()(
  val sessionRepository: UserAnswerRepository,
  val navigator: BankDetailsNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  view: CheckYourAnswersView,
  val controllerComponents: MessagesControllerComponents
)(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val bankDetailsSummaryHelper = new BankDetailsSummaryHelper(request.userAnswers)

    if (bankDetailsSummaryHelper.rows.isEmpty) {

      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))

    } else {

      Ok(view(bankDetailsSummaryHelper.rows, BankDetailsSummaryPage,
        controllers.operationsAndFunds.routes.BankDetailsSummaryController.onSubmit()))
    }

  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
    updatedAnswers <- Future.fromTry(result = request.userAnswers.set(Section6Page, checkComplete(request.userAnswers)))
    _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(BankDetailsSummaryPage, NormalMode, updatedAnswers))

  }
}
