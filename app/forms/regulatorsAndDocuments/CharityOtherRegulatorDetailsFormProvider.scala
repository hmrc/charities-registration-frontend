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

package forms.regulatorsAndDocuments

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import models.CharityOtherRegulatorDetails
import play.api.data.Forms._

class CharityOtherRegulatorDetailsFormProvider @Inject() extends Mappings {

  val validateFields = "^[^@&:)(]+$"

  def apply(): Form[CharityOtherRegulatorDetails] =
    Form(
      mapping(
        "regulatorName" -> text("charityOtherRegulatorDetails.regulatorName.error.required")
          .verifying(maxLength(100, "charityOtherRegulatorDetails.regulatorName.error.length"))
          .verifying(regexp(validateFields,"charityOtherRegulatorDetails.regulatorName.error.format")),

      "registrationNumber" -> text("charityOtherRegulatorDetails.registrationNumber.error.required")
        .verifying(maxLength(20, "charityOtherRegulatorDetails.registrationNumber.error.length"))
        .verifying(regexp(validateFields,"charityOtherRegulatorDetails.registrationNumber.error.format"))
      )(CharityOtherRegulatorDetails.apply)(CharityOtherRegulatorDetails.unapply))

}
