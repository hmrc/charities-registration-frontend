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

package controllers.otherOfficials

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import javax.inject.Inject
import models.{Index, NormalMode}
import navigation.OtherOfficialsNavigator
import pages.otherOfficials.OtherOfficialsSummaryPage
import pages.sections.Section8Page
import play.api.mvc._
import repositories.UserAnswerRepository
import service.CountryService
import viewmodels.authorisedOfficials.AddedOfficialsSummaryHelper
import views.html.common.OfficialsSummaryView

import scala.concurrent.Future

class OtherOfficialsSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: OtherOfficialsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    countryService: CountryService,
    view: OfficialsSummaryView,
    val controllerComponents: MessagesControllerComponents
)(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val firstOtherOfficialsSummaryHelper = new AddedOfficialsSummaryHelper(Index(0), countryService = countryService)(request.userAnswers)
    val secondOtherOfficialsSummaryHelper = new AddedOfficialsSummaryHelper(Index(1), countryService = countryService)(request.userAnswers)

    Ok(view(firstOtherOfficialsSummaryHelper.otherRows, secondOtherOfficialsSummaryHelper.otherRowsAddAnother,
      OtherOfficialsSummaryPage, controllers.otherOfficials.routes.OtherOfficialsSummaryController.onSubmit(),
      h2Required = true))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
    updatedAnswers <- Future.fromTry(result = request.userAnswers.set(Section8Page, true))
    _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, updatedAnswers))

  }
}
