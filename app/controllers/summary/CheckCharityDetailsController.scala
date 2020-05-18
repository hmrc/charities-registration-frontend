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

package controllers.summary

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import models.NormalMode
import navigation.CharityInformationNavigator
import pages.charityInformation.{CharityContactDetailsPage, CharityNamePage}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.UserAnswerRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.contact.{CharityContactDetailsCheckYourAnswersHelper, CharityNameCheckYourAnswersHelper}
import views.html.summary.CheckYourAnswersView

class CheckCharityDetailsController @Inject()(
    val sessionRepository: UserAnswerRepository,
    val navigator: CharityInformationNavigator,
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    requireData: DataRequiredAction,
    charityName: CharityNameCheckYourAnswersHelper,
    charityContactDetails: CharityContactDetailsCheckYourAnswersHelper,
    view: CheckYourAnswersView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends LocalBaseController{

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val charityNameRow = request.userAnswers.get(CharityNamePage).map{ name =>
      charityName.rows(name)
    }.fold(List[SummaryListRow]())(_.toList)

    val charityContactDetailsRow = request.userAnswers.get(CharityContactDetailsPage).map{ contact =>
      charityContactDetails.rows(contact)
    }.fold(List[SummaryListRow]())(_.toList)

    val rows = charityNameRow ++ charityContactDetailsRow

    Ok(view(rows, "charityName", controllers.summary.routes.CheckCharityDetailsController.onSubmit()))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    Redirect(navigator.nextPage(CharityNamePage, NormalMode, request.userAnswers))
  }
}
