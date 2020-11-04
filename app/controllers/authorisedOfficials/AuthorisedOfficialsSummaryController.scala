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
import forms.common.YesNoFormProvider
import javax.inject.Inject
import models.NormalMode
import models.requests.DataRequest
import navigation.AuthorisedOfficialsNavigator
import pages.IndexPage
import pages.authorisedOfficials.{AuthorisedOfficialsNamePage, AuthorisedOfficialsSummaryPage, IsAddAnotherAuthorisedOfficialPage}
import pages.sections.Section7Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.OfficialSummaryListRow
import viewmodels.authorisedOfficials.AuthorisedOfficialsStatusHelper.checkComplete
import viewmodels.officials.OfficialSummaryRowHelper
import views.html.common.OfficialsSummaryViewNewTODO

import scala.concurrent.Future

class AuthorisedOfficialsSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: AuthorisedOfficialsNavigator,
    val formProvider: YesNoFormProvider,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    view: OfficialsSummaryViewNewTODO,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController with OfficialSummaryRowHelper {

  private val messagePrefix: String = "authorisedOfficialsSummary"
  private val form: Form[Boolean] = formProvider(messagePrefix)


  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(IsAddAnotherAuthorisedOfficialPage) match {
      case None => form
      case Some(value) => form.fill(value)
    }

    if ((firstAuthorisedOfficialRow ++ secondAuthorisedOfficialRow) == Seq.empty) {
      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))
    } else {
      Ok(view(preparedForm, messagePrefix, maxOfficials = 2,
        controllers.authorisedOfficials.routes.AuthorisedOfficialsSummaryController.onSubmit(),
        firstAuthorisedOfficialRow ++ secondAuthorisedOfficialRow))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    if (secondAuthorisedOfficialRow.isEmpty) {

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              view(formWithErrors, messagePrefix, maxOfficials = 2,
                controllers.authorisedOfficials.routes.AuthorisedOfficialsSummaryController.onSubmit(),
                firstAuthorisedOfficialRow ++ secondAuthorisedOfficialRow)
            )
          ),

        value =>
          for {
            updatedAnswers  <- Future.fromTry(result = request.userAnswers
                                .set(IsAddAnotherAuthorisedOfficialPage, value))
            taskListUpdated <- Future.fromTry(result = updatedAnswers
                                .set(Section7Page, checkComplete(updatedAnswers)))
            _               <- sessionRepository.set(taskListUpdated)
          } yield Redirect(navigator.nextPage(AuthorisedOfficialsSummaryPage, NormalMode, taskListUpdated))
      )
    } else {

      for {
        updatedAnswers      <- Future.fromTry(result = request.userAnswers
                                .set(Section7Page, checkComplete(request.userAnswers)))
        _                   <- sessionRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(AuthorisedOfficialsSummaryPage, NormalMode, updatedAnswers))
    }

  }
}
