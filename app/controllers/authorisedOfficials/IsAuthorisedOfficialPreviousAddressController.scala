/*
 * Copyright 2022 HM Revenue & Customs
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
import controllers.common.IsPreviousAddressController
import forms.common.YesNoFormProvider
import javax.inject.Inject
import models.{Index, Mode}
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, IsAuthorisedOfficialPreviousAddressPage}
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.common.IsPreviousAddressView

import scala.concurrent.Future

class IsAuthorisedOfficialPreviousAddressController @Inject() (
  val identify: AuthIdentifierAction,
  val getData: UserDataRetrievalAction,
  val requireData: DataRequiredAction,
  val formProvider: YesNoFormProvider,
  override val sessionRepository: UserAnswerService,
  override val navigator: AuthorisedOfficialsNavigator,
  override val controllerComponents: MessagesControllerComponents,
  override val view: IsPreviousAddressView
)(implicit appConfig: FrontendAppConfig)
    extends IsPreviousAddressController {

  override val messagePrefix: String = "isAuthorisedOfficialPreviousAddress"
  private val form: Form[Boolean]    = formProvider(messagePrefix)

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>
        Future.successful(
          getView(
            IsAuthorisedOfficialPreviousAddressPage(index),
            form,
            authorisedOfficialsName,
            controllers.authorisedOfficials.routes.IsAuthorisedOfficialPreviousAddressController.onSubmit(mode, index)
          )
        )
      }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>
        postView(
          mode,
          IsAuthorisedOfficialPreviousAddressPage(index),
          form,
          authorisedOfficialsName,
          Section7Page,
          controllers.authorisedOfficials.routes.IsAuthorisedOfficialPreviousAddressController.onSubmit(mode, index)
        )
      }
  }
}
