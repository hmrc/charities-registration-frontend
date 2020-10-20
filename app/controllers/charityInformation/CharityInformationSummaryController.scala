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

package controllers.charityInformation

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import models.{NormalMode, UserAnswers}
import navigation.CharityInformationNavigator
import pages.{IndexPage, QuestionPage}
import pages.addressLookup.{CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.charityInformation._
import pages.sections.Section1Page
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import viewmodels.charityInformation.CharityInformationSummaryHelper
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CharityInformationSummaryController @Inject()(
   val sessionRepository: UserAnswerRepository,
   val navigator: CharityInformationNavigator,
   identify: AuthIdentifierAction,
   getData: UserDataRetrievalAction,
   requireData: DataRequiredAction,
   view: CheckYourAnswersView,
   val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val charityInformationAnswersHelper = new CharityInformationSummaryHelper(request.userAnswers)
    if (charityInformationAnswersHelper.rows.isEmpty) {
      Redirect(navigator.nextPage(IndexPage, NormalMode, request.userAnswers))
    } else {
      Ok(view(charityInformationAnswersHelper.rows, CharityInformationSummaryPage,
        controllers.charityInformation.routes.CharityInformationSummaryController.onSubmit()))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    for {
      updatedAnswers <- Future.fromTry(result = checkComplete(request.userAnswers) match {
        case inProgressOrComplete => request.userAnswers.set(Section1Page, inProgressOrComplete)
      })
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(CharityInformationSummaryPage, NormalMode, updatedAnswers))

  }

  def checkComplete(userAnswers: UserAnswers): Boolean = {
    val pagesAlwaysRequired: Seq[QuestionPage[_]] =
      Seq(CharityNamePage, CharityContactDetailsPage, CharityOfficialAddressLookupPage, CanWeSendToThisAddressPage)
    val charityPostalAddressIsDefined = userAnswers.arePagesDefined(Seq(CharityPostalAddressLookupPage))

    userAnswers.arePagesDefined(pagesAlwaysRequired) && userAnswers.get(CanWeSendToThisAddressPage).exists {
      case true => !charityPostalAddressIsDefined
      case false => charityPostalAddressIsDefined
    }

  }
}
