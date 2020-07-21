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

package controllers.authorisedOfficials

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import javax.inject.Inject
import models.{Index, NormalMode}
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.AuthorisedOfficialsSummaryPage
import pages.sections.Section7Page
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.authorisedOfficials.AuthorisedOfficialsSummaryHelper
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class AuthorisedOfficialsSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: AuthorisedOfficialsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    view: CheckYourAnswersView,
    val controllerComponents: MessagesControllerComponents
)(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val authorisedOfficialsSummaryHelper = new AuthorisedOfficialsSummaryHelper(Index(0))(request.userAnswers)

    Ok(view(authorisedOfficialsSummaryHelper.rows, AuthorisedOfficialsSummaryPage,
    controllers.authorisedOfficials.routes.AuthorisedOfficialsSummaryController.onSubmit(), h2Required = true))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
    updatedAnswers <- Future.fromTry(result = request.userAnswers.set(Section7Page, true))
    _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(AuthorisedOfficialsSummaryPage, NormalMode, updatedAnswers))

  }
}
