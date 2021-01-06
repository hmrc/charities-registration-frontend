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

package controllers

import config.FrontendAppConfig
import controllers.actions.{AuthIdentifierAction, RegistrationDataRequiredAction, UserDataRetrievalAction}
import javax.inject.Inject
import models.requests.DataRequest
import pages.OldServiceSubmissionPage
import play.api.mvc.Results.Redirect
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import utils.ImplicitDateFormatter
import views.html.ApplicationBeingProcessedView

import scala.concurrent.Future

//scalastyle:off magic.number
class ApplicationBeingProcessedController @Inject()(
    identify: AuthIdentifierAction,
    getData: UserDataRetrievalAction,
    view: ApplicationBeingProcessedView,
    val controllerComponents: MessagesControllerComponents
  )(implicit appConfig: FrontendAppConfig) extends ImplicitDateFormatter with LocalBaseController {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>

    request.userAnswers match {
      case None =>
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      case Some(data) =>
        data.get(OldServiceSubmissionPage) match {

          case Some(oldServiceSubmission) =>

            val date = oldStringToDate(oldServiceSubmission.submissionDate)
            Future.successful(Ok(view(dayToString(date, dayOfWeek = false), oldServiceSubmission.refNumber
            )))

          case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
        }
    }

  }
}
