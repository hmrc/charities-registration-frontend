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
import forms.CharitiesContactDetailsForm
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.home.contactDetails


import scala.concurrent.Future

class CharitiesContactDetailsController @Inject()(implicit val appConfig: AppConfig,
                                                  mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(contactDetails(CharitiesContactDetailsForm.contactDetailsForm)))

  }

 def onSubmit: Action[AnyContent] = Action.async { implicit request =>
   CharitiesContactDetailsForm.contactDetailsForm.bindFromRequest().fold(
      errors => Future.successful(BadRequest(contactDetails(errors))),
     successful => {  Future.successful(Redirect(controllers.routes.ValidCountriesEligibilityController.onPageLoad()))
        //TODO code for data storing

        }
    )
  }
}
