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

package controllers


import models._
import models.requests.DataRequest
import pages.QuestionPage
import play.api.i18n.I18nSupport
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

trait LocalBaseController extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  implicit lazy val ec: ExecutionContext = controllerComponents.executionContext

  def getFullName(page: QuestionPage[Name])(block: String => Future[Result])
                               (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(page).map {
      fullName =>
        block(fullName.getFullName)
    }.getOrElse(Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad())))
  }

}
