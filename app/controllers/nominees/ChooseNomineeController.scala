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
import forms.nominees.ChooseNomineeFormProvider
import javax.inject.Inject
import models.Mode
import navigation.NomineesNavigator
import pages.nominees.ChooseNomineePage
import pages.sections.Section9Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.nominees.ChooseNomineeView

import scala.concurrent.Future


class ChooseNomineeController @Inject()(
     sessionRepository: UserAnswerRepository,
     navigator: NomineesNavigator,
     identify: AuthIdentifierAction,
     getData: UserDataRetrievalAction,
     requireData: DataRequiredAction,
     formProvider: ChooseNomineeFormProvider,
     val controllerComponents: MessagesControllerComponents,
     view: ChooseNomineeView
   )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ChooseNomineePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ChooseNomineePage, value).flatMap(_.set(Section9Page, false)))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ChooseNomineePage, mode, updatedAnswers))
      )
  }
}
