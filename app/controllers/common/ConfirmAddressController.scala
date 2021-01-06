/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.common

import config.FrontendAppConfig
import controllers.LocalBaseController
import models.addressLookup.AddressModel
import models.requests.DataRequest
import pages.QuestionPage
import play.api.mvc._
import service.CountryService
import views.html.common.ConfirmAddressView

import scala.concurrent.Future

trait ConfirmAddressController extends LocalBaseController {
  val view: ConfirmAddressView
  val appConfig: FrontendAppConfig
  val countryService: CountryService
  val messagePrefix: String
  val page: QuestionPage[AddressModel]

  def changeLinkCall: Call

  def getView(submissionCall: Call, name: Option[String] = None)(implicit appConfig: FrontendAppConfig, request: DataRequest[AnyContent]): Future[Result] = {
    getAddress(page) { (addressLine, country) =>
      val addressWithCountry: Seq[String] = addressLine :+ countryService.translatedCountryName(country)
      Future.successful(Ok(view(addressWithCountry, messagePrefix, submissionCall, changeLinkCall, name)))
    }
  }
}
