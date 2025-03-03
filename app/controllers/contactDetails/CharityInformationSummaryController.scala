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

package controllers.contactDetails

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import models.NormalMode
import navigation.CharityInformationNavigator
import pages.IndexPage
import pages.contactDetails._
import pages.sections.Section1Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.UserAnswerService
import viewmodels.charityInformation.CharityInformationStatusHelper.checkComplete
import viewmodels.charityInformation.CharityInformationSummaryHelper
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CharityInformationSummaryController @Inject() (
  val sessionRepository: UserAnswerService,
  val navigator: CharityInformationNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  view: CheckYourAnswersView,
  val controllerComponents: MessagesControllerComponents
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val charityInformationAnswersHelper = new CharityInformationSummaryHelper(request.userAnswers)

    if (charityInformationAnswersHelper.rows.isEmpty) {
      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))
    } else {
      Ok(
        view(
          charityInformationAnswersHelper.rows,
          CharityInformationSummaryPage,
          controllers.contactDetails.routes.CharityInformationSummaryController.onSubmit()
        )
      )
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    for {
      updatedAnswers <-
        Future.fromTry(result =
          request.userAnswers
            .set(Section1Page, checkComplete(request.userAnswers))
        )
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(CharityInformationSummaryPage, NormalMode, updatedAnswers))

  }
}
