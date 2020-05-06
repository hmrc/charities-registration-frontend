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
import forms.CharitiesContactDetailsForm
import javax.inject.Inject
import models.ContactDetailsModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.home.contactDetails

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class CharitiesContactDetailsController @Inject()(implicit val appConfig: AppConfig,
                                                  val dataCacheConnector: DataCacheConnector,
                                                  getData: DataRetrievalAction,
                                                  authAction: AuthAction,
                                                  mcc: MessagesControllerComponents) extends FrontendController(mcc) with I18nSupport{

  def onPageLoad: Action[AnyContent] =  (authAction andThen getData).async {
    implicit request =>
      val preparedForm = request.userAnswers.flatMap(x => x.contactDetails) match {
        case None => CharitiesContactDetailsForm.contactDetailsForm
        case Some(value) => CharitiesContactDetailsForm.contactDetailsForm.fill(value)
      }
      Future.successful(Ok(contactDetails(preparedForm)))

  }

 def onSubmit: Action[AnyContent] = (authAction andThen getData).async {
   implicit request =>
   CharitiesContactDetailsForm.contactDetailsForm.bindFromRequest().fold(
      errors => Future.successful(BadRequest(contactDetails(errors))),
     successful => {
       dataCacheConnector.save[ContactDetailsModel](request.sessionId, ContactDetailsModel.toString, successful).map(cacheMap =>
       Redirect(controllers.routes.HelloWorldController.helloWorld()))
        }
    )
  }
}
