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

package controllers.authorisedOfficials

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.authorisedOfficials.AuthorisedOfficialsPositionFormProvider
import javax.inject.Inject
import models.AuthOfficials.AuthorisedOfficialsPosition
import models.{Index, Mode}
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.AuthorisedOfficialsPositionPage
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.authorisedOfficials.AuthorisedOfficialsPositionView

import scala.concurrent.Future

class AuthorisedOfficialsPositionController @Inject()(
    sessionRepository: UserAnswerRepository,
    navigator: AuthorisedOfficialsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: AuthorisedOfficialsPositionFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: AuthorisedOfficialsPositionView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  val form: Form[AuthorisedOfficialsPosition] = formProvider()

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getAuthorisedOfficialName(index) {
      val preparedForm = request.userAnswers.get(AuthorisedOfficialsPositionPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      authorisedOfficialsName =>
        Future.successful(Ok(view(preparedForm, mode, index, authorisedOfficialsName)))
    }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      formWithErrors =>
        getAuthorisedOfficialName(index) {
          authorisedOfficialsName =>
            Future.successful(BadRequest(view(formWithErrors, mode, index, authorisedOfficialsName)))
        },

      value =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(AuthorisedOfficialsPositionPage(index), value).flatMap(_.set(Section7Page, false)))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(AuthorisedOfficialsPositionPage(index), mode, updatedAnswers))
    )
  }
}
