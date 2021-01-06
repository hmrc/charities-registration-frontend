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

import java.time.LocalDate

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.DateOfBirthController
import forms.common.DateOfBirthFormProvider
import javax.inject.Inject
import models.Mode
import navigation.NomineesNavigator
import pages.nominees.{OrganisationAuthorisedPersonDOBPage, OrganisationAuthorisedPersonNamePage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.DateOfBirthView

import scala.concurrent.Future

class OrganisationAuthorisedPersonDOBController @Inject()(
   val identify: AuthIdentifierAction,
   val getData: UserDataRetrievalAction,
   val requireData: DataRequiredAction,
   val formProvider: DateOfBirthFormProvider,
   override val sessionRepository: UserAnswerRepository,
   override val navigator: NomineesNavigator,
   override val controllerComponents: MessagesControllerComponents,
   override val view: DateOfBirthView
   )(implicit appConfig: FrontendAppConfig) extends DateOfBirthController {

  override val messagePrefix: String = "organisationAuthorisedPersonDOB"

  private val form: Form[LocalDate] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OrganisationAuthorisedPersonNamePage) { organisationAuthorisedPersonName =>

        Future.successful(getView(OrganisationAuthorisedPersonDOBPage, form, organisationAuthorisedPersonName,
          controllers.nominees.routes.OrganisationAuthorisedPersonDOBController.onSubmit(mode)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OrganisationAuthorisedPersonNamePage) { organisationAuthorisedPersonName =>

        postView(mode, OrganisationAuthorisedPersonDOBPage, form, organisationAuthorisedPersonName, Section9Page,
          controllers.nominees.routes.OrganisationAuthorisedPersonDOBController.onSubmit(mode))
      }
  }
}
