/*
 * Copyright 2025 HM Revenue & Customs
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
import controllers.common.DateOfBirthController
import forms.common.DateOfBirthFormProvider
import models.Mode
import navigation.NomineesNavigator
import pages.nominees.{IndividualNomineeDOBPage, IndividualNomineeNamePage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.common.DateOfBirthView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future

class IndividualNomineeDOBController @Inject() (
  val identify: AuthIdentifierAction,
  val getData: UserDataRetrievalAction,
  val requireData: DataRequiredAction,
  val formProvider: DateOfBirthFormProvider,
  override val sessionRepository: UserAnswerService,
  override val navigator: NomineesNavigator,
  override val controllerComponents: MessagesControllerComponents,
  override val view: DateOfBirthView
)(implicit appConfig: FrontendAppConfig)
    extends DateOfBirthController {

  override val messagePrefix: String = "individualNomineeDOB"

  private val form: Form[LocalDate] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getFullName(IndividualNomineeNamePage) { individualNomineeName =>
        Future.successful(
          getView(
            IndividualNomineeDOBPage,
            form,
            individualNomineeName,
            controllers.nominees.routes.IndividualNomineeDOBController.onSubmit(mode)
          )
        )
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getFullName(IndividualNomineeNamePage) { individualNomineeName =>
        postView(
          mode,
          IndividualNomineeDOBPage,
          form,
          individualNomineeName,
          Section9Page,
          controllers.nominees.routes.IndividualNomineeDOBController.onSubmit(mode)
        )
      }
  }
}
