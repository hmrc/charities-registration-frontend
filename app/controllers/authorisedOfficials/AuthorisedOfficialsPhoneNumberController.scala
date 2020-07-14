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
import forms.authorisedOfficials.AuthorisedOfficialsPhoneNumberFormProvider
import javax.inject.Inject
import models.AuthorisedOfficialsPhoneNumber
import models.{Index, Mode}
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.AuthorisedOfficialsPhoneNumberPage
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.authorisedOfficials.AuthorisedOfficialsPhoneNumberView

import scala.concurrent.Future

class AuthorisedOfficialsPhoneNumberController @Inject()(
                                                  val sessionRepository: UserAnswerRepository,
                                                  val navigator: AuthorisedOfficialsNavigator,
                                                  identify: AuthIdentifierAction,
                                                  getData: UserDataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  formProvider: AuthorisedOfficialsPhoneNumberFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: AuthorisedOfficialsPhoneNumberView
                                                )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  val form: Form[AuthorisedOfficialsPhoneNumber] = formProvider()

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    getAuthorisedOfficialName(index) {
      val preparedForm = request.userAnswers.get(AuthorisedOfficialsPhoneNumberPage(index)) match {
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
          updatedAnswers <- Future.fromTry(request.userAnswers.set(AuthorisedOfficialsPhoneNumberPage(index), value).flatMap(_.set(Section7Page, false)))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(AuthorisedOfficialsPhoneNumberPage(index), mode, updatedAnswers))
    )
  }
}
