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

package controllers.nominees

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.nominees.IsIndividualNomineePaymentsFormProvider
import javax.inject.Inject
import models.Mode
import navigation.NomineesNavigator
import pages.nominees.{IndividualNomineeNamePage, IsIndividualNomineePaymentsPage}
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.nominees.IsIndividualNomineePaymentsView

import scala.concurrent.Future


class IsIndividualNomineePaymentsController @Inject()(
     val identify: AuthIdentifierAction,
     val getData: UserDataRetrievalAction,
     val requireData: DataRequiredAction,
     val formProvider: IsIndividualNomineePaymentsFormProvider,
     val sessionRepository: UserAnswerRepository,
     val navigator: NomineesNavigator,
     override val controllerComponents: MessagesControllerComponents,
     val view: IsIndividualNomineePaymentsView
     )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val form = formProvider()

        val preparedForm = request.userAnswers.get(IsIndividualNomineePaymentsPage) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        getFullName(IndividualNomineeNamePage) { individualNomineeName =>

            Future.successful(Ok(view(preparedForm, mode, individualNomineeName)))
        }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          getFullName(IndividualNomineeNamePage) { individualNomineeName =>
              Future.successful(BadRequest(view(formWithErrors, mode, individualNomineeName)))
          },

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IsIndividualNomineePaymentsPage, value).flatMap(_.set(Section9Page, false)))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IsIndividualNomineePaymentsPage, mode, updatedAnswers))
      )
  }
}
