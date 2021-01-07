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

package forms.regulatorsAndDocuments

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import models.CharityOtherRegulatorDetails
import play.api.data.Forms._

class CharityOtherRegulatorDetailsFormProvider @Inject() extends Mappings {

  private[regulatorsAndDocuments] val maxLengthRegulatorName = 100
  private[regulatorsAndDocuments] val maxLengthRegistrationNumber = 20

  def apply(): Form[CharityOtherRegulatorDetails] =
    Form(
      mapping(
        "regulatorName" -> text("charityOtherRegulatorDetails.regulatorName.error.required")
          .verifying(maxLength(maxLengthRegulatorName, "charityOtherRegulatorDetails.regulatorName.error.length"))
          .verifying(regexp(validateFieldWithFullStop,"charityOtherRegulatorDetails.regulatorName.error.format")),

      "registrationNumber" -> text("charityOtherRegulatorDetails.registrationNumber.error.required")
        .verifying(maxLength(maxLengthRegistrationNumber, "charityOtherRegulatorDetails.registrationNumber.error.length"))
        .verifying(regexp(validateFieldWithFullStop,"charityOtherRegulatorDetails.registrationNumber.error.format"))
      )(CharityOtherRegulatorDetails.apply)(CharityOtherRegulatorDetails.unapply))

}
