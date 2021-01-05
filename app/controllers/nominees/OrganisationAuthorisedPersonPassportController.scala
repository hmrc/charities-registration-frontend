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
import controllers.common.PassportController
import forms.common.PassportFormProvider
import javax.inject.Inject
import models.{Mode, Passport}
import navigation.NomineesNavigator
import pages.nominees.{OrganisationAuthorisedPersonNamePage, OrganisationAuthorisedPersonPassportPage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import service.CountryService
import views.html.common.PassportView

import scala.concurrent.Future

class OrganisationAuthorisedPersonPassportController @Inject()(
  val identify: AuthIdentifierAction,
  val getData: UserDataRetrievalAction,
  val requireData: DataRequiredAction,
  val formProvider: PassportFormProvider,
  override val countryService: CountryService,
  override val sessionRepository: UserAnswerRepository,
  override val navigator: NomineesNavigator,
  override val controllerComponents: MessagesControllerComponents,
  override val view: PassportView
  )(implicit appConfig: FrontendAppConfig) extends PassportController {

  override val messagePrefix: String = "organisationAuthorisedPersonPassport"
  private val form: Form[Passport] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OrganisationAuthorisedPersonNamePage) { authorisedPersonsName =>

        Future.successful(getView(OrganisationAuthorisedPersonPassportPage, form, authorisedPersonsName,
          controllers.nominees.routes.OrganisationAuthorisedPersonPassportController.onSubmit(mode)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OrganisationAuthorisedPersonNamePage) { authorisedPersonsName =>

        postView(mode, OrganisationAuthorisedPersonPassportPage, form, authorisedPersonsName, Section9Page,
          controllers.nominees.routes.OrganisationAuthorisedPersonPassportController.onSubmit(mode))
      }
  }

}
