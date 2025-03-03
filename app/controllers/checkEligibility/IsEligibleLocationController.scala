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

package controllers.checkEligibility

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.checkEligibility.IsEligibleLocationFormProvider
import models.Mode
import navigation.EligibilityNavigator
import pages.checkEligibility.IsEligibleLocationPage
import play.api.data.Form
import play.api.mvc._
import repositories.SessionRepository
import views.html.checkEligibility.IsEligibleLocationView

import javax.inject.Inject
import scala.concurrent.Future

class IsEligibleLocationController @Inject() (
  val sessionRepository: SessionRepository,
  val navigator: EligibilityNavigator,
  identify: SessionIdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: IsEligibleLocationFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsEligibleLocationView
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {
  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(IsEligibleLocationPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(IsEligibleLocationPage, value))
              _              <- sessionRepository.upsert(updatedAnswers)
            } yield Redirect(navigator.nextPage(IsEligibleLocationPage, mode, updatedAnswers))
        )
  }
}
