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
import forms.common.YesNoFormProvider
import models.requests.DataRequest
import models.{Index, Mode, NormalMode}
import navigation.FundRaisingNavigator
import pages.QuestionPage
import pages.operationsAndFunds._
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import service.CountryService
import views.html.common.YesNoView

import javax.inject.Inject
import scala.concurrent.Future

class IsRemoveOperatingCountryController @Inject()(
    val identify: AuthIdentifierAction,
    val getData: UserDataRetrievalAction,
    val requireData: DataRequiredAction,
    val formProvider: YesNoFormProvider,
    val sessionRepository: UserAnswerRepository,
    val navigator: FundRaisingNavigator,
    val controllerComponents: MessagesControllerComponents,
    val countryService: CountryService,
    val view: YesNoView
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val messagePrefix: String = "isRemoveOperatingCountry"
  private val form: Form[Boolean] = formProvider(messagePrefix)

  def getCountryName(page: QuestionPage[String])(block: String => Future[Result])
                    (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(page).map {
      countryCode => block(countryService.find(countryCode).fold(countryCode)(_.name))
    }.getOrElse(Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad())))
  }

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      if (request.userAnswers.get(WhatCountryDoesTheCharityOperateInPage(index)).isEmpty) {
        Future.successful(Redirect(navigator.nextPage(IsRemoveOperatingCountryPage, NormalMode, request.userAnswers)))
      } else {
        getCountryName(WhatCountryDoesTheCharityOperateInPage(index)) { countryName =>
          Future.successful(Ok(view(form, countryName, messagePrefix,
            controllers.operationsAndFunds.routes.IsRemoveOperatingCountryController.onSubmit(mode, index),
            "operationsAndFunds", Seq(countryName))))
        }
      }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getCountryName(WhatCountryDoesTheCharityOperateInPage(index)) { countryName =>
        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, countryName, messagePrefix,
              controllers.operationsAndFunds.routes.IsRemoveOperatingCountryController.onSubmit(mode, index),
              "operationsAndFunds", Seq(countryName)))),
          removeOrNot =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.remove(
                                 if (removeOrNot) Seq(WhatCountryDoesTheCharityOperateInDeletePage(index), OverseasOperatingLocationSummaryPage) else Seq()
                                ).flatMap(_.set(IsRemoveOperatingCountryPage, removeOrNot)))
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(WhatCountryDoesTheCharityOperateInPage(index - 1), mode, updatedAnswers))
        )
      }
  }
}
