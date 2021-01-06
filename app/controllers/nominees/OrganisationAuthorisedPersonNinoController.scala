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
import controllers.common.NinoController
import forms.common.NinoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.NomineesNavigator
import pages.nominees.{OrganisationAuthorisedPersonNamePage, OrganisationAuthorisedPersonNinoPage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.NinoView

import scala.concurrent.Future

class OrganisationAuthorisedPersonNinoController @Inject()(
   val identify: AuthIdentifierAction,
   val getData: UserDataRetrievalAction,
   val requireData: DataRequiredAction,
   val formProvider: NinoFormProvider,
   override val sessionRepository: UserAnswerRepository,
   override val navigator: NomineesNavigator,
   override val controllerComponents: MessagesControllerComponents,
   override val view: NinoView
  )(implicit appConfig: FrontendAppConfig) extends NinoController {

  override val messagePrefix: String = "organisationAuthorisedPersonNino"
  private val form: Form[String] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(OrganisationAuthorisedPersonNamePage) { authorisedPersonName =>

      Future.successful(getView(OrganisationAuthorisedPersonNinoPage, form, authorisedPersonName,
        controllers.nominees.routes.OrganisationAuthorisedPersonNinoController.onSubmit(mode)))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(OrganisationAuthorisedPersonNamePage) { authorisedPersonName =>

      postView(mode, OrganisationAuthorisedPersonNinoPage, form, authorisedPersonName, Section9Page,
        controllers.nominees.routes.OrganisationAuthorisedPersonNinoController.onSubmit(mode))
    }
  }
}
