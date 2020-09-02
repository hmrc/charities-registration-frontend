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
import controllers.actions._
import controllers.common.IsAddAnotherController
import forms.common.IsAddAnotherFormProvider
import javax.inject.Inject
import models.Mode
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, IsAddAnotherAuthorisedOfficialPage}
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.IsAddAnotherView

import scala.concurrent.Future

class IsAddAnotherAuthorisedOfficialController  @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: IsAddAnotherFormProvider,
    override val sessionRepository: UserAnswerRepository,
    override val navigator: AuthorisedOfficialsNavigator,
    override val controllerComponents: MessagesControllerComponents,
    override val view: IsAddAnotherView
  )(implicit appConfig: FrontendAppConfig) extends IsAddAnotherController {

  override val messagePrefix: String = "isAddAnotherAuthorisedOfficial"
  private val form: Form[Boolean] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(AuthorisedOfficialsNamePage(0)) { authorisedOfficialsName =>

        Future.successful(getView(IsAddAnotherAuthorisedOfficialPage, form, authorisedOfficialsName, "",
          controllers.authorisedOfficials.routes.IsAddAnotherAuthorisedOfficialController.onSubmit(mode)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(AuthorisedOfficialsNamePage(0)) { authorisedOfficialsName =>

        postView(mode, IsAddAnotherAuthorisedOfficialPage, form, authorisedOfficialsName, "",
          Section7Page, controllers.authorisedOfficials.routes.IsAddAnotherAuthorisedOfficialController.onSubmit(mode))
      }
  }
}
