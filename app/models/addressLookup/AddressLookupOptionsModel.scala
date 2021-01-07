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

package models.addressLookup

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl

case class AddressLookupOptionsModel(continueUrl: SafeRedirectUrl,
                                     phaseFeedbackLink: SafeRedirectUrl,
                                     signOutHref: SafeRedirectUrl,
                                     deskProServiceName: String,
                                     showPhaseBanner: Boolean,
                                     showBackButtons: Boolean,
                                     includeHMRCBranding: Boolean,
                                     allowedCountryCodes: Option[Set[String]],
                                     ukMode: Boolean,
                                     disableTranslations: Boolean,
                                     selectPageConfig: AddressLookupSelectConfigModel,
                                     confirmPageConfig: AddressLookupConfirmConfigModel,
                                     timeoutConfig: AddressLookupConfirmTimeoutModel)

object AddressLookupOptionsModel {
  implicit val writes: Writes[AddressLookupOptionsModel] = Json.writes[AddressLookupOptionsModel]
}

