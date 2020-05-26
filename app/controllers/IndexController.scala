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
import javax.inject.Inject
import models.{NormalMode, Spoke, TaskListSection, UserAnswers}
import pages.{Section1Page, Section2Page, Section3Page, Section4Page}
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

      val section1 = TaskListSection(List(
        Spoke(charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section1Page)))))

      val section2 = TaskListSection(List(
        Spoke(controllers.regulatorsAndDocuments.routes.IsCharityRegulatorController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section2Page))),
        Spoke(charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section2Page)))))

      val section3 = TaskListSection(List(
        Spoke(charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section3Page))),
        Spoke(charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section3Page))),
        Spoke(charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section3Page)))))

      val section4 = TaskListSection(List(
        Spoke(charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section4Page))),
        Spoke(charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section4Page))),
        Spoke(charityInfoRoutes.CharityNameController.onPageLoad(NormalMode).url, getStatus(userAnswers.get(Section4Page)))))

      val result = List(section1, section2, section3, section4)


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
      case Some(false) => "index.section.inpProgress"
      case _ => "index.section.notStarted"
    }
  }
}
