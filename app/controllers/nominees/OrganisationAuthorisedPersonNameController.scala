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

package controllers.nominees

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.NameController
import forms.common.NameFormProvider
import models.{Mode, Name}
import navigation.NomineesNavigator
import pages.nominees.OrganisationAuthorisedPersonNamePage
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.common.NameView

import javax.inject.Inject

class OrganisationAuthorisedPersonNameController @Inject() (
  val identify: AuthIdentifierAction,
  val getData: UserDataRetrievalAction,
  val requireData: DataRequiredAction,
  val formProvider: NameFormProvider,
  override val sessionRepository: UserAnswerService,
  override val navigator: NomineesNavigator,
  override val controllerComponents: MessagesControllerComponents,
  override val view: NameView
)(implicit appConfig: FrontendAppConfig)
    extends NameController {

  override val messagePrefix: String = "organisationAuthorisedPersonName"
  private val form: Form[Name]       = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    getView(
      OrganisationAuthorisedPersonNamePage,
      form,
      controllers.nominees.routes.OrganisationAuthorisedPersonNameController.onSubmit(mode)
    )
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      postView(
        mode,
        OrganisationAuthorisedPersonNamePage,
        form,
        Section9Page,
        controllers.nominees.routes.OrganisationAuthorisedPersonNameController.onSubmit(mode)
      )
  }

}
