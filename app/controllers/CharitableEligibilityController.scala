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
import controllers.auth.SessionIdentifierAction
import controllers.connectors.DataShortCacheConnector
import forms.EligibilityForm
import javax.inject.Inject
import models.YesNoModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.home.eligibility

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CharitableEligibilityController @Inject()(implicit val appConfig: AppConfig,
                                                sessionIdentifierAction: SessionIdentifierAction,
                                                dataShortCacheConnector: DataShortCacheConnector,
                                                getData: DataRetrievalAction,
                                                mcc: MessagesControllerComponents)
  extends FrontendController(mcc) with I18nSupport {

  def onPageLoad: Action[AnyContent] = (sessionIdentifierAction andThen getData).async { implicit request =>

    val preparedForm = request.userAnswers.flatMap(x => x.eligibilityPurposes).
      fold(EligibilityForm.charitableForm)(EligibilityForm.charitableForm.fill)

    Future.successful(Ok(eligibility(preparedForm)))

  }

  def onSubmit: Action[AnyContent] = (sessionIdentifierAction andThen getData).async { implicit request =>
    EligibilityForm.charitableForm.bindFromRequest().fold(
      errors => Future.successful(BadRequest(eligibility(errors))),
      success => {
        dataShortCacheConnector.save[YesNoModel](request.sessionId, YesNoModel.eligibilityPurposesId, success).map{ _ =>
          if (success.value) {
            Redirect(controllers.routes.ValidBankEligibilityController.onPageLoad())
          }
          else {
            Redirect(controllers.routes.IneligibleForRegistrationController.onPageLoad())
          }
        }
      }
    )
  }

}
