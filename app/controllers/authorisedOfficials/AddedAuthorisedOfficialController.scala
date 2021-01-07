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

package controllers.authorisedOfficials

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions._
import controllers.common.AddedOfficialController
import models.Index
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.{AddedAuthorisedOfficialPage, AuthorisedOfficialsNamePage}
import pages.sections.Section7Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import service.CountryService
import views.html.common.AddedOfficialsView

import scala.concurrent.Future

class AddedAuthorisedOfficialController @Inject()(
    override val sessionRepository: UserAnswerRepository,
    override val navigator: AuthorisedOfficialsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    override val countryService: CountryService,
    override val view: AddedOfficialsView,
    override val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends AddedOfficialController {

  override val messagePrefix: String = "addedAuthorisedOfficial"

  def onPageLoad(index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>

      Future.successful(getView(index, controllers.authorisedOfficials.routes.AddedAuthorisedOfficialController.onSubmit(index), authorisedOfficialsName))

  }}

  def onSubmit(index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    postView(AddedAuthorisedOfficialPage(index), Section7Page)
  }
}

