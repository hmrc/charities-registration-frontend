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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import models.{Index, NormalMode}
import navigation.OtherOfficialsNavigator
import pages.otherOfficials.AddedOneOtherOfficialPage
import pages.sections.Section8Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import viewmodels.otherOfficials.AddedOneOtherOfficialHelper
import views.html.common.AddedOfficialsView

import scala.concurrent.Future

class AddedOneOtherOfficialController @Inject()(
     val sessionRepository: UserAnswerRepository,
     val navigator: OtherOfficialsNavigator,
     identify: AuthIdentifierAction,
     getData: UserDataRetrievalAction,
     requireData: DataRequiredAction,
     view: AddedOfficialsView,
     val controllerComponents: MessagesControllerComponents
   )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request => {
    val messagePrefix: String = "addedOneOtherOfficial"
    val addedOneOtherOfficialHelper = new AddedOneOtherOfficialHelper(Index(0))(request.userAnswers)

    Ok(view(addedOneOtherOfficialHelper.rows,
      controllers.otherOfficials.routes.AddedOneOtherOfficialController.onSubmit(), messagePrefix))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
      updatedAnswers <- Future.fromTry(result = request.userAnswers.set(Section8Page, false))
      _ <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(AddedOneOtherOfficialPage, NormalMode, updatedAnswers))

  }
}

