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

import config.AppConfig
import controllers.actions.DataRetrievalAction
import controllers.auth.AuthAction
import controllers.connectors.DataCacheConnector
import forms.CharityDetailsFormProvider
import javax.inject.Inject
import models.CharityNamesModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.CharityNameDetailsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CharityNameDetailsController @Inject()(implicit val appConfig: AppConfig,
                                             val dataCacheConnector: DataCacheConnector,
                                             getData: DataRetrievalAction,
                                             authAction: AuthAction,
                                             mcc: MessagesControllerComponents)
  extends FrontendController(mcc)  with I18nSupport {

  def onPageLoad: Action[AnyContent] = (authAction andThen getData).async {
    implicit request =>
      val preparedForm = request.userAnswers.flatMap(x => x.charityNameDetails) match {
      case None => CharityDetailsFormProvider.charityNamesForm()
      case Some(value) => CharityDetailsFormProvider.charityNamesForm().fill(value)
    }
    Future.successful(Ok(CharityNameDetailsView(preparedForm)))

  }


 def onSubmit: Action[AnyContent] = (authAction andThen getData).async { implicit request =>
   CharityDetailsFormProvider.charityNamesForm().bindFromRequest().fold(
      errors => Future.successful(BadRequest(CharityNameDetailsView(errors))),
      success => {
        dataCacheConnector.save[CharityNamesModel](request.sessionId, CharityNamesModel.toString, success).map(cacheMap =>
         Redirect(controllers.routes.HelloWorldController.helloWorld()))
      }
    )
  }
}
