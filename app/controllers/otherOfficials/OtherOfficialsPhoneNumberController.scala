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
import controllers.common.PhoneNumberController
import forms.common.PhoneNumberFormProvider
import javax.inject.Inject
import models.{Index, Mode, PhoneNumber}
import navigation.OtherOfficialsNavigator
import pages.otherOfficials.{OtherOfficialsNamePage, OtherOfficialsPhoneNumberPage}
import pages.sections.Section8Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.PhoneNumberView

import scala.concurrent.Future

class OtherOfficialsPhoneNumberController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: PhoneNumberFormProvider,
    override val sessionRepository: UserAnswerRepository,
    override val navigator: OtherOfficialsNavigator,
    override val controllerComponents: MessagesControllerComponents,
    override val view: PhoneNumberView
  )(implicit appConfig: FrontendAppConfig) extends PhoneNumberController {

  override val messagePrefix: String = "otherOfficialsPhoneNumber"
  private val form: Form[PhoneNumber] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(OtherOfficialsNamePage(index)) { otherOfficialsName =>

      Future.successful(getView(OtherOfficialsPhoneNumberPage(index), form, otherOfficialsName,
        controllers.otherOfficials.routes.OtherOfficialsPhoneNumberController.onSubmit(mode, index)))
    }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(OtherOfficialsNamePage(index)) { otherOfficialsName =>

      postView(mode, OtherOfficialsPhoneNumberPage(index), form, otherOfficialsName, Section8Page,
        controllers.otherOfficials.routes.OtherOfficialsPhoneNumberController.onSubmit(mode, index))
    }
  }
}
