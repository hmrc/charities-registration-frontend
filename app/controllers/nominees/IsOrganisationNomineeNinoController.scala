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

package controllers.nominees

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.IsOfficialsNinoController
import forms.common.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.NomineesNavigator
import pages.nominees.{IsOrganisationNomineeNinoPage, OrganisationAuthorisedPersonNamePage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.YesNoView

import scala.concurrent.Future

class IsOrganisationNomineeNinoController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: YesNoFormProvider,
    override val sessionRepository: UserAnswerRepository,
    override val navigator: NomineesNavigator,
    override val controllerComponents: MessagesControllerComponents,
    override val view: YesNoView
  )(implicit appConfig: FrontendAppConfig) extends IsOfficialsNinoController {

  override val messagePrefix: String = "isOrganisationNomineeNino"
  private val form: Form[Boolean] = formProvider(messagePrefix)


  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OrganisationAuthorisedPersonNamePage) { organisationAuthorisedPersonName =>

        Future.successful(getView(IsOrganisationNomineeNinoPage, form, organisationAuthorisedPersonName,
          controllers.nominees.routes.IsOrganisationNomineeNinoController.onSubmit(mode)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OrganisationAuthorisedPersonNamePage) { organisationAuthorisedPersonName =>

        postView(mode, IsOrganisationNomineeNinoPage, form, organisationAuthorisedPersonName, Section9Page,
          controllers.nominees.routes.IsOrganisationNomineeNinoController.onSubmit(mode))
      }
  }
}
