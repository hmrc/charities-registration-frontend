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

package controllers.operationsAndFunds

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.operationsAndFunds.PublicBenefitsFormProvider
import javax.inject.Inject
import models.Mode
import navigation.ObjectivesNavigator
import pages.operationsAndFunds.PublicBenefitsPage
import pages.sections.Section4Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import views.html.operationsAndFunds.PublicBenefitsView

import scala.concurrent.Future

class PublicBenefitsController @Inject()(
  val sessionRepository: UserAnswerRepository,
  val navigator: ObjectivesNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PublicBenefitsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PublicBenefitsView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  val form: Form[String] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(PublicBenefitsPage) match {
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
          updatedAnswers <- Future.fromTry(request.userAnswers.set(PublicBenefitsPage, value).flatMap(_.set(Section4Page, false)))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(PublicBenefitsPage, mode, updatedAnswers))
    )
  }
}
