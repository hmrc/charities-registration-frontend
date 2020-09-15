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

class SectionsChangedGoverningDocumentFormProvider @Inject() extends Mappings {

  private[regulatorsAndDocuments] val validateReasonField = """^[a-zA-Z0-9 ][^@&:)(]*$"""
  private[regulatorsAndDocuments] val maxLength = 350

  def apply(): Form[String] =
    Form(
      "value" -> text("sectionsChangedGoverningDocument.error.required")
         .verifying(maxLength(maxLength, "sectionsChangedGoverningDocument.error.length"))
        .verifying(regexp(validateReasonField,"sectionsChangedGoverningDocument.error.format"))
      )
}