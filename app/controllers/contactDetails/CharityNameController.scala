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

package controllers.contactDetails

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.contactDetails.CharityNameFormProvider
import javax.inject.Inject
import models.{CharityName, Mode}
import navigation.CharityInformationNavigator
import pages.contactDetails.CharityNamePage
import pages.operationsAndFunds.BankDetailsPage
import pages.sections.Section1Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.charityInformation.CharityInformationStatusHelper.checkComplete
import views.html.contactDetails.CharityNameView

import scala.concurrent.Future

class CharityNameController @Inject()(
   val sessionRepository: UserAnswerRepository,
   val navigator: CharityInformationNavigator,
   identify: AuthIdentifierAction,
   getData: UserDataRetrievalAction,
   requireData: DataRequiredAction,
   formProvider: CharityNameFormProvider,
   val controllerComponents: MessagesControllerComponents,
   view: CharityNameView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  val form: Form[CharityName] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(CharityNamePage) match {
      case None => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      formWithErrors =>
        Future.successful(BadRequest(view(formWithErrors, mode))),

      value =>
        for {
          updatedBankDetails <- request.userAnswers.get(BankDetailsPage).fold(Future.successful(request.userAnswers))(bakDetails =>
                                Future.fromTry(request.userAnswers.set(BankDetailsPage, bakDetails.copy(accountName = value.fullName))))
          updatedAnswers <- Future.fromTry(updatedBankDetails.set(CharityNamePage, value).flatMap(_.set(Section1Page, checkComplete(updatedBankDetails))))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(CharityNamePage, mode, updatedAnswers))
    )
  }
}
