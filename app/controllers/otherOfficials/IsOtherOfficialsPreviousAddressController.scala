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

package controllers.otherOfficials

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.IsPreviousAddressController
import forms.common.YesNoFormProvider
import javax.inject.Inject
import models.{Index, Mode}
import navigation.OtherOfficialsNavigator
import pages.otherOfficials.{IsOtherOfficialsPreviousAddressPage, OtherOfficialsNamePage}
import pages.sections.Section8Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.IsPreviousAddressView

import scala.concurrent.Future

class IsOtherOfficialsPreviousAddressController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: YesNoFormProvider,
    override val sessionRepository: UserAnswerRepository,
    override val navigator: OtherOfficialsNavigator,
    override val controllerComponents: MessagesControllerComponents,
    override val view: IsPreviousAddressView
  )(implicit appConfig: FrontendAppConfig) extends IsPreviousAddressController {

  override val messagePrefix: String = "isOtherOfficialsPreviousAddress"
  private val form: Form[Boolean] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OtherOfficialsNamePage(index)) { officialsName =>

        Future.successful(getView(IsOtherOfficialsPreviousAddressPage(index), form, officialsName,
          controllers.otherOfficials.routes.IsOtherOfficialsPreviousAddressController.onSubmit(mode, index)))
      }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OtherOfficialsNamePage(index)) { officialsName =>

        postView(mode, IsOtherOfficialsPreviousAddressPage(index), form, officialsName, Section8Page,
          controllers.otherOfficials.routes.IsOtherOfficialsPreviousAddressController.onSubmit(mode, index))
      }
  }
}
