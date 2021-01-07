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

package forms.common

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class NinoFormProvider @Inject() extends Mappings {

  // scalastyle:off line.size.limit
  private[common] val ninoPattern = """^[ \t]*[A-Za-z]{1}[ \t]*[ \t]*[A-Za-z]{1}[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*[A-Da-d]{1}[ \t]*$""".r.anchored.toString()

  def apply(messagePrefix: String): Form[String] =
    Form(
      "nino" -> text(s"$messagePrefix.error.required")
          .verifying(regexp(ninoPattern,s"$messagePrefix.error.format"))
    )
}


