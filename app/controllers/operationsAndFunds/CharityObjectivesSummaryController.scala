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
import navigation.ObjectivesNavigator
import pages.IndexPage
import pages.operationsAndFunds.{CharitableObjectivesPage, CharitablePurposesPage, CharityObjectivesSummaryPage, PublicBenefitsPage}
import pages.sections.Section4Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import viewmodels.operationsAndFunds.{CharityObjectivesStatusHelper, CharityObjectivesSummaryHelper}
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CharityObjectivesSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: ObjectivesNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    view: CheckYourAnswersView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val charityObjectivesAnswersHelper = new CharityObjectivesSummaryHelper(request.userAnswers)

    if(charityObjectivesAnswersHelper.rows.isEmpty) {
      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))
    } else {
      Ok(view(charityObjectivesAnswersHelper.rows, CharityObjectivesSummaryPage,
        controllers.operationsAndFunds.routes.CharityObjectivesSummaryController.onSubmit()))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
      updatedAnswers <- Future.fromTry(result = request.userAnswers.set(
                          Section4Page, CharityObjectivesStatusHelper.checkComplete(request.userAnswers)))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(CharityObjectivesSummaryPage, NormalMode, updatedAnswers))

  }
}
