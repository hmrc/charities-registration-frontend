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

package controllers.regulatorsAndDocuments

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import models.regulators.CharityRegulator
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Scottish}
import models.regulators.SelectWhyNoRegulator.Other
import models.{NormalMode, UserAnswers}
import navigation.RegulatorsAndDocumentsNavigator
import pages.{IndexPage, QuestionPage}
import pages.regulatorsAndDocuments._
import pages.sections.Section2Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import viewmodels.regulatorsAndDocuments.{RegulatorsStatusHelper, RegulatorsSummaryHelper}
import views.html.CheckYourAnswersView

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class RegulatorsSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: RegulatorsAndDocumentsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    view: CheckYourAnswersView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val regulatorsAnswersHelper = new RegulatorsSummaryHelper(request.userAnswers)
    if (regulatorsAnswersHelper.rows.isEmpty) {
      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))
    } else {
      Ok(view(regulatorsAnswersHelper.rows, RegulatorsSummaryPage,
        controllers.regulatorsAndDocuments.routes.RegulatorsSummaryController.onSubmit()))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
      updatedAnswers <- Future.fromTry(result = request.userAnswers.set(Section2Page, RegulatorsStatusHelper.checkComplete(request.userAnswers)))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(RegulatorsSummaryPage, NormalMode, updatedAnswers))

  }
}
