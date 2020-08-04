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

package controllers.otherOfficials

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.common.IsAddAnotherFormProvider
import javax.inject.Inject
import models.{Index, Mode}
import navigation.OtherOfficialsNavigator
import pages.otherOfficials.{AddAnotherOtherOfficialPage, OtherOfficialsNamePage}
import pages.sections.Section8Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.otherOfficials.AddAnotherOtherOfficialView

import scala.concurrent.Future

class AddAnotherOtherOfficialController @Inject()(
    sessionRepository: UserAnswerRepository,
    navigator: OtherOfficialsNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: IsAddAnotherFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: AddAnotherOtherOfficialView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

   val messagePrefix: String = "addAnotherOtherOfficial"
   val form: Form[Boolean] = formProvider(messagePrefix)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddAnotherOtherOfficialPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      getFullName(OtherOfficialsNamePage(Index(0))) {
        firstOfficialsName =>
          getFullName(OtherOfficialsNamePage(Index(1))) {
            secondOfficialsName=>
          Future.successful(Ok(view(preparedForm, mode, firstOfficialsName, secondOfficialsName)))
      }
  }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          getFullName(OtherOfficialsNamePage(Index(0))) {
            firstOfficialsName =>
              getFullName(OtherOfficialsNamePage(Index(1))) {
                secondOfficialsName=>
              Future.successful(BadRequest(view(formWithErrors, mode, firstOfficialsName, secondOfficialsName)))
          }},

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherOtherOfficialPage, value).flatMap(_.set(Section8Page, false)))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddAnotherOtherOfficialPage, mode, updatedAnswers))
      )
  }

}
