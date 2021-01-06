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

package controllers.otherOfficials

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.common.YesNoFormProvider
import javax.inject.Inject
import models.{Index, NormalMode}
import navigation.OtherOfficialsNavigator
import pages.otherOfficials.{IsAddAnotherOtherOfficialPage, OtherOfficialsId, OtherOfficialsNamePage, RemoveOtherOfficialsPage}
import pages.sections.Section8Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.otherOfficials.OtherOfficialStatusHelper.checkComplete
import views.html.common.YesNoView

import scala.concurrent.Future

class RemoveOtherOfficialsController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: YesNoFormProvider,
    val sessionRepository: UserAnswerRepository,
    val navigator: OtherOfficialsNavigator,
    val controllerComponents: MessagesControllerComponents,
    val view: YesNoView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val messagePrefix: String = "removeOtherOfficial"
  private val form: Form[Boolean] = formProvider(messagePrefix)

  def onPageLoad(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      if (request.userAnswers.get(OtherOfficialsId(index)).isEmpty) {
        Future.successful(Redirect(navigator.nextPage(RemoveOtherOfficialsPage, NormalMode, request.userAnswers)))
      } else {
        getFullName(OtherOfficialsNamePage(index)) { otherOfficialsName =>
          Future.successful(Ok(view(form, otherOfficialsName, messagePrefix,
            controllers.otherOfficials.routes.RemoveOtherOfficialsController.onSubmit(index),
            "officialsAndNominees")))
        }
      }
  }

  def onSubmit(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getFullName(OtherOfficialsNamePage(index)) { otherOfficialsName =>
        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, otherOfficialsName, messagePrefix,
              controllers.otherOfficials.routes.RemoveOtherOfficialsController.onSubmit(index),
              "officialsAndNominees"))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.remove(
                                 if(value) Seq(OtherOfficialsId(index), IsAddAnotherOtherOfficialPage) else Seq()
                                ))
              taskListUpdated <- Future.fromTry(result = updatedAnswers.set(Section8Page, checkComplete(updatedAnswers)))
              _ <- sessionRepository.set(taskListUpdated)
            } yield Redirect(navigator.nextPage(RemoveOtherOfficialsPage, NormalMode, updatedAnswers))
        )
      }
  }
}
