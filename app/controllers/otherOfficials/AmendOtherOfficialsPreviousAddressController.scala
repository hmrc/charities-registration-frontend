/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.otherOfficials

import config.FrontendAppConfig
import controllers.actions._
import controllers.common.AmendAddressController
import forms.common.AmendAddressFormProvider
import models.{Index, Mode}
import navigation.OtherOfficialsNavigator
import pages.addressLookup.OtherOfficialPreviousAddressLookupPage
import pages.otherOfficials.OtherOfficialsNamePage
import pages.sections.Section8Page
import play.api.mvc._
import service.{CountryService, UserAnswerService}
import views.html.common.AmendAddressView

import javax.inject.Inject
import scala.concurrent.Future

class AmendOtherOfficialsPreviousAddressController @Inject() (
  val identify: AuthIdentifierAction,
  val getData: UserDataRetrievalAction,
  val requireData: DataRequiredAction,
  val countryService: CountryService,
  val formProvider: AmendAddressFormProvider,
  override val sessionRepository: UserAnswerService,
  override val navigator: OtherOfficialsNavigator,
  override val controllerComponents: MessagesControllerComponents,
  override val view: AmendAddressView
)(implicit appConfig: FrontendAppConfig)
    extends AmendAddressController {

  override val messagePrefix: String = "amendOtherOfficialPreviousAddress"

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getFullName(OtherOfficialsNamePage(index)) { otherOfficialsName =>
        Future.successful(
          getView(
            OtherOfficialPreviousAddressLookupPage(index),
            controllers.otherOfficials.routes.AmendOtherOfficialsPreviousAddressController.onSubmit(mode, index),
            countryService.countries(),
            Some(otherOfficialsName)
          )
        )
      }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getFullName(OtherOfficialsNamePage(index)) { otherOfficialsName =>
        postView(
          mode,
          OtherOfficialPreviousAddressLookupPage(index),
          Section8Page,
          controllers.otherOfficials.routes.AmendOtherOfficialsPreviousAddressController.onSubmit(mode, index),
          countryService.countries(),
          Some(otherOfficialsName)
        )
      }
  }

}
