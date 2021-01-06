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

package controllers.checkEligibility

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.checkEligibility.IsEligiblePurposeFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.EligibilityNavigator
import pages.checkEligibility.IsEligiblePurposePage
import play.api.mvc._
import repositories.SessionRepositoryImpl
import views.html.checkEligibility.IsEligiblePurposeView

import scala.concurrent.Future

class IsEligiblePurposeController @Inject()(
   val sessionRepository: SessionRepositoryImpl,
   val navigator: EligibilityNavigator,
   identify: SessionIdentifierAction,
   getData: DataRetrievalAction,
   formProvider: IsEligiblePurposeFormProvider,
   val controllerComponents: MessagesControllerComponents,
   view: IsEligiblePurposeView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {
  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val preparedForm = request.userAnswers.flatMap(_.get(IsEligiblePurposePage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Future.successful(Ok(view(preparedForm, mode)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(UserAnswers(request.internalId).set(IsEligiblePurposePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IsEligiblePurposePage, mode, updatedAnswers))
      )
  }
}
