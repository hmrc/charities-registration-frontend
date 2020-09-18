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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.{AuthIdentifierAction, DataRequiredAction, UserDataRetrievalAction}
import controllers.common.AddedOfficialController
import models.Index
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.AddedSecondAuthorisedOfficialPage
import pages.sections.Section7Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import views.html.common.AddedOfficialsView

class AddedSecondAuthorisedOfficialController @Inject()(
    override val sessionRepository: UserAnswerRepository,
    override val navigator: AuthorisedOfficialsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    override val view: AddedOfficialsView,
    override val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends AddedOfficialController {

  override val messagePrefix: String = "addedSecondAuthorisedOfficial"

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    getView(Index(1), controllers.authorisedOfficials.routes.AddedSecondAuthorisedOfficialController.onSubmit())
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    postView(AddedSecondAuthorisedOfficialPage, Section7Page)
  }
}
