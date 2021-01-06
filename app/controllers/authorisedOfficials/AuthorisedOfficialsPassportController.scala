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

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.PassportController
import forms.common.PassportFormProvider
import javax.inject.Inject
import models.{Index, Mode, Passport}
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, AuthorisedOfficialsPassportPage}
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import service.CountryService
import views.html.common.PassportView

import scala.concurrent.Future

class AuthorisedOfficialsPassportController @Inject()(
  val identify: AuthIdentifierAction,
  val getData: UserDataRetrievalAction,
  val requireData: DataRequiredAction,
  val formProvider: PassportFormProvider,
  override val countryService: CountryService,
  override val sessionRepository: UserAnswerRepository,
  override val navigator: AuthorisedOfficialsNavigator,
  override val controllerComponents: MessagesControllerComponents,
  override val view: PassportView
  )(implicit appConfig: FrontendAppConfig) extends PassportController {

  override val messagePrefix: String = "authorisedOfficialsPassport"
  private val form: Form[Passport] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>

        Future.successful(getView(AuthorisedOfficialsPassportPage(index), form, authorisedOfficialsName,
          controllers.authorisedOfficials.routes.AuthorisedOfficialsPassportController.onSubmit(mode, index)))
      }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>

        postView(mode, AuthorisedOfficialsPassportPage(index), form, authorisedOfficialsName, Section7Page,
          controllers.authorisedOfficials.routes.AuthorisedOfficialsPassportController.onSubmit(mode, index))
      }
  }

}
