/*
 * Copyright 2026 HM Revenue & Customs
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

case class ManualAddressEntryConfigModel(
  line1MaxLength: Int,
  line2MaxLength: Int,
  line3MaxLength: Int,
  townMaxLength: Int,
  mandatoryFields: MandatoryFields,
  showOrganisationName: Boolean
)

object ManualAddressEntryConfigModel {
  implicit val writes: Writes[ManualAddressEntryConfigModel] = Json.writes[ManualAddressEntryConfigModel]
}

case class MandatoryFields(
  addressLine1: Boolean,
  addressLine2: Boolean,
  addressLine3: Boolean,
  town: Boolean,
  postcode: Boolean
)

object MandatoryFields {
  implicit val writes: Writes[MandatoryFields] = Json.writes[MandatoryFields]
}
