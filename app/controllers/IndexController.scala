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

package controllers

import config.FrontendAppConfig
import controllers.actions.{AuthIdentifierAction, UserDataRetrievalAction}
import controllers.charityInformation.{routes => charityInfoRoutes}
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import controllers.operationsAndFunds.{routes => opsAndFundsRoutes}
import javax.inject.Inject
import models.{NormalMode, TaskListSection, UserAnswers}
import pages.sections._
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import views.html.Index


class IndexController @Inject()(
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    userAnswerRepository: UserAnswerRepository,
    view: Index,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId))
    userAnswerRepository.set(userAnswers).map { _ =>

      val section1 = TaskListSection(
        charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section1Page)))

      val section2 = TaskListSection(
        regulatorDocsRoutes.IsCharityRegulatorController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section2Page)))

      val section3 = TaskListSection(
        regulatorDocsRoutes.SelectGoverningDocumentController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section3Page)))

      val section4 = TaskListSection(
        opsAndFundsRoutes.CharitableObjectivesController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section4Page)))

      val section5 = TaskListSection(
        opsAndFundsRoutes.FundRaisingController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section5Page)))

      val section6 = TaskListSection(
        opsAndFundsRoutes.BankDetailsController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section6Page)))

      val result = List(section1, section2, section3, section4, section5, section6)

      Ok(view(result))
    }
  }

  def keepalive: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId))
    userAnswerRepository.set(userAnswers).map { _ =>
      NoContent
    }
  }
  
  private def getStatus(status : Option[Boolean]) : String = {
    status match {
      case Some(true) => "index.section.completed"
      case Some(false) => "index.section.inProgress"
      case _ => "index.section.notStarted"
    }
  }

}
