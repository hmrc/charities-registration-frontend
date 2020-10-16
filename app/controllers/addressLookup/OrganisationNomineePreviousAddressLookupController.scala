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

package controllers.addressLookup

import config.FrontendAppConfig
import connectors.addressLookup.AddressLookupConnector
import controllers.actions._
import javax.inject.Inject
import models.Mode
import navigation.NomineesNavigator
import pages.addressLookup.OrganisationNomineePreviousAddressLookupPage
import pages.nominees.OrganisationNomineeNamePage
import pages.sections.Section9Page
import play.api.mvc._
import repositories.UserAnswerRepository
import viewmodels.ErrorHandler

class OrganisationNomineePreviousAddressLookupController @Inject()(
  override val sessionRepository: UserAnswerRepository,
  override val navigator: NomineesNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  override val addressLookupConnector: AddressLookupConnector,
  override val errorHandler: ErrorHandler,
  val controllerComponents: MessagesControllerComponents
 )(implicit appConfig: FrontendAppConfig) extends BaseAddressController {

  override val messagePrefix : String = "nomineeOrganisationPreviousAddress"

  def initializeJourney(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getOrganisationName(OrganisationNomineeNamePage) { organisationName =>

        val callBack: String = controllers.addressLookup.routes.OrganisationNomineePreviousAddressLookupController.callback(mode).url

        addressLookupInitialize(callBack, Some(organisationName))
      }
  }

  def callback(mode: Mode, id: Option[String]): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      addressLookupCallback(OrganisationNomineePreviousAddressLookupPage, Section9Page, id, mode)
  }
}