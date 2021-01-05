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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions.{AuthIdentifierAction, DataRequiredAction, UserDataRetrievalAction}
import forms.operationsAndFunds.OverseasOperatingLocationSummaryFormProvider
import models.{Index, Mode}
import navigation.FundRaisingNavigator
import pages.operationsAndFunds.{OverseasOperatingLocationSummaryPage, WhatCountryDoesTheCharityOperateInPage}
import pages.sections.Section5Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import service.CountryService
import viewmodels.operationsAndFunds.OverseasOperatingLocationSummaryHelper
import views.html.operationsAndFunds.OverseasOperatingLocationSummaryView

import scala.concurrent.Future

class OverseasOperatingLocationSummaryController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: FundRaisingNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    countryService: CountryService,
    formProvider: OverseasOperatingLocationSummaryFormProvider,
    view: OverseasOperatingLocationSummaryView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val summaryHelper = new OverseasOperatingLocationSummaryHelper(request.userAnswers, countryService, mode)

    request.userAnswers.get(WhatCountryDoesTheCharityOperateInPage(0)) match {
      case Some(_) => Ok(view(form, mode, summaryHelper.rows))
      case None => Redirect(navigator.nextPage(OverseasOperatingLocationSummaryPage, mode, request.userAnswers))
    }

  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val summaryHelper = new OverseasOperatingLocationSummaryHelper(request.userAnswers, countryService, mode)

    if(summaryHelper.rows.length < 5) {

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, summaryHelper.rows))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(result = request.userAnswers.set(
              OverseasOperatingLocationSummaryPage, value).flatMap(_.set(Section5Page, false)))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(OverseasOperatingLocationSummaryPage, mode, updatedAnswers))
      )
    } else {
      for {
        updatedAnswers <- Future.fromTry(result = request.userAnswers.set(
          OverseasOperatingLocationSummaryPage, true))
        _ <- sessionRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(OverseasOperatingLocationSummaryPage, mode, updatedAnswers))
    }
  }

}
