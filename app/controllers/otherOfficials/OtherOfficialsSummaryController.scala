/*
 * Copyright 2024 HM Revenue & Customs
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
import models.NormalMode
import navigation.OtherOfficialsNavigator
import pages.IndexPage
import pages.otherOfficials.{IsAddAnotherOtherOfficialPage, OtherOfficialsSummaryPage}
import pages.sections.Section8Page
import play.api.data.Form
import play.api.mvc._
import service.UserAnswerService
import viewmodels.officials.OfficialSummaryRowHelper
import viewmodels.otherOfficials.OtherOfficialStatusHelper.checkComplete
import views.html.common.OfficialsSummaryView

import javax.inject.Inject
import scala.concurrent.Future

class OtherOfficialsSummaryController @Inject() (
  val sessionRepository: UserAnswerService,
  val navigator: OtherOfficialsNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  val formProvider: YesNoFormProvider,
  view: OfficialsSummaryView,
  val controllerComponents: MessagesControllerComponents
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController
    with OfficialSummaryRowHelper {

  val messagePrefix: String = "otherOfficialsSummary"
  val form: Form[Boolean]   = formProvider(messagePrefix)

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    if ((firstOtherOfficialRow ++ secondOtherOfficialRow ++ thirdOtherOfficialRow) == Seq.empty) {
      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))
    } else {
      Ok(
        view(
          form,
          messagePrefix,
          maxOfficials = 3,
          controllers.otherOfficials.routes.OtherOfficialsSummaryController.onSubmit,
          firstOtherOfficialRow ++ secondOtherOfficialRow ++ thirdOtherOfficialRow
        )
      )
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    if (firstOtherOfficialRow.nonEmpty && secondOtherOfficialRow.nonEmpty && thirdOtherOfficialRow.isEmpty) {

      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(
              BadRequest(
                view(
                  formWithErrors,
                  messagePrefix,
                  maxOfficials = 3,
                  controllers.otherOfficials.routes.OtherOfficialsSummaryController.onSubmit,
                  firstOtherOfficialRow ++ secondOtherOfficialRow ++ thirdOtherOfficialRow
                )
              )
            ),
          value =>
            for {
              updatedAnswers  <- Future.fromTry(result = request.userAnswers.set(IsAddAnotherOtherOfficialPage, value))
              taskListUpdated <-
                Future.fromTry(result = updatedAnswers.set(Section8Page, checkComplete(updatedAnswers)))
              _               <- sessionRepository.set(taskListUpdated)
            } yield Redirect(navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, taskListUpdated))
        )
    } else {

      for {
        updatedAnswers <-
          Future.fromTry(result =
            request.userAnswers
              .set(Section8Page, checkComplete(request.userAnswers))
          )
        _              <- sessionRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(OtherOfficialsSummaryPage, NormalMode, updatedAnswers))
    }
  }
}
