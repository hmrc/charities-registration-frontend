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

import java.util.UUID

import config.AppConfig
import controllers.auth.AuthAction
import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CharitiesController @Inject()(servicesConfig: ServicesConfig,
                                    implicit val appConfig: AppConfig,
                                    authAction: AuthAction,
                                    mcc: MessagesControllerComponents)
  extends FrontendController(mcc) with I18nSupport {
  val CharitiesStartPageURL: String = appConfig.startURL

  /**
   * every session should have an ID: required by key-store
   * If no session Id is found or session was deleted (NOSESSION), user is redirected to welcome page, where new session id will be issued on submit
   * @return redirect to required page
   */

  def generateSessionId: (String, String) = SessionKeys.sessionId -> s"session-${UUID.randomUUID}"

  def authenticate: Action[AnyContent] = authAction.async{
    implicit request =>
      Future.successful(Redirect(controllers.routes.HelloWorldController.helloWorld()))
  }
}
