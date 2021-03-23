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
import controllers.actions.SessionIdentifierAction
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import views.html.errors.PageNotFoundView

class PageNotFoundController @Inject()(
  identify: SessionIdentifierAction,
  view: PageNotFoundView,
  authConnector: AuthConnector,
  val controllerComponents: MessagesControllerComponents)(
  implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authConnector.authorise(AffinityGroup.Organisation, Retrievals.credentials).map {
      case Some(_) => Ok(view(signedIn = true))
      case _ => Ok(view(signedIn = false))
    }.recover{
      case _ => Ok(view(signedIn = false))
    }
  }

  def redirectToStartOfJourney(): Action[AnyContent] = identify.async { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authConnector.authorise(AffinityGroup.Organisation, Retrievals.credentials).map {
      case Some(_) => Redirect(controllers.routes.IndexController.onPageLoad(None))
      case _ => Redirect(appConfig.signOutUrl).withNewSession
    }.recover{
      case _ => Redirect(appConfig.signOutUrl).withNewSession
    }
  }
}
