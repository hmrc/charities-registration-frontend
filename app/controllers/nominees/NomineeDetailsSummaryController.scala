/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.nominees

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import models.NormalMode
import navigation.NomineesNavigator
import pages.nominees.NomineeDetailsSummaryPage
import pages.sections.Section9Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import viewmodels.nominees.NomineeDetailsSummaryHelper
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class NomineeDetailsSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: NomineesNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    view: CheckYourAnswersView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val nomineeSummaryHelper = new NomineeDetailsSummaryHelper(request.userAnswers)

    Ok(view(nomineeSummaryHelper.rows, NomineeDetailsSummaryPage,
      controllers.nominees.routes.NomineeDetailsSummaryController.onSubmit(), h2Required = true))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
      updatedAnswers <- Future.fromTry(result = request.userAnswers.set(Section9Page, true))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(NomineeDetailsSummaryPage, NormalMode, updatedAnswers))

  }
}