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

package forms.operationsAndFunds

import forms.mappings.Mappings
import javax.inject.Inject
import models.BankDetails
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

class BankDetailsFormProvider @Inject() extends Mappings {

  val validateFields: String = "^[^@&:)(]+$".r.anchored.toString
  val sortCodePattern: String = "^[ -]*(?:\\d[ -]*){6,6}$".r.anchored.toString
  val accountNumberPattern: String = "^[ -]*(?:\\d[ -]*){6,8}$".r.anchored.toString
  val rollNumberPattern: String = "^[a-zA-Z0-9,. -]*$".r.anchored.toString


  def apply(): Form[BankDetails] =
    Form(
      mapping(
        "accountName" -> text("bankDetails.accountName.error.required")
          .verifying(maxLength(60, "bankDetails.accountName.error.length"))
          .verifying(regexp(validateFields,"bankDetails.accountName.error.format")),
        "sortCode" -> text("bankDetails.sortCode.error.required")
          .verifying(sortCodeLength(6, "bankDetails.sortCode.error.length"))
          .verifying(regexp(sortCodePattern,"bankDetails.sortCode.error.format")),
        "accountNumber" -> text("bankDetails.accountNumber.error.required")
          .verifying(accountNumberLength(6, 8, "bankDetails.accountNumber.error.length"))
          .verifying(regexp(accountNumberPattern,"bankDetails.accountNumber.error.format")),
        "rollNumber" -> optional(text()
          .verifying(maxLength(18, "bankDetails.rollNumber.error.length")).
          verifying(regexp(rollNumberPattern,"bankDetails.rollNumber.error.format")))
      )(BankDetails.apply)(BankDetails.unapply)
    )
}
