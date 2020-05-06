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
import controllers.actions.{DataRequiredAction, DataRetrievalAction}
import controllers.auth.{AuthAction, SessionIdentifierAction}
import controllers.connectors.{DataCacheConnector, DataShortCacheConnector}
import forms.EligibilityForm
import javax.inject.Inject
import models.YesNoModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.home.validBank

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ValidBankEligibilityController @Inject()(implicit val appConfig: AppConfig,
   sessionIdentifierAction: SessionIdentifierAction,
   dataShortCacheConnector: DataShortCacheConnector,
   getData: DataRetrievalAction,
   requiredData: DataRequiredAction,
   mcc: MessagesControllerComponents)
  extends FrontendController(mcc) with I18nSupport {

  def onPageLoad: Action[AnyContent] = (sessionIdentifierAction andThen getData andThen requiredData).async { implicit request =>

    val preparedForm = request.userAnswers.eligibilityAccount.
      fold(EligibilityForm.validBankForm)(EligibilityForm.validBankForm.fill)

    Future.successful(Ok(validBank(preparedForm)))

  }

  def onSubmit: Action[AnyContent] = (sessionIdentifierAction andThen getData andThen requiredData).async { implicit request =>
    EligibilityForm.validBankForm.bindFromRequest().fold(
      errors => Future.successful(BadRequest(validBank(errors))),
      success => {
        dataShortCacheConnector.save[YesNoModel](request.sessionId, YesNoModel.eligibilityAccountId, success).map{ _ =>
          if (success.value) {
            Redirect(controllers.routes.UKBasedEligibilityController.onPageLoad())
          }
          else {
            Redirect(controllers.routes.IneligibleForRegistrationController.onPageLoad())
          }
        }
      }
    )
  }
}

