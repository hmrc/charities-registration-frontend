/*
 * Copyright 2024 HM Revenue & Customs
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

import connectors.addressLookup.AddressLookupConnector
import controllers.actions._
import navigation.CharityInformationNavigator
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.sections.Section1Page
import play.api.mvc._
import service.UserAnswerService
import viewmodels.ErrorHandler

import javax.inject.Inject

class CharityOfficialAddressLookupController @Inject() (
  override val sessionRepository: UserAnswerService,
  override val navigator: CharityInformationNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  override val addressLookupConnector: AddressLookupConnector,
  override val errorHandler: ErrorHandler,
  val controllerComponents: MessagesControllerComponents
) extends BaseAddressController {

  override val messagePrefix: String = "charityOfficialAddress"

  def initializeJourney: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    addressLookupInitialize(
      controllers.addressLookup.routes.CharityOfficialAddressLookupController.callback().url,
      None,
      Option(
        Set(
          "AT",
          "BE",
          "BG",
          "HR",
          "CY",
          "CZ",
          "DK",
          "EE",
          "FI",
          "FR",
          "DE",
          "GR",
          "HU",
          "IS",
          "IE",
          "IT",
          "LV",
          "LI",
          "LT",
          "LU",
          "MT",
          "NL",
          "NO",
          "PL",
          "PT",
          "RO",
          "SK",
          "SI",
          "ES",
          "SE",
          "GB"
        )
      )
    )
  }

  def callback(id: Option[String]): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      addressLookupCallback(CharityOfficialAddressLookupPage, Section1Page, id)
  }
}
