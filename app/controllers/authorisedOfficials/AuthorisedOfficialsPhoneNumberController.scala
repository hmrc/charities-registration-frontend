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
import controllers.common.PhoneNumberController
import forms.common.PhoneNumberFormProvider
import javax.inject.Inject
import models.{Index, Mode, PhoneNumber}
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, AuthorisedOfficialsPhoneNumberPage}
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.PhoneNumberView

import scala.concurrent.Future

class AuthorisedOfficialsPhoneNumberController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: PhoneNumberFormProvider,
    override val sessionRepository: UserAnswerRepository,
    override val navigator: AuthorisedOfficialsNavigator,
    override val controllerComponents: MessagesControllerComponents,
    override val view: PhoneNumberView
  )(implicit appConfig: FrontendAppConfig) extends PhoneNumberController {

  override val messagePrefix: String = "authorisedOfficialsPhoneNumber"
  private val form: Form[PhoneNumber] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>

      Future.successful(getView(AuthorisedOfficialsPhoneNumberPage(index), form, authorisedOfficialsName,
        controllers.authorisedOfficials.routes.AuthorisedOfficialsPhoneNumberController.onSubmit(mode, index)))
    }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>

      postView(mode, AuthorisedOfficialsPhoneNumberPage(index), form, authorisedOfficialsName, Section7Page,
        controllers.authorisedOfficials.routes.AuthorisedOfficialsPhoneNumberController.onSubmit(mode, index))
    }
  }
}
