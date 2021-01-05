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
import forms.operationsAndFunds.WhatCountryDoesTheCharityOperateInFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.FundRaisingNavigator
import pages.operationsAndFunds.WhatCountryDoesTheCharityOperateInPage
import pages.sections.Section5Page
import play.api.data.Form
import play.api.mvc._
import repositories.UserAnswerRepository
import service.CountryService
import views.html.operationsAndFunds.WhatCountryDoesTheCharityOperateInView

import javax.inject.Inject
import scala.concurrent.Future

class WhatCountryDoesTheCharityOperateInController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: FundRaisingNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: WhatCountryDoesTheCharityOperateInFormProvider,
    view: WhatCountryDoesTheCharityOperateInView,
    val countryService: CountryService,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  private val form: Form[String] = formProvider()

  private def getCountries(implicit request: DataRequest[_]): Option[String] = {

    val result = for(i <- 0 to 4) yield request.userAnswers.get(WhatCountryDoesTheCharityOperateInPage(i))
    val countryList = result.filter(_.nonEmpty).flatten.map(code => countryService.find(code).fold(code)(_.name))
    if(countryList.nonEmpty) {
      if(countryService.isWelsh){
        Some(countryList.mkString(", "))
      }
      else {
        Some(countryList.mkString(", ")
        .replaceFirst(",(?=[^,]+$)", s" ${messagesApi.preferred(request).apply("service.separator.and")}"))
      }
    } else {
      None
    }
  }

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(WhatCountryDoesTheCharityOperateInPage(index)) match {
      case None => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode,index, countryService.countries().filter(country => country._1 != "GB"), getCountries))
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(view(formWithErrors, mode,index, countryService.countries()
          .filter(country => country._1 != "GB"), getCountries)))},

      value =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatCountryDoesTheCharityOperateInPage(index), value).flatMap(_.set(Section5Page, false)))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(WhatCountryDoesTheCharityOperateInPage(index), mode, updatedAnswers))
    )
  }

}
