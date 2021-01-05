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

package controllers.authorisedOfficials

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.common.YesNoFormProvider
import javax.inject.Inject
import models.{Index, NormalMode}
import navigation.AuthorisedOfficialsNavigator
import pages.authorisedOfficials.{AuthorisedOfficialsId, AuthorisedOfficialsNamePage, IsAddAnotherAuthorisedOfficialPage, RemoveAuthorisedOfficialsPage}
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.otherOfficials.OtherOfficialStatusHelper.checkComplete
import views.html.common.YesNoView

import scala.concurrent.Future

class RemoveAuthorisedOfficialsController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: YesNoFormProvider,
    val sessionRepository: UserAnswerRepository,
    val navigator: AuthorisedOfficialsNavigator,
    val controllerComponents: MessagesControllerComponents,
    val view: YesNoView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val messagePrefix: String = "removeAuthorisedOfficial"
  private val form: Form[Boolean] = formProvider(messagePrefix)

  def onPageLoad(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      if (request.userAnswers.get(AuthorisedOfficialsId(index)).isEmpty) {
        Future.successful(Redirect(navigator.nextPage(RemoveAuthorisedOfficialsPage, NormalMode, request.userAnswers)))
      } else {
        getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>
          Future.successful(Ok(view(form, authorisedOfficialsName, messagePrefix,
            controllers.authorisedOfficials.routes.RemoveAuthorisedOfficialsController.onSubmit(index),
            "officialsAndNominees")))
        }
      }
  }

  def onSubmit(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getFullName(AuthorisedOfficialsNamePage(index)) { authorisedOfficialsName =>
        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, authorisedOfficialsName, messagePrefix,
              controllers.authorisedOfficials.routes.RemoveAuthorisedOfficialsController.onSubmit(index),
              "officialsAndNominees"))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.remove(
                                 if(value) Seq(AuthorisedOfficialsId(index), IsAddAnotherAuthorisedOfficialPage) else Seq()
                                ))
              taskListUpdated <- Future.fromTry(result = updatedAnswers.set(Section7Page, checkComplete(updatedAnswers)))
              _ <- sessionRepository.set(taskListUpdated)
            } yield Redirect(navigator.nextPage(RemoveAuthorisedOfficialsPage, NormalMode, updatedAnswers))
        )
      }
  }
}
