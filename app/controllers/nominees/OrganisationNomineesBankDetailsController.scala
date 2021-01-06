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
import controllers.LocalBaseController
import controllers.actions._
import forms.common.BankDetailsFormProvider
import javax.inject.Inject
import models.{BankDetails, Mode}
import navigation.NomineesNavigator
import pages.nominees.{OrganisationNomineeNamePage, OrganisationNomineesBankDetailsPage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.common.BankAccountDetailsView

import scala.concurrent.Future

class OrganisationNomineesBankDetailsController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: NomineesNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: BankDetailsFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: BankAccountDetailsView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val messagePrefix: String = "organisationNomineesBankDetails"
  private val sectionName: String = "officialsAndNominees.section"
  private val form: Form[BankDetails] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(OrganisationNomineesBankDetailsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      getOrganisationName(OrganisationNomineeNamePage) { organisationNomineeName =>

        Future.successful(Ok(view(preparedForm,
          controllers.nominees.routes.OrganisationNomineesBankDetailsController.onSubmit(mode),
          messagePrefix, sectionName, Some(organisationNomineeName))))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          getOrganisationName(OrganisationNomineeNamePage) { organisationNomineeName =>
            Future.successful(BadRequest(view(formWithErrors,
              controllers.nominees.routes.OrganisationNomineesBankDetailsController.onSubmit(mode),
              messagePrefix, sectionName, Some(organisationNomineeName))))
          },

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(OrganisationNomineesBankDetailsPage, value).flatMap(_.set(Section9Page, false)))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(OrganisationNomineesBankDetailsPage, mode, updatedAnswers))
      )
  }
}
