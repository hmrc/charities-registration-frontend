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

package forms.charityInformation

import forms.mappings.Mappings
import javax.inject.Inject
import models.CharityUKAddress
import play.api.data.Form
import play.api.data.Forms._

class CharityUKAddressFormProvider @Inject() extends Mappings {

  val validateFields = "^[^@&:)(]+$"
  val validatePostcode = "^([Gg][Ii][Rr] 0[Aa][Aa])|((([A-Za-z][0-9][0-9]?)|(([A-Za-z][A-Ha-hJ-Yj-y][0-9][0-9]?)|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z])))) [0-9][A-Za-z]{2})$"

  def apply(): Form[CharityUKAddress] =
    Form(
      mapping(
        "addressLine1" -> text("charityUKAddress.addressLine1.required")
          .verifying(maxLength(35, "charityUKAddress.addressLine1.error.length"))
          .verifying(regexp(validateFields,"charityUKAddress.addressLine1.error.format")),

        "addressLine2" -> optional(text().verifying(maxLength(35, "charityUKAddress.addressLine2.error.length"))
          .verifying(regexp(validateFields,"charityUKAddress.addressLine2.error.format"))),

        "addressLine3" -> optional(text().verifying(maxLength(35, "charityUKAddress.addressLine3.error.length"))
          .verifying(regexp(validateFields,"charityUKAddress.addressLine3.error.format"))),

        "townCity" -> text("charityUKAddress.townCity.required")
          .verifying(maxLength(35, "charityUKAddress.townCity.error.length"))
          .verifying(regexp(validateFields,"charityUKAddress.townCity.error.format")),

        "postcode" -> text("charityUKAddress.postcode.required")
            .verifying(regexp(validatePostcode, "charityUKAddress.postcode.error.format"))
      )
      (CharityUKAddress.apply)(CharityUKAddress.unapply)
    )

}
