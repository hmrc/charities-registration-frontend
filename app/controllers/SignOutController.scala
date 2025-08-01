/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import views.html.errors.SignedYouOutView

import javax.inject.Inject

class SignOutController @Inject() (val controllerComponents: MessagesControllerComponents, view: SignedYouOutView)(
  implicit appConfig: FrontendAppConfig
) extends LocalBaseController {

  def signOut(): Action[AnyContent] = Action { implicit request =>
    Redirect(appConfig.signOutUrl, Map("continue" -> Seq(appConfig.exitSurveyUrl)))
  }

  def signedYouOut: Action[AnyContent] = Action { implicit request =>
    Ok(view())
  }

  def signOutNoSurvey(): Action[AnyContent] = Action { implicit request =>
    val signOutServiceUrl = appConfig.host + routes.SignOutController.signedYouOut.url
    Redirect(
      appConfig.signOutUrl,
      Map("continue" -> Seq(signOutServiceUrl))
    )
  }

}
