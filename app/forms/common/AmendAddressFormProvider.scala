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

package forms.common

import forms.mappings.Mappings
import models.addressLookup.AmendAddressModel
import play.api.data.Form
import play.api.data.Forms.{default, mapping, optional, text => posttext}
import play.api.i18n.Messages

import javax.inject.Inject

class AmendAddressFormProvider @Inject() extends Mappings {

  private[common] val postcodePattern =
    """^(?i)(GIR 0AA)|((([A-Z][0-9][0-9]?)|(([A-Z][A-HJ-Y][0-9][0-9]?)|(([A-Z][0-9][A-Z])|([A-Z][A-HJ-Y][0-9]?[A-Z])))) ?[0-9][A-Z]{2})$"""
  private[common] val maxLength       = 35

  def apply(messagePrefix: String): Form[AmendAddressModel] =
    Form(
      mapping(
        "line1"    -> text(s"$messagePrefix.addressLine1.error.required")
          .verifying(maxLength(maxLength, s"$messagePrefix.addressLine1.error.length"))
          .verifying(regexp(validateFieldWithFullStop, s"$messagePrefix.addressLine1.error.format")),
        "line2"    -> optional(
          text()
            .verifying(maxLength(maxLength, s"$messagePrefix.addressLine2.error.length"))
            .verifying(regexp(validateFieldWithFullStop, s"$messagePrefix.addressLine2.error.format"))
        ),
        "line3"    -> optional(
          text()
            .verifying(maxLength(maxLength, s"$messagePrefix.addressLine3.error.length"))
            .verifying(regexp(validateFieldWithFullStop, s"$messagePrefix.addressLine3.error.format"))
        ),
        "town"     -> text(s"$messagePrefix.townOrCity.error.required")
          .verifying(maxLength(maxLength, s"$messagePrefix.townOrCity.error.length"))
          .verifying(regexp(validateFieldWithFullStop, s"$messagePrefix.townOrCity.error.format")),
        "postcode" -> default(posttext, ""),
        "country"  -> text(s"$messagePrefix.country.error.required")
      )(AmendAddressModel.apply)(AmendAddressModel.unapply)
    )

  def validatePostCode(form: Form[AmendAddressModel])(implicit messages: Messages): Form[AmendAddressModel] = {

    val isGB: Boolean = form("country").value.fold(true)(_ == "GB")
    val postcode      = form("postcode").value.getOrElse("")

    (isGB, postcode) match {
      case (true, _) if postcode.nonEmpty && !postcode.matches(postcodePattern)            =>
        form.withError("postcode", messages(s"amendAddress.postcode.error.format"))
      case (false, _) if postcode.nonEmpty && !postcode.matches(validateFieldWithFullStop) =>
        form.withError("postcode", messages(s"amendAddress.postcode.error.format.nonUK"))
      case (false, _) if postcode.nonEmpty && postcode.length > maxLength                  =>
        form.withError("postcode", messages(s"amendAddress.postcode.error.length"))
      case _                                                                               => form
    }
  }
}
