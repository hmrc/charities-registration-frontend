/*
 * Copyright 2025 HM Revenue & Customs
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

package pages.addressLookup

import models.addressLookup.AddressModel
import pages.QuestionPage
import pages.nominees.NomineeIndividualIdPage
import play.api.libs.json.JsPath

case object NomineeIndividualAddressLookupPage extends QuestionPage[AddressModel] {

  override def path: JsPath = NomineeIndividualIdPage.path \ toString

  override lazy val toString: String = "individualAddress"
}
