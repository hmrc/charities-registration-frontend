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

package controllers.otherOfficials

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.IsOfficialsNinoController
import forms.common.IsOfficialsNinoFormProvider
import javax.inject.Inject
import models.{Index, Mode}
import navigation.OtherOfficialsNavigator
import pages.otherOfficials.{OtherOfficialsNamePage, IsOtherOfficialNinoPage}
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.IsOfficialsNinoView

import scala.concurrent.Future

class IsOtherOfficialNinoController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: IsOfficialsNinoFormProvider,
    override val sessionRepository: UserAnswerRepository,
    override val navigator: OtherOfficialsNavigator,
    override val controllerComponents: MessagesControllerComponents,
    override val view: IsOfficialsNinoView
  )(implicit appConfig: FrontendAppConfig) extends IsOfficialsNinoController {

  override val messagePrefix: String = "isOtherOfficialNino"
  private val form: Form[Boolean] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OtherOfficialsNamePage(index)) { otherOfficialsName =>

        Future.successful(getView(IsOtherOfficialNinoPage(index), form, otherOfficialsName,
          controllers.otherOfficials.routes.IsOtherOfficialNinoController.onSubmit(mode, index)))
      }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getFullName(OtherOfficialsNamePage(index)) { otherOfficialsName =>

        postView(mode, IsOtherOfficialNinoPage(index), form, otherOfficialsName, Section7Page,
          controllers.otherOfficials.routes.IsOtherOfficialNinoController.onSubmit(mode, index))
      }
  }
}