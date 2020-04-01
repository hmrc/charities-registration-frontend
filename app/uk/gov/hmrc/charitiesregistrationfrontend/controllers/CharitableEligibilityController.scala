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

package uk.gov.hmrc.charitiesregistrationfrontend.controllers

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.charitiesregistrationfrontend.config.AppConfig
import uk.gov.hmrc.charitiesregistrationfrontend.forms.EligibilityForm
import uk.gov.hmrc.charitiesregistrationfrontend.views.html.home.eligibility
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class CharitableEligibilityController @Inject()(implicit val appConfig: AppConfig,
                                                mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(eligibility(EligibilityForm.charitableForm)))

  }

  def onSubmit: Action[AnyContent] = Action.async { implicit request =>
    EligibilityForm.charitableForm.bindFromRequest().fold(
      errors => Future.successful(BadRequest(eligibility(errors))),
      success => {
        //TODO code for data storing
        if (success.charitable) {
          Future.successful(Redirect(routes.HelloWorldController.helloWorld()))
        }
        else {
          Future.successful(Redirect(routes.HelloWorldController.helloWorld()))
        }
      }
    )
  }

}
