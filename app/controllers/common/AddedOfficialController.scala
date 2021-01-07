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

package controllers.common

import config.FrontendAppConfig
import controllers.LocalBaseController
import models.requests.DataRequest
import models.{CheckMode, Index, NormalMode}
import navigation.BaseNavigator
import pages.QuestionPage
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents, Result}
import repositories.UserAnswerRepository
import service.CountryService
import viewmodels.authorisedOfficials.AddedAuthorisedOfficialHelper
import viewmodels.otherOfficials.AddedOtherOfficialHelper
import views.html.common.AddedOfficialsView

import scala.concurrent.Future

trait AddedOfficialController extends LocalBaseController {
  protected val sessionRepository: UserAnswerRepository
  protected val navigator: BaseNavigator
  protected val view: AddedOfficialsView
  protected val controllerComponents: MessagesControllerComponents
  protected val messagePrefix: String
  protected val countryService: CountryService

  def getView(index: Index, submitCall: Call, officialsName: String)(implicit appConfig: FrontendAppConfig, request: DataRequest[AnyContent]): Result = {

    val rows = messagePrefix match {
      case "addedAuthorisedOfficial" =>
        new AddedAuthorisedOfficialHelper(index, CheckMode, countryService)(request.userAnswers).rows
      case "addedOtherOfficial" =>
        new AddedOtherOfficialHelper(index, CheckMode, countryService)(request.userAnswers).rows
    }

    Ok(view(rows, submitCall, officialsName, messagePrefix))
  }

  def postView(page: QuestionPage[String], section: QuestionPage[Boolean])(
    implicit appConfig: FrontendAppConfig, request: DataRequest[AnyContent]): Future[Result] = {

    for {
      updatedAnswers <- Future.fromTry(result = request.userAnswers.set(section, false))
      _ <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(page, NormalMode, updatedAnswers))
  }

}

