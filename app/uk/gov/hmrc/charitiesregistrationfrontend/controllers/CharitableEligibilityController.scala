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
import uk.gov.hmrc.charitiesregistrationfrontend.models.EligibilityModel
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.charitiesregistrationfrontend.views.html.home.eligibility
import play.api.data.Form
import play.api.i18n.Messages

import scala.concurrent.Future

class CharitableEligibilityController @Inject()(applicationConfig: AppConfig,
                                                mcc: MessagesControllerComponents, forms : EligibilityForm) extends FrontendController(mcc) {

  implicit val config: AppConfig = applicationConfig


  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(eligibility(config, forms.charitableForm)))

  }

  def onSubmit : Action[AnyContent] = Action.async { implicit request =>
    def errorAction(errors: Form[EligibilityModel]) = Future.successful(BadRequest(eligibility(config, errors)))

    def successAction(model: EligibilityModel) = {
      Future.successful(Redirect(routes.HelloWorldController.helloWorld()))
    }
    forms.charitableForm.bindFromRequest().fold(errorAction, successAction)
  }

}
