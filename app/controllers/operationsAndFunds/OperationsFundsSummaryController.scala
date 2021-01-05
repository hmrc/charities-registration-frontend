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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions.{AuthIdentifierAction, DataRequiredAction, UserDataRetrievalAction}
import models.NormalMode
import navigation.FundRaisingNavigator
import pages.IndexPage
import pages.operationsAndFunds.OperationsFundsSummaryPage
import pages.sections.Section5Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import service.CountryService
import viewmodels.operationsAndFunds.OperationsFundsStatusHelper.checkComplete
import viewmodels.operationsAndFunds.OperationsFundsSummaryHelper
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class OperationsFundsSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: FundRaisingNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    countryService: CountryService,
    view: CheckYourAnswersView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val operationsFundsSummaryHelper = new OperationsFundsSummaryHelper(request.userAnswers, countryService)
    if (operationsFundsSummaryHelper.rows.isEmpty) {
      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))
    } else {
      Ok(view(operationsFundsSummaryHelper.rows, OperationsFundsSummaryPage,
        controllers.operationsAndFunds.routes.OperationsFundsSummaryController.onSubmit()))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
      updatedAnswers <- Future.fromTry(result = request.userAnswers.set(Section5Page, checkComplete(request.userAnswers)))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(OperationsFundsSummaryPage, NormalMode, updatedAnswers))

  }
}
