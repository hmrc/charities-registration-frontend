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

package controllers.nominees

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.common.BankDetailsFormProvider
import javax.inject.Inject
import models.{BankDetails, Mode}
import navigation.NomineesNavigator
import pages.nominees.{IndividualNomineeNamePage, IndividualNomineesBankDetailsPage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import views.html.common.BankAccountDetailsView

import scala.concurrent.Future

class IndividualNomineesBankDetailsController @Inject()(
    val sessionRepository: UserAnswerService,
    val navigator: NomineesNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: BankDetailsFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: BankAccountDetailsView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val messagePrefix: String = "individualNomineesBankDetails"
  private val sectionName: String = "officialsAndNominees.section"
  private val form: Form[BankDetails] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(IndividualNomineesBankDetailsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      getFullName(IndividualNomineeNamePage) { individualNomineeName =>

        Future.successful(Ok(view(preparedForm,
          controllers.nominees.routes.IndividualNomineesBankDetailsController.onSubmit(mode),
          messagePrefix, sectionName, Some(individualNomineeName))))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          getFullName(IndividualNomineeNamePage) { individualNomineeName =>
            Future.successful(BadRequest(view(formWithErrors,
              controllers.nominees.routes.IndividualNomineesBankDetailsController.onSubmit(mode),
              messagePrefix, sectionName, Some(individualNomineeName))))
          },

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualNomineesBankDetailsPage, value).flatMap(_.set(Section9Page, false)))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IndividualNomineesBankDetailsPage, mode, updatedAnswers))
      )
  }
}
