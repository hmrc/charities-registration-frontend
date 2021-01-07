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
import controllers.common.PhoneNumberController
import forms.common.PhoneNumberFormProvider
import javax.inject.Inject
import models.{Mode, PhoneNumber}
import navigation.NomineesNavigator
import pages.nominees.{IndividualNomineeNamePage, IndividualNomineesPhoneNumberPage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.PhoneNumberView

import scala.concurrent.Future

class IndividualNomineesPhoneNumberController @Inject()(
   val identify: AuthIdentifierAction,
   val getData: UserDataRetrievalAction,
   val requireData: DataRequiredAction,
   val formProvider: PhoneNumberFormProvider,
   override val sessionRepository: UserAnswerRepository,
   override val navigator: NomineesNavigator,
   override val controllerComponents: MessagesControllerComponents,
   override val view: PhoneNumberView
  )(implicit appConfig: FrontendAppConfig) extends PhoneNumberController {

  override val messagePrefix: String = "individualNomineesPhoneNumber"
  private val form: Form[PhoneNumber] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(IndividualNomineeNamePage) { individualNomineeName =>

      Future.successful(getView(IndividualNomineesPhoneNumberPage, form, individualNomineeName,
        controllers.nominees.routes.IndividualNomineesPhoneNumberController.onSubmit(mode)))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getFullName(IndividualNomineeNamePage) { individualNomineeName =>

      postView(mode, IndividualNomineesPhoneNumberPage, form, individualNomineeName, Section9Page,
        controllers.nominees.routes.IndividualNomineesPhoneNumberController.onSubmit(mode))
    }
  }
}
