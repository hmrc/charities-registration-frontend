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
import controllers.common.IsPreviousAddressController
import forms.common.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.NomineesNavigator
import pages.nominees.{IsOrganisationNomineePreviousAddressPage, OrganisationNomineeNamePage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.IsPreviousAddressView

import scala.concurrent.Future

class IsOrganisationNomineePreviousAddressController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: YesNoFormProvider,
    override val sessionRepository: UserAnswerRepository,
    override val navigator: NomineesNavigator,
    override val controllerComponents: MessagesControllerComponents,
    override val view: IsPreviousAddressView
  )(implicit appConfig: FrontendAppConfig) extends IsPreviousAddressController {

  override val messagePrefix: String = "isOrganisationNomineePreviousAddress"
  private val form: Form[Boolean] = formProvider(messagePrefix)


  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getOrganisationName(OrganisationNomineeNamePage) { organisationNomineeName =>

        Future.successful(getView(IsOrganisationNomineePreviousAddressPage, form, organisationNomineeName,
          controllers.nominees.routes.IsOrganisationNomineePreviousAddressController.onSubmit(mode)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getOrganisationName(OrganisationNomineeNamePage) { organisationNomineeName =>

        postView(mode, IsOrganisationNomineePreviousAddressPage, form, organisationNomineeName, Section9Page,
          controllers.nominees.routes.IsOrganisationNomineePreviousAddressController.onSubmit(mode))
      }
  }
}
